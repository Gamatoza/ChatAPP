package com.example.chatapp.source;

import java.util.Date;

///Класс форума или вопроса
///Предназначен для хранения и отображения информации о текущем вопросе, форуме, ответе и т.п.


public class Question implements Cloneable {



    private String id;              //ID форума, push ключ          g

    //region Колонка пользователя
    ///Важность параметры сверху вниз
    private String userID;          // ID с Firebase                g
    //endregion

    private String title;           //Заголовок                     gs

    private Boolean isDecided;      //Проверка ответа               gs
    private String answerMessageID; //ID ответа                     gs

    private Message mainMessage;    //Вопрос                        gs
    private Date stateTime;         //Время создания вопроса        gs

    {
        this.stateTime = new Date();
        isDecided = false;
    }

    //region Constructors
    public Question() { }

    public Question(String id, String userID) {
        this.id = id;
        this.userID = userID;
        isDecided = false;
    }

    public Question(String id, String userID, String title, Message mainMessage) {
        this(id,userID);
        this.title = title;
        this.mainMessage = mainMessage;
        mainMessage.setMessageTime(stateTime);
        isDecided = false;
    }


    public Question(Question parent) {
        this.id = parent.getId();
        this.userID = parent.getUserID();
        this.title = parent.getTitle();
        this.mainMessage = parent.getMainMessage();
        isDecided = false;
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

    public Date getStateTime() {
        return stateTime;
    }

    public void resetResponse(){isDecided = false; answerMessageID = null;}

    public String getAnswer() {
        return answerMessageID;
    }

    public void setAnswer(String ID) {
        isDecided = true;
        this.answerMessageID = ID;
    }

    //endregion

    @Override
    public Question clone() throws CloneNotSupportedException {
        return (Question) super.clone();
    }

    public Boolean isDecided(){
        return  isDecided;
    }
    public Boolean isDecided(Boolean set){
        return  isDecided;
    }

    public void removeAnswer(){
        isDecided = false;
        answerMessageID = null;
    }

    public QuestionInfo generateInfo() {
            String name;
            if(mainMessage.getUserDisplayName() != null && !mainMessage.getUserDisplayName().isEmpty())
                name = mainMessage.getUserDisplayName();
            else
                name = mainMessage.getUserEmail().split("@")[0];
            return new QuestionInfo(name,getUserID(),getId(),getTitle(),isDecided(),getAnswer(),getStateTime());
    }
}
