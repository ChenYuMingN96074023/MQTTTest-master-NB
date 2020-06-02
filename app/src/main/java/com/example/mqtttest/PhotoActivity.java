package com.example.mqtttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.example.mqtttest.database.DBHelper_ChatMessages;
import com.example.mqtttest.recyclerPhoto.PhotoAdapter;
import com.example.mqtttest.recyclerPhoto.PhotoBean;

import java.io.Serializable;
import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = PhotoActivity.class.getSimpleName();
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayout.HORIZONTAL,false);
    private DBHelper_ChatMessages dbHelper_chatMessages = null;
    private String topic,time;
    private int indOrGrp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //以下藉由topic,indOrGrp,time，得到ArrayList與index
        topic = getIntent().getStringExtra("TOPIC");
        indOrGrp = getIntent().getIntExtra("IND_OR_GRP",-1);
        time = getIntent().getStringExtra("TIME");
        dbHelper_chatMessages = new DBHelper_ChatMessages(this, null, indOrGrp, topic, null, 1);
        ArrayList<PhotoBean> photoArrayList = dbHelper_chatMessages.getPhotoRecSet(topic);
        int itemIndex = dbHelper_chatMessages.getPhotoIndexByTime(topic, time);

        // 以下用ArrayList顯示於畫面，並移至index的地方(原始程式的"PhotoFunction.class"做的事)
        RecyclerView recyclerView = findViewById(R.id.recyclerPhoto);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        PhotoAdapter adapter = new PhotoAdapter(this, photoArrayList);
        linearLayoutManager.scrollToPosition(itemIndex);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setAdapter(adapter);

        //以下為recyclerView滑動停止後item對齊
        PagerSnapHelper mPagerSnapHelper = new PagerSnapHelper();
        mPagerSnapHelper.attachToRecyclerView(recyclerView);

    }
}
