package com.example.chatapp.test;

public class Storage {


    String ID;
    String Name;
    String Type;
    long DOA; //Date of arrival
    int ShelfLife;
    int Quantity;

    public Storage(){

    }

    public Storage(String ID, String name, String type, long DOA, int shelfLife, int quantity) {
        this.ID = ID;
        Name = name;
        Type = type;
        this.DOA = DOA;
        ShelfLife = shelfLife;
        Quantity = quantity;
    }




    //region GSetters

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public long getDOA() {
        return DOA;
    }

    public void setDOA(long DOA) {
        this.DOA = DOA;
    }

    public int getShelfLife() {
        return ShelfLife;
    }

    public void setShelfLife(int shelfLife) {
        ShelfLife = shelfLife;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    //endregion
}
