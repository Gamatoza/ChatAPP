package com.example.chatapp;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
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

import com.example.chatapp.activities.ProfileActivity;
import com.example.chatapp.fragments.FragmentGallery;
import com.example.chatapp.fragments.FragmentImport;
import com.example.chatapp.fragments.FragmentSend;
import com.example.chatapp.fragments.FragmentShare;
import com.example.chatapp.fragments.FragmentSlideshow;
import com.example.chatapp.fragments.FragmentTools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentImport fimport;
    FragmentGallery fgallery;
    FragmentSend fsend;
    FragmentShare fshare;
    FragmentSlideshow fshow;
    FragmentTools ftools;

    private FirebaseAuth mAuth;
    private View header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        fgallery = new FragmentGallery();
        fimport = new FragmentImport();
        fsend = new FragmentSend();
        fshare = new FragmentShare();
        fshow = new FragmentSlideshow();
        ftools = new FragmentTools();

        mAuth = FirebaseAuth.getInstance();
        loadUserInformation();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction ftrans = getFragmentManager().beginTransaction();

        if (id == R.id.nav_camara) {
            ftrans.replace(R.id.container, fimport);
        } else if (id == R.id.nav_gallery) {
            ftrans.replace(R.id.container, fgallery);

        } else if (id == R.id.nav_slideshow) {
            ftrans.replace(R.id.container, fshow);

        } else if (id == R.id.nav_manage) {
            ftrans.replace(R.id.container, ftools);

        } else if (id == R.id.nav_share) {
            ftrans.replace(R.id.container, fshare);

        } else if (id == R.id.nav_send) {
            ftrans.replace(R.id.container, fsend);

        } ftrans.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadUserInformation() {

        final FirebaseUser user = mAuth.getCurrentUser();
        final ImageView imageView = header.findViewById(R.id.imageViewDisplayAvatar);
        final TextView textView = header.findViewById(R.id.textViewDisplayLogin);
        if(user != null) {
            if (user.getPhotoUrl() != null) {
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
                        Toast toast = Toast.makeText(getApplicationContext(),
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

    }

}
