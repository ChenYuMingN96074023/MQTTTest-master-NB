package com.example.mqtttest.recyclerChatRoom;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.mqtttest.MainActivity;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MQTTFunction {
    Context context;
    ArrayList<MQTTBean> arrayList = new ArrayList<>();
    RecyclerView recyclerView;
    String myClientId;
    MQTTBean data;
    private final LinearLayoutManager layoutManager;
//    private DBHelper_ChatMessages dbHelper_chatMessages = null;

    public MQTTFunction(Context context, RecyclerView recyclerView, String myClientId) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.myClientId = myClientId;
        this.recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        this.recyclerView.setLayoutManager(layoutManager);
    }

    public void addData(String string){
        try {
            data = new Gson().fromJson(string, MQTTBean.class);
        }catch (Exception e){
            Log.d("TAG", "addData: " + e.getMessage());
            data = new MQTTBean(string,"ERROR", MainActivity.ERRROR);
        }
        arrayList.add(data);
        layoutManager.scrollToPosition(arrayList.size()-1);
        recyclerView.setAdapter(new MQTTAdapter(context,arrayList,myClientId));
    }
}
