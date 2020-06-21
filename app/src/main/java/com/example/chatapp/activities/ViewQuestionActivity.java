package com.example.chatapp.activities;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.dialogs.MessageOptionsDialog;
import com.example.chatapp.dialogs.RateDialog;
import com.example.chatapp.services.PushService;
import com.example.chatapp.source.Message;
import com.example.chatapp.source.Question;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import android.text.format.DateFormat;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Date;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
/*
whatever you think
whatever you don't want to change
STAY THE FUCK OUT OF HERE
DON'T TOUCH IT
HERE EVERYTHING IS KEPT ON THE WILL OF GOD
 */
public class ViewQuestionActivity extends AppCompatActivity {

    private RelativeLayout activity_question;           //Основной бэкграунд

    private FirebaseRecyclerAdapter<Message,MessageViewHolder> adapter; //Адаптер для обновления сообщений

    private EmojiconEditText emojiconEditText;          //Сообщение, одновременно является обработчиком смайлов
    private ImageView emojiButton, submitButton;        //Два изображения, отправка и вызов смайлов
    private EmojIconActions emojIconActions;            //Окно где был выбран смайл

    private String FORUM_ID;                            //ID вопроса, передается с нажатия на вопрос
    private Question currentQuestion;                   //Сам вопрос
    private Boolean isAuthor = false;                   //Проверка на авторство

    private FirebaseUser user;                          //Текущий пользователь
    private FirebaseDatabase database;                  //БД
    private DatabaseReference mainRef;                  //Ссылка на главный рут

    private Boolean isTracked = false;
    private String trackedInLibraryID = "";

    private ValueEventListener mainListener;

    private RecyclerView recyclerView;
    private StorageReference storageRef;

    private static String LOG_TAG;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Intent intent = getIntent();
        FORUM_ID = intent.getStringExtra("forumRef"); //Передаем ID форума
        isTracked = intent.getBooleanExtra("is_tracked",false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mainRef = database.getReference();

        activity_question = findViewById(R.id.activity_question);

        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://chat-program-43efe.appspot.com/profilepics");

        final DatabaseReference HistoryRef = FirebaseDatabase.getInstance().getReference()
                .child("UsersLibrary")
                .child(user.getUid())
                .child("History");

        recyclerView = findViewById(R.id.list_of_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(false);

        //region Получение текущего вопроса и назначение заголовка с вопросом
        //Достаем текущий форум и сразу назначаем вопрос
        //Таким образом он будет обновлятся, если вдруг автор решит изменить вопрос
        final TextView textViewTitle, textViewDescription;
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDescription = findViewById(R.id.textViewDescription);
        mainListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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
                } else {
                    finish();
                    Toast.makeText(getApplicationContext(), R.string.deleted_question_on_view, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(LOG_TAG, "Failed to read value.");
            }
        };

        mainRef.child("Forums").child(FORUM_ID).addValueEventListener(mainListener);

        //endregion

        //region Добавление обработчика на посыл сообщения

        submitButton = findViewById(R.id.submit_button);
        emojiButton = findViewById(R.id.emoji_button);
        emojiconEditText = findViewById(R.id.text_layout);
        emojIconActions = new EmojIconActions(getApplicationContext(), activity_question, emojiconEditText, emojiButton);
        emojIconActions.ShowEmojIcon();

        //обработчик для посыла сообщения
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pushID = mainRef.child("Messages").child(FORUM_ID).push().getKey();
                if (!emojiconEditText.getText().toString().isEmpty()) {
                    mainRef.child("Messages").child(FORUM_ID).child(pushID).setValue(new Message(
                            pushID,                                         //передаем
                            emojiconEditText.getText().toString().trim(),
                            user
                    ));
                    emojiconEditText.setText("");
                }
            }
        });

        //endregion

        //region Добавление обработчика на добавления вопроса в отслеживаемые

        final DatabaseReference trackRef = FirebaseDatabase.getInstance().getReference()
                .child("UsersLibrary")
                .child(user.getUid())
                .child("Tracked");

        final ImageView imageViewTracked = findViewById(R.id.imageViewTracked);

        if(isTracked) imageViewTracked.setImageResource(R.drawable.ic_star_on_black);
        else
            trackRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        if (datas.getValue(String.class).equals(currentQuestion.getId())) {
                            isTracked = true;
                            trackedInLibraryID = datas.getKey();
                            imageViewTracked.setImageResource(R.drawable.ic_star_on_black);
                            PushService.updateLocalTracked();
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
                    imageViewTracked.setImageResource(R.drawable.ic_star_off_black);
                    trackRef.child(trackedInLibraryID).getRef().removeValue();
                } else {
                    isTracked = true;
                    imageViewTracked.setImageResource(R.drawable.ic_star_on_black);
                    trackedInLibraryID = trackRef.push().getKey();
                    trackRef.child(trackedInLibraryID).setValue(currentQuestion.getId());
                }
                startService(new Intent(getApplicationContext(),PushService.class));
            }
        });

        //endregion

    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(getApplicationContext(),PushService.class));
        displayAllMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayAllMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        if(mainListener!=null)
        mainRef.child("Forums").child(FORUM_ID).removeEventListener(mainListener);
    }

    Boolean isMessageOwner = false;

    //обработчик данных для получения сообщений
    //кроме того ставит на них нажатие, при котором автор может назначить это сообщение ответом
    private void displayAllMessages() {
        DatabaseReference ref = mainRef.child("Messages").child(FORUM_ID);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(ref,Message.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {
            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view;
                if(isMessageOwner){
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_mes, parent, false);
                }else{
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_mes,parent,false);
                }
                return new MessageViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final MessageViewHolder mvh, int position, @NonNull final Message model) {
                mvh.relativeLayout.setOnClickListener(null);
                //получение аватарки для пользователя
                storageRef.child(model.getUserID() + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .into(mvh.avatar);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        mvh.avatar.setImageResource(R.drawable.no_image);
                    }
                });

                //Добавляет звезду если сообщение отправлено автором
                if(model.getUserID().equals(currentQuestion.getUserID()))
                    mvh.star.setVisibility(View.VISIBLE);
                else mvh.star.setVisibility(View.GONE);

                //Если это сообщение является ответом, оно помечается
                if (currentQuestion.isDecided()) {
                    if (model.getId().equals(currentQuestion.getAnswer())) {
                        mvh.relativeLayout.setBackgroundResource(R.drawable.side_answer);
                    } else {
                        mvh.relativeLayout.setBackgroundResource(android.R.color.background_light);
                    }
                }

                //Ставит подпись того, кто сообщение отправил
                if (model.getUserID().equals(user.getUid())) mvh.mess_user.setText("You");
                else if (model.getUserDisplayName() != null)
                    mvh.mess_user.setText(model.getUserDisplayName());
                else mvh.mess_user.setText(model.getUserEmail());

                //Текст сообщения
                mvh.mess_text.setText(model.getText());

                //TODO дописать более подробную ветвь для времени отправки сообщения
                //Время отправки сообщения
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat format;
                if(new Date().getYear() == model.getMessageTime().getYear()){
                    format = new SimpleDateFormat("HH:mm:ss");
                }else format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                mvh.mess_time.setText(format.format(model.getMessageTime()));

                final String id = model.getId();
                //Если пользователь является тем, кто написал это сообщение, то может его удалить или изменить
                if (model.getUserID().equals(user.getUid())) {
                    isMessageOwner = true;
                    //придумать как закинуть облачко на правую сторону
                    mvh.relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final MessageOptionsDialog dlg = new MessageOptionsDialog();
                            Bundle args = new Bundle();
                            args.putString("forum_key", FORUM_ID);
                            args.putString("message_key", id);
                            args.putBoolean("is_author", isAuthor);

                            dlg.setArguments(args);
                            dlg.show(getFragmentManager(), "dlg");
                        }
                    });
                } else {
                    isMessageOwner = false;
                    if (isAuthor) {
                        mvh.relativeLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RateDialog dlg = new RateDialog();
                                Bundle args = new Bundle();
                                args.putString("forum_key", FORUM_ID);
                                args.putString("message_key", id);
                                dlg.setArguments(args);
                                dlg.show(getFragmentManager(), "dlg");
                            }
                        });
                    }
                }
            }

        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainRef.child("Forums").child(FORUM_ID).removeEventListener(mainListener);

    }


    private static class MessageViewHolder extends RecyclerView.ViewHolder{
        final RelativeLayout relativeLayout;
        final ImageView avatar, star;
        TextView mess_user, mess_time;
        final TextView mess_text;

        public MessageViewHolder(@NonNull View v) {
            super(v);
            relativeLayout = v.findViewById(R.id.dsMessage);
            avatar = v.findViewById(R.id.imageViewAvatar);
            star = v.findViewById(R.id.imageViewStar);
            mess_user = v.findViewById(R.id.message_user);
            mess_time = v.findViewById(R.id.message_time);
            mess_text = v.findViewById(R.id.message_text);

            /*FirebaseStorage.getInstance().getReferenceFromUrl("gs://chat-program-43efe.appspot.com/profilepics")
                    .child(FirebaseAuth.getInstance().getUid() + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get()
                            .load(uri)
                            .into(avatar);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    avatar.setImageResource(R.drawable.no_image);
                }
            });*/
        }
    }
}