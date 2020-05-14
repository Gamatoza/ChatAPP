package com.example.chatapp.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;

import com.example.chatapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RateDialog extends DialogFragment implements OnClickListener {

    private static String MESSAGE_KEY = "";
    private static String FORUM_KEY = "";

    final String LOG_TAG = "myLogs";

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        FORUM_KEY = getArguments().getString("forum_key");
        MESSAGE_KEY = getArguments().getString("message_key");
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Удаление")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Да", this)
                .setNegativeButton("Отмена", this)
                //.setNeutralButton(R.string.maybe, this)
                .setMessage(R.string.rate_message_text);
        return adb.create();
    }

    public void onClick(DialogInterface dialog, int which) {
        int i = 0;
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                i = R.string.yes;
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference()
                        .child("Messages")
                        .child(FORUM_KEY).child(MESSAGE_KEY).getRef();
                myRef.removeValue();
                break;
            case Dialog.BUTTON_NEGATIVE:
                i = R.string.no;
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
