package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity for the leaderboard is created to see
 * the current rankings of all players
 */

public class LeaderboardActivity extends AppCompatActivity {

    //Logger retrieved from Slide 139 and the GeoGame example
    private static Logger log = LoggerFactory.getLogger(AddPlayersActivity.class);

    private DatabaseHelper dbHelper = null;
    private ArrayAdapter<String> adapter;

    private static final String TAG = "TournamentActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.leaderboard_menu, menu);
        return true;
    }

    //Slide 146 (Options-Menu Lifecycle)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {                                 //switch was used to extend the options menu if necessary
            case R.id.item_new_tournament:
                log.info("onOptionsItemSelected item_new_tournament clicked");

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseCalculationHelper allHelper = new DatabaseCalculationHelper();
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                log.info("DialogInterface.BUTTON_POSITIVE clicked");
                                //Yes button clicked
                                allHelper.resultDeleteAll();
                                finish();

                                /*
                                 * The tournament is finished and the result table is cleared.
                                 * Therefore the previous activities should be cleared.
                                 * Souce: https://stackoverflow.com/questions/6330260/finish-all-previous-activities
                                 */

                                Intent intent = new Intent(getBaseContext(), AddPlayersActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                log.info("DialogInterface.BUTTON_NEGATIVE clicked");
                                //No button clicked
                                break;
                        }
                    }
                };


                /*
                 * ALert with the information that
                 * all results are deleted if a new tournament is started
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.newTournament).setPositiveButton(R.string.alterDialogYes, dialogClickListener)
                        .setNegativeButton(R.string.altertDialogNo, dialogClickListener).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.debug("onCreate debug");
        log.info("onCreate info");
        DatabaseCalculationHelper dbAllHelper = new DatabaseCalculationHelper();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        final ListView mListView = findViewById(R.id.listView);
        List<Player> ausdbliste = dbAllHelper.getPlayersWithResult();
        final ArrayList<Player> playerList = new ArrayList<Player>(ausdbliste);
        final LeaderboardListAdapter adapter = new LeaderboardListAdapter(this, R.layout.content_leaderboard, playerList);
        mListView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_btn);

        Boolean finalround = getIntent().getExtras().getBoolean("finalround");

        /*
         * The FloatingActionButton should be only shown if the current round is the last one
         * and then could be used to finish the tournament
         */
        if (!finalround) {
            fab.hide();
            log.info("Not in the final round.");
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.info("onClick started info");
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseCalculationHelper allHelper = null;
                        allHelper = new DatabaseCalculationHelper();
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                allHelper.resultDeleteAll();
                                finish();
                                Intent intent = new Intent(getBaseContext(), HomescreenActivity.class);

                                /*
                                 * The tournament is finished and the result table is cleared.
                                 * Therefore all previous activities should be cleared.
                                 * Souce: https://stackoverflow.com/questions/6330260/finish-all-previous-activities
                                 */

                                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //alte Version funktioniert nicht optimal
                                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // funktioniert
                                startActivity(intent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };


                AlertDialog.Builder builder = new AlertDialog.Builder(LeaderboardActivity.this); //Source: https://stackoverflow.com/questions/21814825/you-need-to-use-a-theme-appcompat-theme-or-descendant-with-this-activity
                builder.setMessage(getString(R.string.quitTournament)).setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();

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


}
