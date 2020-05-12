package com.example.chatapp;

import java.util.Date;

public class Message {
    private String userName;
    private String text;
    private long messageTime;

    Message(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public Message(String userName, String text){
        this.userName = userName;
        this.text = text;

        this.messageTime = new Date().getTime();

    }
}