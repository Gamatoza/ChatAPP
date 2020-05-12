package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ForumActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private FirebaseListAdapter<Question> adapter;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        mAuth = FirebaseAuth.getInstance();
        listView = (ListView)findViewById(R.id.list_of_questions);
        myRef = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        ListView listOfMessages = (ListView)findViewById(R.id.list_of_questions);
        adapter = new FirebaseListAdapter<Question>
                (this,Question.class,R.layout.list_questions,myRef.child("Forums")) {
            @Override
            protected void populateView(@NonNull View v, @NonNull final Question model, int position) {
                TextView text = (TextView)v.findViewById(R.id.forum_question);
                ImageView imageView = (ImageView)v.findViewById(R.id.imageViewGotAnswer);
                text.setText(model.getTitle());
                RelativeLayout forum = (RelativeLayout)v.findViewById(R.id.dsForum);
                forum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ForumActivity.this,ViewQuestionActivity.class);
                        intent.putExtra("forumRef",model.getId());
                        startActivity(intent);
                    }
                });
                if(model.isDecided()) imageView.setImageResource(R.drawable.star_on);
                else imageView.setImageResource(R.drawable.star_off);
            }
        };
        listOfMessages.setAdapter(adapter);
    }



}
