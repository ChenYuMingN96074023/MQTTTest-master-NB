package com.example.mqtttest.recyclerChatRoomList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
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
    private String DB_NAME = "chatMessages.db";
    private final int DB_ChatMessage_Version = 1;




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
    public void onBindViewHolder(@NonNull final myViewHolder myViewHolder, final int i) {
        myViewHolder.topic.setText(arrayList.get(i).getTopic());
        myViewHolder.time.setText(arrayList.get(i).getTime());
        myViewHolder.message.setText(arrayList.get(i).getMessage());
        myViewHolder.unread_msg_num.setText(Integer.toString(arrayList.get(i).getUnread_msg_num()));

//        myViewHolder.topic.setTypeface(myViewHolder.topic.getTypeface(), Typeface.BOLD);

        if(arrayList.get(i).getUnread_msg_num() > 0){
            myViewHolder.topic.setTypeface(myViewHolder.topic.getTypeface(), Typeface.BOLD);
            myViewHolder.message.setTypeface(myViewHolder.message.getTypeface(), Typeface.BOLD);
            myViewHolder.unread_msg_num.setVisibility(View.VISIBLE);
        }else{
            myViewHolder.topic.setTypeface(myViewHolder.topic.getTypeface(), Typeface.NORMAL);
            myViewHolder.message.setTypeface(myViewHolder.message.getTypeface(), Typeface.NORMAL);
            myViewHolder.unread_msg_num.setVisibility(View.GONE);
        }

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_cr_intent = new Intent(context, MainActivity.class);
                to_cr_intent.putExtra("MY_TOPIC",arrayList.get(i).getTopic());

                context.startActivity(to_cr_intent);

            }
        });

        myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(context,"這是長按事件",Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView topic,time, message, unread_msg_num;
        ImageView icon;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.crl_topic);
            time = itemView.findViewById(R.id.crl_time);
            message = itemView.findViewById(R.id.crl_message);
            icon = itemView.findViewById(R.id.crl_icon);
            unread_msg_num = itemView.findViewById(R.id.crl_unread_msg_num);
        }
    }


}
