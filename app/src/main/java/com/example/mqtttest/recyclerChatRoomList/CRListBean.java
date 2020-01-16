package com.example.mqtttest.recyclerChatRoomList;

public class CRListBean {
    private String topic;
    private String time;
    private String message;
    private int img_id;
    private int unread_msg_num;

    public CRListBean(String topic, String time, String message, int img_id, int unread_msg_num){
        this.topic = topic;
        this.time = time;
        this.message = message;
        this.img_id = img_id;
        this.unread_msg_num = unread_msg_num;
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

    public int getUnread_msg_num(){
        return unread_msg_num;
    }

    public void setUnread_msg_num(int unread_msg_num){
        this.unread_msg_num = unread_msg_num;
    }

}
