package com.example.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.source.Message;
import com.example.chatapp.source.Question;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class ChangeQuestionActivity extends AppCompatActivity {

    private String FORUM_KEY;
    private String LIB_KEY;
    private String USER_KEY;


    private Question currentQuestion;

    private static String LOG_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_question);

        Intent intent = getIntent();
        FORUM_KEY = intent.getStringExtra("forum_key"); //Передаем ID форума


        final EditText title = findViewById(R.id.editTextTitle);
        final EditText message = findViewById(R.id.editTextMessage);

        FirebaseDatabase.getInstance().getReference()
                .child("Forums")
                .child(FORUM_KEY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentQuestion = dataSnapshot.getValue(Question.class);
                        currentQuestion.resetResponse();
                        currentQuestion.getMainMessage().setMessageTime(new Date().getTime());
                        title.setText(currentQuestion.getTitle());
                        message.setText(currentQuestion.getMainMessage().getText());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(LOG_TAG, "Failed to read value.");
                    }
                });

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuestion.setTitle(title.getText().toString().trim());
                currentQuestion.getMainMessage().setText(message.getText().toString().trim());


                FirebaseDatabase.getInstance().getReference()
                        .child("Forums")
                        .child(FORUM_KEY)
                        .setValue(currentQuestion);
                FirebaseDatabase.getInstance().getReference()
                        .child("Messages")
                        .child(FORUM_KEY)
                        .getRef().removeValue();
                finish();

            }
        });
    }
}
