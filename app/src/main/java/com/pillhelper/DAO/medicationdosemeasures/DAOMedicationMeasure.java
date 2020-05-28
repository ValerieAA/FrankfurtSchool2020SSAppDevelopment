package com.pillhelper.DAO.medicationdosemeasures;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pillhelper.DAO.DAOBase;
import com.pillhelper.DAO.DAOUtils;
import com.pillhelper.DAO.Profile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DAOMedicationMeasure extends DAOBase {

    // Contacts table name
    public static final String TABLE_NAME = "EF";

    public static final String KEY = "_id";
    public static final String MEDICATIONPART_KEY = "medicationpart_id";
    public static final String MEASURE = "mesure";
    public static final String DATE = "date";
    public static final String UNIT = "unit";
    public static final String PROFIL_KEY = "profil_id";

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE + " DATE, " + MEDICATIONPART_KEY + " INTEGER, " + MEASURE + " REAL , " + PROFIL_KEY + " INTEGER, " + UNIT + " INTEGER);";

    public static final String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    private Profile mProfile = null;
    private Cursor mCursor = null;

    public DAOMedicationMeasure(Context context) {
        super(context);
    }

    public void setProfil(Profile pProfile) {
        mProfile = pProfile;
    }

    /**
     * @param pDate           date of the dose measure
     * @param pmedicationmeasure_id id of the medication part
     * @param pMeasure        medication measure
     * @param pProfileID      profil associated with the measure
     */
    public void addMedicationMeasure(Date pDate, int pmedicationmeasure_id, float pMeasure, long pProfileID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        value.put(DAOMedicationMeasure.DATE, dateFormat.format(pDate));
        value.put(DAOMedicationMeasure.MEDICATIONPART_KEY, pmedicationmeasure_id);
        value.put(DAOMedicationMeasure.MEASURE, pMeasure);
        value.put(DAOMedicationMeasure.PROFIL_KEY, pProfileID);

        db.insert(DAOMedicationMeasure.TABLE_NAME, null, value);
        db.close(); // Closing database connection
    }

    // Getting single value
    private MedicationDoseMeasure getMeasure(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        mCursor = null;
        mCursor = db.query(TABLE_NAME,
            new String[]{KEY, DATE, MEDICATIONPART_KEY, MEASURE, PROFIL_KEY},
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

        MedicationDoseMeasure value = new MedicationDoseMeasure(mCursor.getLong(0),
            date,
            mCursor.getInt(2),
            mCursor.getFloat(3),
            mCursor.getLong(4)
        );

        db.close();

        // return value
        return value;
    }

    // Getting All Measures
    private List<MedicationDoseMeasure> getMeasuresList(String pRequest) {
        List<MedicationDoseMeasure> valueList = new ArrayList<>();
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

                MedicationDoseMeasure value = new MedicationDoseMeasure(mCursor.getLong(0),
                    date,
                    mCursor.getInt(2),
                    mCursor.getFloat(3),
                    mCursor.getLong(4)
                );

                // Adding value to list
                valueList.add(value);
            } while (mCursor.moveToNext());
        }

        // return value list
        return valueList;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * Getting All Measures associated to a Medication part for a specific Profile
     *
     * @param pMedicationPartID
     * @param pProfile
     * @return List<MedicationDoseMeasure>
     */
    public List<MedicationDoseMeasure> getMedicationPartMeasuresList(long pMedicationPartID, Profile pProfile) {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + MEDICATIONPART_KEY + "=" + pMedicationPartID + " AND " + PROFIL_KEY + "=" + pProfile.getId() + " GROUP BY " + DATE + " ORDER BY date(" + DATE + ") DESC";

        // return value list
        return getMeasuresList(selectQuery);
    }

    /**
     * Getting All Measures for a specific Profile
     *
     *
     * @param mMedicationPartID
     * @param pProfile
     * @return List<MedicationDoseMeasure>
     */
    public List<MedicationDoseMeasure> getMedicationMeasuresList(int mMedicationPartID, Profile pProfile) {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + PROFIL_KEY + "=" + pProfile.getId() + " ORDER BY date(" + DATE + ") DESC";

        // return value list
        return getMeasuresList(selectQuery);
    }

    /**
     * Getting All Measures associated to a Medication part for a specific Profile
     *
     * @param pMedicationPartID
     * @param pProfile
     * @return List<MedicationDoseMeasure>
     */
    public MedicationDoseMeasure getLastMedicationMeasures(long pMedicationPartID, Profile pProfile) {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + MEDICATIONPART_KEY + "=" + pMedicationPartID + " AND " + PROFIL_KEY + "=" + pProfile.getId() + " GROUP BY " + DATE + " ORDER BY date(" + DATE + ") DESC";

        List<MedicationDoseMeasure> array = getMeasuresList(selectQuery);
        if (array.size() <= 0) {
            return null;
        }

        // return value list
        return getMeasuresList(selectQuery).get(0);
    }

    // Updating single value
    public int updateMeasure(MedicationDoseMeasure m) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateString = dateFormat.format(m.getDate());
        value.put(DAOMedicationMeasure.DATE, dateString);
        value.put(DAOMedicationMeasure.MEDICATIONPART_KEY, m.getMedicationPartID());
        value.put(DAOMedicationMeasure.MEASURE, m.getMedicationMeasure());
        value.put(DAOMedicationMeasure.PROFIL_KEY, m.getProfileID());

        // updating row
        return db.update(TABLE_NAME, value, KEY + " = ?",
            new String[]{String.valueOf(m.getId())});
    }

    // Deleting single Measure
    public void deleteMeasure(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY + " = ?",
            new String[]{String.valueOf(id)});
    }

    // Getting Profils Count
    public int getCount() {
        String countQuery = "SELECT * FROM " + TABLE_NAME;
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
        Date date = new Date();
        int poids = 10;

        for (int i = 1; i <= 5; i++) {
            date.setTime(date.getTime() + i * 1000 * 60 * 60 * 24 * 2);
            //addMedicationMeasure(date, (float) i, mProfile);
        }
    }

    public <MedicationMeasure> void getMedcationMeasuresList(Profile pProfile) {
    }

    public List<MedicationDoseMeasure> getMedicationMeasuresList(Profile profil) {
        return null;
    }
}


