package com.example.mqtttest.recyclerChatRoom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mqtttest.MainActivity;
import com.example.mqtttest.PhotoActivity;
import com.example.mqtttest.R;
import com.example.mqtttest.recyclerPhoto.PhotoBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MQTTAdapter extends RecyclerView.Adapter<MQTTAdapter.MQTTHolder> {

    Context context;
    ArrayList<MQTTBean> arrayList;
    String myClientId;

    ArrayList<PhotoBean> photoArrayList = new ArrayList<>();

    public MQTTAdapter(Context context, ArrayList<MQTTBean> arrayList, String myClientId) {
        this.context = context;
        this.arrayList = arrayList;
        this.myClientId = myClientId;
    }

    @NonNull
    @Override
    public MQTTHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_layout, viewGroup, false);
        return new MQTTHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MQTTHolder mqttHolder, int i) {
//        Log.d("TAG", "onBindViewHolder: "+i);
        switch (arrayList.get(i).type) { //訊息是圖片檔or文字
            case MainActivity.TEXT:
                mqttHolder.txMessage.setText(arrayList.get(i).getMessage());
                mqttHolder.messageImg.setVisibility(View.GONE);
                break;
            case MainActivity.PHOTO:
                mqttHolder.txMessage.setVisibility(View.GONE);
                mqttHolder.messageImg.setVisibility(View.VISIBLE);
                byte[] decodeByte = Base64.decode(arrayList.get(i).getMessage().getBytes(),Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodeByte,0,decodeByte.length);
                mqttHolder.messageImg.setImageBitmap(bitmap);
                mqttHolder.messageImg.setOnClickListener(showImage);

                int photoArrayListItemNum = photoArrayList.size();
                mqttHolder.messageImg.setTag(photoArrayListItemNum);
                Log.d("TAG", "onBindViewHolderPHOTO: "+photoArrayListItemNum);
                photoArrayList.add(new PhotoBean(decodeByte,arrayList.get(i).getClientID(), photoArrayListItemNum));

                break;
        }

        //設定小人的顏色
        int colorSeed = setImgColor(arrayList.get(i).clientID);
        mqttHolder.imgOtherUser.setColorFilter(Color.argb(255,(colorSeed%128*2),(colorSeed%51*5),(colorSeed%256)));
        mqttHolder.imgUser.setColorFilter(Color.argb(255,(colorSeed%128*2),(colorSeed%51*5),(colorSeed%256)));

        //顯示傳送時間
        mqttHolder.msgPubTime.setText(arrayList.get(i).getTime());

//        mqttHolder.txMessage.setOnClickListener(new View.OnClickListener() {//開啟/收合該則訊息的資訊
//            @Override
//            public void onClick(View v) {
//                if(mqttHolder.msgInfoLayout.getVisibility() == View.GONE){
//                    mqttHolder.msgInfoLayout.setVisibility(View.VISIBLE);
//                    mqttHolder.txMessage.setBackground(context.getResources().getDrawable(R.drawable.edit_text_bg_gray));
//                }
//                else{
//                    mqttHolder.msgInfoLayout.setVisibility(View.GONE);
//                    mqttHolder.txMessage.setBackground(context.getResources().getDrawable(R.drawable.edit_text_bg));
//                }
//            }
//        });

        mqttHolder.imgOtherUser.setOnClickListener(new View.OnClickListener() {//顯示該訊息傳送者的資訊(尚未開發)
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "預計要顯示這個使用者的資訊，不過功能還沒實作>.<|||", Toast.LENGTH_SHORT).show();
            }
        });

        if (arrayList.get(i).getClientID().equals(myClientId)) { //如果這則訊息就是使用者傳送的
            mqttHolder.itemLayout.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            mqttHolder.imgOtherUser.setVisibility(View.GONE);
            mqttHolder.imgUser.setVisibility(View.VISIBLE);
            mqttHolder.msgUserID.setText(arrayList.get(i).clientID);
            mqttHolder.msgOtherUserID.setVisibility(View.INVISIBLE);
            mqttHolder.msgUserID.setVisibility(View.VISIBLE);
        } else {                                                 //如果這則訊息是由其他人傳送
            mqttHolder.itemLayout.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            mqttHolder.imgOtherUser.setVisibility(View.VISIBLE);
            mqttHolder.imgUser.setVisibility(View.GONE);
            mqttHolder.msgOtherUserID.setText(arrayList.get(i).clientID);
            mqttHolder.msgOtherUserID.setVisibility(View.VISIBLE);
            mqttHolder.msgUserID.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MQTTHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout, msgInfoLayout;
        TextView txMessage, msgOtherUserID, msgUserID, msgPubTime;
        ImageView imgUser, imgOtherUser, messageImg;

        public MQTTHolder(@NonNull View itemView) {
            super(itemView);
            txMessage = itemView.findViewById(R.id.chatroomMessage);
            msgOtherUserID = itemView.findViewById(R.id.msgOtherUserID);
            msgUserID = itemView.findViewById(R.id.msgUserID);
            msgPubTime = itemView.findViewById(R.id.msgPublishTime);
            messageImg = itemView.findViewById(R.id.chatroomImg);
            imgUser = itemView.findViewById(R.id.chatroomUser);
            imgOtherUser = itemView.findViewById(R.id.chatroomOtherUser);
            itemLayout = itemView.findViewById(R.id.chatroomLayout);
            msgInfoLayout = itemView.findViewById(R.id.msgInfoLayout);
        }
    }

    View.OnClickListener showImage = new View.OnClickListener() { //點擊圖片，intent跳進"顯示圖片的activity"
        @Override
        public void onClick(View v) {
            Intent photoPage = new Intent(context, PhotoActivity.class);
            photoPage.putExtra("PHOTO_ARRAY_LIST", photoArrayList); //以此方法傳圖片的arraylist，可能導致太大而無法intent跳轉!!!

            /////////之後要在這裡做"生成該圖片的索引index值"
            int item = (int) v.getTag();
            Log.d("TAG", "onClick: "+ item);
            photoPage.putExtra("PHOTO_SCREEN_ITEM_NUM", item);
            context.startActivity(photoPage);

        }
    };

    private int setImgColor(String clientID){ //藉由產生一數，改變android小人圖片的顏色
        char[] chars = clientID.toCharArray();
        int seed = 0;
        for (int i = 0; i < chars.length; i++) {
            seed = seed + (int) chars[i];
        }
        return seed;
    }

}
