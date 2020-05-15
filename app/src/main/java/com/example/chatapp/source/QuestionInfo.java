package com.example.chatapp.source;

import com.example.chatapp.source.Question;

import java.util.Date;

public class QuestionInfo {
    //all g
    private String UserName;        //имя или мыло
    private String UserID;          //id создателя
    private String QuestionID;      //ID с Firebase
    private String Title;           //Заголовок
    private Boolean IsDecided;      //Проверка ответа
    private String AnswerMessageID; //ID ответа
    private long StateTime;         //Время создания вопроса

    public QuestionInfo(){}

    public QuestionInfo(String userName, String userID, String questionID, String title, Boolean isDecided, String answerMessageID, long stateTime){
        UserName = userName;
        UserID = userID;
        QuestionID = questionID;
        Title = title;
        IsDecided = isDecided;
        AnswerMessageID = answerMessageID;
        StateTime = stateTime;
    }

    //region Getters

    public String getUserName() {
        return UserName;
    }

    public String getUserID() {
        return UserID;
    }

    public String getQuestionID() {
        return QuestionID;
    }

    public String getTitle() {
        return Title;
    }

    public Boolean getDecided() {
        return IsDecided;
    }

    public String getAnswerMessageID() {
        return AnswerMessageID;
    }

    public long getStateTime() {
        return StateTime;
    }

    public void setCurrentTime() { StateTime = new Date().getTime(); }
    //endregion

}
