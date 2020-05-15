package com.example.chatapp.source.userlibrary.old;

import com.example.chatapp.source.userlibrary.UserLibrary;

public interface IUserLibrary {

    void setOwner(String ID);
    String getOwner();
    UserLibrary getUserLibrary();
    void setUserLibrary(UserLibrary library) throws CloneNotSupportedException;

}
