package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.chatapp.R;
import com.example.chatapp.source.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeMessageActivity extends AppCompatActivity {

    public String FORUM_KEY;
    public String MESSAGE_KEY;

    private Message currentMessage;

    private static String LOG_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_message);

        Intent intent = getIntent();
        FORUM_KEY = intent.getStringExtra("forum_key"); //Передаем ID форума
        MESSAGE_KEY = intent.getStringExtra("message_key"); //Передаем ID сообщения

        final EditText text = findViewById(R.id.editTextEditableText);

        FirebaseDatabase.getInstance().getReference()
                .child("Messages")
                .child(FORUM_KEY)
                .child(MESSAGE_KEY).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentMessage = dataSnapshot.getValue(Message.class);
                        currentMessage.setCurrentTime();
                        text.setText(currentMessage.getText());
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
                currentMessage.setText(text.getText().toString().trim());
                //Добавить проверку на изменения
                FirebaseDatabase.getInstance().getReference()
                        .child("Messages")
                        .child(FORUM_KEY).child(MESSAGE_KEY)
                        .setValue(currentMessage);
                finish();
            }
        });
    }
}
