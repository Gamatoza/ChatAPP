package com.example.chatapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.chatapp.R;
import com.example.chatapp.activities.CreateQuestionActivity;
import com.example.chatapp.activities.ViewQuestionActivity;
import com.example.chatapp.activities.userlibrary.CreatedActivity;
import com.example.chatapp.source.Question;
import com.example.chatapp.source.QuestionInfo;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentForums#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentForums extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private FirebaseListAdapter<Question> adapter;

    private ListView listView;


    private ArrayList<Question> searchList;
    private EditText searchText;

    View root;

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
    public static FragmentForums newInstance(String param1, String param2) {
        FragmentForums fragment = new FragmentForums();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentForums() {
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
        root = inflater.inflate(R.layout.activity_forum, container, false);

        mAuth = FirebaseAuth.getInstance();
        listView = (ListView) root.findViewById(R.id.list_of_questions);
        myRef = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        ListView listOfMessages = (ListView) root.findViewById(R.id.list_of_questions);

        searchText = root.findViewById(R.id.editTextSearch);

        root.findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchText.getText().toString().isEmpty()) {
                    searchUI();
                }
            }
        });

        root.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.isEmailVerified())
                startActivity(new Intent(getActivity(), CreateQuestionActivity.class));
                else
                    Toast.makeText(getActivity().getApplicationContext(), R.string.verification_check,Toast.LENGTH_SHORT).show();
            }
        });
        // Inflate the layout for this fragment
        return root;
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


    private class SearchAdapter extends BaseAdapter {

        private ArrayList<Question> searchList;
        private Context context;

        public SearchAdapter(Context context, ArrayList<Question> searchList) {
            this.context = context;
            notifyDataSetChanged();
            this.searchList = searchList;
            notifyDataSetChanged();
        }


        private Context getContext() {
            return context;
        }

        @Override
        public int getCount() {
            return searchList.size();
        }

        @Override
        public Question getItem(int position) {
            return searchList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return searchList.indexOf(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Question qo = getItem(position);
            notifyDataSetChanged();

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_questions, null);
                notifyDataSetChanged();
            }
            final TextView text, owner;
            final ImageView imageView;
            final RelativeLayout forum;

            text = (TextView) convertView.findViewById(R.id.forum_question);
            notifyDataSetChanged();
            owner = (TextView) convertView.findViewById(R.id.textViewOwnerID);
            notifyDataSetChanged();
            imageView = (ImageView) convertView.findViewById(R.id.imageViewIsTracked);
            notifyDataSetChanged();
            forum = (RelativeLayout) convertView.findViewById(R.id.dsForum);
            notifyDataSetChanged();

            final QuestionInfo qi = qo.generateInfo();
            String title = "";
            if(qi.getTitle().length() >= 16){
                title = qi.getTitle().substring(0,15) + "...";
            }else title = qi.getTitle();
            text.setText(title);
            notifyDataSetChanged();
            String Author = "Author: ";
            if (mAuth.getUid().equals(qi.getUserID())) Author += "You";
            else Author += qi.getUserName();
            owner.setText(Author);
            notifyDataSetChanged();

            forum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ViewQuestionActivity.class);
                    intent.putExtra("forumRef", qi.getQuestionID());
                    startActivity(intent);
                }
            });
            notifyDataSetChanged();
            if (qi.isDecided()) imageView.setImageResource(R.drawable.star_on);
            else imageView.setImageResource(R.drawable.star_off);
            notifyDataSetChanged();

            return convertView;
        }

    }

    private void searchUI() {
        final ListView list = root.findViewById(R.id.listViewContent);
        searchList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference()
                .child("Forums")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {

                            if (dataSnapshot.exists()) {
                                Question qo = datas.getValue(Question.class);

                                if (qo.getTitle().toLowerCase().trim()
                                        .contains(searchText.getText()
                                                .toString().toLowerCase().trim())) {
                                    searchList.add(qo);
                                }else if(qo.getMainMessage().getText().toLowerCase().trim()
                                        .contains(searchText.getText()
                                                .toString().toLowerCase().trim())){
                                    searchList.add(qo);
                                }
                            }
                        }

                        SearchAdapter adapter = new SearchAdapter(getActivity().getApplicationContext(), searchList);

                        list.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("TAG", "Failed to read value.");
                    }
                });
    }
}

