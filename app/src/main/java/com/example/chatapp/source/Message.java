package com.example.chatapp.source;

import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class Message implements UserInformation, Cloneable {

    private String id;              //идентификатор сообщения       g
    private String text;            //внутренний текст              gs
    private long messageTime;       //время отправки сообщения      gs

    //region Колонка пользователя
    ///Важность параметры сверху вниз
    private String userID;          // ID с Firebase                g
    private String userEmail;       // Е-мейл                       g
    private String userDisplayName; // Никнейм                      g
    private String userAvatarURL;   // URL на аватарку              g
    //endregion

    {
        id = "0";
        this.messageTime = new Date().getTime();
    }

    //region Constructors

    Message(){}

    public Message(Message parent){
        id = parent.getId();
        text = parent.getText();
        messageTime = parent.getMessageTime();

        userID = parent.getUserID();
        userEmail = parent.getUserEmail();
        userDisplayName = parent.getUserDisplayName();
        userAvatarURL = parent.getUserAvatarURL();
    }

    public Message(String text,FirebaseUser user){
        this.id = null;
        this.text = text;
        this.userID = user.getUid();
        this.userEmail = user.getEmail();
        if(user.getDisplayName() != null)
        this.userDisplayName = user.getDisplayName();
        if(user.getPhotoUrl() != null)
        this.userAvatarURL = user.getPhotoUrl().toString();
    }//специально для Question

    public Message(String id,String text){
        this.id = id;
        this.text = text;
    }

    public Message(String id, String text, FirebaseUser user){
        this(text,user);
        this.id = id;
    }

    public Message(String id,String text,String userID){
        this(id,text);
        this.userID = userID;
    }

    public Message(String id,String text,String userID,String userEmail,String userDisplayName){
        this(id,text,userID);
        this.userEmail = userEmail;
        this.userDisplayName = userDisplayName;
    }

    public Message(String id,String text,String userID,String userEmail,String userDisplayName,String userAvatarURL){
        this(id,text,userID,userEmail,userDisplayName);
        this.userAvatarURL = userAvatarURL;
    }


    //endregion

    //region Getters Setters

    public String getId() {
        return id;
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

    //endregion

    //region Override user information
    @Override
    public String getUserDisplayName() {
        return userDisplayName;
    }

    @Override
    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    @Override
    public String getUserEmail() {
        return userEmail;
    }

    @Override
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String getUserID() {
        return userID;
    }

    @Override
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String getUserAvatarURL() {
        return userAvatarURL;
    }

    @Override
    public void setUserAvatarURL(String userAvatarURL) {
        this.userAvatarURL = userAvatarURL;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    //endregion

    public void setCurrentTime(){
        this.messageTime = new Date().getTime();
    }

}