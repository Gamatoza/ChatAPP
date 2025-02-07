package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.chatapp.NavigationActivity;
import com.example.chatapp.R;
import com.example.chatapp.fragments.FragmentTopQuestions;
import com.example.chatapp.source.Message;
import com.example.chatapp.source.Question;
import com.example.chatapp.source.QuestionInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateQuestionActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;

    EditText nameEditText,contentEditText; //два заполненных поля, Заголовок и сам Вопрос соответсвенно
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        nameEditText = (EditText)findViewById(R.id.question_name);
        contentEditText = (EditText)findViewById(R.id.question_content);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Forums");

        //обработчик кнопки
        //пушит в Forums новый вопрос
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
                final String pushKey = myRef.push().getKey();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Question question = new Question(pushKey, user.getUid(),Title,new Message(
                        Content,
                        user
                ));
                myRef.child(pushKey).setValue(question);
                final DatabaseReference ChangeRef = FirebaseDatabase.getInstance().getReference()
                        .child("UsersLibrary")
                        .child(user.getUid())
                        .child("Created");

                //question.generateInfo()
                ChangeRef.push().setValue(question.getId());

                //после создания переходит обратно в мейн
                Intent intent = new Intent(CreateQuestionActivity.this, ViewQuestionActivity.class);

                FragmentTransaction ftrans = getFragmentManager().beginTransaction();
                ftrans.replace(R.id.container, new FragmentTopQuestions());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("forumRef",pushKey);
                finish();
                startActivity(intent);

            }
        });

    }
}
