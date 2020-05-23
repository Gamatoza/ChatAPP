package com.example.chatapp.source.userlibrary;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocalUserLibraryUpdater implements IUserLibrary {

    private LocalUserLibrary lul;

    void update(){
        String userID = lul.getOwner();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("UserLibrary").child(userID);

        lul = new LocalUserLibrary(userID);

        ref.child("Created").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    lul.getHistory().add(String.valueOf(dataSnapshot.child(ds.getKey()).getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("History").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    lul.getHistory().add(String.valueOf(dataSnapshot.child(ds.getKey()).getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("Tracked").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    lul.getHistory().add(String.valueOf(dataSnapshot.child(ds.getKey()).getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public LocalUserLibrary getUserLibrary() {
        return lul;
    }

    //Не копия потому что это апдейтер
    @Override
    public void setUserLibrary(LocalUserLibrary library) throws CloneNotSupportedException {
        lul = library;
    }
}
