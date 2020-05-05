package com.example.mqtttest.recyclerChatRoomList;

public class CRListBean {
    private String topic, time, message;
    private int img_id, unread_msg_num, individual_or_group;

    public CRListBean(String topic, String time, String message, int img_id, int unread_msg_num, int individual_or_group){
        this.topic = topic;
        this.time = time;
        this.message = message;
        this.img_id = img_id;
        this.unread_msg_num = unread_msg_num;
        this.individual_or_group = individual_or_group;
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

    public int getIndividual_or_group() {
        return individual_or_group;
    }

    public void setIndividual_or_group(int individual_or_group) {
        this.individual_or_group = individual_or_group;
    }
}
