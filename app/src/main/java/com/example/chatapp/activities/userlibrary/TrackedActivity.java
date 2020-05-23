package com.example.chatapp.activities.userlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.activities.ViewQuestionActivity;
import com.example.chatapp.source.Question;
import com.example.chatapp.source.QuestionInfo;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrackedActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracked);

        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        //started
        bottomNavigationView.setSelectedItemId(R.id.tracked);

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

    private void updateUI(){

        ListView list = findViewById(R.id.listViewContent);

        user = mAuth.getCurrentUser();

        myRef = FirebaseDatabase.getInstance().getReference()
                .child("UsersLibrary")
                .child(user.getUid());

        FirebaseListAdapter<String> lightAdapter = new FirebaseListAdapter<String>(this, String.class, R.layout.list_questions, myRef.child("Tracked")) {
            @Override
            protected void populateView(final View v, final String model, int position) {
                final TextView text, owner;
                final ImageView imageView;
                final RelativeLayout forum;

                text = (TextView) v.findViewById(R.id.forum_question);
                owner = (TextView) v.findViewById(R.id.textViewOwnerID);
                imageView = (ImageView) v.findViewById(R.id.imageViewIsTracked);
                forum = (RelativeLayout) v.findViewById(R.id.dsForum);
                if(model.getClass() == String.class) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Forums")
                            .child(model).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Question qo = dataSnapshot.getValue(Question.class);
                                final QuestionInfo qi = qo.generateInfo();
                                text.setText(qi.getTitle());
                                String Author = "Author: ";
                                if (user.getUid().equals(qi.getUserID())) Author += "You";
                                else Author += qi.getUserName();
                                owner.setText(Author);

                                forum.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(TrackedActivity.this, ViewQuestionActivity.class);
                                        intent.putExtra("forumRef", qi.getQuestionID());
                                        startActivity(intent);
                                    }
                                });
                                if (qi.isDecided()) imageView.setImageResource(R.drawable.star_on);
                                else imageView.setImageResource(R.drawable.star_off);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w("TAG", "Failed to read value.");
                        }
                    });
                }
            }
        };

        list.setAdapter(lightAdapter);
    }

}