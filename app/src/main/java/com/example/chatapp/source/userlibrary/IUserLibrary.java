package com.example.chatapp.source.userlibrary;

import com.example.chatapp.source.userlibrary.LocalUserLibrary;

public interface IUserLibrary {

    LocalUserLibrary getUserLibrary();
    void setUserLibrary(LocalUserLibrary library) throws CloneNotSupportedException;

}
