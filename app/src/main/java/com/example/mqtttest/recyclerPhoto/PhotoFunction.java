package com.example.mqtttest.recyclerPhoto;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.example.mqtttest.PhotoActivity;

import java.util.ArrayList;

public class PhotoFunction {

    Context context;
    RecyclerView recyclerView;
    ArrayList<PhotoBean> arrayList;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayout.HORIZONTAL,false);

    public PhotoFunction(Context context, RecyclerView recyclerView, ArrayList<PhotoBean> arrayList,int item) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.arrayList = arrayList;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        PhotoAdapter adapter = new PhotoAdapter(context,arrayList);
        linearLayoutManager.scrollToPosition(item);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setAdapter(adapter);
    }


}
