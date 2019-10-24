package com.example.mqtttest.recyclerPhoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mqtttest.R;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    Context context;
    ArrayList<PhotoBean> arrayList;

    public PhotoAdapter(Context context, ArrayList<PhotoBean> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_screen,viewGroup,false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder photoViewHolder, int i) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(arrayList.get(i).getBitmap(),0,arrayList.get(i).getBitmap().length);
        photoViewHolder.imgPhoto.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
        }
    }
}
