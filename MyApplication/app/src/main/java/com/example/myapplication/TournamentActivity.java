package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TournamentActivity extends AppCompatActivity {

    //Logger retrieved from Slide 139 and the GeoGame example
    private static Logger log = LoggerFactory.getLogger(TournamentActivity.class);
    private static final String TAG = "TournamentActivity";

    private DatabaseHelper dbHelper = null;
    private ArrayAdapter<String> adapter;
    DatabaseCalculationHelper allHelper = null;
    private int positionRes;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        log.info("onCreateOptionsMenu started");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.existingtournament_menu, menu);

        //the menu item for the round back should be invisible
        // for the first round because there is no round 0
        String round = (String) getSupportActionBar().getTitle();
        String[] split = round.split(" ", 2);
        int roundnumber = Integer.parseInt(split[1]);
        MenuItem item = menu.findItem(R.id.item_back);
        if (roundnumber == 1) {
            item.setVisible(false);
        }

        return true;
    }

    //Slide 146 (Options-Menu Lifecycle)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {                                 //switch wurde verwendet, um das options menu ggf. zu erweitern
            case R.id.item_leaderboard:
                log.info("onOptionsItemSelected item_leaderboard clicked");
                Intent intent = new Intent(this, LeaderboardActivity.class);
                intent.putExtra("finalround", false);
                startActivity(intent);
                return true;
            case R.id.item_new_tournament:
                log.info("onOptionsItemSelected item_new_tournament clicked");

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        allHelper = new DatabaseCalculationHelper();
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                log.info("DialogInterface.BUTTON_POSITIVE clicked");
                                allHelper.resultDeleteAll();
                                finish();

                                /*
                                 * The tournament is finished and the result table is cleared.
                                 * Therefore all previous activities should be cleared.
                                 * Souce: https://stackoverflow.com/questions/6330260/finish-all-previous-activities
                                 */

                                Intent intent = new Intent(getBaseContext(), AddPlayersActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                log.info("DialogInterface.BUTTON_NEGATIVE clicked");
                                break;
                        }
                    }
                };


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.newTournament).setPositiveButton(R.string.alterDialogYes, dialogClickListener)
                        .setNegativeButton(R.string.altertDialogNo, dialogClickListener).show();

                return true;
            case R.id.item_back:
                log.info("onOptionsItemSelected item_back clicked");
                String round = (String) getSupportActionBar().getTitle();
                String[] split = round.split(" ", 2);
                int roundnumber = Integer.parseInt(split[1]);
                intent = new Intent(getBaseContext(), TournamentActivity.class);
                round = "Round " + (roundnumber - 2);
                intent.putExtra("round", round); // Source: https://stackoverflow.com/questions/24032956/action-bar-back-button-not-working
                intent.putExtra("back", true);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.debug("onCreate debug");
        log.info("onCreate info");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);
        Log.d(TAG, "onCreate: Started.");
        final ListView mListView = findViewById(R.id.listView);

        getAndSetDataForResult(mListView);
        FloatingActionButton fab = findViewById(R.id.fab_btn);

        /*
         * was added to the new round at onClick and is called here
         * Source: https://www.android-hilfe.de/forum/android-app-entwicklung.9/werte-mit-intent-aus-neu-gestarteter-activity-uebergeben.210517.html
         */
        String round = getIntent().getStringExtra("round");
        if (round != null) startNextRound(round);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = checkResultsCompleteForRound();
                if (check) {
                    String round = (String) getSupportActionBar().getTitle();
                    String[] split = round.split(" ", 2);
                    int roundnumber = Integer.parseInt(split[1]);
                    List<Result> ausdbliste = doResultDataStuff();
                    int lastRound = getLastRoundnr(ausdbliste);

                    /*
                     *  If not in the last round: Actitvity should call itself again
                     */
                    if (lastRound > roundnumber) {
                        //Runde beim intent mitgeben
                        Intent intent = new Intent(getBaseContext(), TournamentActivity.class);
                        intent.putExtra("round", round); // Source: https://stackoverflow.com/questions/24032956/action-bar-back-button-not-working
                        startActivity(intent);
                    }

                    /*
                     *  We are in the last round: Leaderboard should be shown
                     */
                    else {
                        Intent intent = new Intent(getBaseContext(), LeaderboardActivity.class);
                        Intent intentTournament = getIntent();
                        //Give information that you are in the last round
                        intent.putExtra("finalround", true);
                        startActivity(intent);
                    }
                } else {
                    String round = (String) getSupportActionBar().getTitle();
                    Snackbar.make(v, (getString(R.string.notAllResults1) + round + (getString(R.string.notAllResults2))), Snackbar.LENGTH_LONG).show();

                }

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

                positionRes = position;
                view.showContextMenu();


            }
        });

        registerForContextMenu(mListView);

    }


    @Override
    protected void onStart() {
        log.debug("onStart debug");
        log.info("onStart info");

        DatabaseCalculationHelper dbAllhelp = new DatabaseCalculationHelper();

        Intent intent = getIntent();
        Boolean forward = intent.getExtras().getBoolean("forward"); //Source: https://www.android-hilfe.de/forum/android-app-entwicklung.9/werte-mit-intent-aus-neu-gestarteter-activity-uebergeben.210517.html
        if (forward) {
            intent.putExtra("forward", false);

            int min = dbAllhelp.getCurrentRoundnr();


            String round = "Round " + (min - 1);
            startNextRound(round);
        }

        super.onStart();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        log.info("onCreatedContextMenu started");
        menu.setHeaderTitle(R.string.enterResult);
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        log.info("onContextItemSelected started");
        final ListView mListView = findViewById(R.id.listView);
        final Result itemRes = (Result) mListView.getItemAtPosition(positionRes);
        switch (item.getItemId()) {
            case R.id.option_1:
                String result = getString(R.string.whiteWin);
                enterResult(itemRes, result);
                getAndSetDataForResult(mListView);
                log.info("onContextItemSelected option white wins");
                return true;
            case R.id.option_2:
                result = getString(R.string.BlackWin);
                enterResult(itemRes, result);
                getAndSetDataForResult(mListView);
                log.info("onContextItemSelected option black wins");
                return true;
            case R.id.option_3:
                result = getString(R.string.draw);
                enterResult(itemRes, result);
                getAndSetDataForResult(mListView);
                log.info("onContextItemSelected option remis");
                return true;
            default:
                return super.onContextItemSelected(item);
        }


    }

    public void getAndSetDataForResult(ListView mListView) {
        log.info("getAndSetDataForResult started");
        /// used to read the list
        List<Result> ausdbliste = doResultDataStuff(); //Liste umbennen, stürzt direkt ab, wenn einkommentiert
        final ArrayList<Result> resultList = new ArrayList<Result>(ausdbliste);
        final ResultListAdapter adapter = new ResultListAdapter(this, R.layout.content_tournament, resultList);
        mListView.setAdapter(adapter);
    }


    private void enterResult(Result item, String result) {
        log.info("enterResult started");
        RuntimeExceptionDao<Result, Integer> resultDao = getdbHelper().getResultDataDao();

        UpdateBuilder<Result, Integer> updateBuilder = resultDao.updateBuilder();
        //query
        try {
            // set the criteria like you would a QueryBuilder
            updateBuilder.where().eq("id", item.getID());
            // update the value of your field(s)
            updateBuilder.updateColumnValue("result", result);
            updateBuilder.update();
            log.info("Result Update completed");
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Result Update failed");
        }
    }


    private List<Result> doResultDataStuff() {
        log.info("doResultDataStuff started");
        RuntimeExceptionDao<Result, Integer> resultDao = getdbHelper().getResultDataDao();

        List<Result> resultList = Collections.EMPTY_LIST;

        String round = (String) getSupportActionBar().getTitle();
        String[] split = round.split(" ", 2);
        int roundnumber = Integer.parseInt(split[1]);
        //query
        try {
            resultList = getdbHelper().getResultDao().queryForEq("roundnumber", roundnumber);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Log.d("TournamentActivity", resultList.toString());
        return resultList;
    }

    public void startNextRound(String round) {
        Log.d("TournamentActivity", "startNextRound");

        //increase the current roundnumber by one for the next round
        String[] split = round.split(" ", 2);
        int roundnumber = Integer.parseInt(split[1]);
        roundnumber = roundnumber + 1;
        String newRound = getString(R.string.roundspace) + roundnumber;
        getSupportActionBar().setTitle(newRound);

        /// to also display the next round
        final ListView mListView = findViewById(R.id.listView);

        List<Result> ausdbliste = doResultDataStuff(); //Liste umbennen, stürzt direkt ab, wenn einkommentiert
        final ArrayList<Result> resultList = new ArrayList<Result>(ausdbliste); //hier steht das korrekte drin
        final ResultListAdapter adapter = new ResultListAdapter(getBaseContext(), R.layout.content_tournament, resultList);
        mListView.setAdapter(adapter);
        int lastRound = getLastRoundnr(ausdbliste);

        FloatingActionButton fab = findViewById(R.id.fab_btn);

        if (lastRound == roundnumber) {
            /*
             * in the last round the fab icon should be a medal
             * because on click the leaderboard is opened
             */
            fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_medal_icon)); //Source: https://stackoverflow.com/questions/33140706/android-dynamically-change-fabfloating-action-button-icon-from-code
            log.info("last Round: fab Image changed");
        }
        log.info("Next round started.");
    }

    private int getLastRoundnr(List<Result> list) {
        int lastRound = 0;
        AddPlayersActivity objectTemp = new AddPlayersActivity();
        List<Player> listp = objectTemp.doPlayerDataStuff(true);
        int count = listp.size();
        if (count % 2 == 0)
            lastRound = count - 1;
        else lastRound = count;
        return lastRound;
    }


    // check if all results are already entered for the current round
    private boolean checkResultsCompleteForRound() {
        RuntimeExceptionDao<Result, Integer> resultDao = getdbHelper().getResultDataDao();
        Boolean complete = true;

        List<Result> resultList = Collections.EMPTY_LIST;
        String round = (String) getSupportActionBar().getTitle();
        String[] split = round.split(" ", 2);
        int roundnumber = Integer.parseInt(split[1]);
        //query
        try {
            resultList = getdbHelper().getResultDao().queryForEq("roundnumber", roundnumber);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < resultList.size(); i++) {
            Result tempr = resultList.get(i);
            String result = tempr.getResult();
            if (result.isEmpty()) {
                complete = false;
            }
        }
        return complete;
    }

    //Source: https://stackoverflow.com/questions/6937782/how-to-show-a-dialog-box-after-pressing-the-back-button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String round = (String) getSupportActionBar().getTitle(); //auskommentiert am 25.4.2020
        String[] split = round.split(" ", 2);
        int roundnumber = Integer.parseInt(split[1]);

        //String back = getIntent().getStringExtra("back");
        Boolean back = getIntent().getExtras().getBoolean("back");
        if (roundnumber == 1 && !back) {

            /*
             * if you are in the very first round and reached it not through the round back button
             * and press the back button a popup opens
             * and asks if you would like to quit and delete all results
             * this check is necessary so that the user does not accidentally
             * leave the tournament and delete all results
             */

            if (keyCode != KeyEvent.KEYCODE_BACK) return super.onKeyDown(keyCode, event);

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    allHelper = new DatabaseCalculationHelper();
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            allHelper.resultDeleteAll();
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.round1back)).setPositiveButton(R.string.alterDialogYes, dialogClickListener)
                    .setNegativeButton(R.string.altertDialogNo, dialogClickListener).show();
        } else {
            MainActivity.activeTournament = true;
        }

        return super.onKeyDown(keyCode, event);
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
