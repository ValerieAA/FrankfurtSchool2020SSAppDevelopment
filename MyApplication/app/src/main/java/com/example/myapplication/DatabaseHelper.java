package com.example.myapplication;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import androidx.annotation.Nullable;

/**
 * Databasehelper necessary for ORMlite
 * sources:
 * https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Use-With-Android
 * https://www.eforce21.com/grundlage-android-entwicklung-mit-ormlite/
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    //name of the database file for your application
    private static final String DATABASE_NAME = "players.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 3;

    private Dao<Player, Integer> playerDao = null;
    private RuntimeExceptionDao<Player, Integer> playerRuntimeDao = null;
    private Dao<Result, Integer> resultDao = null;
    private RuntimeExceptionDao<Result, Integer> resultRuntimeDao = null;


    public DatabaseHelper(@Nullable Context context) {
        // super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config); //R.raw diese Zeile auskommentieren und untere rein dann compilieren dann run aud DatabaseConfigUtil
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // super(context, DATABASE_NAME, null, 1); //evtl: super(context, DATABASE_NAME, null, 1, R.raw.ormlite_config);

    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Player.class);
            TableUtils.createTable(connectionSource, Result.class);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Player.class, true);
            TableUtils.dropTable(connectionSource, Result.class, true);

            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }


    }

    public Dao<Player, Integer> getPlayerDao() throws SQLException {
        if (playerDao == null) {
            playerDao = getDao(Player.class);
        }
        return playerDao;
    }

    public RuntimeExceptionDao<Player, Integer> getPlayerDataDao() {
        if (playerRuntimeDao == null) {
            playerRuntimeDao = getRuntimeExceptionDao(Player.class);
        }
        return playerRuntimeDao;
    }


    public Dao<Result, Integer> getResultDao() throws SQLException {
        if (resultDao == null) {
            resultDao = getDao(Result.class);
        }
        return resultDao;
    }

    public RuntimeExceptionDao<Result, Integer> getResultDataDao() {
        if (resultRuntimeDao == null) {
            resultRuntimeDao = getRuntimeExceptionDao(Result.class);
        }
        return resultRuntimeDao;
    }


    @Override
    public void close() {
        Log.i(DatabaseHelper.class.getName(), "close() started");
        super.close();
        playerDao = null;
        playerRuntimeDao = null;
        resultDao = null;
        resultRuntimeDao = null;

    }
}


