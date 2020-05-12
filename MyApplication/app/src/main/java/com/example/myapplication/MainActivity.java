package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * This activity opens at the first start for the app
 * and includes the Google+ Login
 */


public class MainActivity extends AppCompatActivity {
    public static Boolean activeTournament = false;
    public static Boolean noTournamentBack = false;

    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);

    private Button button;
    int RC_SIGN_IN = 0;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;

    public void clickFunction(View view) {
        Log.i("Info", "Button auf Startbildschirm angeklickt");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.debug("onCreate debug");
        log.info("onCreate info");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Views
        signInButton = findViewById(R.id.sign_in_button);

        //Configure sign-in to request the user's ID, email adress and basic
        //profile, ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Build a GoogleSignInClient with the options specified by gso-
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    public void signIn() {
        log.info("signIn start");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            log.info("Signed in successfully");
            startActivity(new Intent(MainActivity.this, HomescreenActivity.class));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(MainActivity.this, R.string.fail, Toast.LENGTH_LONG).show();
        }
    }


    public void openActivity_gotonewtournament() {
        log.info("openActivity_gotonewtournament(), started");
        Intent intent = new Intent(this, HomescreenActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        log.debug("onStart debug");
        log.info("onStart info");
        super.onStart();

        if (noTournamentBack) {
            //   finish();
            log.debug("noTournamentBack: " + noTournamentBack);
            int a = 1;
            noTournamentBack = false;
            return;
        }


        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


        if (account != null) {
            Intent intent = new Intent(MainActivity.this, HomescreenActivity.class);
            intent.putExtra("forward", true); //raus am 7.5.2020
            startActivity(intent);
            finish();
        }

    }
}
