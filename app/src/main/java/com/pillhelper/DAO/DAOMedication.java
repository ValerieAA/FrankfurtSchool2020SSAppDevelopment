package com.pillhelper.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DAOMedication extends DAOBase {

    // Contacts table name
    public static final String TABLE_NAME = "EFmedication";

    public static final String KEY = "_id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String TYPE = "type";
    public static final String PICTURE = "picture";
    public static final String MEDICATIONPARTS = "medicationparts";
    public static final String FAVORITES = "favorites"; // DEPRECATED - Specific DataBase created for this.


    public static final int TYPE_FONTE = 0;
    public static final int TYPE_LONGTERM = 1;

    public static final String TABLE_CREATE_5 = "CREATE TABLE " + TABLE_NAME
        + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME
        + " TEXT, " + DESCRIPTION + " TEXT, " + TYPE + " INTEGER);";

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
        + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME
        + " TEXT, " + DESCRIPTION + " TEXT, " + TYPE + " INTEGER, " + MEDICATIONPARTS + " TEXT, " + PICTURE + " TEXT, " + FAVORITES + " INTEGER);"; //", " + PICTURE + " INTEGER);";

    public static final String TABLE_DROP = "DROP TABLE IF EXISTS "
        + TABLE_NAME + ";";

    private Profile mProfile = null;
    private Cursor mCursor = null;

    public DAOMedication(Context context) {
        super(context);
    }

/*
    public void setProfile(Profile pProfile) {
        mProfile = pProfile;
    }
*/

    /**
     * @param pName
     * @param pDescription
     * @param pType
     */
    public long addMedication(String pName, String pDescription, int pType, String pPicture, boolean pFav) {
        long new_id = -1;

        ContentValues value = new ContentValues();

        value.put(DAOMedication.NAME, pName);
        value.put(DAOMedication.DESCRIPTION, pDescription);
        value.put(DAOMedication.TYPE, pType);
        value.put(DAOMedication.PICTURE, pPicture);
        value.put(DAOMedication.FAVORITES, pFav);

        SQLiteDatabase db = this.getWritableDatabase();
        new_id = db.insert(DAOMedication.TABLE_NAME, null, value);
        close();

        return new_id;
    }

    // Getting single value
    public Medication getMedication(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;
        mCursor = db.query(TABLE_NAME, new String[]{KEY, NAME, DESCRIPTION, TYPE, MEDICATIONPARTS, PICTURE, FAVORITES}, KEY + "=?",
            new String[]{String.valueOf(id)}, null, null, null, null);
        if (mCursor != null)
            mCursor.moveToFirst();

        if (mCursor.getCount() == 0)
            return null;

        Medication value = new Medication(mCursor.getString(1), mCursor.getString(2), mCursor.getInt(3), mCursor.getString(4), mCursor.getString(5), mCursor.getInt(6) == 1);

        value.setId(mCursor.getLong(0));
        // return value
        mCursor.close();
        close();
        return value;
    }

    // Getting single value
    public Medication getMedication(String pName) {
        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;
        mCursor = db.query(TABLE_NAME, new String[]{KEY, NAME, DESCRIPTION, TYPE, MEDICATIONPARTS, PICTURE, FAVORITES}, NAME + "=?",
            new String[]{pName}, null, null, null, null);
        if (mCursor != null)
            mCursor.moveToFirst();

        if (mCursor.getCount() == 0)
            return null;

        Medication value = new Medication(mCursor.getString(1),
            mCursor.getString(2),
            mCursor.getInt(3),
            mCursor.getString(4),
            mCursor.getString(5),
            mCursor.getInt(6) == 1);

        value.setId(mCursor.getLong(0));
        // return value
        mCursor.close();
        close();
        return value;
    }

    public boolean medicationExists(String name) {
        Medication lMed = getMedication(name);
        return lMed != null;
    }

    // Getting All Records
    private ArrayList<Medication> getMedicationList(String pRequest) {
        ArrayList<Medication> valueList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Select All Query

        mCursor = null;
        mCursor = db.rawQuery(pRequest, null);

        // looping through all rows and adding to list
        if (mCursor.moveToFirst()) {
            do {
                Medication value = new Medication(mCursor.getString(1),
                    mCursor.getString(2), mCursor.getInt(3), mCursor.getString(4), mCursor.getString(5), mCursor.getInt(6) == 1);

                value.setId(mCursor.getLong(0));

                // Adding value to list
                valueList.add(value);
            } while (mCursor.moveToNext());
        }
        // return value list
        return valueList;
    }

    // Getting All Records
    private Cursor getMedicationListCursor(String pRequest) {
        ArrayList<Medication> valueList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Select All Query

        return db.rawQuery(pRequest, null);
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void closeCursor() {
        mCursor.close();
    }

    /**
     * @return List of Medication object ordered by Favorite and Name
     */
    public Cursor getAllMedication() {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY "
            + FAVORITES + " DESC," + NAME + " COLLATE NOCASE ASC";

        // return value list
        return getMedicationListCursor(selectQuery);
    }

    /**
     * @return List of Medication object ordered by Favorite and Name
     */
    public Cursor getAllMedication(int type) {
        // Select All Query
        String selectQuery = "";
        selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + TYPE + "=" + type + " ORDER BY "
            + FAVORITES + " DESC," + NAME + " COLLATE NOCASE ASC";

        // return value list
        return getMedicationListCursor(selectQuery);
    }

    /**
     * @return List of Medication object ordered by Favorite and Name
     */
    public Cursor getFilteredMedication(CharSequence filterString) {
        // Select All Query
        // like '%"+inputText+"%'";
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + NAME + " LIKE " + "'%" + filterString + "%' " + " ORDER BY "
            + FAVORITES + " DESC," + NAME + " ASC";
        // return value list
        return getMedicationListCursor(selectQuery);
    }


    /**
     * @return List of Medication object ordered by Favorite and Name
     */
    public void deleteAllEmptyExercises() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, NAME + " = ?",
            new String[]{""});
        db.close();
    }

    /**
     * @return List of Medication object ordered by Favorite and Name
     */
    public ArrayList<Medication> getAllMedicationArray() {
// Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY "
            + FAVORITES + " DESC," + NAME + " COLLATE NOCASE ASC";

        // return value list
        return getMedicationList(selectQuery);
    }

    /**
     * @param idList List of Medication IDs to be return
     * @return List of Medication object ordered by Favorite and Name
     */
    public List<Medication> getAllMedication(List<Long> idList) {

        String ids = idList.toString();
        ids = ids.replace('[', '(');
        ids = ids.replace(']', ')');

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY + " in " + ids + " ORDER BY "
            + FAVORITES + " DESC," + NAME + " COLLATE NOCASE ASC";

        // return value list
        return getMedicationList(selectQuery);
    }

    // Getting All Medication
    public String[] getAllMedicationName() {
        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;

        // Select All Medication
        String selectQuery = "SELECT DISTINCT  " + NAME + " FROM "
            + TABLE_NAME + " ORDER BY " + NAME + " COLLATE NOCASE ASC";
        mCursor = db.rawQuery(selectQuery, null);

        int size = mCursor.getCount();

        String[] valueList = new String[size];

        // looping through all rows and adding to list
        if (mCursor.moveToFirst()) {
            int i = 0;
            do {
                String value = mCursor.getString(0);
                valueList[i] = value;
                i++;
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        close();
        // return value list
        return valueList;
    }

    // Updating single value
    public int updateMedication(Medication m) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(DAOMedication.NAME, m.getName());
        value.put(DAOMedication.DESCRIPTION, m.getDescription());
        value.put(DAOMedication.TYPE, m.getType());
        value.put(DAOMedication.MEDICATIONPARTS, m.getMedicationParts());
        value.put(DAOMedication.PICTURE, m.getPicture());
        if (m.getFavorite()) value.put(DAOMedication.FAVORITES, 1);
        else value.put(DAOMedication.FAVORITES, 0);

        // updating row
        return db.update(TABLE_NAME, value, KEY + " = ?",
            new String[]{String.valueOf(m.getId())});
    }

    // Deleting single Record
    public void delete(Medication m) {
        if (m != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME, KEY + " = ?",
                new String[]{String.valueOf(m.getId())});
            db.close();
        }
    }

    // Deleting single Record
    public void delete(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Getting Profils Count
    public int getCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        open();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int value = cursor.getCount();

        cursor.close();
        close();

        // return count
        return value;
    }

    public void populate() {

        addMedication("Dev Couche", "Developper couche : blabla ", TYPE_FONTE, "", true);
        addMedication("Resp", "Developper couche : blabla ", TYPE_FONTE, "", false);
    }

    public void deleteAllEmptyMedication() {
    }
}
