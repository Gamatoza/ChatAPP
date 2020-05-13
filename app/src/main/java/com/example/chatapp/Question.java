package com.example.chatapp;

import java.util.Date;
import java.util.List;

///Класс форума или вопроса
///Предназначен для хранения и отображения информации о текущем вопросе, форуме, ответе и т.п.


public class Question {



    private String id;              //ID форума, push ключ          g

    //region Колонка пользователя
    ///Важность параметры сверху вниз
    private String userID;          // ID с Firebase                g
    //endregion

    private String title;           //Заголовок                     gs

    private Boolean isDecided;      //Проверка ответа               gs
    private String answerMessageID; //ID ответа                     gs

    private Message mainMessage;    //Вопрос                        gs
    private long stateTime;         //Время создания вопроса        gs

    {
        this.stateTime = new Date().getTime();
        isDecided = false; //????
    }

    //region Constructors

    public Question() { }


    public Question(String id, String userID) {
        this.id = id;
        this.userID = userID;
    }

    public Question(String id, String userID, String title, Message mainMessage) {
        this(id,userID);
        this.title = title;
        this.mainMessage = mainMessage;
        mainMessage.setMessageTime(stateTime);
    }

    public Question(Question parent) {
        this.id = parent.getId();
        this.userID = parent.getUserID();
        this.title = parent.getTitle();
        this.mainMessage = parent.getMainMessage();
    }
    //endregion

    //region Getters Setters

    public String getId() {
        return id;
    }

    public String getUserID() {
        return userID;
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

    public String getAnswer() {
        return answerMessageID;
    }

    public void setAnswer(String ID) {
        isDecided = true;
        this.answerMessageID = ID;
    }

    //endregion

    public Boolean isDecided(){
        return  isDecided;
    }

    public void removeAnswer(){
        isDecided = false;
        answerMessageID = null;
    }
}
