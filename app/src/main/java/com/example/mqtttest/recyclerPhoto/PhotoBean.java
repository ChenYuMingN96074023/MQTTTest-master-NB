package com.example.mqtttest.recyclerPhoto;

import android.graphics.Bitmap;

import java.io.Serializable;

public class PhotoBean implements Serializable {
    byte[] bitmap;
    String id;
    int item;

    public PhotoBean() {
    }

    public PhotoBean(byte[] bitmap, String id, int item) {
        this.bitmap = bitmap;
        this.id = id;
        this.item = item;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }
}
