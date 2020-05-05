package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonChangePhoto).setOnClickListener(this);
        findViewById(R.id.buttonViewForum).setOnClickListener(this);
        findViewById(R.id.buttonCreateQuestion).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonChangePhoto:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.buttonViewForum:
                finish();
                startActivity(new Intent(MainActivity.this, ForumActivity.class));
                break;
            case R.id.buttonCreateQuestion:
                startActivity(new Intent(MainActivity.this, CreateQuestionActivity.class));
                break;
        }
    }
}
