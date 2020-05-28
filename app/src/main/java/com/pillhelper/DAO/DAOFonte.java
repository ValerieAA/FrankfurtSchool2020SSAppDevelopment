package com.pillhelper.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.pillhelper.DateGraphData;
import com.pillhelper.utils.DateConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.pillhelper.DAO.DAODose.DOSE;
import static com.pillhelper.DAO.DAOFavorites.MEDICATION_KEY;

public class DAOFonte extends DAORecord {

    public static final int SUM_FCT = 0;
    public static final int MAX1_FCT = 1;
    public static final int MAX5_FCT = 2;
    public static final int NBSERIE_FCT = 3;

    private static final String TABLE_ARCHI = KEY + "," + DATE + "," + MEDICATION + "," + SERIE + "," + DOSE + "," + INTAKE + "," + UNIT + "," + PROFIL_KEY + "," + NOTES + "," + MEDICATION_KEY + "," + TIME;


    public DAOFonte(Context context) {
        super(context);
    }

    /**
     * @param pDate    Date
     * @param pMedication Medication name
     *                 Le Record a ajouter a la base
     */
    public long addMedicationRecord(Date pDate, String pMedication, int pIntake, int pDaily, float pDose, Profile pProfile, int pUnit, String pNote, String pTime) {
        return addRecord(pDate, pMedication, DAOMedication.TYPE_FONTE, pIntake, pDaily, pDose, pProfile, pUnit, pNote, pTime, 0, 0);
    }

    // Getting single value
    public Fonte getMedicationRecord(long id) {
        String selectQuery = "SELECT  " + TABLE_ARCHI + " FROM " + TABLE_NAME + " WHERE " + KEY + "=" + id;
        List<Fonte> valueList;

        valueList = getRecordsList(selectQuery);
        if (valueList.isEmpty())
            return null;
        else
            return valueList.get(0);
    }

    // Getting All Records
    private List<Fonte> getRecordsList(String pRequest) {
        List<Fonte> valueList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Select All Query

        mCursor = null;
        mCursor = db.rawQuery(pRequest, null);

        // looping through all rows and adding to list
        if (mCursor.moveToFirst() && mCursor.getCount() > 0) {
            do {
                //Get Date
                Date date;
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    date = dateFormat.parse(mCursor.getString(mCursor.getColumnIndex(DAOFonte.DATE)));
                } catch (ParseException e) {
                    e.printStackTrace();
                    date = new Date();
                }

                //Get Profile
                DAOProfil lDAOProfil = new DAOProfil(mContext);
                Profile lProfile = lDAOProfil.getProfil(mCursor.getLong(mCursor.getColumnIndex(DAOFonte.PROFIL_KEY)));

                long medication_key = -1;

                //Test is Medication exists. If not create it.
             /*   DAOMedication lDAOMedication = new DAOMedication(mContext);
                if (mCursor.getString(mCursor.getColumnIndex(DAOFonte.MEDICATION_KEY)) == null) {
                    medication_key = lDAOMedication.addMedication(mCursor.getString(mCursor.getColumnIndex(DAOFonte.MEDICATION)), "", DAOMedication.TYPE_FONTE, "", false);
                } else {
                    medication_key = mCursor.getLong(mCursor.getColumnIndex(DAOFonte.MEDICATION_KEY));
                }*/

                Fonte value = new Fonte(date, mCursor.getString(mCursor.getColumnIndex(DAOFonte.MEDICATION)),
                    mCursor.getInt(mCursor.getColumnIndex(DAOFonte.SERIE)),
                    mCursor.getInt(mCursor.getColumnIndex(DAOFonte.DOSE)),
                    mCursor.getFloat(mCursor.getColumnIndex(DAOFonte.INTAKE)),
                    lProfile,
                    mCursor.getInt(mCursor.getColumnIndex(DAOFonte.UNIT)),
                    mCursor.getString(mCursor.getColumnIndex(DAOFonte.NOTES)),
                    medication_key,
                    mCursor.getString(mCursor.getColumnIndex(DAOFonte.TIME)));

                value.setId(mCursor.getLong(mCursor.getColumnIndex(DAOFonte.KEY)));

                // Adding value to list
                valueList.add(value);
            } while (mCursor.moveToNext());
        }
        // return value list
        return valueList;
    }

    // Getting All Records
    public List<Fonte> getAllMedicationRecords() {
        // Select All Query
        String selectQuery = "SELECT  " + TABLE_ARCHI + " FROM " + TABLE_NAME
            + " WHERE " + TYPE + "=" + DAOMedication.TYPE_FONTE
            + " ORDER BY " + DATE + " DESC," + KEY + " DESC";

        // return value list
        return getRecordsList(selectQuery);
    }

    // Getting All Records
    public List<Fonte> getAllMedicationRecordsByProfileArray(Profile pProfile) {
        return getAllMedicationRecordsByProfileArray(pProfile, -1);
    }

    private List<Fonte> getAllMedicationRecordsByProfileArray(Profile pProfile, int pNbRecords) {
        String mTop;
        if (pNbRecords == -1) mTop = "";
        else mTop = " LIMIT " + pNbRecords;


        // Select All Query
        String selectQuery = "SELECT " + TABLE_ARCHI + " FROM " + TABLE_NAME
            + " WHERE " + PROFIL_KEY + "=" + pProfile.getId()
            + " AND " + TYPE + "=" + DAOMedication.TYPE_FONTE
            + " ORDER BY " + DATE + " DESC," + KEY + " DESC" + mTop;

        // Return value list
        return getRecordsList(selectQuery);
    }

    // Getting Function records
    public List<DateGraphData> getMedicationFunctionRecords(Profile pProfile, String pMedication,
                                                              int pFunction) {

        String selectQuery = null;


        if (pFunction == DAOFonte.SUM_FCT) {
            selectQuery = "SELECT SUM(" + SERIE + "*" + DOSE + "*"
                + INTAKE + "), " + DATE + " FROM " + TABLE_NAME
                + " WHERE " + MEDICATION + "=\"" + pMedication + "\""
                + " AND " + PROFIL_KEY + "=" + pProfile.getId()
                + " GROUP BY " + DATE
                + " ORDER BY date(" + DATE + ") ASC";
        } else if (pFunction == DAOFonte.MAX5_FCT) {
            selectQuery = "SELECT MAX(" + INTAKE + ") , " + DATE + " FROM "
                + TABLE_NAME
                + " WHERE " + MEDICATION + "=\"" + pMedication + "\""
                + " AND " + DOSE + ">=5"
                + " AND " + PROFIL_KEY + "=" + pProfile.getId()
                + " GROUP BY " + DATE
                + " ORDER BY date(" + DATE + ") ASC";
        } else if (pFunction == DAOFonte.MAX1_FCT) {
            selectQuery = "SELECT MAX(" + INTAKE + ") , " + DATE + " FROM "
                + TABLE_NAME
                + " WHERE " + MEDICATION + "=\"" + pMedication + "\""
                + " AND " + DOSE + ">=1"
                + " AND " + PROFIL_KEY + "=" + pProfile.getId()
                + " GROUP BY " + DATE
                + " ORDER BY date(" + DATE + ") ASC";
        } else if (pFunction == DAOFonte.NBSERIE_FCT) {
            selectQuery = "SELECT count(" + KEY + ") , " + DATE + " FROM "
                + TABLE_NAME
                + " WHERE " + MEDICATION + "=\"" + pMedication + "\""
                + " AND " + PROFIL_KEY + "=" + pProfile.getId()
                + " GROUP BY " + DATE
                + " ORDER BY date(" + DATE + ") ASC";
        }

        // Formation de tableau de valeur
        List<DateGraphData> valueList = new ArrayList<>();
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

                DateGraphData value = new DateGraphData(DateConverter.nbDays(date.getTime()), mCursor.getDouble(0));

                // Adding value to list
                valueList.add(value);
            } while (mCursor.moveToNext());
        }

        // return value list
        return valueList;
    }

    /**
     * @return the number of series for this medication for this day
     */
    public int getNbSeries(Date pDate, String pMedication) {


        int lReturn = 0;

        //Test is Medication exists. If not create it.
        DAOMedication lDAOMedication = new DAOMedication(mContext);
        long medication_key = lDAOMedication.getMedication(pMedication).getId();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String lDate = dateFormat.format(pDate);

        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;

        // Select All Medications

        String selectQuery = "SELECT SUM(" + SERIE + ") FROM " + TABLE_NAME
            + " WHERE " + DATE + "=\"" + lDate + "\" AND " + MEDICATION_KEY + "=" + medication_key;
        mCursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list

        mCursor.moveToFirst();
        try {
            lReturn = mCursor.getInt(0);
        } catch (NumberFormatException e) {

            //Date date = new Date();
            lReturn = 0; // Return une valeur
        }

        close();

        // return value
        return lReturn;
    }

    /**
     * @return the total dose for this medication for this day
     */
    public float getTotalDoseMedication(Date pDate, String pMedication) {

        float lReturn = 0;

        //Test is Medication exists. If not create it.
        DAOMedication lDAOMedication = new DAOMedication(mContext);
        long medication_key = lDAOMedication.getMedication(pMedication).getId();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String lDate = dateFormat.format(pDate);

        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;
        // Select All Medication
        String selectQuery = "SELECT " + SERIE + ", " + INTAKE + ", " + DOSE + " FROM " + TABLE_NAME
            + " WHERE " + DATE + "=\"" + lDate + "\" AND " + MEDICATION_KEY + "=" + medication_key;
        mCursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (mCursor.moveToFirst()) {
            int i = 0;
            do {
                float value = mCursor.getInt(0) * mCursor.getFloat(1) * mCursor.getInt(2);
                lReturn += value;
                i++;
            } while (mCursor.moveToNext());
        }
        close();

        // return value
        return lReturn;
    }


    /**
     * @return the total Dose for this day
     */
    public float getTotalDoseSession(Date pDate) {

        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;
        float lReturn = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String lDate = dateFormat.format(pDate);

        // Select All Medication
        String selectQuery = "SELECT " + SERIE + ", " + INTAKE + ", " + DOSE + " FROM " + TABLE_NAME
            + " WHERE " + DATE + "=\"" + lDate + "\"";
        mCursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (mCursor.moveToFirst()) {
            int i = 0;
            do {
                float value = mCursor.getInt(0) * mCursor.getFloat(1) * mCursor.getInt(2);
                lReturn += value;
                i++;
            } while (mCursor.moveToNext());
        }
        close();

        // return value
        return lReturn;
    }

    /**
     * @return Max dose for a profile p and a medication m
     */
    public Measures getMax(Profile p, Medication m) {

        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;
        Measures w = null;

        // Select All Medications
        String selectQuery = "SELECT MAX(" + INTAKE + "), " + UNIT + " FROM " + TABLE_NAME
            + " WHERE " + PROFIL_KEY + "=" + p.getId() + " AND " + MEDICATION_KEY + "=" + m.getId();
        mCursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (mCursor.moveToFirst()) {
            do {
                w = new Measures(mCursor.getFloat(0), mCursor.getInt(1));
            } while (mCursor.moveToNext());
        }
        close();

        // return value
        return w;
    }

    public Measures getMin(Profile p, Medication m) {

        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;
        Measures w = null;

        // Select All Medication
        String selectQuery = "SELECT MIN(" + INTAKE + "), " + UNIT + " FROM " + TABLE_NAME
            + " WHERE " + PROFIL_KEY + "=" + p.getId() + " AND " + MEDICATION_KEY + "=" + m.getId();
        mCursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (mCursor.moveToFirst()) {
            do {
                w = new Measures(mCursor.getFloat(0), mCursor.getInt(1));
            } while (mCursor.moveToNext());
        }
        close();

        // return value
        return w;
    }

    // Updating single value
    public int updateRecord(Fonte m) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        value.put(DAOFonte.DATE, dateFormat.format(m.getDate()));
        value.put(DAOFonte.MEDICATION, m.getMedication());
        value.put(DAOFonte.MEDICATION_KEY, m.getMedicationKey());
        value.put(DAOFonte.SERIE, m.getSerie());
        value.put(DAOFonte.INTAKE, m.getIntake());
        value.put(DAOFonte.DOSE, m.getDose());
        value.put(DAOFonte.UNIT, m.getUnit());
        value.put(DAOFonte.NOTES, m.getNote());
        value.put(DAOFonte.PROFIL_KEY, m.getProfilKey());
        value.put(DAOFonte.TIME, m.getTime());
        value.put(DAOFonte.TYPE, DAOMedication.TYPE_FONTE);

        // updating row
        return db.update(TABLE_NAME, value, KEY + " = ?",
            new String[]{String.valueOf(m.getId())});
    }


    public void populate() {
        // DBORecord(long id, Date pDate, String pMedication, int pSerie, int
        // pRepetition, int pDose)

        Date date = new Date();
        int Medication = 10;

        for (int i = 1; i <= 5; i++) {
            String medication = "Cardiovascular";
            date.setDate(date.getDay() + i * 10);
            addMedicationRecord(date, medication, i * 2, 10 + i,Medication * i, mProfile, 0, "", "12:34:56");
        }

        date = new Date();
        Medication = 12;

        for (int i = 1; i <= 5; i++) {
            String medication = "Respiratory";
            date.setDate(date.getDay() + i * 10);
            addMedicationRecord(date, medication, i * 2, 10 + i, Medication * i, mProfile, 0, "", "12:34:56");
        }
    }

}
