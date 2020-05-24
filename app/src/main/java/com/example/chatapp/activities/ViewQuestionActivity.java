package com.example.chatapp.activities;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.dialogs.MessageOptionsDialog;
import com.example.chatapp.source.Message;
import com.example.chatapp.source.Question;
import com.firebase.ui.database.FirebaseListAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
/*
whatever you think
whatever you don't want to change
STAY THE FUCK OUT OF HERE
DON'T TOUCH IT
HERE EVERYTHING IS KEPT ON THE WILL OF GOD
 */
public class ViewQuestionActivity extends AppCompatActivity{

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

    private Boolean  isTracked = false;
    private String trackedInLibraryID = "";


    //private LayoutInflater inflater;                  //TODO  Смещение сообщения влево или вправо

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

    Map<String,Question> History;

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

        final DatabaseReference HistoryRef = FirebaseDatabase.getInstance().getReference()
                .child("UsersLibrary")
                .child(user.getUid())
                .child("History");

        //region Получение текущего вопроса и назначение заголовка с вопросом
        //Достаем текущий форум и сразу назначаем вопрос
        //Таким образом он будет обновлятся, если вдруг автор решит изменить вопрос
        final TextView textViewTitle, textViewDescription;
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDescription = findViewById(R.id.textViewDescription);
        mainRef.child("Forums").child(FORUM_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentQuestion = dataSnapshot.getValue(Question.class);

                //currentQuestion.getId() если нужно что бы оно не добовляло нового
                /*
                Date now = new Date();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
                //.child(f.format(now)) если нужно подстраивать под время
                */
                HistoryRef.push().setValue(currentQuestion.getId());
                //FULL.addContent(Purpose.History,currentQuestion.generateInfo());
                textViewTitle.setText(currentQuestion.getTitle());
                textViewDescription.setText(currentQuestion.getMainMessage().getText());

                //Проверяет является ли автором зашедший пользователь
                if (currentQuestion.getUserID().equals(user.getUid()))
                    isAuthor = true;
                displayAllMessages(); //отобразить все сообщения с постоянным обновлениемы

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(LOG_TAG, "Failed to read value.");
            }
        });
        //endregion

        //region Добавление обработчика на посыл сообщения

        submitButton = findViewById(R.id.submit_button);
        emojiButton = findViewById(R.id.emoji_button);
        emojiconEditText = findViewById(R.id.text_layout);
        emojIconActions = new EmojIconActions(getApplicationContext(), activity_question, emojiconEditText, emojiButton);
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

        //region Добавление обработчика на добавления вопроса в отслеживаемые

        final DatabaseReference trackRef = FirebaseDatabase.getInstance().getReference()
                .child("UsersLibrary")
                .child(user.getUid())
                .child("Tracked");

        final ImageView imageViewTracked = findViewById(R.id.imageViewTracked);

        trackRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    if (datas.getValue(String.class).equals(currentQuestion.getId())) {

                        isTracked = true;
                        trackedInLibraryID = datas.getKey();
                        imageViewTracked.setImageResource(R.drawable.star_on);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        findViewById(R.id.imageViewTracked).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTracked) {
                    isTracked = false;
                    imageViewTracked.setImageResource(R.drawable.star_off);
                    trackRef.child(trackedInLibraryID).getRef().removeValue();
                } else {
                    isTracked = true;
                    imageViewTracked.setImageResource(R.drawable.star_on);
                    trackedInLibraryID = trackRef.push().getKey();
                    trackRef.child(trackedInLibraryID).setValue(currentQuestion.getId());
                }
            }
        });

        //endregion


    }

    //обработчик данных для получения сообщений
    //кроме того ставит на них нажатие, при котором автор может назначить это сообщение ответом
    private void displayAllMessages() {
        DatabaseReference ref = mainRef.child("Messages").child(FORUM_ID);
        //if(ref == null) ref.setValue(KEY);
        final ListView listOfMessages = findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<Message>(ViewQuestionActivity.this,Message.class,R.layout.left_mes_new, ref) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void populateView(View v, final Message model, int position) {

                final RelativeLayout relativeLayout;
                relativeLayout = v.findViewById(R.id.dsMessage);
                //inflater = (LayoutInflater)v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //Если это сообщение является ответом, оно помечается
                if(model.getId().equals(currentQuestion.getAnswer())){
                    v.setBackgroundResource(R.color.colorHaveAnswer);
                  //  v = inflater.inflate(R.layout.right_mes,relativeLayout);

                }else {
                    v.setBackgroundResource(android.R.color.background_light);
                   // v = inflater.inflate(R.layout.left_mes,relativeLayout);

                }

                final ImageView avatar = v.findViewById(R.id.imageViewAvatar);
                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://chat-program-43efe.appspot.com/profilepics");
                ref.child(user.getUid()+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri)
                                .into(avatar);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                exception.getMessage(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

                //Назначение информации в облачке
                TextView mess_user,mess_time;
                final BubbleTextView mess_text;
                mess_user = v.findViewById(R.id.message_user);
                mess_time = v.findViewById(R.id.message_time);
                mess_text = v.findViewById(R.id.message_text);


                if(model.getUserID().equals(user.getUid())) mess_user.setText("You");
                else if(model.getUserDisplayName()!=null)
                mess_user.setText(model.getUserDisplayName());
                else mess_user.setText(model.getUserEmail());

                mess_text.setText(model.getText());
                mess_time.setText(DateFormat.format("dd-mm-yyyy HH:mm:ss",model.getMessageTime()));



                //Если текущий пользователь является автором вопроса
                if(isAuthor){
                    //Проверка
                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Snackbar.make(activity_question, "Это сообщение имеет id = " + model.getId(), Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }

                //Если пользователь является тем, кто написал это сообщение, то может его удалить или изменить
                if(model.getUserID().equals(user.getUid())){
                    //придумать как закинуть облачко на правую сторону

                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final MessageOptionsDialog dlg = new MessageOptionsDialog();
                            Bundle args = new Bundle();
                            args.putString("forum_key", FORUM_ID);
                            args.putString("message_key",model.getId());
                            args.putBoolean("is_author",isAuthor);

                            dlg.setArguments(args);
                            dlg.show(getFragmentManager(),"dlg");
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