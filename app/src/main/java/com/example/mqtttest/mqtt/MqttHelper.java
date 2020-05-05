package com.example.mqtttest.mqtt;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.mqtttest.MainActivity;
import com.example.mqtttest.R;
import com.example.mqtttest.database.DBHelper_CRL;
import com.example.mqtttest.database.DBHelper_ChatMessages;
import com.example.mqtttest.recyclerChatRoom.MQTTAdapter;
import com.example.mqtttest.recyclerChatRoom.MQTTBean;
import com.example.mqtttest.recyclerChatRoomList.CRListAdapter;
import com.example.mqtttest.recyclerChatRoomList.CRListBean;
import com.example.mqtttest.timeHelper.TimeGenerateHelper;
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
    private String userName = "normal-user";
    private String password = "psd";
    private String myTopic;
    private String myClientId;
    private int indOrGrp;
    Handler handler = new Handler();

    Context context;
    ArrayList<MQTTBean> arrayList = new ArrayList<>();
    ArrayList<CRListBean> arrayList_CRLItem = new ArrayList<>();
    MQTTBean data;
    private LinearLayoutManager layoutManager;

    private DBHelper_ChatMessages dbHelper_chatMessages;
    private DBHelper_CRL dbHelper_CRL;
    private TimeGenerateHelper timeGenerateHelper = new TimeGenerateHelper();

    public MqttHelper(Context context, String myTopic, int indOrGrp, String myClientId, RecyclerView recyclerView){ //給CR用
        this.context = context;
        this.recyclerView = recyclerView;
        this.myTopic = myTopic;
        this.indOrGrp = indOrGrp;
        this.myClientId = myClientId;
        connectMQTTServer();
    }

    public MqttHelper(Context context, String myClientId, RecyclerView recyclerView){ //給CRLActivity用的
        this.context = context;
        this.recyclerView = recyclerView;
        this.myTopic = null;
        this.indOrGrp = -1;
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
            options.setUserName(userName);
            options.setPassword(password.toCharArray());
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Snackbar.make(recyclerView,cause.getMessage(),Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    Log.d(TAG, "connectionLost: " + cause.getMessage());
                }
                @Override
                public void messageArrived(final String topic, MqttMessage message) throws Exception {
                    final String messagePayload = new String(message.getPayload());
                    Log.d(TAG, "傳入新訊息, topic:"+ topic+"\tmessage: "+ messagePayload);
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
            reconnectServerSnackbar(e);
        }
    }

    public void reconnectServerSnackbar(Exception e){
        e.printStackTrace();
        Log.d("","MqttHelper: "+e.getMessage());
        Snackbar.make(recyclerView,e.getMessage(),Snackbar.LENGTH_INDEFINITE).setAction(R.string.click_to_reconnect, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectMQTTServer();
            }
        }).show();
    }

    public void subscribeIndividual(){
        try {
            int[] Qos = {1};
            String[] topic = {"individual/"+myClientId+"/#"};
            client.subscribe(topic, Qos);
        } catch (MqttException e) {
            reconnectServerSnackbar(e);
        }
    }

    public void subscribeGroupTopic(String subTopic){
        try {
            int[] Qos = {1};
            String[] topic = {"group/" + subTopic};
            client.subscribe(topic, Qos);
            Snackbar.make(recyclerView,"Start subscribe to " + subTopic ,Snackbar.LENGTH_SHORT).show();
        } catch (MqttException e) {
            reconnectServerSnackbar(e);
        }
    }

    public void publish(String pubTopic , int pubIndOrGrp, String message, int type){
        try {
            MQTTBean data = null;
            switch (type) {
                case MainActivity.TEXT:
                    //send text
                    data = new MQTTBean(message, myClientId, timeGenerateHelper.getTime(), MainActivity.TEXT);
                    break;
                case MainActivity.PHOTO:
                    //send image with base64
                    data = new MQTTBean(message,myClientId, timeGenerateHelper.getTime(),MainActivity.PHOTO);
                    break;
            }
            MqttMessage mqttMessage = new MqttMessage(new Gson().toJson(data).getBytes());
            mqttMessage.setQos(1);

            if (pubIndOrGrp == 0) {
                client.publish("individual/"+pubTopic+"/"+myClientId, mqttMessage);//傳給對方
                client.publish("individual/"+myClientId+"/"+pubTopic, mqttMessage);//傳一份給自己
            }else if(pubIndOrGrp == 1){
                client.publish("group/" + pubTopic, mqttMessage);//傳到群組
            }else {
                Log.d(TAG,"程式錯誤，不該執行至此");
            }

        } catch (MqttException e) {
            reconnectServerSnackbar(e);
        }
    }

    private void addData(String msgArriveTopic, String messagePayload){
        layoutManager = new LinearLayoutManager(context);
        this.recyclerView.setLayoutManager(layoutManager);
        dbHelper_chatMessages = null;
        dbHelper_CRL = new DBHelper_CRL(context, null, null, 1);

        String addDataTopic = null;
        int addDataIOrG = -2;

        //以下辨識傳入的topic是 群組 or 個人私訊///////////////////////
        String topicLevel[] = msgArriveTopic.split("/"); //依照主題層級分隔符"/"，將收到的主題切割，存入String陣列topicLevel
        switch (topicLevel[0]){
            case "individual":
                if(topicLevel[1].equals(myClientId)){
                    addDataTopic = topicLevel[2];
                    addDataIOrG = 0;
                    dbHelper_chatMessages = new DBHelper_ChatMessages(context, null,0, addDataTopic,null,1);//開啟儲存group的SQLite
                    dbHelper_chatMessages.createTable(addDataTopic); //如果第一次接收到對方的訊息，需幫他建立table
                }else {
                    Log.d(TAG, "偵測到不合法信息!此訊息可能不是從app發送!");
                }
                break;
            case "group":
                addDataTopic = topicLevel[1];
                addDataIOrG = 1;
                dbHelper_chatMessages = new DBHelper_ChatMessages(context, null,1, addDataTopic,null,1);//開啟儲存group的SQLite
                break;

            default:
                Log.d(TAG, "偵測到不合法信息!此訊息可能不是從app發送!");

        }

        //以下判定是否符合json格式
        try { //如果符合格式(是由這個架構發出的訊息)
            data = new Gson().fromJson(messagePayload, MQTTBean.class);
        } catch (Exception e){//如果不符合格式
            Log.d("TAG", "addData: " + e.getMessage());
            Toast.makeText(context, "偵測到不合法的訊息!!此訊息可能不是從app發送!", Toast.LENGTH_LONG).show();
            data = new MQTTBean("illegal message!", "", "2000/01/01 00:00:00", -1);
        }

        dbHelper_chatMessages.addRec(data, addDataTopic);//新加這筆訊息至聊天室
        //以下更新聊天列表物件的信息
        int unreadMsgNum = dbHelper_CRL.getUNREAD_MSG_NUM(addDataTopic, addDataIOrG);
        dbHelper_CRL.deleteRec(addDataTopic,addDataIOrG);
        dbHelper_CRL.addRec(new CRListBean(addDataTopic, timeGenerateHelper.formatTheTime(data.getTime()), data.getMessage(), 1, unreadMsgNum+1, addDataIOrG));

        //以下判斷所在的activity為聊天室或列表，並做出對應的更新畫面(此為權宜之計)
        if(myTopic == null && indOrGrp == -1){                         //在CRL activity
            arrayList_CRLItem = dbHelper_CRL.getRecSet();
            recyclerView.setAdapter(new CRListAdapter(context, arrayList_CRLItem));
        }else if (addDataTopic.equals(myTopic) && addDataIOrG == indOrGrp){    //在聊天室中，傳入訊息topic跟所在聊天室topic相同
            arrayList = dbHelper_chatMessages.getRecSet(myTopic);
            layoutManager.scrollToPosition(arrayList.size()-1);
            recyclerView.setAdapter(new MQTTAdapter(context, arrayList, myClientId));
        }else {                                     //在聊天室中，傳入訊息topic跟所在聊天室topic不相同
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
            options.setUserName(userName);
            options.setPassword(password.toCharArray());
            client.connect(options);
            Log.d(TAG,"clean session OK,clientID:" + myClientId);
            client.disconnect();
        }
        catch (MqttException e) {
            reconnectServerSnackbar(e);
        }
    }
}

