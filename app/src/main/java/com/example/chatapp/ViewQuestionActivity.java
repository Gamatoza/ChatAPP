package com.example.chatapp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.text.format.DateFormat;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ViewQuestionActivity extends AppCompatActivity {

    private static int SIGN_IN_CODE = 1;           //Нужен для проверки на авторизацию, позже заменю
    private RelativeLayout activity_question;      //Основной бэкграунд
    private FirebaseListAdapter<Message> adapter;  //Адаптер для обновления сообщений
    private EmojiconEditText emojiconEditText;     //Сообщение, одновременно является обработчиком смайлов
    private ImageView emojiButton, submitButton;   //Два изображения, отправка и вызов смайлов
    private EmojIconActions emojIconActions;       //Окно где был выбран смайл

    private String KEY;                     //ID вопроса, передается с нажатия на вопрос
    private Question currentQuestion;              //Сам вопрос
    private Boolean isAuthor = false;       //Проверка на авторство

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_CODE){
            if(resultCode == RESULT_OK){
                Snackbar.make(activity_question, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
                displayAllMessages();
            }
            else {
                Snackbar.make(activity_question, "Вы не авторизованы", Snackbar.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Intent intent = getIntent();
        KEY = intent.getStringExtra("forumRef"); //Передаем ID форума

        //Достаем текущий форум и сразу назначаем вопрос
        //Таким образом он будет обновлятся, если вдруг автор решит изменить вопрос
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference()
                .child("Forums").child(KEY);
        final TextView textViewTitle = (TextView)findViewById(R.id.textViewTitle);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentQuestion = dataSnapshot.getValue(Question.class);
                textViewTitle.setText(currentQuestion.getMainMessage().getText());
                //Проверяет является ли автором зашедший пользователь
                if(currentQuestion.getOwnerID().equals(FirebaseAuth.getInstance().getUid()))
                    isAuthor = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value.");
            }
        });

        activity_question = findViewById(R.id.activity_question);

        submitButton= findViewById(R.id.submit_button);
        emojiButton= findViewById(R.id.emoji_button);
        emojiconEditText = findViewById(R.id.text_layout);
        emojIconActions = new EmojIconActions(getApplicationContext(),activity_question,emojiconEditText,emojiButton);
        emojIconActions.ShowEmojIcon();

        //посылает данные на сервер
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = FirebaseDatabase.getInstance().getReference().child("Messages").child(KEY).push().getKey();
                FirebaseDatabase.getInstance().getReference().child("Messages").child(KEY).child(key).setValue(new Message(
                        FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        emojiconEditText.getText().toString(),key
                ));
                emojiconEditText.setText("");
            }
        });
/*
        //Проверка на авторизацию
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
        }
        else{
            Snackbar.make(activity_question, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
        }*/
        displayAllMessages();

    }
    //обработчик данных для получения сообщений
    //кроме того ставит на них нажатие, при котором автор может назначить это сообщение ответом
    private void displayAllMessages() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Messages").child(KEY);
        if(ref == null) ref.setValue(KEY);
        ListView listOfMessages = findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<Message>(ViewQuestionActivity.this,Message.class,R.layout.list_item, ref) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void populateView(View v, final Message model, int position) {
                TextView mess_user,mess_time;
                BubbleTextView mess_text;
                ImageView deleteImageView;
                mess_user = v.findViewById(R.id.message_user);
                mess_time = v.findViewById(R.id.message_time);
                mess_text = v.findViewById(R.id.message_text);
                deleteImageView = v.findViewById(R.id.imageViewDelete);

                RelativeLayout relativeLayout;
                relativeLayout = v.findViewById(R.id.dsMessage);

                mess_user.setText(model.getUserName());
                mess_text.setText(model.getText());
                mess_time.setText(DateFormat.format("dd-mm-yyyy HH:mm:ss",model.getMessageTime()));

                //Если это сообщение является ответом, оно помечается
                if(model.getId().equals(currentQuestion.getAnswer())){
                    relativeLayout.setBackgroundColor(R.color.answerBackground);
                }
                //добавить обработчик для назначения ответа и для снятия его
                if(isAuthor){
                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Snackbar.make(activity_question, "Это сообщение имеет id = " + model.getId(), Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
                //переделать позже под userid когда изменишь Message
                //?? isAuthor ||
                if(model.getUserName().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    deleteImageView.setVisibility(View.VISIBLE);
                    final DeleteDialog dlg = new DeleteDialog();
                    /*
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewQuestionActivity.this);
                    builder.setTitle("Предупреждение");
                    builder.setMessage("Вы действительно хотите удалить сообщение?");
                    builder.setCancelable(true);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //обработчик удаления сообщения
                            //FirebaseDatabase.getInstance().getReference().child("Messages").child(KEY).removeValue(model);
                            dialog.dismiss(); // Отпускает диалоговое окно
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    */

                    deleteImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle args = new Bundle();
                            args.putString("forum_key",KEY);
                            args.putString("message_key",model.getId());
                            dlg.setArguments(args);
                            dlg.show(getFragmentManager(),"dlg");
                            //dialog.show();
                        }
                    });

                }
            }
        };
        listOfMessages.setAdapter(adapter);
    }

}