package com.pillhelper.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DAODose<ProfileDose> extends DAOBase {

    // Contacts table name
    public static final String TABLE_NAME = "EFdose";

    public static final String KEY = "_id";
    public static final String DOSE = "dose";
    public static final String DATE = "date";
    public static final String PROFIL_KEY = "profil_id";

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE + " DATE, " + DOSE + " REAL , " + PROFIL_KEY + " INTEGER);";

    public static final String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    private Profile mProfile = null;
    private Cursor mCursor = null;

    private long id;
    private Date mDate;
    private float mDose;
    private long mProfil_id;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return mDate;
    }

    public float getDose() {
        return mDose;
    }

    public long getProfilId() {
        return mProfil_id;
    }


    public DAODose(Context context) {
        super(context);
    }

    public void setProfil(Profile pProfile) {
    mProfile = pProfile;
    }

    /**
     * @param pDate    date of the dose measure
     * @param pDose  dose
     * @param pProfile profil associated with the measure
     */
    public void addDose(Date pDate, float pDose, Profile pProfile) {
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues value = new ContentValues();

    SimpleDateFormat dateFormat = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

    value.put(DAODose.DATE, dateFormat.format(pDate));
    value.put(DAODose.DOSE, pDose);
    value.put(DAODose.PROFIL_KEY, pProfile.getId());

    db.insert(DAODose.TABLE_NAME, null, value);
    db.close(); // Closing database connection
    }

    // Getting single value
    private ProfileDose getMeasure(long id) {
    SQLiteDatabase db = this.getReadableDatabase();

    mCursor = null;
    mCursor = db.query(TABLE_NAME,
    new String[]{KEY, DATE, DOSE, PROFIL_KEY},
    KEY + "=?",
    new String[]{String.valueOf(id)},
    null, null, null, null);
    if (mCursor != null)
    mCursor.moveToFirst();

    Date date;
    try {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    date = dateFormat.parse(mCursor.getString(1));
    } catch (ParseException e) {
    e.printStackTrace();
    date = new Date();
    }

        com.pillhelper.DAO.ProfileDose value = new com.pillhelper.DAO.ProfileDose(mCursor.getLong(0),
            date,
            mCursor.getFloat(2),
            mCursor.getLong(3)
        );

        db.close();

    // return value
    return (ProfileDose) value;
    }

    // Getting single value
    public ProfileDose getLastMeasure() {
    SQLiteDatabase db = this.getReadableDatabase();

    mCursor = null;
    mCursor = db.query(TABLE_NAME,
    new String[]{KEY, DATE, DOSE, PROFIL_KEY},
    PROFIL_KEY + "=?",
    new String[]{String.valueOf(mProfile.getId())},
    null, null, DATE + " desc, " + KEY + " desc", null);

    if (mCursor != null)
    mCursor.moveToFirst();

    Date date;
    try {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    date = dateFormat.parse(mCursor.getString(1));
    } catch (ParseException e) {
    e.printStackTrace();
    date = new Date();
    }

        com.pillhelper.DAO.ProfileDose value = new com.pillhelper.DAO.ProfileDose(mCursor.getLong(0),
            date,
            mCursor.getFloat(2),
            mCursor.getLong(3)
        );

        db.close();

    // return value
    return (ProfileDose) value;
    }

    // Getting All Measures
    private List<ProfileDose> getMeasuresList(String pRequest) {
    List<ProfileDose> valueList = new ArrayList<>();
    // Select All Query

    SQLiteDatabase db = this.getReadableDatabase();
    mCursor = null;
    mCursor = db.rawQuery(pRequest, null);

    // looping through all rows and adding to list
    if (mCursor.moveToFirst()) {
    do {
    Date date;
    try {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    date = dateFormat.parse(mCursor.getString(1));
    } catch (ParseException e) {
    e.printStackTrace();
    date = new Date();
    }

    com.pillhelper.DAO.ProfileDose value = new com.pillhelper.DAO.ProfileDose(mCursor.getLong(0),
    date,
    mCursor.getFloat(2),
    mCursor.getLong(3)
    );

    // Adding value to list
    valueList.add((ProfileDose) value);
    } while (mCursor.moveToNext());
    }

    // return value list
    return valueList;
    }

    public Cursor GetCursor() {
    return mCursor;
    }

    // Getting All Measures
    public List<ProfileDose> getDoseList(Profile pProfile) {
    // Select All Query
    String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + PROFIL_KEY + "=" + pProfile.getId() + " GROUP BY " + DATE + " ORDER BY date(" + DATE + ") DESC";

    // return value list
    return getMeasuresList(selectQuery);
    }

    // Updating single value
    public int updateMeasure(ProfileDose m) {
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues value = new ContentValues();
    value.put(com.pillhelper.DAO.ProfileDose.DATE, getDate().toString());
    value.put(com.pillhelper.DAO.ProfileDose.DOSE, getDose());
    value.put(com.pillhelper.DAO.ProfileDose.PROFIL_KEY, getProfilId());

    // updating row
    return db.update(TABLE_NAME, value, KEY + " = ?",
    new String[]{String.valueOf(getId())});
    }

    // Deleting single Measure
    public void deleteMeasure(ProfileDose m) {
    deleteMeasure(getId());
    }

    // Deleting single Measure
    public void deleteMeasure(long id) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(TABLE_NAME, KEY + " = ?",
    new String[]{String.valueOf(id)});
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

    public <ProfileDose> List<ProfileDose> getAllRecords() {
    String selectQuery = "SELECT * FROM " + TABLE_NAME;
    return (List<ProfileDose>) getMeasuresList(selectQuery);
    }

    public void populate() {
    Date date = new Date();
    int dose = 10;

    for (int i = 1; i <= 5; i++) {
    date.setTime(date.getTime() + i * 1000 * 60 * 60 * 24 * 2);
    addDose(date, (float) i, mProfile);
    }
    }
    }



