package com.example.chatapp.services;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.chatapp.R;
import com.example.chatapp.activities.ViewQuestionActivity;
import com.example.chatapp.activities.userlibrary.TrackedActivity;
import com.example.chatapp.source.Question;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class PushService extends Service {

    Map<String,Question> currentTracked;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        PushService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PushService.this;
        }
    }

    public PushService() {
        currentTracked = new HashMap<>();}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        updateLocalTracked();
    }

    void updateLocalTracked(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference()
                .child("UsersLibrary")
                .child(user.getUid())
                .child("Tracked").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Forums")
                            .child(datas.getValue(String.class))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Question qo = dataSnapshot.getValue(Question.class);
                                        currentTracked.put(qo.getId(), qo);
                                    }
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

    private static boolean compareMaps(Map<String, Question> map1,
                                       Map<String, Question> map2) {
        return map1.entrySet().containsAll(map2.entrySet())
                && map2.entrySet().containsAll(map1.entrySet());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateLocalTracked();
        for ( final Map.Entry<String, Question> item: currentTracked.entrySet()) {
            FirebaseDatabase.getInstance().getReference()
                    .child("Forums")
                    .child(item.getKey())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Question qo = dataSnapshot.getValue(Question.class);
                                if(qo.isDecided() && !item.getValue().isDecided()) {
                                    showNotification(getString(R.string.track_question_got),getString(R.string.click_to_open),qo.getId());
                                    item.setValue(qo);
                                }
                                else if(qo.isDecided() &&  item.getValue().isDecided() &&
                                        item.getValue().getAnswer() != qo.getAnswer()){
                                    showNotification(getString(R.string.track_question_change),getString(R.string.click_to_open),qo.getId());
                                    item.setValue(qo);
                                }
                                else if(!qo.isDecided() && item.getValue().isDecided()){
                                    showNotification(getString(R.string.track_question_delete),getString(R.string.click_to_open),qo.getId());
                                    item.setValue(qo);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w("TAG", "Failed to read value.");
                        }
                    });
        }
        return Service.START_NOT_STICKY;
    }



    private RemoteViews getCustomDesign(String title, String message){
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_firebase);
        remoteViews.setTextViewText(R.id.title,title);
        remoteViews.setTextViewText(R.id.message,message);
        remoteViews.setImageViewResource(R.id.icon,R.drawable.ic_notifications_active);
        return remoteViews;
    }

    public void showNotification(String title, String message,String id){
        //переход на активити когда нажал на пуш
        Intent intent = new Intent(this, ViewQuestionActivity.class);
        intent.putExtra("forumRef",id);
        intent.putExtra("is_tracked",id);
        String channel_id="tracked_notify_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),channel_id)
                .setSmallIcon(R.drawable.ic_notifications_active)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            builder=builder.setContent(getCustomDesign(title,message));
        }
        else {
            builder=builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_notifications_active);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id,"tracked_notify",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri,null);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0,builder.build());

    }
}
