package com.example.chatapp;

public class Forum {

    private int id;
    private String name;
    private Message[] messages;
    private String question;
    private long stateTime;

    public Forum(){}

    public Forum(Message[] messages, String question, long stateTime){
        this.messages = messages;
        this.question = question;
        this.stateTime = stateTime;
    }

    public Message[] getMessages() {
        return messages;
    }

    public void setMessages(Message[] messages) {
        this.messages = messages;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public long getStateTime() {
        return stateTime;
    }

    public void setStateTime(long stateTime) {
        this.stateTime = stateTime;
    }
}
