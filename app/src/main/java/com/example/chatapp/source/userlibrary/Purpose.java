package com.example.chatapp.source.userlibrary;

public enum Purpose {
    Null(""),
    History("History"),
    Created("Created"),
    Tracked("Tracked");
    String content;

    Purpose(String content){
        this.content = content;
    }

    public String get(){return content;}

}
