package com.example.mqtttest;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.mqtttest.recyclerChatRoomList.CRListAdapter;
import com.example.mqtttest.recyclerChatRoomList.CRListBean;

import java.util.ArrayList;

public class ChatRoomListActivity extends AppCompatActivity {

    private RecyclerView chatRoomListRv;
    private ArrayList arrayListCRList = new ArrayList<>();
    private Button btn_addCR;
    private LinearLayout addCRLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);
        chatRoomListRv = findViewById(R.id.chat_room_list_recyclerview);
        chatRoomListRv.setLayoutManager(new LinearLayoutManager(ChatRoomListActivity.this));
        chatRoomListRv.setAdapter(new CRListAdapter(ChatRoomListActivity.this, arrayListCRList));


        arrayListCRList.add(new CRListBean("NCKU_TOPIC", "12:20", "1", 1));
        arrayListCRList.add(new CRListBean("user2", "10:10", "2", 2));
        arrayListCRList.add(new CRListBean("user2", "10:10", "3", 2));
        arrayListCRList.add(new CRListBean("user2", "10:10", "4", 2));


        addCRLayout = findViewById(R.id.addCRLayout);
        btn_addCR = findViewById(R.id.add_chat_room_btn);
        btn_addCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addCRLayout.getVisibility() == View.GONE) {
                    addCRLayout.setVisibility(View.VISIBLE);
                    btn_addCR.setText("-");
                }else {
                    addCRLayout.setVisibility(View.GONE);
                    btn_addCR.setText("+");
                }
            }
        });
    }

    public void addCRok(View view) {
        TextInputEditText et_add_CR = findViewById(R.id.et_add_CR);
        arrayListCRList.add(new CRListBean(et_add_CR.getText().toString(), "14:32", "YAAAAAA", 3));
        et_add_CR.setText("");
        addCRLayout.setVisibility(View.GONE);
        btn_addCR.setText("+");
    }




}
