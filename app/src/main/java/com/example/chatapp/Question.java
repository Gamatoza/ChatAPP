package com.example.chatapp;

import java.util.Date;
import java.util.List;

///Класс форума или вопроса
///Предназначен для хранения и отображения информации о текущем вопросе, форуме, ответе и т.п.


public class Question {


    private String id;                  //ID форума, сюда передается ключ push
    private String ownerID;             //ID владельца

    private String title;               //Название, отображается в списке

    private Boolean isDecided;          //Есть ли у этого вопроса ответ
    private String answerMessageID;     //ID ответа

    private Message mainMessage;        //Главное сообщение, являющееся вопросом
    private long stateTime;             //Время создания вопроса

    {
        this.stateTime = new Date().getTime();
        isDecided = false; //????
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


    public Boolean isDecided(){
        return  isDecided;
    }

    public String getAnswer() {
        return answerMessageID;
    }

    public void setAnswer(String ID) {
        isDecided = true;
        this.answerMessageID = ID;
    }

    public void removeAnswer(){
        isDecided = false;
        answerMessageID = null;
    }
}
