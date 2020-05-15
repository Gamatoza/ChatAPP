package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatapp.R;
import com.example.chatapp.activities.userlibrary.CreatedActivity;
import com.example.chatapp.source.userlibrary.UserLibrary;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){

            startActivity(new Intent(this,LoginActivity.class));
        }

        findViewById(R.id.buttonChangePhoto).setOnClickListener(this);
        findViewById(R.id.buttonViewForum).setOnClickListener(this);
        findViewById(R.id.buttonCreateQuestion).setOnClickListener(this);
        findViewById(R.id.buttonQuit).setOnClickListener(this);
        findViewById(R.id.buttonShowProfile).setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonChangePhoto:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.buttonViewForum:

                startActivity(new Intent(MainActivity.this, ForumActivity.class));
                break;
            case R.id.buttonCreateQuestion:
                startActivity(new Intent(MainActivity.this, CreateQuestionActivity.class));
                break;
            case R.id.buttonQuit:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this,LoginActivity.class));
                break;
            case R.id.buttonShowProfile:
                startActivity(new Intent(MainActivity.this, CreatedActivity.class));
                break;
        }
    }
}
