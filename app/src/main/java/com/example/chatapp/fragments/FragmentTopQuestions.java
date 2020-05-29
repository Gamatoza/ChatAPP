package com.example.chatapp.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatapp.R;
import com.example.chatapp.activities.CreateQuestionActivity;
import com.example.chatapp.activities.ViewQuestionActivity;
import com.example.chatapp.source.Question;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentTopQuestions#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTopQuestions extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private FirebaseListAdapter<Question> adapter;

    private ListView listView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPattern.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTopQuestions newInstance(String param1, String param2) {
        FragmentTopQuestions fragment = new FragmentTopQuestions();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentTopQuestions() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.top_questions_activity, container, false);


        mAuth = FirebaseAuth.getInstance();
        listView = (ListView)root.findViewById(R.id.list_of_questions);
        myRef = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();



        ListView listOfMessages = (ListView)root.findViewById(R.id.list_of_questions);

        Query query= myRef.child("Forums").limitToLast(20);

        FirebaseListOptions options = new FirebaseListOptions.Builder<Question>()
                .setQuery(query,Question.class)
                .setLayout(R.layout.list_questions)
                .build();


        adapter = new FirebaseListAdapter<Question>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull final Question model, int position) {
                TextView text,owner;
                text  = (TextView)v.findViewById(R.id.forum_question);
                owner = (TextView)v.findViewById(R.id.textViewOwnerID);
                final ImageView imageView = (ImageView)v.findViewById(R.id.imageViewIsTracked);
                imageView.setVisibility(View.VISIBLE);
                String title="";
                if(model.getTitle().length() >= 16){
                    title = model.getTitle().substring(0,15) + "...";
                }else title = model.getTitle();
                text.setText(title);
                String Author = "Author: ";
                if(mAuth.getUid().equals(model.getUserID())) Author += "You";
                else Author+=model.generateInfo().getUserName();
                owner.setText(Author);
                RelativeLayout forum = (RelativeLayout)v.findViewById(R.id.dsForum);
                forum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ViewQuestionActivity.class);
                        intent.putExtra("forumRef",model.getId());
                        startActivity(intent);
                    }
                });

                FirebaseDatabase.getInstance().getReference()
                        .child("UsersLibrary")
                        .child(user.getUid())
                        .child("Tracked")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                    if (datas.getValue(String.class).equals(model.getId())) {
                                        imageView.setImageResource(R.drawable.ic_star_on_black);
                                        return;
                                    }else imageView.setImageResource(R.drawable.ic_star_off_black);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                if(model.isDecided()) forum.setBackgroundResource(R.color.colorHaveAnswer);
                else forum.setBackgroundResource(android.R.color.background_light);
            }
        };

        //TODO переделать ListViewer в RecyclerViewer
        /*LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        listView.setLayoutManager(mLayoutManager);*/
        adapter.notifyDataSetChanged();
        listOfMessages.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listOfMessages.scrollTo(0,listOfMessages.getHeight());
        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
