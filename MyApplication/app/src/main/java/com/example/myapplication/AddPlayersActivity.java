package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;


/**
 * This activity is used to add new players,
 * select their participation and then start a new tournament.
 */

public class AddPlayersActivity extends AppCompatActivity {

    //Logger retrieved from Slide 139 and the GeoGame example
    private static Logger log = LoggerFactory.getLogger(AddPlayersActivity.class);

    private DatabaseHelper dbHelper = null;
    private ArrayAdapter<String> adapter;
    private Player item;
    private int positionPlayer;
    Boolean delete = false;
    private Snackbar snackbar;
    private boolean onClickCheck = false;
    private View view;


    private static final String TAG = "AddPlayersActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        log.debug("onCreateOptionsMenu debug");
        log.info("onCreateOptionsMenu info");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.newtournament_menu, menu);
        return true;
    }


    //Skript Slide 146 (Options-Menu Lifecycle)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        log.debug("onCreateItemSelected debug");
        log.info("onCreateItemSelected info");

        DatabaseCalculationHelper dbAllHelper = new DatabaseCalculationHelper();
        List<Player> player = dbAllHelper.doPlayerDataStuff(true);
        int size = player.size(); //String.valueOf(size)

        /*
         * a tournament may only be started if there are at least 3 participants
         */
        if (size >= 3) {
            switch (item.getItemId()) {                                 //switch wurde verwendet, um das options menu ggf. zu erweitern
                case R.id.item_start_new_tournament:

                    CreateResultTable();

                    //wie im Skript Slide 146 (Options-Menu Lifycycle Slide)
                    Intent intent = new Intent(this, TournamentActivity.class);
                    intent.putExtra("forward", false);
                    startActivity(intent);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        /*
         * Number of participants is less than 3,
         * therefore no tournament should be started
         * and a message should be sent to the user
         */
        else {
            log.info("onOptionsItemSelected info: Less than 3 participants");

            //Source: (only very roughly orientation for the dialogue): https://stackoverflow.com/questions/6937782/how-to-show-a-dialog-box-after-pressing-the-back-button
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {//Yes button clicked
                    }
                }
                //////////////////////////
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (size == 2) {
                builder.setMessage(getString(R.string.pleaseAdd) + getString(R.string.playerTournStart)).setPositiveButton(getString(R.string.ok), dialogClickListener).show();
            } else {
                builder.setMessage(getString(R.string.pleaseAdd) + (3 - size) + getString(R.string.playersTournStart)).setPositiveButton(getString(R.string.ok), dialogClickListener).show();
            }

            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.debug("onCreate debug");
        log.info("onCreate info");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addplayers);
        final ListView mListView = findViewById(R.id.listView);
        List<Player> ausdbliste = doPlayerDataStuff(false);
        final ArrayList<Player> playerList = new ArrayList<Player>(ausdbliste);
        final PlayerListAdapter adapter = new PlayerListAdapter(this, R.layout.content_addplayers, playerList);
        mListView.setAdapter(adapter);

        snackbar = Snackbar.make(mListView, R.string.deletePlayer, Snackbar.LENGTH_LONG);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            /**
             * OnItemLongClick is used to delete players.
             */
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (!onClickCheck) {

                    unregisterForContextMenu(mListView);

                    item = (Player) mListView.getItemAtPosition(position);

                    snackbar = Snackbar
                            .make(view, R.string.deletePlayer, Snackbar.LENGTH_LONG)
                            .setAction(R.string.yes, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        // Get deleteBuilder from NoteDao
                                        DeleteBuilder<Player, Integer> deleteBuilder = getdbHelper().getPlayerDao().deleteBuilder();
                                        // Only delete the matching item
                                        deleteBuilder.where().eq("id", item.getID());
                                        deleteBuilder.delete();
                                        log.info("setOnItemLongClickListener Player deleted with ID = " + item.getID());

                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    adapter.remove(item);
                                    delete = false;
                                    getAndSetDataForPlayer(mListView);
                                    // snackbar.dismiss();
                                    registerForContextMenu(mListView);
                                }


                            });

                    snackbar.show();

                }


                return false;
            }
        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!snackbar.isShown()) {
                    registerForContextMenu(mListView);
                    onClickCheck = true;
                    positionPlayer = position;
                    view.showContextMenu();
                }

            }
        });


        FloatingActionButton fab = findViewById(R.id.fab_btn);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity_addnewplayer();
            }
        });


        Boolean forward = getIntent().getBooleanExtra("forward", false); //neu 5.5.2020
        if (forward) {


            DatabaseCalculationHelper dbAllHelper = new DatabaseCalculationHelper();
            int currentRound = 0;
            try {
                currentRound = dbAllHelper.getCurrentRoundnr();
            } catch (Exception e) {
            }

            if (forward && currentRound > 0) {
                Intent intent = new Intent(this, TournamentActivity.class);
                intent.putExtra("forward", true);
                startActivity(intent);
                finish();
                log.info("forward to TournamentActivity");

            } else {
                Intent intent = new Intent(this, HomescreenActivity.class);
                intent.putExtra("forward", false); //false because backward direction;
                startActivity(intent);
                finish();
                log.info("forward to HomescreenActivity");
            }
        }

        registerForContextMenu(mListView);


/*        Boolean playerAdded = getIntent().getExtras().getBoolean("playerAdded");
        if(playerAdded) {
            Snackbar.make(view, R.string.addPlayerText, Snackbar.LENGTH_LONG).show();
        }*/

    }


    //Source for orientation: https://www.tutlane.com/tutorial/android/android-context-menu-with-examples
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle(R.string.playerParticipateMenu);
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.participate_context_menu, menu);


    }


    //Source for orientation: https://www.tutlane.com/tutorial/android/android-context-menu-with-examples
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        final ListView mListView = findViewById(R.id.listView);
        final Player itemPlayer = (Player) mListView.getItemAtPosition(positionPlayer);

        switch (item.getItemId()) {
            case R.id.option_1:
                changeParticipation(itemPlayer, true);
                getAndSetDataForPlayer(mListView);
                onClickCheck = false;
                //adapter.notifyDataSetChanged();
                return true;
            case R.id.option_2:
                changeParticipation(itemPlayer, false);
                getAndSetDataForPlayer(mListView);
                onClickCheck = false;
                //adapter.notifyDataSetChanged();
                return true;

            default:
                onClickCheck = false;
                return super.onContextItemSelected(item);
        }


    }


    private void changeParticipation(Player item, boolean partic) {
        RuntimeExceptionDao<Player, Integer> playerDao = getdbHelper().getPlayerDataDao();


        UpdateBuilder<Player, Integer> updateBuilder = playerDao.updateBuilder();
        //query
        try {
            // set the criteria like you would a QueryBuilder
            updateBuilder.where().eq("id", item.getID());

            // update the value of your field(s)
            updateBuilder.updateColumnValue("participation", partic);

            updateBuilder.update();
            log.info("changeParticipation to: " + partic + "with ID = " + item.getID());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void getAndSetDataForPlayer(ListView mListView) {
        List<Player> ausdbliste = doPlayerDataStuff(false); //Liste umbennen, stürzt direkt ab, wenn einkommentiert
        final ArrayList<Player> playerList = new ArrayList<Player>(ausdbliste);
        final PlayerListAdapter adapter = new PlayerListAdapter(getBaseContext(), R.layout.content_addplayers, playerList);
        mListView.setAdapter(adapter);
        log.info("getAndSetDataForPlayer info");
    }


    public List<Player> doPlayerDataStuff(boolean onlyParticipationTrue) {
        RuntimeExceptionDao<Player, Integer> playerDao = getdbHelper().getPlayerDataDao();
        List<Player> playerList = Collections.EMPTY_LIST;

        //query
        try {
            if (onlyParticipationTrue)
                playerList = getdbHelper().getPlayerDao().queryForEq("participation", true);
            else
                playerList = getdbHelper().getPlayerDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        log.info("doPlayerDataStuff - playerlist:" + playerList.toString());
        return playerList;
    }


    private void CreateResultTable() {
        log.info("CreateResultTable started");
        RuntimeExceptionDao<Result, Integer> resultDao = getdbHelper().getResultDataDao();

        //delete all data from the result table
        DatabaseCalculationHelper allHelper = new DatabaseCalculationHelper();
        allHelper.resultDeleteAll();

        List<Player> ausdbliste = doPlayerDataStuff(true);

        int nr = 1;
        for (Player p : ausdbliste) {
            p.setNr(nr);
            nr++;
        }

        //determine number of players
        int countPlayer = nr - 1;

        // fill the Result-table
        ArrayList<Result> listresulttemp = new ArrayList<Result>();
        for (int p1 = 1; p1 <= countPlayer; p1++) {
            for (int p2 = 1; p2 <= countPlayer; p2++) {
                if (p1 != p2) {
                    if (countPlayer % 2 == 1 || (p1 != countPlayer && p2 != countPlayer)) {
                        Result res = GetResult(countPlayer, p1, p2, GetPlayerIDFromNr(ausdbliste, p1), GetPlayerIDFromNr(ausdbliste, p2));
                        if (res != null)
                            listresulttemp.add(res);
                    } else {
                        Result res = GetResultLastEvenPlayer(countPlayer, p1, p2, GetPlayerIDFromNr(ausdbliste, p1), GetPlayerIDFromNr(ausdbliste, p2));
                        if (res != null)
                            listresulttemp.add(res);
                    }
                }
            }
        }

        //write in Result-table
        for (Result r : listresulttemp) {
            resultDao.create(new Result(r.getRoundnumber(), r.getPlayerID_white(), r.getPlayerID_black(), "")); //war davor auskommentiert als es lief
        }
        log.info("CreateResultTable finished");
    }

    private int GetPlayerIDFromNr(List<Player> listp, int nr) {
        for (Player p : listp) {
            if (p.getNr() == nr)
                return p.getID();
        }
        return 0;
    }

    private Result GetResultLastEvenPlayer(int countPlayer, int player1, int player2, int player1id, int player2id) {
        log.info("GetResultLastEvenPlayer started");
        // calculate round
        int round = 0;
        int temp = 0;
        if (player1 > player2)
            temp = player2 * 2;
        else
            temp = player1 * 2;
        if (temp <= countPlayer)
            round = temp - 1;
        else
            round = temp - countPlayer;

        // calculate colour (white or black) for player 1
        int lastPlayer = player1;
        int noLastPlayer = player2;
        int lastPlayerID = player1id;
        int noLastPlayerID = player2id;
        if (player1 < player2) {
            lastPlayer = player2;
            noLastPlayer = player1;
            lastPlayerID = player2id;
            noLastPlayerID = player1id;
        }
        if (noLastPlayer <= countPlayer / 2) {
            if (noLastPlayer == player1)
                return new Result(round, noLastPlayerID, lastPlayerID, "");

        } else {
            if (lastPlayer == player1)
                return new Result(round, lastPlayerID, noLastPlayerID, "");
        }
        return null;
    }

    private Result GetResult(int countPlayer, int player1, int player2, int player1id, int player2id) {
        log.info("GetResult started");
        // calculate round
        int round = 0;
        int sum = player1 + player2;
        int lastEvenNr = 0;
        if (countPlayer % 2 == 0)
            lastEvenNr = countPlayer;
        else
            lastEvenNr = countPlayer + 1;
        if (sum <= lastEvenNr)
            round = sum - 1;
        else
            round = sum - lastEvenNr;

        // determine colour for player1
        if (sum % 2 == 0) {
            if (player1 > player2)
                return new Result(round, player1id, player2id, "");
        } else {
            if (player1 < player2)
                return new Result(round, player1id, player2id, "");

        }
        return null;
    }


    public void openActivity_addnewplayer() {
        log.info("openActivity_addnewplayer started");
        Intent intent = new Intent(this, NewPlayerFormActivity.class);
        startActivity(intent);
    }


    private DatabaseHelper getdbHelper() {
        if (dbHelper == null) {
            dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            log.info("getdbHelper finished");
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
