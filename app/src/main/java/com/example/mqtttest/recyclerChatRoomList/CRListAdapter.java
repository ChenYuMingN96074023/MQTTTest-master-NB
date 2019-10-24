package com.example.mqtttest.recyclerChatRoomList;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mqtttest.MainActivity;
import com.example.mqtttest.R;

import java.util.ArrayList;

public class CRListAdapter extends RecyclerView.Adapter<CRListAdapter.myViewHolder> {
    Context context;
    ArrayList<CRListBean> arrayList;

    public CRListAdapter(Context context, ArrayList<CRListBean> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.chatroomlist_layout,viewGroup,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, final int i) {
        myViewHolder.topic.setText(arrayList.get(i).getTopic());
        myViewHolder.time.setText(arrayList.get(i).getTime());
        myViewHolder.message.setText(arrayList.get(i).getMessage());

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"變成按鈕了!",Toast.LENGTH_SHORT).show();
                Intent to_cr_intent = new Intent(context, MainActivity.class);
                to_cr_intent.putExtra("MY_TOPIC",arrayList.get(i).getTopic().toString());

                context.startActivity(to_cr_intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView topic,time, message;
        ImageView icon;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.crl_topic);
            time = itemView.findViewById(R.id.crl_time);
            message = itemView.findViewById(R.id.crl_message);
            icon = itemView.findViewById(R.id.crl_icon);
        }
    }


}
