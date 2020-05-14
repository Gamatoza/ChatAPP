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

import com.example.chatapp.activities.ChangeMessageActivity;
import com.example.chatapp.R;
import com.google.firebase.database.FirebaseDatabase;

public class MessageOptionsDialog extends DialogFragment implements OnClickListener {

    final String LOG_TAG = "myLogs";

    private static String MESSAGE_KEY = "";
    private static String FORUM_KEY = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Message options");
        View v = inflater.inflate(R.layout.message_options_dialog, null);
        v.findViewById(R.id.btnChange).setOnClickListener(this);
        v.findViewById(R.id.btnDelete).setOnClickListener(this);

        FORUM_KEY = getArguments().getString("forum_key");
        MESSAGE_KEY = getArguments().getString("message_key");

        return v;
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnChange:
                Intent intent = new Intent(getActivity(), ChangeMessageActivity.class);
                intent.putExtra("forum_key",FORUM_KEY);
                intent.putExtra("message_key",MESSAGE_KEY);
                startActivity(intent);
                dismiss();
                break;
            case R.id.btnDelete:
                FirebaseDatabase.getInstance().getReference()
                        .child("Messages")
                        .child(FORUM_KEY).child(MESSAGE_KEY)
                        .getRef().removeValue();
                dismiss();
                break;
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