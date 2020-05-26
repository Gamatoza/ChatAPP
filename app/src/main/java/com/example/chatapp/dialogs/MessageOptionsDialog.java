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

import com.example.chatapp.activities.ChangeMessageActivity;
import com.example.chatapp.R;
import com.example.chatapp.source.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
* AND HERE TOO
*/
public class MessageOptionsDialog extends DialogFragment implements OnClickListener {

    final String LOG_TAG = "myLogs";

    private String MESSAGE_KEY = "";
    private String FORUM_KEY = "";

    Question current;
    DatabaseReference Qref;
    String queString = "";
    Boolean isThatMessage = false;

    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        getDialog().setTitle("Message options");
        View v = inflater.inflate(R.layout.message_options_dialog, null);
        v.findViewById(R.id.btnChange).setOnClickListener(this);
        v.findViewById(R.id.btnDelete).setOnClickListener(this);

        FORUM_KEY = getArguments().getString("forum_key");
        MESSAGE_KEY = getArguments().getString("message_key");
        Boolean isAuthor = getArguments().getBoolean("is_author");

        if(isAuthor){
            final Button btn = v.findViewById(R.id.btnSetAnswer);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(this);
            Qref = FirebaseDatabase.getInstance().getReference().child("Forums").child(FORUM_KEY);
            Qref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {

                        current = (Question) dataSnapshot.getValue(Question.class);
                        btn.setText("SET an answer");
                        if (current.isDecided())
                            if (current.getAnswer().contains(MESSAGE_KEY)) {
                                btn.setText("REMOVE an answer");
                                isThatMessage = true;
                            }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        return v;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChange:
                Intent intent = new Intent(getActivity(), ChangeMessageActivity.class);
                intent.putExtra("forum_key", FORUM_KEY);
                intent.putExtra("message_key", MESSAGE_KEY);
                startActivity(intent);
                dismiss();
                break;
            case R.id.btnDelete:
                FirebaseDatabase.getInstance().getReference()
                        .child("Messages")
                        .child(FORUM_KEY).child(MESSAGE_KEY)
                        .getRef().removeValue();
                if(isThatMessage){
                    current.setAnswer(null);
                    current.isDecided(false);
                    Qref.setValue(current);
                }
                dismiss();
                break;
            case R.id.btnSetAnswer:

                if (!current.isDecided() && (current.getAnswer() == null || current.getAnswer().isEmpty())) //если ответа нет
                {
                    current.setAnswer(MESSAGE_KEY); //ставит вопрос
                } else if (current.isDecided() && current.getAnswer() != null && current.getAnswer().contains(MESSAGE_KEY)) { //если ответ есть и id совпадают
                    current.setAnswer(null); //убирает
                    current.isDecided(false);
                } else {
                    current.setAnswer(MESSAGE_KEY); //переназначает
                }


                //current.setAnswer(MESSAGE_KEY); //ставит вопрос
                Qref.setValue(current);
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