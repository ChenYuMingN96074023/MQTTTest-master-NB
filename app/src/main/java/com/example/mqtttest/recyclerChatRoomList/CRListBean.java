package com.example.mqtttest.recyclerChatRoomList;

public class CRListBean {
    private String topic;
    private String time;
    private String message;
    private int img_id;

    public CRListBean(String topic, String time, String message, int img_id){
        this.topic = topic;
        this.time = time;
        this.message = message;
        this.img_id = img_id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getImg_id() {
        return img_id;
    }

    public void setImg_id(int img_id) {
        this.img_id = img_id;
    }

}
