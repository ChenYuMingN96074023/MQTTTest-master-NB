package com.example.mqtttest.recyclerMQTT;


public class MQTTBean {
    String message;
    String id;
    int type;

    public MQTTBean() {
    }

    public MQTTBean(String message, String id, int type) {
        this.message = message;
        this.id = id;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
