package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;

public class CreateQuestionActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    EditText nameEditText,contentEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        nameEditText = (EditText)findViewById(R.id.question_name);
        contentEditText = (EditText)findViewById(R.id.question_content);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Questions");

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.submit_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Title = nameEditText.getText().toString();
                String Content = contentEditText.getText().toString();
                if(Title.isEmpty()){
                    nameEditText.setError("Email is required");
                    nameEditText.requestFocus();
                    return;
                }
                if(Content.isEmpty()){
                    contentEditText.setError("Email is required");
                    contentEditText.requestFocus();
                    return;
                }
                Question question = new Question("0", mAuth.getUid());
                question.setTitle(Title);
                question.setMainMessage(new Message(
                        FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        Content)
                );

                myRef.setValue(question);

                Intent intent = new Intent(CreateQuestionActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

    }
}
