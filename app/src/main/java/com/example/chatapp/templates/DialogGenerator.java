package com.example.chatapp.templates;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;

import com.example.chatapp.R;

@SuppressLint("ValidFragment")
public class DialogGenerator extends DialogFragment implements OnClickListener {

    final String LOG_TAG = "myLogs";
    AlertDialog.Builder adb;

    public DialogGenerator(String DialogText, String DialogTitle, Boolean WithNeutral){
        adb = new AlertDialog.Builder(getActivity())
                .setTitle(DialogTitle)
                .setPositiveButton(R.string.yes, this)
                .setNegativeButton(R.string.no, this)
                .setMessage(DialogText);
        if(WithNeutral)
            adb.setNeutralButton(R.string.maybe, this);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return adb.create();
    }

    public void onClick(DialogInterface dialog, int which) {
        int i = 0;
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                i = R.string.yes;
                break;
            case Dialog.BUTTON_NEGATIVE:
                i = R.string.no;
                break;
            case Dialog.BUTTON_NEUTRAL:
                i = R.string.maybe;
                break;
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