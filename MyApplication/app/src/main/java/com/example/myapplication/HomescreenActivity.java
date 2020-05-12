package com.example.myapplication;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/**
 * This activity opens if no tournament has been created yet
 * and the google+ login has already been completed
 * or if you have finished a tournament
 */

public class HomescreenActivity extends AppCompatActivity {

    //Logger retrieved from Slide 139 and the GeoGame example
    private static Logger log = LoggerFactory.getLogger(HomescreenActivity.class);

    private Button button; // fuer den neues Turnier Button
    GoogleSignInClient mGoogleSignInClient;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {                                 //switch wurde verwendet, um das options menu ggf. zu erweitern
            case R.id.item_logout:
                log.info("onOptionsItemSelected item_logout selected");
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(HomescreenActivity.this, R.string.signedOut, Toast.LENGTH_SHORT).show();
                        log.info("Successfully signed out");
                        startActivity(new Intent(HomescreenActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.debug("onCreate debug");
        log.info("onCreate info");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity_createnewtournament();
            }
        });

        //for Logout
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Boolean forward = getIntent().getBooleanExtra("forward", false); //neu 5.5.2020

        if (forward) {
            Intent intent = new Intent(this, AddPlayersActivity.class); //danach erst TournamentActivity
            intent.putExtra("forward", true); //raus am 7.5.2020
            startActivity(intent);
            finish();
            log.info("forward to AddPlayersActivity");

        }


    }


    public void openActivity_createnewtournament() {
        log.info("openActivity_createnewtournament() started");
        Intent intent = new Intent(this, AddPlayersActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        log.debug("onStart debug");
        log.info("onStart info");

        super.onStart();
    }

}

