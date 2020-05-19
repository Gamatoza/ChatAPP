package com.example.chatapp.activities.userlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.activities.ViewQuestionActivity;
import com.example.chatapp.source.QuestionInfo;
import com.example.chatapp.source.userlibrary.Purpose;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class HistoryActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private ListView listView;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private FirebaseListAdapter<QuestionInfo> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        //started
        bottomNavigationView.setSelectedItemId(R.id.history);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.created:
                        finish();
                        startActivity(new Intent(getApplicationContext(),CreatedActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.tracked:
                        finish();
                        startActivity(new Intent(getApplicationContext(),TrackedActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.history:
                        return true;

                }

                return false;
            }
        });
        addAddapter(Purpose.History);
    }

    void addAddapter(Purpose p){
        mAuth = FirebaseAuth.getInstance();
        listView = (ListView)findViewById(R.id.contentListView);
        myRef = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        adapter = new FirebaseListAdapter<QuestionInfo>
                (this, QuestionInfo.class, R.layout.list_questions, myRef.child("UsersLibrary").child(user.getUid()).child(p.get())) {
            @Override
            protected void populateView(View v, final QuestionInfo model, int position) {
                TextView text, owner;
                text = (TextView) v.findViewById(R.id.forum_question);
                owner = (TextView) v.findViewById(R.id.textViewOwnerID);
                ImageView imageView = (ImageView) v.findViewById(R.id.imageViewGotAnswer);
                text.setText(model.getTitle());
                String Author = "Author: " + model.getUserName();
                owner.setText(Author);
                RelativeLayout forum = (RelativeLayout) v.findViewById(R.id.dsForum);
                forum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HistoryActivity.this, ViewQuestionActivity.class);
                        intent.putExtra("forumRef", model.getQuestionID());
                        startActivity(intent);
                    }
                });
                if (model.isDecided()) imageView.setImageResource(R.drawable.star_on);
                else imageView.setImageResource(R.drawable.star_off);
            }
        };
        listView.setAdapter(adapter);

    }
}
