package com.example.chatapp;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chatapp.activities.LoginActivity;
import com.example.chatapp.activities.userlibrary.CreatedActivity;
import com.example.chatapp.dialogs.AboutProgram;
import com.example.chatapp.fragments.FragmentForums;
import com.example.chatapp.fragments.FragmentLittleHelp;
import com.example.chatapp.fragments.FragmentSettings;
import com.example.chatapp.fragments.FragmentTopQuestions;
import com.example.chatapp.services.PushService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CHOOSE_IMAGE = 101;
    FragmentTopQuestions topQuestions;
    FragmentLittleHelp littleHelp;
    FragmentForums forms;
    FragmentSettings settings;

    FragmentTransaction ftrans;

    private FirebaseAuth mAuth;
    private View header;


    AboutProgram dlgAP = new AboutProgram();

    Intent pushService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        if(!hasConnection(this)) {
            startActivity(new Intent(this,LoginActivity.class));
            Toast.makeText(this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
        }

        if(mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getEmail() == null){
            mAuth.signOut();
            startActivity(new Intent(this,LoginActivity.class));
            Toast.makeText(this, R.string.not_authorized,Toast.LENGTH_LONG).show();
        }



            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                loadUserInformation();
            }};

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        topQuestions = new FragmentTopQuestions();
        littleHelp = new FragmentLittleHelp();
        settings = new FragmentSettings();

        forms = new FragmentForums();


        loadUserInformation();


    }

    @Override
    protected void onStart() {
        super.onStart();

        /*textView = findViewById(R.id.textViewMainMessage);
        textView.setMovementMethod(new ScrollingMovementMethod());*/

        ftrans = getFragmentManager().beginTransaction();
        ftrans.replace(R.id.container, forms);

        if(!hasConnection(getApplicationContext())){
            Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
        }

        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(this,LoginActivity.class));
            Toast.makeText(this, R.string.not_authorized,Toast.LENGTH_LONG).show();
        }
        /*pushService = new Intent(this,PushService.class);
        startService(pushService);*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //textView.setVisibility(View.GONE);
        FragmentTransaction ftrans = getFragmentManager().beginTransaction();

        switch (id) {
            case R.id.nav_Forum:
                ftrans.replace(R.id.container, forms);
                break;
            case R.id.nav_topQuestions:
                ftrans.replace(R.id.container, topQuestions);
                break;
            case R.id.nav_littleHelp:
                ftrans.replace(R.id.container, littleHelp);
                break;
            case R.id.nav_library:
                startActivity(new Intent(this, CreatedActivity.class));
                break;
            case R.id.nav_settings:
                ftrans.replace(R.id.container, settings);
                break;
            case R.id.nav_aboutProgram:
                dlgAP.show(getFragmentManager(), "dlg");
                break;
        }
         ftrans.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadUserInformation() {

        final FirebaseUser user = mAuth.getCurrentUser();
        final ImageView imageView = header.findViewById(R.id.imageViewDisplayAvatar);
        final TextView textView = header.findViewById(R.id.textViewDisplayLogin);
        if(user != null) {
                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://chat-program-43efe.appspot.com/profilepics");
                ref.child(user.getUid()+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Picasso.get().load(uri)
                                .into(imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        /*Toast toast = Toast.makeText(getApplicationContext(),
                                exception.getMessage(), Toast.LENGTH_SHORT);
                        toast.show();*/
                        Picasso.get().load(R.drawable.no_image).into(imageView);
                    }
                });
                //Picasso.get().load(user.getPhotoUrl()).into(imageView);
/*
                Glide.with(ProfileActivity.this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);*/
            if (user.getDisplayName() != null) {
                textView.setText(user.getDisplayName());
            } else textView.setText(user.getEmail());

            /*if(user.isEmailVerified()){
                textView.setText("Email Verified");
            } else {

                textView.setText("Email Not Verified (Click to Verify)");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this,"Verification email sent",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }*/
        }

        pushService = new Intent(this,PushService.class);
        startService(pushService);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(pushService);

    }

    public static boolean hasConnection(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
}
