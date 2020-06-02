package com.example.mqtttest.recyclerChatRoom;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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
import com.example.mqtttest.database.DBHelper_CRL;
import com.example.mqtttest.database.DBHelper_ChatMessages;
import com.example.mqtttest.recyclerChatRoomList.CRListBean;
import com.example.mqtttest.recyclerPhoto.PhotoBean;

import java.util.ArrayList;

public class MQTTAdapter extends RecyclerView.Adapter<MQTTAdapter.MQTTHolder> {

    Context context;
    ArrayList<MQTTBean> arrayList;
    String myClientId;

    ArrayList<PhotoBean> photoArrayList = new ArrayList<>();
    private DBHelper_ChatMessages dbHelper_chatMessages = null;
    private DBHelper_CRL dbHelper_crl = null;
    private String topic;
    private int intOrGrp;

    public MQTTAdapter(Context context, ArrayList<MQTTBean> arrayList, String topic, int intOrGrp, String myClientId) {
        this.context = context;
        this.arrayList = arrayList;
        this.topic = topic;
        this.intOrGrp = intOrGrp;
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
                mqttHolder.messageImg.setOnClickListener(new View.OnClickListener() { //點擊圖片，進入PhotoActivity
                    @Override
                    public void onClick(View v) {
                        Intent photoPage = new Intent(context, PhotoActivity.class);

                        photoPage.putExtra("TOPIC",topic);
                        photoPage.putExtra("IND_OR_GRP", intOrGrp);
                        photoPage.putExtra("TIME", arrayList.get(i).getTime());
                        context.startActivity(photoPage);
                    }
                });
                break;
        }

        //設定android小人的顏色
        int colorSeed = generateClientIDSeed(arrayList.get(i).clientID);
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

        mqttHolder.imgOtherUser.setOnClickListener(new View.OnClickListener() {//目前為一鍵私訊功能
            @Override
            public void onClick(View v) {
                if(intOrGrp==1){//在群組裡才有新增私訊聊天室的必要
                    addChatroomAlertdialog(arrayList.get(i).clientID);
                }
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

    private int generateClientIDSeed(String clientID){ //藉由clientID的ascii值，產生一數(用以改變android小人圖片的顏色)
        char[] chars = clientID.toCharArray();
        int seed = 0;
        for (int i = 0; i < chars.length; i++) {
            seed = seed + (int) chars[i];
        }
        return seed;
    }

    private void addChatroomAlertdialog(String communicator){ //跳Alertdialog詢問使用者是否要與該communicator建立私訊聊天
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("建立");
        dialog.setMessage("您想與"+communicator+"建立聊天室?");
        dialog.setPositiveButton("是",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                addChatroom(communicator);
            }

        });
        dialog.setNeutralButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //不做任何動作
            }

        });
        dialog.show();
    }

    private void addChatroom(String chatroomName){//與該communicator建立私訊聊天
        try {
            //以下建立SQLite
            dbHelper_chatMessages = new DBHelper_ChatMessages(context, null, 0, chatroomName, null, 1);
            dbHelper_chatMessages.createTable(chatroomName);
            dbHelper_chatMessages.close();
            dbHelper_chatMessages = null;
            dbHelper_crl = new DBHelper_CRL(context, null,null, 0);
            dbHelper_crl.addRec(new CRListBean(chatroomName, "null", "null", 3, 0, 0));
            Toast.makeText(context, "與"+chatroomName+"的聊天室建立成功", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
