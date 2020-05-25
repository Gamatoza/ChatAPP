package com.example.chatapp.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.chatapp.R;
import com.example.chatapp.activities.ChangeMessageActivity;
import com.example.chatapp.activities.ChangeQuestionActivity;
import com.example.chatapp.source.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
* AND HERE TOO
*/
public class QuestionsOptionsDialog extends DialogFragment implements OnClickListener {

    final String LOG_TAG = "myLogs";

    private String FORUM_KEY;
    private String LIB_KEY;
    private String USER_KEY;

    Question current;


    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        getDialog().setTitle("Question options");
        View v = inflater.inflate(R.layout.questions_options_dialog, null);
        v.findViewById(R.id.btnChange).setOnClickListener(this);
        v.findViewById(R.id.btnDelete).setOnClickListener(this);

        FORUM_KEY = getArguments().getString("forum_key"); //Передаем ID форума
        LIB_KEY = getArguments().getString("lib_key"); //Передаем ID строки в библиотеке
        USER_KEY = getArguments().getString("user_key"); //Передаем ID юзера

        return v;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChange:
                Intent intent = new Intent(getActivity(), ChangeQuestionActivity.class);
                intent.putExtra("forum_key", FORUM_KEY);

                startActivity(intent);
                dismiss();
                break;
            case R.id.btnDelete:
                FirebaseDatabase.getInstance().getReference()
                        .child("UsersLibrary")
                        .child(USER_KEY)
                        .child("Created")
                        .child(LIB_KEY)
                        .getRef().removeValue();
                FirebaseDatabase.getInstance().getReference()
                        .child("Forums")
                        .child(FORUM_KEY)
                        .getRef().removeValue();
                FirebaseDatabase.getInstance().getReference()
                        .child("Messages")
                        .child(FORUM_KEY)
                        .getRef().removeValue();

                dismiss();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }

    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "Dialog 1: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "Dialog 1: onCancel");
    }


}