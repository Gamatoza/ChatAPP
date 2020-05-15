package com.example.chatapp.source.userlibrary;

import com.example.chatapp.source.userlibrary.old.IUserLibrary;
import com.firebase.ui.auth.data.model.User;

public class UserLibrary implements IUserLibrary, Cloneable {

    private String userID;

    private Filler History;
    private Filler Tracked;
    private Filler Created;

    public Filler getHistory() {
            return History;
    }

    public Filler getTracked() {
            return Tracked;
    }

    public Filler getCreated() {
            return Created;
    }

    public void setHistory(Filler history) {
        History = history;
    }

    public void setTracked(Filler tracked) {
        Tracked = tracked;
    }

    public void setCreated(Filler created) {
        Created = created;
    }

    public UserLibrary(String userID){
        this.userID = userID;
        History = new Filler(Purpose.History);
        Tracked = new Filler(Purpose.Tracked);
        Created = new Filler(Purpose.Created);
    }

    public UserLibrary(Filler History, Filler Tracked, Filler Created){
        this.History = History;
        this.Tracked = Tracked;
        this.Created = Created;
    }

    //region Override UserLibrary Getter Setter

    @Override
    public void setOwner(String ID) {
        userID = ID;
    }

    @Override
    public String getOwner() {
        return userID;
    }

    @Override
    public UserLibrary getUserLibrary() {
        return this;
    }

    @Override
    public void setUserLibrary(UserLibrary library){

    }

    //endregion
}
