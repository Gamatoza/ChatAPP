package com.example.chatapp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.text.format.DateFormat;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ViewQuestionActivity extends AppCompatActivity {

    private int SIGN_IN_CODE = 1;                       //Нужен для проверки на авторизацию, позже заменю
    private RelativeLayout activity_question;           //Основной бэкграунд
    private FirebaseListAdapter<Message> adapter;       //Адаптер для обновления сообщений
    private EmojiconEditText emojiconEditText;          //Сообщение, одновременно является обработчиком смайлов
    private ImageView emojiButton, submitButton;        //Два изображения, отправка и вызов смайлов
    private EmojIconActions emojIconActions;            //Окно где был выбран смайл

    private String FORUM_ID;                            //ID вопроса, передается с нажатия на вопрос
    private Question currentQuestion;                   //Сам вопрос
    private Boolean isAuthor = false;                   //Проверка на авторство


    private FirebaseUser user;                          //Текущий пользователь
    private FirebaseDatabase database;                  //БД
    private DatabaseReference mainRef;                  //Ссылка на главный рут

    private static String LOG_TAG;

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
        FORUM_ID = intent.getStringExtra("forumRef"); //Передаем ID форума

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mainRef = database.getReference();

        activity_question = findViewById(R.id.activity_question);

        //region Получение текущего вопроса и назначение заголовка с вопросом
        //Достаем текущий форум и сразу назначаем вопрос
        //Таким образом он будет обновлятся, если вдруг автор решит изменить вопрос
        final TextView textViewTitle,textViewDescription;
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDescription = findViewById(R.id.textViewDescription);

        mainRef.child("Forums").child(FORUM_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentQuestion = dataSnapshot.getValue(Question.class);

                textViewTitle.setText(currentQuestion.getTitle());
                textViewDescription.setText(currentQuestion.getMainMessage().getText());

                //Проверяет является ли автором зашедший пользователь
                if(currentQuestion.getUserID().equals(user.getUid()))
                    isAuthor = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(LOG_TAG, "Failed to read value.");
            }
        });
        //endregion

        //region Добавление обработчика на посыл сообщения

        submitButton= findViewById(R.id.submit_button);
        emojiButton= findViewById(R.id.emoji_button);
        emojiconEditText = findViewById(R.id.text_layout);
        emojIconActions = new EmojIconActions(getApplicationContext(),activity_question,emojiconEditText,emojiButton);
        emojIconActions.ShowEmojIcon();

        //посылает данные на сервер
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pushID = mainRef.child("Messages").child(FORUM_ID).push().getKey();
                mainRef.child("Messages").child(FORUM_ID).child(pushID).setValue(new Message(
                        pushID,                                         //передаем
                        emojiconEditText.getText().toString().trim(),
                        user
                ));
                emojiconEditText.setText("");
            }
        });

        //endregion

        displayAllMessages(); //отобразить все сообщения с постоянным обновлениемы

    }

    //обработчик данных для получения сообщений
    //кроме того ставит на них нажатие, при котором автор может назначить это сообщение ответом
    private void displayAllMessages() {
        DatabaseReference ref = mainRef.child("Messages").child(FORUM_ID);
        //if(ref == null) ref.setValue(KEY);
        final ListView listOfMessages = findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<Message>(ViewQuestionActivity.this,Message.class,R.layout.list_item, ref) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void populateView(View v, final Message model, int position) {
                //Назначение информации в облачке
                TextView mess_user,mess_time;
                final BubbleTextView mess_text;
                mess_user = v.findViewById(R.id.message_user);
                mess_time = v.findViewById(R.id.message_time);
                mess_text = v.findViewById(R.id.message_text);


                //Получаем id удаления сообщения и ответа
                final ImageView rateImageView;
                rateImageView = v.findViewById(R.id.imageViewRate);

                RelativeLayout relativeLayout;
                relativeLayout = v.findViewById(R.id.dsMessage);

                if(model.getUserID().equals(user.getUid())) mess_user.setText("You");
                else if(model.getUserDisplayName()!=null)
                mess_user.setText(model.getUserDisplayName());
                else mess_user.setText(model.getUserEmail());

                mess_text.setText(model.getText());
                mess_time.setText(DateFormat.format("dd-mm-yyyy HH:mm:ss",model.getMessageTime()));

                //Если это сообщение является ответом, оно помечается
                if(model.getId().equals(currentQuestion.getAnswer())){
                    rateImageView.setVisibility(View.VISIBLE);
                    rateImageView.setImageResource(R.drawable.star_on);
                }else rateImageView.setImageResource(R.drawable.star_off);

                //Если текущий пользователь является автором вопроса
                if(isAuthor){

                    //Проверка
                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Snackbar.make(activity_question, "Это сообщение имеет id = " + model.getId(), Snackbar.LENGTH_SHORT).show();
                        }
                    });

                    //Обработчик для назначения ответа
                    rateImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //Это надо как то упростить
                            if(currentQuestion.isDecided()) { //Если ответ уже есть
                                if(currentQuestion.getAnswer().equals(model.getId())) {     //Если нажато сообщение что уже является ответом
                                    currentQuestion.removeAnswer();                         //Удаляет ответ
                                    rateImageView.setImageResource(R.drawable.star_off);    //Удаляем пометку
                                }
                                else{                                                       //Иначе
                                    currentQuestion.setAnswer(model.getId());               //Ставим новый ответ
                                    rateImageView.setImageResource(R.drawable.star_on);     //Ставим пометку
                                }
                            }
                            else {                                                          //Если ответа нет
                                currentQuestion.setAnswer(model.getId());                   //Ставим его
                                rateImageView.setImageResource(R.drawable.star_on);         //Ставим пометку
                            }
                            FirebaseDatabase.getInstance().getReference().child("Forums").child(FORUM_ID).setValue(currentQuestion);
                        }
                    });
                }

                final ImageView deleteImageView;
                deleteImageView = v.findViewById(R.id.imageViewDelete);
                //Если пользователь является тем, кто написал это сообщение, то может его удалить
                if(model.getUserID().equals(user.getUid())){
                    //придумать как закинуть облачко на правую сторону
                    relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            final MessageOptionsDialog dlg = new MessageOptionsDialog();
                            Bundle args = new Bundle();
                            args.putString("forum_key", FORUM_ID);
                            args.putString("message_key",model.getId());
                            dlg.setArguments(args);
                            dlg.show(getFragmentManager(),"dlg");
                            return false;
                        }
                    });
                    /*
                    deleteImageView.setVisibility(View.VISIBLE);
                    final DeleteDialog dlg = new DeleteDialog();

                    deleteImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle args = new Bundle();
                            args.putString("forum_key", FORUM_ID);
                            args.putString("message_key",model.getId());
                            dlg.setArguments(args);
                            dlg.show(getFragmentManager(),"dlg");
                        }
                    });*/

                }else {
                    relativeLayout.setOnClickListener(null);
                }
                //else deleteImageView.setVisibility(View.GONE);
            }
        };

        listOfMessages.setAdapter(adapter);
    }
}