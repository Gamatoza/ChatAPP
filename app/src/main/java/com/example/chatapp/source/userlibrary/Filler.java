package com.example.chatapp.source.userlibrary;

import androidx.annotation.NonNull;

import com.example.chatapp.source.QuestionInfo;

import java.util.HashMap;
import java.util.Map;

public class Filler implements Cloneable {                           //Наполнитель

    private Purpose purpose;

    @NonNull
    @Override
    public Filler clone() throws CloneNotSupportedException {
        return (Filler) super.clone();
    }

    //SearchFilter filter;                      //Фильтр, на стадии разработки
    private Map<String, QuestionInfo> content;   //Ключ - pushID который закидывается в закладку

    public Filler(){
        purpose = Purpose.Null;
        content = new HashMap<>();
    }

    public Filler(Purpose p){
        purpose = p;
        content = new HashMap<>();
    }

    public HashMap<String, QuestionInfo> getContent() { return (HashMap<String, QuestionInfo>) content; }

    public void setContent(HashMap<String,QuestionInfo> map) {
        this.content = map;
    }

    public void addContent(String key, QuestionInfo qi){ content.put(key,qi); }

    public void removeContent(String key){
        content.remove(key);
    }

    public void clearContent(){
        content.clear();
    }

    //Добавить фильтры
    public Map<String,QuestionInfo> searchContent(String line){

        Map<String,QuestionInfo> buf = new HashMap<>();
        for(Map.Entry<String,QuestionInfo> entry : content.entrySet() ){
            if(entry.getValue().getTitle().trim().toLowerCase().contains(line.toLowerCase().trim())) {
                buf.put(entry.getKey(),entry.getValue());
            }
            if(entry.getValue().getUserName().trim().toLowerCase().contains(line.toLowerCase().trim())) {
                buf.put(entry.getKey(),entry.getValue());
            }
        }
        return buf;
    }


    public Purpose getPurpose() {
        return purpose;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

}
