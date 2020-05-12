package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.sql.SQLException;

import static android.text.TextUtils.isEmpty;

/**
 * Activity to add new player through a form to the database
 */

public class NewPlayerFormActivity extends AppCompatActivity {
    //Logger retrieved from Slide 139 and the GeoGame example
    private static Logger log = LoggerFactory.getLogger(AddPlayersActivity.class);

    EditText etNewplayer;
    Button btnAddnewplayer;

    private DatabaseHelper dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.debug("onCreate debug");
        log.info("onCreate info");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_player_form);

        etNewplayer = findViewById(R.id.etNewplayer);

        btnAddnewplayer = findViewById(R.id.btnAddnewplayer);

        btnAddnewplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean correctInput = OnAddPlayer(v);
                if (correctInput) {

                    //Snackbar.make(v, R.string.addPlayerText, Snackbar.LENGTH_LONG).show(); //wird nicht angezeigt, da direkt eine andere View geöffnet wird, evtl bei Intent mitgeben und dann anzeigen

                    Intent i = new Intent(NewPlayerFormActivity.this, AddPlayersActivity.class);
                   // i.putExtra("playerAdded", true);
                    startActivity(i);
                }
            }
        });
    }


    private DatabaseHelper getdbHelper() {
        if (dbHelper == null) {
            dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return dbHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
            log.info("onDestroy finished and dbHelper released");
        }
    }


    public boolean OnAddPlayer(View view) {
        //Get text from EditText
        EditText newPlayer = findViewById(R.id.etNewplayer);
        String content = newPlayer.getText().toString();

        /*
         * Validation to check that the entered playername is not empty / null
         * source (strongly changed):
         *  https://codinginflow.com/tutorials/android/validate-email-password-regular-expressions
         */
        boolean checkInput = checkDataEntered(newPlayer, content);

        if (checkInput) {
            newPlayer.setText("");

            //Create new player
            Player player = new Player(content, true); //default einfach immer auf true setzen

            try {
                //Save player in database
                getdbHelper().getPlayerDao().create(player);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return checkInput;

    }

    private boolean checkDataEntered(EditText newPlayer, String content) {
        if (isEmpty(content)) {
            newPlayer.setError(getString(R.string.enterNameError));
            return false;
        } else return true;
    }
}
