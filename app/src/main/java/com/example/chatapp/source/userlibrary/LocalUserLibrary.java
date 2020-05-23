package com.example.chatapp.source.userlibrary;

import com.example.chatapp.source.userlibrary.old.Filler;

import java.util.ArrayList;
import java.util.List;

public class LocalUserLibrary implements IUserLibrary, Cloneable {

    private String userID;

    private List<String> History;
    private List<String> Tracked;
    private List<String> Created;

    //region Getter Setter

    public void setOwner(String ID) {
        userID = ID;
    }

    public String getOwner(){
        return userID;
    }

    public List<String> getHistory() {
        return History;
    }

    public void setHistory(List<String> history) {
        History = history;
    }

    public List<String> getTracked() {
        return Tracked;
    }

    public void setTracked(List<String> tracked) {
        Tracked = tracked;
    }

    public List<String> getCreated() {
        return Created;
    }

    public void setCreated(List<String> created) {
        Created = created;
    }

    public LocalUserLibrary(String userID){
        this.userID = userID;
        History = new ArrayList<>();
        Tracked = new ArrayList<>();
        Created = new ArrayList<>();
    }

    public LocalUserLibrary(List History, List Tracked, List Created){
        this.History = History;
        this.Tracked = Tracked;
        this.Created = Created;
    }

    //endregion

    //region Override UserLibrary Getter Setter



    @Override
    public LocalUserLibrary getUserLibrary() {
        return this;
    }

    @Override
    public void setUserLibrary(LocalUserLibrary library){

    }

    //endregion
}
