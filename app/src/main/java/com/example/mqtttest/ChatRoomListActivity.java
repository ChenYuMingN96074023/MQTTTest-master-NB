package com.example.mqtttest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private static final String TAG = ChatRoomListActivity.class.getSimpleName();
    private static final int DB_CRItem_VERSION = 1;
    //以下為測試連線
    private MqttHelper mqttHelper;


    public ChatRoomListActivity() { }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);
        findViewById();

        //以下開啟SQLite，讀取列表，顯示於畫面上
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
            mqttHelper.subscribeIndividual();
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

    public void addCRok(View view) {//按鈕 "確定" ，新增聊天室 //功能已暫時被取代
        TextInputEditText et_add_CR = findViewById(R.id.et_add_CR);
        String topic = et_add_CR.getText().toString();

        addChatroom(1, topic);
        //以下為設定畫面
        et_add_CR.setText("");
        addCRLayout.setVisibility(View.GONE);
        btn_addCR.setText("+");

    }

    public void add_chat_room_btn(View view){//按鈕 "+-"
//        if (addCRLayout.getVisibility() == View.GONE) {
//            addCRLayout.setVisibility(View.VISIBLE);
//            btn_addCR.setText("-");
//        }else {
//            addCRLayout.setVisibility(View.GONE);
//            btn_addCR.setText("+");
//        }
        addChatroomAlertdialog_chooseIndOrGrp();
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

    private void addChatroomAlertdialog_chooseIndOrGrp(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(ChatRoomListActivity.this);
        dialog.setTitle("建立");
        dialog.setMessage("您想建立何種聊天室?");
        dialog.setNegativeButton("個人私訊",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                addChatroomAlertdialog_editCRTitle(0);
            }

        });
        dialog.setPositiveButton("群組",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                addChatroomAlertdialog_editCRTitle(1);
            }

        });
        dialog.setNeutralButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //不做任何動作
            }

        });
        dialog.show();
    }
    private void addChatroomAlertdialog_editCRTitle(int indOrGrp){

        LayoutInflater inflater = LayoutInflater.from(ChatRoomListActivity.this);
        final View v = inflater.inflate(R.layout.add_chatroom_alertdialog_layout, null);
        EditText editText = (EditText) (v.findViewById(R.id.editText_addCR));
        new AlertDialog.Builder(ChatRoomListActivity.this)
                .setTitle(indOrGrp==1?"請輸入想要新增的群組名稱":"請輸入想要私訊的對象")
                .setView(v)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addChatroom(indOrGrp, editText.getText().toString());
                    }
                })
                .show();
    }

    public void addChatroom(int indOrGrp, String chatroomName){
        try{
            //以下為建立SQLite，並判斷是否需Sub
            dbHelper_chatMessages = new DBHelper_ChatMessages(this, null, indOrGrp, chatroomName, null, 1);
            dbHelper_chatMessages.createTable(chatroomName);
            dbHelper_chatMessages.close();
            dbHelper_chatMessages = null;
            if(indOrGrp ==1){
                mqttHelper.subscribeGroupTopic(chatroomName);
            }
            //以下為設定CRLItem，並更新畫面
            dbHelper_CRL.addRec(new CRListBean(chatroomName, "null", "null", 3, 0, indOrGrp));
            arrayListCRList = dbHelper_CRL.getRecSet();
            chatRoomListRv.setAdapter(new CRListAdapter(ChatRoomListActivity.this, arrayListCRList));
        }catch (Exception e){
            Toast.makeText(ChatRoomListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
