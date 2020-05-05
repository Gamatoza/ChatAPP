package com.example.chatapp;

import java.util.Date;
import java.util.List;

public class Question {



    private String id;
    private String ownerID;

    private String title;
    private List<Message> messages;



    private Message mainMessage;
    private long stateTime;

    {
        messages = null;
    }

    public Question(){}

    public Question(String id,String ownerID){
        this.id = id;
        this.ownerID = ownerID;
    }

    public Question(String id, String ownerID,String title, Message mainMessage){
        this.id = id;
        this.ownerID = ownerID;
        this.title = title;
        this.mainMessage = mainMessage;
        this.stateTime = new Date().getTime();
    }

    public String getId() {
        return id;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Message getMainMessage() {
        return mainMessage;
    }

    public void setMainMessage(Message mainMessage) {
        this.mainMessage = mainMessage;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message){
        messages.add(message);
    }

    public void addMessages(List<Message> messages) { this.messages.addAll(messages);}

    public long getStateTime() {
        return stateTime;
    }
}
