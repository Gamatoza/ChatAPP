package com.example.chatapp.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.chatapp.R;
import com.example.chatapp.activities.LoginActivity;
import com.example.chatapp.dialogs.DeleteAccountDialog;
import com.example.chatapp.templates.DialogGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSettings extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int CHOOSE_IMAGE = 101;
    TextView textView;
    ImageView imageView;
    EditText editText;
    ProgressBar progressBar;
    Uri uriProfileImage;
    String profileImgURL;

    FirebaseAuth mAuth;
    FirebaseUser user;

    View root;

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
    public static FragmentSettings newInstance(String param1, String param2) {
        FragmentSettings fragment = new FragmentSettings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentSettings() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //multy check for request
        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){

            uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uriProfileImage);
                imageView.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
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

        root = inflater.inflate(R.layout.activity_settings, container, false);

        editText = (EditText) root.findViewById(R.id.editTextDisplayName);
        imageView = (ImageView) root.findViewById(R.id.imageView);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        textView = (TextView) root.findViewById(R.id.textViewVerified);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        //call an image selection manager
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(user.isEmailVerified())
                    showImageChooser();
                else Toast.makeText(getActivity().getApplicationContext(), R.string.verification_check,Toast.LENGTH_SHORT).show();

            }
        });

        loadUserInformation(); //load info into imageView & text
        //!!!!!!!!!!!some troubles with load to imageView

        //save in firebase click listener
        root.findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.isEmailVerified())
                    saveUserInformation();
                else Toast.makeText(getActivity().getApplicationContext(), R.string.verification_check,Toast.LENGTH_SHORT).show();
            }
        });

        root.findViewById(R.id.buttonExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
                startActivity(new Intent(getActivity(),LoginActivity.class));
            }
        });

        root.findViewById(R.id.buttonDeleteAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteAccountDialog dlg = new DeleteAccountDialog();
                dlg.show(getFragmentManager(),"DeleteAccountDLG");
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


    private void saveUserInformation() {
        //there are a coupe of bugs hidden here
        String displayName = editText.getText().toString();
        if(displayName.isEmpty()){
            editText.setError("Name required");
            editText.requestFocus();
            return;
        }


        if(user != null){
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(),"Profile Uploaded",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void uploadImageToFirebaseStorage() {
        //upload the image to Storage in firebox in profilepics/[samename].jgp
        final StorageReference profileImageReference =
                FirebaseStorage.getInstance().getReference("profilepics/"+user.getUid()+".png");
        if(uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageReference.delete();
            profileImageReference.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    profileImgURL = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    //Image Chooser Manager
    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"),CHOOSE_IMAGE);
    }


    private void loadUserInformation() {

        final FirebaseUser user = mAuth.getCurrentUser();

        if(user != null) {
            if (user.getPhotoUrl() != null) {
                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://chat-program-43efe.appspot.com/profilepics");
                ref.child(user.getUid()+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Picasso.get()
                                .load(uri)
                                .error(R.drawable.no_image)
                                .into(imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                exception.getMessage(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                //Picasso.get().load(user.getPhotoUrl()).into(imageView);
/*
                Glide.with(ProfileActivity.this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);*/
            }
            if (user.getDisplayName() != null) {
                editText.setText(user.getDisplayName());
            }

            if(user.isEmailVerified()){
                textView.setText(R.string.email_verified);
            } else {

                textView.setText(R.string.email_not_verified);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(),"Verification email sent",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }

    }

}
