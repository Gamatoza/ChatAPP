package com.example.chatapp.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chatapp.R;
import com.example.chatapp.source.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RateDialog extends DialogFragment implements OnClickListener {

    private static String MESSAGE_KEY = "";
    private static String FORUM_KEY = "";

    Question current;


    final String LOG_TAG = "myLogs";

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        FORUM_KEY = getArguments().getString("forum_key");
        MESSAGE_KEY = getArguments().getString("message_key");
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Title!")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Yes", this)
                .setNegativeButton("Cancel", this)
                //.setNeutralButton(R.string.maybe, this)
                .setMessage(R.string.rate_message_text);
        return adb.create();
    }

    public void onClick(DialogInterface dialog, int which) {
        int i = 0;
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                i = R.string.yes;
                DatabaseReference Qref = FirebaseDatabase.getInstance().getReference().child("Forums").child(FORUM_KEY);
                final DatabaseReference finalRef = Qref;
                Qref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {

                            current = (Question) dataSnapshot.getValue(Question.class);
                            /*
                            if (current.isDecided() != null)
                                if (!current.isDecided() && current.getAnswer() == null) //если ответа нет
                                {
                                    current.setAnswer(MESSAGE_KEY); //ставит вопрос
                                } else if (current.isDecided() && current.getAnswer() != null) { //если ответ есть и id совпадают
                                    current.isDecided(false);
                                    current.setAnswer(null); //убирает
                                } else {
                                    current.setAnswer(MESSAGE_KEY); //переназначает
                                }
                            */

                            current.setAnswer(MESSAGE_KEY); //ставит вопрос
                            finalRef.setValue(current);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }});

                dismiss();
                break;
            case Dialog.BUTTON_NEGATIVE:
                i = R.string.no;
                dismiss();
                break;/*
            case Dialog.BUTTON_NEUTRAL:
                i = R.string.maybe;
                break;*/
        }
        if (i > 0)
            Log.d(LOG_TAG, "Dialog 2: " + getResources().getString(i));
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "Dialog 2: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "Dialog 2: onCancel");
    }
}
