package com.example.mqtttest;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mqtttest.database.DBHelper_CRL;
import com.example.mqtttest.database.DBHelper_ChatMessages;
import com.example.mqtttest.mqtt.MqttHelper;
import com.example.mqtttest.recyclerChatRoomList.CRListAdapter;
import com.example.mqtttest.recyclerChatRoomList.CRListBean;

import java.util.ArrayList;

public class ChatRoomListActivity extends AppCompatActivity {

    public RecyclerView chatRoomListRv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList arrayListCRList = new ArrayList<>();
    private Button btn_addCR;
    private Button btn_logout;
    private LinearLayout addCRLayout;
    public String myClientID;
    //以下為SQL
    private DBHelper_CRL dbHelper_CRL = null;
    private DBHelper_ChatMessages dbHelper_chatMessages = null;
    private static final String DB_CRItem_NAME = "myDatabase.db";
    private static final int DB_CRItem_VERSION = 1;
    //以下為測試連線
    private MqttHelper mqttHelper;


    public ChatRoomListActivity() { }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);
        findViewById();
        //以下為測試SQL用
        dbHelper_CRL = new DBHelper_CRL(this, DB_CRItem_NAME,null, DB_CRItem_VERSION);
        chatRoomListRv.setLayoutManager(new LinearLayoutManager(ChatRoomListActivity.this));
        arrayListCRList = dbHelper_CRL.getRecSet();
        chatRoomListRv.setAdapter(new CRListAdapter(ChatRoomListActivity.this, arrayListCRList));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { //上滑刷新
            @Override
            public void onRefresh() {
                arrayListCRList = dbHelper_CRL.getRecSet();
                chatRoomListRv.setAdapter(new CRListAdapter(ChatRoomListActivity.this, arrayListCRList));
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        SharedPreferences preferences = getSharedPreferences("testSharePreferences", MODE_PRIVATE);
        myClientID = preferences.getString("clientID", "");

        if(myClientID != null){
            mqttHelper = new MqttHelper(this, myClientID, chatRoomListRv);
        }
    }

    private void findViewById() {
        addCRLayout = findViewById(R.id.addCRLayout);
        btn_addCR = findViewById(R.id.add_chat_room_btn);
        btn_logout = findViewById(R.id.btn_logout);
        chatRoomListRv = findViewById(R.id.chat_room_list_recyclerview);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(dbHelper_CRL ==null){
            dbHelper_CRL = new DBHelper_CRL(this, DB_CRItem_NAME,null, DB_CRItem_VERSION);
        }

        dbHelper_CRL = new DBHelper_CRL(this, DB_CRItem_NAME,null, DB_CRItem_VERSION);

        //更新聊天列表畫面
        arrayListCRList = dbHelper_CRL.getRecSet();
        chatRoomListRv.setAdapter(new CRListAdapter(ChatRoomListActivity.this, arrayListCRList));

        //重連線
        mqttHelper = new MqttHelper(this, myClientID, chatRoomListRv);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dbHelper_CRL != null){
            dbHelper_CRL.close();
            dbHelper_CRL = null;
        }
    }

    public void addCRok(View view) {//按鈕 "確定" ，新增聊天室
        TextInputEditText et_add_CR = findViewById(R.id.et_add_CR);

        try{
            String topic = et_add_CR.getText().toString();
            //以下為Subscribe，並建立SQLite，並發布消息
            dbHelper_chatMessages = new DBHelper_ChatMessages(this, null, topic, null, 1);/////思考該建立在哪
            dbHelper_chatMessages.createTable(topic);
            dbHelper_chatMessages.close();
            dbHelper_chatMessages = null;
            mqttHelper.startSubscribe(topic);
            mqttHelper.startPublish(topic, myClientID + "已加入聊天室", 1);
            //以下為設定CRLItem
            dbHelper_CRL.addRec(new CRListBean(topic, "null", "null", 3, 0));
            arrayListCRList = dbHelper_CRL.getRecSet();
            chatRoomListRv.setAdapter(new CRListAdapter(ChatRoomListActivity.this, arrayListCRList));
            //以下為設定畫面
            et_add_CR.setText("");
            addCRLayout.setVisibility(View.GONE);
            btn_addCR.setText("+");
        }catch (Exception e){
            Toast.makeText(ChatRoomListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void add_chat_room_btn(View view){//按鈕 "+-"
        if (addCRLayout.getVisibility() == View.GONE) {
            addCRLayout.setVisibility(View.VISIBLE);
            btn_addCR.setText("-");
        }else {
            addCRLayout.setVisibility(View.GONE);
            btn_addCR.setText("+");
        }
    }

    public void logout_btn(View view){//登出按鈕的layout

        AlertDialog.Builder ad=new AlertDialog.Builder(ChatRoomListActivity.this);
        ad.setTitle("登出");
        ad.setMessage("確定要登出嗎?\n" +
                "不過這個功能還沒完整，可能會有未知的錯誤>.<|||\n" +
                "若要完整登出並刪除紀錄，建議登出後再解除安裝此程式\n");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() {//登出按鈕
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub
                logout();
            }
        });
        ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                //不退出不用執行任何操作
            }
        });
        ad.show();//顯示對話框
    }
    private void logout(){//logout時真正做的事
        mqttHelper.connectToCleanSession();

        SharedPreferences preferences = getSharedPreferences("testSharePreferences", MODE_PRIVATE);
        preferences.edit()
                .putString("clientID", null)
                .commit();

        dbHelper_CRL.deleteTable();

        Intent logoutIntent  = new Intent(ChatRoomListActivity.this, LoginActivity.class);
        startActivity(logoutIntent);

        ChatRoomListActivity.this.finish();//關閉activity

        //////之後要做刪除Database!!
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ConfirmExit();//按返回鍵，則執行退出確認
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void ConfirmExit(){//退出確認
        AlertDialog.Builder ad=new AlertDialog.Builder(ChatRoomListActivity.this);
        ad.setTitle("離開");
        ad.setMessage("確定要離開此程式嗎?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() {//退出按鈕
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub
                ChatRoomListActivity.this.finish();//關閉activity
            }
        });
        ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                //不退出不用執行任何操作
            }
        });
        ad.show();//顯示對話框
    }

}
