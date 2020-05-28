package com.example.pillhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME="User.db";
    public static final String TABLE_NAME="User_table.db";
    public static final String COL_1="USERID";
    public static final String COL_2="USERNAME";
    public static final String COL_3="EMAIL";
    public static final String COL_4="NAME";
    public static final String COL_5="SURNAME";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(USERID INTEGER PRIMARY KEY AUTOINCREMENT, USERNAME TEXT, EMAIL TEXT, NAME TEXT, SURNAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS"  );
        onCreate(db);

    }
}
