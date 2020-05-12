package com.example.chatapp;

import java.util.Date;
import java.util.List;

public class Question {


    private String id;
    private String ownerID;

    private String title;


    private Message mainMessage;
    private long stateTime;

    {
        this.stateTime = new Date().getTime();
    }

    public Question() { }


    public Question(String id, String ownerID) {
        this.id = id;
        this.ownerID = ownerID;
    }

    public Question(String id, String ownerID, String forumID, String title, Message mainMessage) {
        this.id = id;
        this.ownerID = ownerID;
        this.title = title;
        this.mainMessage = mainMessage;
    }

    public Question(Question parent) {
        this.id = parent.getId();
        this.ownerID = parent.getOwnerID();
        this.title = parent.getTitle();
        this.mainMessage = parent.getMainMessage();
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


    public long getStateTime() {
        return stateTime;
    }
}
