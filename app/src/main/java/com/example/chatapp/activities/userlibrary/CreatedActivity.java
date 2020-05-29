package com.example.chatapp.activities.userlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.activities.ViewQuestionActivity;
import com.example.chatapp.dialogs.QuestionsOptionsDialog;
import com.example.chatapp.source.Question;
import com.example.chatapp.source.QuestionInfo;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CreatedActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser user;

    private ArrayList<Question> searchList;
    private EditText searchText;

    FirebaseListAdapter<String> adapter;

    String LOG_TAG = "Логи";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_created);
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        //started
        bottomNavigationView.setSelectedItemId(R.id.created);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.created:
                        return true;
                    case R.id.tracked:
                        finish();
                        startActivity(new Intent(getApplicationContext(), TrackedActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.history:
                        finish();
                        startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                }

                return false;
            }
        });


        searchText = findViewById(R.id.editTextSearch);

        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchText.getText().toString().isEmpty()) {
                    updateUI();
                } else {
                    searchUI();
                }
            }
        });


        updateUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private class SearchAdapter extends BaseAdapter {

        private ArrayList<Question> searchList;
        private Context context;
        public SearchAdapter(Context context,ArrayList<Question> searchList){
            this.context = context;
            notifyDataSetChanged();
            this.searchList = searchList;
            notifyDataSetChanged();
        }


        private Context getContext() {
            return context;
        }

        @Override
        public int getCount() {
            return searchList.size();
        }

        @Override
        public Question getItem(int position) {
            return searchList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return searchList.indexOf(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Question qo = getItem(position);
            notifyDataSetChanged();

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_questions, null);
                notifyDataSetChanged();
            }
            final TextView text, owner;
            final ImageView imageView;
            final RelativeLayout forum;

            text = (TextView) convertView.findViewById(R.id.forum_question);
            notifyDataSetChanged();
            owner = (TextView) convertView.findViewById(R.id.textViewOwnerID);
            notifyDataSetChanged();
            imageView = (ImageView) convertView.findViewById(R.id.imageViewIsTracked);
            notifyDataSetChanged();
            forum = (RelativeLayout) convertView.findViewById(R.id.dsForum);
            notifyDataSetChanged();

            final QuestionInfo qi = qo.generateInfo();
            text.setText(qi.getTitle());
            notifyDataSetChanged();
            String Author = "Author: ";
            if (mAuth.getUid().equals(qi.getUserID())) Author += "You";
            else Author += qi.getUserName();
            owner.setText(Author);
            notifyDataSetChanged();

            forum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CreatedActivity.this, ViewQuestionActivity.class);
                    intent.putExtra("forumRef", qi.getQuestionID());
                    startActivity(intent);
                }
            });
            notifyDataSetChanged();
            if (qi.isDecided()) imageView.setImageResource(R.drawable.star_on);
            else imageView.setImageResource(R.drawable.star_off);
            notifyDataSetChanged();

            return convertView;
        }

    }

    private void searchUI() {
        final ListView list = findViewById(R.id.listViewContent);

        user = mAuth.getCurrentUser();

        myRef = FirebaseDatabase.getInstance().getReference()
                .child("UsersLibrary")
                .child(user.getUid());

        FirebaseDatabase.getInstance().getReference()
                .child("UsersLibrary")
                .child(user.getUid())
                .child("Created").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchList = new ArrayList<>();
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Forums")
                            .child(datas.getValue(String.class))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {
                                        Question qo = dataSnapshot.getValue(Question.class);

                                        if (qo.getTitle().toLowerCase().trim()
                                                .contains(searchText.getText()
                                                        .toString().toLowerCase().trim())) {
                                            searchList.add(qo);
                                        }
                                    }
                                    SearchAdapter adapter = new SearchAdapter(getApplicationContext(), searchList);

                                    list.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.w("TAG", "Failed to read value.");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void updateUI() {

        ListView list = findViewById(R.id.listViewContent);

        user = mAuth.getCurrentUser();

        myRef = FirebaseDatabase.getInstance().getReference()
                .child("UsersLibrary")
                .child(user.getUid());

        Query query = myRef.child("Created");

        FirebaseListOptions options = new FirebaseListOptions.Builder<String>()
                .setQuery(query,String.class)
                .setLayout(R.layout.list_questions)
                .build();



        adapter = new FirebaseListAdapter<String>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull String model,final int position) {

                final TextView text, owner;
                final ImageView imageView;
                final RelativeLayout forum;

                text = (TextView) v.findViewById(R.id.forum_question);
                owner = (TextView) v.findViewById(R.id.textViewOwnerID);
                imageView = (ImageView) v.findViewById(R.id.imageViewIsTracked);
                forum = (RelativeLayout) v.findViewById(R.id.dsForum);



                FirebaseDatabase.getInstance().getReference()
                        .child("Forums")
                        .child(model).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Question qo = dataSnapshot.getValue(Question.class);
                            final QuestionInfo qi = qo.generateInfo();
                            text.setText(qi.getTitle());
                            owner.setText("Author: You");

                            forum.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    final QuestionsOptionsDialog dlg = new QuestionsOptionsDialog();
                                    Bundle args = new Bundle();
                                    args.putString("forum_key", qo.getId());
                                    args.putString("lib_key",getRef(position).getKey());
                                    args.putString("user_key",user.getUid());
                                    dlg.setArguments(args);
                                    dlg.show(getFragmentManager(),"dlg");
                                    return true;
                                }
                            });

                            forum.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(CreatedActivity.this, ViewQuestionActivity.class);
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
        };

        list.setAdapter(adapter);
    }

}