package com.example.mqtttest.mqtt;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mqtttest.MainActivity;
import com.example.mqtttest.R;
import com.example.mqtttest.database.DBHelper_CRL;
import com.example.mqtttest.database.DBHelper_ChatMessages;
import com.example.mqtttest.recyclerChatRoom.MQTTAdapter;
import com.example.mqtttest.recyclerChatRoom.MQTTBean;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;

public class MqttHelper {
    private final String TAG = MqttHelper.class.getSimpleName();
    private RecyclerView recyclerView;

    private static MqttConnectOptions options;
    private static MqttClient client;
    private int version = MqttConnectOptions.MQTT_VERSION_3_1_1;
    private final String mqttHost ="tcp://140.116.226.96:1883";
    private String myTopic;
    private String myClientId;
    Handler handler = new Handler();

    Context context;
    ArrayList<MQTTBean> arrayList = new ArrayList<>();
    MQTTBean data;
    private LinearLayoutManager layoutManager;

    private DBHelper_ChatMessages dbHelper_chatMessages;
    private DBHelper_CRL dbHelper_CRL;

    public MqttHelper(final TextView textView)//default使用預設的變數值
    {
        try {
            client = new MqttClient(mqttHost, "NCKU1", new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setMqttVersion(version);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(TAG, "connectionLost: " + cause.getMessage());

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(TAG, "messageArrived: topic: "+ topic);
                    Log.d(TAG, "messageArrived: message: "+ new String(message.getPayload()));
                    textView.setText( new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "deliveryComplete: up date state --- " + token.isComplete());
                }
            });
            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public MqttHelper(Context context, String myTopic, String myClientId, RecyclerView recyclerView){ //給CR用
        this.context = context;
        this.recyclerView = recyclerView;
        this.myTopic = myTopic;
        this.myClientId = myClientId;
        connectMQTTServer();
    }

    public MqttHelper(Context context, String myClientId, RecyclerView recyclerView){ //給CRLActivity用的
        this.context = context;
        this.recyclerView = recyclerView;
        this.myTopic = null;
        this.myClientId = myClientId;
        connectMQTTServer();
    }

    private void connectMQTTServer() {
        try {
            client = new MqttClient(mqttHost, myClientId, new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setMqttVersion(version);
            options.setCleanSession(false);
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(20);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Snackbar.make(recyclerView,cause.getMessage(),Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    Log.d(TAG, "connectionLost: " + cause.getMessage());
                }
                @Override
                public void messageArrived(final String topic, MqttMessage message) throws Exception {
                    final String messagePayload = new String(message.getPayload());
                    Log.d(TAG, "get new data, topic:"+ topic+"\tmessage: "+ messagePayload);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            addData(topic,messagePayload);
                        }
                    });
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "update state: " + token.isComplete());
                }
            });
            client.connect(options);
            Snackbar.make(recyclerView,"Connect MQTT Server OK", Snackbar.LENGTH_SHORT).show();
        }

        catch (MqttException e) {
//            e.printStackTrace();
//            Log.d(TAG, "MqttHelper: "+e.getMessage());
//            Snackbar.make(recyclerView,e.getMessage(),Snackbar.LENGTH_INDEFINITE).setAction(R.string.click_to_reconnect, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    connectMQTTServer();
//                }
//            }).show();
            reconnectMQTTServer(e);
        }
    }

    public void reconnectMQTTServer(Exception e){
        e.printStackTrace();
        Log.d("","MqttHelper: "+e.getMessage());
        Snackbar.make(recyclerView,e.getMessage(),Snackbar.LENGTH_INDEFINITE).setAction(R.string.click_to_reconnect, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectMQTTServer();
            }
        }).show();
    }

    public void startSubscribe(String subTopic){
        try {
            int[] Qos = {1};
            String[] topic1 = {subTopic};
            client.subscribe(topic1, Qos);
            Snackbar.make(recyclerView,"Start subscribe to " + subTopic ,Snackbar.LENGTH_SHORT).show();
        } catch (MqttException e) {
//            e.printStackTrace();
//            Snackbar.make(recyclerView,e.getMessage(),Snackbar.LENGTH_INDEFINITE).setAction(R.string.click_to_reconnect, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    connectMQTTServer();
//                }
//            }).show();
            reconnectMQTTServer(e);
        }
    }

    public void startPublish(String pubTopic , String message, int type){
        try {
            MQTTBean data = null;
            switch (type) {
                case MainActivity.TEXT:
                    //send text
                    data = new MQTTBean(message, myClientId, MainActivity.TEXT);
                    break;
                case MainActivity.PHOTO:
                    //send image with base64
                    data = new MQTTBean(message,myClientId,MainActivity.PHOTO);
                    break;
            }
            MqttMessage mqttMessage = new MqttMessage(new Gson().toJson(data).getBytes());
            mqttMessage.setQos(1);

            client.publish(pubTopic, mqttMessage);
        } catch (MqttException e) {
//            e.printStackTrace();
//            Snackbar.make(recyclerView,"fail up ("+e.getMessage()+")",Snackbar.LENGTH_LONG).setAction(R.string.click_to_reconnect, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    connectMQTTServer();
//                }
//            }).show();
            reconnectMQTTServer(e);
        }
    }

    public void addData(String addDataTopic,String messagePayload){
        layoutManager = new LinearLayoutManager(context);
        this.recyclerView.setLayoutManager(layoutManager);

        dbHelper_chatMessages = new DBHelper_ChatMessages(context, null,addDataTopic,null,1);
        dbHelper_CRL = new DBHelper_CRL(context, null, null, 1);

        try { //如果符合格式(是由這個架構發出的訊息)
            data = new Gson().fromJson(messagePayload, MQTTBean.class);
        }
        catch (Exception e){//如果不符合格式
            Log.d("TAG", "addData: " + e.getMessage());
            Toast.makeText(context, "偵測到不合法的訊息!!", Toast.LENGTH_LONG).show();
            data = new MQTTBean("illegal message!", "", -1);
        }

        dbHelper_chatMessages.addRec(data, addDataTopic);
        dbHelper_CRL.refreshMessage(addDataTopic, data.getMessage());

        if (addDataTopic.equals(myTopic)){    //如果傳入訊息跟所在聊天室相同
            arrayList = dbHelper_chatMessages.getRecSet(myTopic);
            layoutManager.scrollToPosition(arrayList.size()-1);
            recyclerView.setAdapter(new MQTTAdapter(context,arrayList,myClientId));
        }else {                               //如果不相同就跳通知
            Toast toast = Toast.makeText(context, addDataTopic + "中有新訊息:" + data.getMessage(), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }
    }

    public void connectToCleanSession(){
        try {
            client = new MqttClient(mqttHost, myClientId, new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setMqttVersion(version);
            options.setCleanSession(true);
            client.connect(options);
            Log.d(TAG,"clean session OK,clientID:" + myClientId);
            client.disconnect();
        }
        catch (MqttException e) {
            reconnectMQTTServer(e);
        }
    }
}

