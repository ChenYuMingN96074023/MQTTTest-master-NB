package com.example.mqtttest.mqtt;

import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mqtttest.MainActivity;
import com.example.mqtttest.R;
import com.example.mqtttest.recyclerMQTT.MQTTBean;
import com.example.mqtttest.recyclerMQTT.MQTTFunction;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttHelper {
    private final String TAG = MqttHelper.class.getSimpleName();
    private View errorView;
    MQTTFunction mqttFunction;

    private static MqttConnectOptions options;
    private static MqttClient client;
    private int version = MqttConnectOptions.MQTT_VERSION_3_1_1;

    //private String mqttHost ="tcp://35.229.37.146:1883";
    private String mqttHost ="tcp://140.116.226.96:1883";
    private String myTopic = "NCKU_TOPIC";
    private String myClientId = "NCKU1";

    Handler handler = new Handler();

    public MqttHelper(final TextView textView){
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
    } //default使用預設的變數值

    public MqttHelper(MQTTFunction mqttFunction, String myTopic, String myClientId, View errorView){
        this.errorView = errorView;
        this.myTopic = myTopic;
        this.myClientId = myClientId;
        this.errorView = errorView;
        this.mqttFunction = mqttFunction;
        // connect MQTT Server
        connectMQTTServer();
    }

    public MqttHelper(String mqttHost, String myTopic, MQTTFunction mqttFunction, String myClientId, View errorView){
        this.mqttHost = "tcp://"+mqttHost;
        this.myTopic = myTopic;
        this.myClientId = myClientId;
        this.errorView = errorView;
        this.mqttFunction = mqttFunction;
        // connect MQTT Server
        connectMQTTServer();
    }

    private void connectMQTTServer() {
        try {
            client = new MqttClient(mqttHost, myClientId, new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setMqttVersion(version);
            options.setCleanSession(false);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Snackbar.make(errorView,cause.getMessage(),Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    Log.d(TAG, "connectionLost: " + cause.getMessage());
                }
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    final String s = new String(message.getPayload());
                    Log.d(TAG, "get new data, topic:"+ topic+"\tmessage: "+ s);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mqttFunction.addData(s);
                        }
                    });
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "update state: " + token.isComplete());
                }
            });
            client.connect(options);
            Snackbar.make(errorView,"Connect MQTT Server OK", Snackbar.LENGTH_SHORT).show();
            startSubscribe();
        }

        catch (MqttException e) {
            e.printStackTrace();
            Log.d(TAG, "MqttHelper: "+e.getMessage());
            Snackbar.make(errorView,e.getMessage(),Snackbar.LENGTH_INDEFINITE).setAction(R.string.click_to_reconnect, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectMQTTServer();
                }
            }).show();
        }
    }

    public void startSubscribe(){
        //abc
        try {
            //int[] Qos = {0};
            int[] Qos = {1};
            String[] topic1 = {myTopic};
            client.subscribe(topic1, Qos);
            Snackbar.make(errorView,"Start subscribe to " + myTopic ,Snackbar.LENGTH_SHORT).show();
        } catch (MqttException e) {
            e.printStackTrace();
            Snackbar.make(errorView,e.getMessage(),Snackbar.LENGTH_INDEFINITE).setAction(R.string.click_to_reconnect, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectMQTTServer();
                }
            }).show();
        }
    }

    public void startPublisher(String message, int type){
        try {
            MQTTBean data = null;
            switch (type) {
                case MainActivity.TEXT:
                    //send textq
                    data = new MQTTBean(message, myClientId, MainActivity.TEXT);
                    break;
                case MainActivity.PHOTO:
                    //send image with base64
                    data = new MQTTBean(message,myClientId,MainActivity.PHOTO);
                    break;
            }
            MqttMessage mqttMessage = new MqttMessage(new Gson().toJson(data).getBytes());
            mqttMessage.setQos(1);
            //mqttMessage.setQos(0);

            client.publish(myTopic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
            Snackbar.make(errorView,"fail up ("+e.getMessage()+")",Snackbar.LENGTH_LONG).setAction(R.string.click_to_reconnect, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectMQTTServer();
                }
            }).show();
        }
    }
}

