package com.example.mqtttest.recyclerChatRoom;


public class MQTTBean {
    String message;
    String clientID;
    String time;
    int type;

    public MQTTBean() {
    }

    public MQTTBean(String message, String clientID, String time, int type) {
        this.message = message;
        this.clientID = clientID;
        this.time = time;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String id) {
        this.clientID = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String msgPubTime) {
        this.time = msgPubTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
