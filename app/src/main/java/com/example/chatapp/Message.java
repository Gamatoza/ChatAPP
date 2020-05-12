package com.example.chatapp;

import java.util.Date;

public class Message {

    private String id;              //идентификатор сообщения
    private String userName;        //email пользователя
    private String text;            //внутренний текст
    private long messageTime;       //время отправки сообщения

    {
        id = "0";
        this.messageTime = new Date().getTime();
    }

    Message(){}

    public String getId() {
        return id;
    }

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
    }

    public Message(String userName, String text,String id){
        this.userName = userName;
        this.text = text;
        this.id = id;
    }
    public Message(String userName, String text,long messageTime){
        this.userName = userName;
        this.text = text;
        this.messageTime = messageTime;
    }
    public Message(String userName, String text,String id,long messageTime){
        this.userName = userName;
        this.text = text;
        this.id = id;
        this.messageTime = messageTime;
    }

}