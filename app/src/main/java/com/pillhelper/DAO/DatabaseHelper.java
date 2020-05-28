package com.pillhelper.DAO;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.pillhelper.DAO.medicationdosemeasures.MedicationDose;
import com.pillhelper.DAO.medicationdosemeasures.DAOMedicationMeasure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 17;
    public static final String OLD09_DATABASE_NAME = "pillheper";
    public static final String DATABASE_NAME = "pillhelper.db";
    private static DatabaseHelper sInstance;
    private Context mContext = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public static void renameOldDatabase(Activity activity) {
        File oldDatabaseFile = activity.getDatabasePath(OLD09_DATABASE_NAME);
        if (oldDatabaseFile.exists()) {
            File newDatabaseFile = new File(oldDatabaseFile.getParentFile(), DATABASE_NAME);
            oldDatabaseFile.renameTo(newDatabaseFile);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DAORecord.TABLE_CREATE); // Covers Fonte and LongTermPrescription
        db.execSQL(DAOProfil.TABLE_CREATE);
        db.execSQL(DAODose.TABLE_CREATE);
        db.execSQL(DAOMedication.TABLE_CREATE);
        db.execSQL(DAOMedicationMeasure.TABLE_CREATE);
    }

    @Override
    public void onUpgrade(
        final SQLiteDatabase db, final int oldVersion,
        final int newVersion) {
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 1:
                    db.execSQL(DAOLongTerm.TABLE_CREATE);
                    break;
                case 2:
                    db.execSQL(DAOLongTerm.TABLE_CREATE);
                    break;
                case 3:
                    db.execSQL(DAOLongTerm.TABLE_DROP);
                    db.execSQL(DAOLongTerm.TABLE_CREATE);
                    break;
                case 4: // Pillhelper 0.7
                    db.execSQL("ALTER TABLE " + DAOFonte.TABLE_NAME + " ADD COLUMN " + DAOFonte.NOTES + " TEXT");
                    db.execSQL("ALTER TABLE " + DAOFonte.TABLE_NAME + " ADD COLUMN " + DAOFonte.UNIT + " INTEGER DEFAULT 0");
                    break;
              //  case 5:
               //     db.execSQL(DAOMedication.TABLE_CREATE_5);
                 //   db.execSQL("ALTER TABLE " + DAOFonte.TABLE_NAME + " ADD COLUMN " + DAOFonte.MEDICATION_KEY + " INTEGER");
                 //   break;
                case 5: // Pillhelper 0.8
                    if (!isFieldExist(db, DAOMedication.TABLE_NAME, DAOMedication.MEDICATIONPARTS))
                        db.execSQL("ALTER TABLE " + DAOMedication.TABLE_NAME + " ADD COLUMN " + DAOMedication.MEDICATIONPARTS + " TEXT");
                    break;
                case 7: // Pillhelper 0.10
                    db.execSQL("ALTER TABLE " + DAOMedication.TABLE_NAME + " ADD COLUMN " + DAOMedication.PICTURE + " TEXT");
                    break;
                case 8: // Pillhelper 0.12
                    db.execSQL("ALTER TABLE " + DAOFonte.TABLE_NAME + " ADD COLUMN " + DAOFonte.TIME + " TEXT");
                    break;
                case 9:
                    db.execSQL(DAOMedicationMeasure.TABLE_CREATE);
                    break;
                case 10:
                    db.execSQL("ALTER TABLE " + DAOMedication.TABLE_NAME + " ADD COLUMN " + DAOMedication.FAVORITES + " INTEGER");
                    break;
                case 11:
                    db.execSQL("ALTER TABLE " + DAOFonte.TABLE_NAME + " RENAME TO tmp_table_name");

                    db.execSQL(DAOFonte.TABLE_CREATE);

                    db.execSQL("INSERT INTO " + DAOFonte.TABLE_NAME + " SELECT * FROM tmp_table_name");

                    break;
                case 12:
                    // Delete old table table
                    db.execSQL("DROP TABLE IF EXISTS tmp_table_name");
                    break;
                case 13:
                    // Update profile database
                    db.execSQL("ALTER TABLE " + DAOProfil.TABLE_NAME + " ADD COLUMN " + DAOProfil.SIZE + " INTEGER");
                    db.execSQL("ALTER TABLE " + DAOProfil.TABLE_NAME + " ADD COLUMN " + DAOProfil.BIRTHDAY + " DATE");
                    break;
                case 14:
                    db.execSQL("ALTER TABLE " + DAOProfil.TABLE_NAME + " ADD COLUMN " + DAOProfil.PHOTO + " TEXT");
                    break;
                case 15:
                    // Merge of LongTermPrescription DB and Fonte DB
                    db.execSQL("ALTER TABLE " + DAORecord.TABLE_NAME + " ADD COLUMN " + DAORecord.DISTANCE + " REAL");
                    db.execSQL("ALTER TABLE " + DAORecord.TABLE_NAME + " ADD COLUMN " + DAORecord.DURATION + " INTEGER");
                    db.execSQL("ALTER TABLE " + DAORecord.TABLE_NAME + " ADD COLUMN " + DAORecord.TYPE + " INTEGER DEFAULT " + DAOMedication.TYPE_FONTE);
                    break;
                case 16:
                    // Merge of LongTermPrescription DB and Fonte DB
                    db.execSQL("ALTER TABLE " + DAOMedicationMeasure.TABLE_NAME + " ADD COLUMN " + DAOMedicationMeasure.UNIT + " INTEGER");
                    migrateMedicationTable(db, mContext);
                    break;
                case 17:
                    db.execSQL("ALTER TABLE " + DAOProfil.TABLE_NAME + " ADD COLUMN " + DAOProfil.GENDER + " INTEGER");
                    break;
            }
            upgradeTo++;
        }
    }

    @Override
    public void onDowngrade(
        final SQLiteDatabase db, final int oldVersion,
        final int newVersion) {
        int upgradeTo = oldVersion - 1;
        while (upgradeTo >= newVersion) {
            switch (upgradeTo) {
                case 2:
                    // Ne fonctionne pas pour ces versions
                    break;
                case 3:
                    // Ne fonctionne pas pour ces versions
                    //db.execSQL("ALTER TABLE "+ DAOFonte.TABLE_NAME + " DROP COLUMN " + DAOFonte.NOTES);
                    //db.execSQL("ALTER TABLE "+ DAOFonte.TABLE_NAME + " DROP COLUMN " + DAOFonte.UNIT);
                    break;
                case 4:
                    //db.execSQL(DAOMedication.TABLE_DROP);
                    //db.execSQL("ALTER TABLE "+ DAOFonte.TABLE_NAME + " DROP COLUMN " + DAOFonte.MEDICATION_KEY );
                    break;
                case 5:
                    //db.execSQL("ALTER TABLE "+ DAOMedication.TABLE_NAME + " DROP COLUMN " + DAOMedication.BODYPARTS );
                    break;
            }
            upgradeTo--;
        }
    }

    // This method will return if your table exist a field or not
    public boolean isFieldExist(SQLiteDatabase db, String tableName, String fieldName) {
        boolean isExist = true;
        Cursor res;

        try {
            res = db.rawQuery("SELECT " + fieldName + " FROM " + tableName, null);
            res.close();
        } catch (SQLiteException e) {
            isExist = false;
        }

        return isExist;
    }

    public boolean tableExists(SQLiteDatabase db, String tableName) {
        boolean isExist = true;
        Cursor res;

        try {
            res = db.rawQuery("SELECT * FROM " + tableName, null);
            res.close();
        } catch (SQLiteException e) {
            isExist = false;
        }
        return isExist;
    }

    private void migrateMedicationTable(SQLiteDatabase db, Context context) {
        List<ProfileDose> valueList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + DAODose.TABLE_NAME;
        //SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = null;
        mCursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (mCursor.moveToFirst()) {
            do {
                ContentValues value = new ContentValues();

                value.put(DAOMedicationMeasure.DATE, mCursor.getString(mCursor.getColumnIndex(DAODose.DATE)));
                value.put(DAOMedicationMeasure.MEDICATIONPART_KEY, MedicationDose.DOSE);
                value.put(DAOMedicationMeasure.MEASURE, mCursor.getFloat(mCursor.getColumnIndex(DAODose.DOSE)));
                value.put(DAOMedicationMeasure.PROFIL_KEY, mCursor.getLong(mCursor.getColumnIndex(DAODose.PROFIL_KEY)));

                db.insert(DAOMedicationMeasure.TABLE_NAME, null, value);

            } while (mCursor.moveToNext());
            mCursor.close();
            //db.close(); // Closing database connection
        }

    }
}
