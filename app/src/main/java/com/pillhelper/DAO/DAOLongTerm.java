package com.pillhelper.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.pillhelper.DateGraphData;
import com.pillhelper.R;
import com.pillhelper.utils.DateConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DAOLongTerm extends DAORecord {

    public static final int DISTANCE_FCT = 0;
    public static final int DURATION_FCT = 1;
    public static final int SPEED_FCT = 2;
    public static final int MAXDURATION_FCT = 3;
    public static final int MAXDISTANCE_FCT = 4;
    public static final int NBSERIE_FCT = 5;

    private static final String OLD_TABLE_NAME = "EFLongTerm";

    private static final String TABLE_ARCHI = KEY + "," + DATE + "," + MEDICATION + "," + DISTANCE + "," + DURATION + "," + PROFIL_KEY + "," + TIME;

    public DAOLongTerm(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * @param pDate
     * @param pTime
     * @param pMedication
     * @param pDistance
     * @param pDuration
     * @param pProfile
     * @return
     */
    public long addLongTermRecord(Date pDate, String pTime, String pMedication, float pDistance, long pDuration, Profile pProfile) {
        return addRecord(pDate, pMedication, DAOMedication.TYPE_LONGTERM, 0, 0, 0, pProfile, 0, "", pTime, pDistance, pDuration);
    }

    // Getting single value
    public LongTermPrescription getRecord(long id) {
        String selectQuery = "SELECT  " + TABLE_ARCHI + " FROM " + TABLE_NAME
            + " WHERE " + KEY + "=" + id;
        List<LongTermPrescription> valueList;

        valueList = getRecordsList(selectQuery);
        if (valueList.isEmpty())
            return null;
        else
            return valueList.get(0);
    }

    // Getting All Records
    private List<LongTermPrescription> getRecordsList(String pRequest) {
        List<LongTermPrescription> valueList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Select All Query

        mCursor = null;
        mCursor = db.rawQuery(pRequest, null);

        // looping through all rows and adding to list
        if (mCursor.moveToFirst()) {
            do {
                //Get Date
                Date date;
                try {
                    date = new SimpleDateFormat(DAOUtils.DATE_FORMAT).parse(mCursor.getString(mCursor.getColumnIndex(DAOLongTerm.DATE)));
                } catch (ParseException e) {
                    e.printStackTrace();
                    date = new Date();
                }

                //Get Profile
                DAOProfil lDAOProfil = new DAOProfil(mContext);
                Profile lProfile = lDAOProfil.getProfil(mCursor.getLong(mCursor.getColumnIndex(DAOLongTerm.PROFIL_KEY)));

                LongTermPrescription value = new LongTermPrescription(date,
                    mCursor.getString(mCursor.getColumnIndex(DAOLongTerm.MEDICATION)),
                    mCursor.getFloat(mCursor.getColumnIndex(DAOLongTerm.DISTANCE)),
                    mCursor.getLong(mCursor.getColumnIndex(DAOLongTerm.DURATION)),
                    lProfile);

                value.setId(Long.parseLong(mCursor.getString(mCursor.getColumnIndex(DAOLongTerm.KEY))));

                // Adding value to list
                valueList.add(value);
            } while (mCursor.moveToNext());
        }
        // return value list
        return valueList;
    }

    // Getting All Records
    public List<LongTermPrescription> getAllRecords() {
        // Select All Query
        String selectQuery = "SELECT " + TABLE_ARCHI + " FROM " + TABLE_NAME
            + " ORDER BY " + KEY + " DESC";

        // return value list
        return getRecordsList(selectQuery);
    }

    // Getting All Records
    public List<LongTermPrescription> getAllCardioRecordsByProfile(Profile pProfile) {
        // Select All Query
        String selectQuery = "SELECT " + TABLE_ARCHI + " FROM " + TABLE_NAME
            + " WHERE " + PROFIL_KEY + "=" + pProfile.getId()
            + " AND " + TYPE + "=" + DAOMedication.TYPE_LONGTERM
            + " ORDER BY " + KEY + " DESC";

        // return value list
        return getRecordsList(selectQuery);
    }

    // Getting Top 10 Records
    public List<LongTermPrescription> getTop10Records(Profile pProfile) {
        // Select All Query
        String selectQuery = "SELECT TOP 10 * FROM " + TABLE_NAME
            + " WHERE " + PROFIL_KEY + "=" + pProfile.getId()
            + " AND " + TYPE + "=" + DAOMedication.TYPE_LONGTERM
            + " ORDER BY " + KEY + " DESC";

        // return value list
        return getRecordsList(selectQuery);
    }

    // Getting Function records
    public List<DateGraphData> getFunctionRecords(Profile pProfile, String pMedication,
                                                  int pFunction) {

        boolean lfilterMedication = true;
        boolean lfilterFunction = true;
        String selectQuery = null;

        if (pMedication == null || pMedication.isEmpty() || pMedication.equals(mContext.getResources().getText(R.string.all).toString())) {
            lfilterMedication = false;
        }

        if (pFunction == DAOLongTerm.DISTANCE_FCT) {
            selectQuery = "SELECT SUM(" + DISTANCE + "), " + DATE + " FROM " + TABLE_NAME
                + " WHERE " + MEDICATION + "=\"" + pMedication + "\""
                + " AND " + PROFIL_KEY + "=" + pProfile.getId()
                + " GROUP BY " + DATE
                + " ORDER BY date(" + DATE + ") ASC";
        } else if (pFunction == DAOLongTerm.DURATION_FCT) {
            selectQuery = "SELECT SUM(" + DURATION + ") , " + DATE + " FROM "
                + TABLE_NAME
                + " WHERE " + MEDICATION + "=\"" + pMedication + "\""
                + " AND " + PROFIL_KEY + "=" + pProfile.getId()
                + " GROUP BY " + DATE
                + " ORDER BY date(" + DATE + ") ASC";
        } else if (pFunction == DAOLongTerm.SPEED_FCT) {
            selectQuery = "SELECT SUM(" + DISTANCE + ") / SUM(" + DURATION + ")," + DATE + " FROM "
                + TABLE_NAME
                + " WHERE " + MEDICATION + "=\"" + pMedication + "\""
                + " AND " + PROFIL_KEY + "=" + pProfile.getId()
                + " GROUP BY " + DATE
                + " ORDER BY date(" + DATE + ") ASC";
        } else if (pFunction == DAOLongTerm.MAXDISTANCE_FCT) {
            selectQuery = "SELECT MAX(" + DISTANCE + ") , " + DATE + " FROM "
                + TABLE_NAME
                + " WHERE " + MEDICATION + "=\"" + pMedication + "\""
                + " AND " + PROFIL_KEY + "=" + pProfile.getId()
                + " GROUP BY " + DATE
                + " ORDER BY date(" + DATE + ") ASC";
        }

        //Formatting
        List<DateGraphData> valueList = new ArrayList<DateGraphData>();
        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;
        mCursor = db.rawQuery(selectQuery, null);

        double i = 0;

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

                DateGraphData value = new DateGraphData(DateConverter.nbDays(date.getTime()),
                    mCursor.getDouble(0));

                // Adding value to list
                valueList.add(value);
            } while (mCursor.moveToNext());
        }

        // return value list
        return valueList;
    }

    // Getting All Medication
    public String[] getAllMedication(Profile pProfile) {
        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;

        // Select All Medication
        String selectQuery = "SELECT DISTINCT  " + MEDICATION + " FROM " + TABLE_NAME
            + " WHERE " + PROFIL_KEY + "=" + pProfile.getId()
            + " AND " + TYPE + "=" + DAOMedication.TYPE_LONGTERM
            + " ORDER BY " + MEDICATION + " COLLATE NOCASE ASC";
        mCursor = db.rawQuery(selectQuery, null);

        int size = mCursor.getCount();

        String[] valueList = new String[size];

        // looping through all rows and adding to list
        if (mCursor.moveToFirst()) {
            int i = 0;
            do {
                String value = mCursor.getString(mCursor.getColumnIndex(DAOLongTerm.MEDICATION));
                valueList[i] = value;
                i++;
            } while (mCursor.moveToNext());
        }
        close();
        // return value list
        return valueList;
    }

    // Get all record for one Exercise
    public List<LongTermPrescription> getAllCardioRecordByMedication(Profile pProfile, String pExercise) {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME
            + " WHERE " + MEDICATION + "=\"" + pExercise + "\""
            + " AND " + PROFIL_KEY + "=" + pProfile.getId()
            + " ORDER BY " + KEY + " DESC";

        // return value list
        return getRecordsList(selectQuery);
    }

    // Updating single value
    public int updateRecord(Profile pProfile, LongTermPrescription m) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(DAOLongTerm.DATE, m.getDate().toString());
        value.put(DAOLongTerm.MEDICATION, m.getMedication());
        value.put(DAOLongTerm.MEDICATION_KEY, m.getMedicationKey());
        value.put(DAOLongTerm.DISTANCE, m.getDose());
        value.put(DAOLongTerm.DURATION, m.getDuration());
        value.put(DAOLongTerm.PROFIL_KEY, pProfile.getId());

        // updating row
        return db.update(TABLE_NAME, value, KEY + " = ?",
            new String[]{String.valueOf(m.getId())});
    }

    public void populate() {
        // DBORecord(long id, Date pDate, String pMedication, int pSerie, int
        // pRepetition, int pDose)
        Date date = new Date();
        int dose = 10;

        for (int i = 1; i <= 5; i++) {
            String medication = "Cardiovascular";
            date.setDate(date.getDay() + i * 10);
            addLongTermRecord(date, "00:00", medication, (float) i * 20, 120000 * i, mProfile);
        }

        date = new Date();
        dose = 12;

        for (int i = 1; i <= 5; i++) {
            String medication = "Respiratory";
            date.setDate(date.getDay() + i * 10);
            addLongTermRecord(date, "00:00", medication, 0, 120000 * i * 3, mProfile);
        }
    }

}
