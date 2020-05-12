package com.example.myapplication;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;

/**
 * This Class is used for calculations connected with the database and other database operations.
 */

public class DatabaseCalculationHelper extends AppCompatActivity {

    private DatabaseHelper dbHelper = null;

    public void getAndSetDataForPlayer(ListView mListView) {
        /// read the table from database
        List<Player> ausdbliste = doPlayerDataStuff(false);
        final ArrayList<Player> playerList = new ArrayList<Player>(ausdbliste);
        final PlayerListAdapter adapter = new PlayerListAdapter(getBaseContext(), R.layout.content_addplayers, playerList);
        mListView.setAdapter(adapter);
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


        Log.d("demo", playerList.toString());

        return playerList;
    }


    private List<Result> getResultsForPlayer(int playerId, boolean blackPlayer) {

        RuntimeExceptionDao<Result, Integer> resultDao = getdbHelper().getResultDataDao();

        List<Result> resultList = Collections.EMPTY_LIST;


        //query
        try {

            if (!blackPlayer)
                resultList = getdbHelper().getResultDao().queryForEq("playerID_white", playerId);
            else resultList = getdbHelper().getResultDao().queryForEq("playerID_black", playerId);

        } catch (SQLException e) {
            e.printStackTrace();
        }


        Log.d("demo", resultList.toString());

        return resultList;
    }


    private DatabaseHelper getdbHelper() {
        if (dbHelper == null) {
            dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            Log.i("DatabaseCalcHelper", "getdbHelper finished");
        }
        return dbHelper;
    }


    public List<Player> getPlayersWithResult() {
        List<Player> players = doPlayerDataStuff(true);
        //calculate points
        for (Player p : players) {
            List<Result> resultsWhite = getResultsForPlayer(p.getID(), false);
            for (Result r : resultsWhite) {
                switch (r.getResult()) {
                    case "1-0":
                        p.setPoints(p.getPoints() + 1);
                        break;
                    case "1/2":
                        p.setPoints(p.getPoints() + 0.5f);
                        break;
                    default:
                        break;

                }
            }

            List<Result> resultsBlack = getResultsForPlayer(p.getID(), true);
            for (Result r : resultsBlack) {
                switch (r.getResult()) {
                    case "0-1":
                        p.setPoints(p.getPoints() + 1);
                        break;
                    case "1/2":
                        p.setPoints(p.getPoints() + 0.5f);
                        break;
                    default:
                        break;

                }
            }
        }


        /*
         * Tie-breaking for Round Robin Tournaments
         * Tie-break systems are used in chess round robin tournaments to break ties
         * between players who have the same total number of points (after the last round).
         * The usual Sonneborn-Berger system is used here
         * How it works:https://de.wikipedia.org/wiki/Feinwertung#Olympiade-Sonneborn-Berger-Wertung
         */
        for (Player p : players) {
            List<Result> resultsWhite = getResultsForPlayer(p.getID(), false);
            for (Result r : resultsWhite) {
                Player black = getPlayerById(players, r.getPlayerID_black());
                switch (r.getResult()) {
                    case "1-0":
                        p.setSoBe(p.getSoBe() + black.getPoints());
                        break;
                    case "1/2":
                        p.setSoBe(p.getSoBe() + (black.getPoints() / 2));
                        break;
                    default:
                        break;

                }
            }

            List<Result> resultsBlack = getResultsForPlayer(p.getID(), true);
            for (Result r : resultsBlack) {
                Player white = getPlayerById(players, r.getPlayerID_white());
                switch (r.getResult()) {
                    case "0-1":
                        p.setSoBe(p.getSoBe() + white.getPoints());
                        break;
                    case "1/2":
                        p.setSoBe(p.getSoBe() + (white.getPoints() / 2));
                        break;
                    default:
                        break;

                }
            }
        }

        //sort by rank
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                if (p1.getPoints() == p2.getPoints()) {
                    if (p1.getSoBe() > p2.getSoBe())
                        return -1;
                    else if (p1.getSoBe() < p2.getSoBe())
                        return 1;
                    else
                        return 0; //both have the same SoBe rating and therefore the same rank
                } else if (p1.getPoints() > p2.getPoints()) {
                    return -1;
                } else return 1;
            }
        });


        //assign rank
        int rank = 1;
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (i == 0) {
                p.setRank(rank);
            } else {
                Player previous = players.get(i - 1);
                if (p.getPoints() == previous.getPoints() && p.getSoBe() == previous.getSoBe())
                    p.setRank(rank);
                else {
                    rank++;
                    p.setRank(rank);
                }

            }
        }

        return players;
    }

    private Player getPlayerById(List<Player> players, int id) {
        for (Player p : players) {
            if (p.getID() == id)
                return p;
        }
        return null;
    }


    public void resultDeleteAll() {


        RuntimeExceptionDao<Result, Integer> resultDao = getdbHelper().getResultDataDao();

        //delete all data from the Result-table
        DeleteBuilder<Result, Integer> deleteBuilder = resultDao.deleteBuilder();

        //query
        try {
            resultDao.deleteBuilder().where().isNotNull("id");
            deleteBuilder.delete();
            Log.i("DatabaseCalcHelper", "Result Table deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentRoundnr() {
        RuntimeExceptionDao<Result, Integer> resultDao = getdbHelper().getResultDataDao();

        List<Result> resultList = Collections.EMPTY_LIST;


        //query
        try {
            resultList = getdbHelper().getResultDao().queryForEq("result", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (resultList.size() > 0) {
            //order by round number ascending
            Collections.sort(resultList, new Comparator<Result>() {
                @Override
                public int compare(Result r1, Result r2) {
                    if (r1.getRoundnumber() == r2.getRoundnumber()) {
                        return 0;
                    } else if (r1.getRoundnumber() < r2.getRoundnumber())
                        return -1;
                    else
                        return 1;
                }
            });
        } else {
            resultList = Collections.EMPTY_LIST;
            //query
            try {
                resultList = getdbHelper().getResultDao().queryForAll();
                /////////////////
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //order by round number desc
            Collections.sort(resultList, new Comparator<Result>() {
                @Override
                public int compare(Result r1, Result r2) {
                    if (r1.getRoundnumber() == r2.getRoundnumber()) {
                        return 0;
                    } else if (r1.getRoundnumber() < r2.getRoundnumber())
                        return 1;
                    else
                        return -1;
                }
            });
        }
        Result result = resultList.get(0);
        int roundnr = (result.getRoundnumber());

        Log.d("DatabaseCalcHelper", resultList.toString());

        return roundnr;
    }


}
