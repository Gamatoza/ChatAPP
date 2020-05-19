package com.example.chatapp.activities.userlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.activities.ProfileActivity;
import com.example.chatapp.activities.ViewQuestionActivity;
import com.example.chatapp.source.Question;
import com.example.chatapp.source.QuestionInfo;
import com.example.chatapp.source.userlibrary.Purpose;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreatedActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private FirebaseListAdapter<QuestionInfo> adapter;
    private ArrayList<QuestionInfo> arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created);

        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        //started
        bottomNavigationView.setSelectedItemId(R.id.created);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.created:
                        return true;
                    case R.id.tracked:
                        finish();
                        startActivity(new Intent(getApplicationContext(),TrackedActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.history:
                        finish();
                        startActivity(new Intent(getApplicationContext(),HistoryActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                }

                return false;
            }
        });

        updateUI();
    }

    private FirebaseListAdapter<String> LightAdapter;

    private void updateUI(){

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.contentRecyclerView);

        FirebaseRecyclerAdapter<QuestionInfo,TaskViewHolder> adapter;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        user = mAuth.getCurrentUser();

        myRef = FirebaseDatabase.getInstance().getReference()
                .child("UsersLibrary")
                .child(user.getUid())
                .child("Created");
        //QuestionInfo
        /*
        adapter = new FirebaseRecyclerAdapter<QuestionInfo, TaskViewHolder>(
                QuestionInfo.class,
                R.layout.list_questions,
                TaskViewHolder.class,
                myRef)
        {
            @Override
            protected void populateViewHolder(TaskViewHolder taskViewHolder, final QuestionInfo model, int i) {
                String Author = "Author: " + model.getUserName();
                taskViewHolder.text.setText(model.getTitle());
                taskViewHolder.owner.setText(Author);

                taskViewHolder.forum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CreatedActivity.this, ViewQuestionActivity.class);
                        intent.putExtra("forumRef", model.getQuestionID());
                        startActivity(intent);
                    }
                });
                if (model.isDecided() != null) {
                    if (model.isDecided())
                        taskViewHolder.imageView.setImageResource(R.drawable.star_on);
                    else taskViewHolder.imageView.setImageResource(R.drawable.star_off);
                }
            }

        };*/

        //recyclerView.setAdapter(adapter);

    }

    private static class TaskViewHolder extends RecyclerView.ViewHolder{
        TextView text, owner;
        ImageView imageView;
        RelativeLayout forum;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.forum_question);
            owner = (TextView) itemView.findViewById(R.id.textViewOwnerID);
            imageView = (ImageView) itemView.findViewById(R.id.imageViewGotAnswer);
            forum = (RelativeLayout) itemView.findViewById(R.id.dsForum);
        }
    }

}
