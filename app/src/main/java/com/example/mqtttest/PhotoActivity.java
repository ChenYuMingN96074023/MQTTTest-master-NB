package com.example.mqtttest;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.mqtttest.recyclerPhoto.PhotoBean;
import com.example.mqtttest.recyclerPhoto.PhotoFunction;

import java.io.Serializable;
import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = PhotoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Serializable serializable = getIntent().getSerializableExtra("PHOTO_ARRAY_LIST");
        int itemNum = getIntent().getIntExtra("PHOTO_SCREEN_ITEM_NUM",0);
        ArrayList<PhotoBean> photo = (ArrayList<PhotoBean>) serializable;

        RecyclerView recyclerView = findViewById(R.id.recyclerPhoto);
        PhotoFunction photoFunction = new PhotoFunction(this, recyclerView, photo, itemNum);

    }
}
