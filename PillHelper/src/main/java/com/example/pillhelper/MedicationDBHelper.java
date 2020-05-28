package com.example.pillhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class MedicationDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "medicationlist.db";
    public static final int DATABASE_VERSION = 1;

    public MedicationDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MEDICATIONLIST_TABLE ="CREATE TABLE" +
                MedicationContract.MedicationEntry.TABLE_NAME + " (" +
                MedicationContract.MedicationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MedicationContract.MedicationEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MedicationContract.MedicationEntry.COLUMN_AMOUNT + " INTEGER NOT NULL" +
                MedicationContract.MedicationEntry.COLUMN_TIMESTAMP + "TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

                db.execSQL(SQL_CREATE_MEDICATIONLIST_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        SQLiteDatabase db = null;
        db.execSQL("DROP TABLE IF EXISTS " + MedicationContract.MedicationEntry.TABLE_NAME);

    }
}
