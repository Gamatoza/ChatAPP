package com.example.chatapp.source.userlibrary;

import androidx.annotation.NonNull;

import com.example.chatapp.source.Question;
import com.example.chatapp.source.QuestionInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FirebaseUserLibraryLoader{

    private UserLibrary uL;
    private FirebaseDatabase db;
    private DatabaseReference ref;

    public FirebaseUserLibraryLoader(UserLibrary uploader){
        uL = uploader;
        db = FirebaseDatabase.getInstance();
        ref = db.getReference().child("UsersLibrary").child(uL.getOwner());
        localDownload();
    }

    public void addContent(Purpose p, QuestionInfo qi){
        Filler buf;
        switch (p){
            case History:
                buf = uL.getHistory();
                break;
            case Tracked:
                buf = uL.getTracked();
                break;
            case Created:
                buf = uL.getCreated();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + p.get());
        }

        String key = ref.child(p.get()).push().getKey();
        buf.addContent(key,qi);
        ref.child(p.get()).setValue(buf.getContent());
    }

    public void removeContent(Purpose p, String key){
        Filler buf;
        switch (p){
            case History:
                buf = uL.getHistory();
                break;
            case Tracked:
                buf = uL.getTracked();
                break;
            case Created:
                buf = uL.getCreated();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + p.get());
        }
        buf.removeContent(key);
        ref.child(p.get()).setValue(buf);
    }

    public void clearContent(Purpose p){
        Filler buf;
        switch (p){
            case History:
                buf = uL.getHistory();
                break;
            case Tracked:
                buf = uL.getTracked();
                break;
            case Created:
                buf = uL.getCreated();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + p.get());
        }
        buf.clearContent();
        ref.child(p.get()).setValue(buf);
    }

    public void updateAll(){
        Filler buf = new Filler();

        buf.setContent(uL.getHistory().getContent());
        ref.child("History").setValue(buf);

        buf.setContent(uL.getCreated().getContent());
        ref.child("Created").setValue(buf);

        buf.setContent(uL.getTracked().getContent());
        ref.child("Tracked").setValue(buf);

    }

    public void localDownload(){

        ref.child("History").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    HashMap buf = (HashMap<String, Question>) dataSnapshot.getValue();
                    uL.getHistory().setContent(buf);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("Created").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    HashMap buf = (HashMap<String, Question>) dataSnapshot.getValue();
                    uL.getCreated().setContent(buf);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ref.child("Tracked").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    HashMap buf = (HashMap<String, Question>) dataSnapshot.getValue();
                    uL.getTracked().setContent(buf);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public static UserLibrary download(String userID){
        final UserLibrary uL = new UserLibrary(userID);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("History").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    Filler buf = dataSnapshot.getValue(Filler.class);
                    uL.getHistory().setContent(buf.getContent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("Created").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    Filler buf = dataSnapshot.getValue(Filler.class);
                    uL.getCreated().setContent(buf.getContent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ref.child("Tracked").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    Filler buf = dataSnapshot.getValue(Filler.class);
                    uL.getTracked().setContent(buf.getContent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return uL;
    }
}
