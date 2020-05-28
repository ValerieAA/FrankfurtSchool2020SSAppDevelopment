package com.pillhelper.DAO;

import java.util.Date;

/* DataBase Object */

public class Fonte extends ARecord {

    private int mSerie;
    private int mIntake;
    private float mDose;
    private int mUnit;
    private String mNote;

    public Fonte(Date pDate, String pMedication, int pSerie, int pIntake, float pDose, Profile pProfile, int pUnit, String pNote, long pMedicationKey, String pTime) {
        super();
        this.mDate = pDate;
        this.mMedication= pMedication;
        this.mSerie = pSerie;
        this.mIntake = pIntake;
        this.mDose = pDose;
        this.mUnit = pUnit;
        this.mNote = pNote;
        this.mProfile = pProfile;
        this.mMedicationId = pMedicationKey;
        this.mTime = pTime;
        this.mType = DAOMedication.TYPE_FONTE;
    }

    public int getSerie() {
        return mSerie;
    }

    public int getIntake() {
        return mIntake;
    }

    public float getDose() {
        return mDose;
    }

    @Override
    public void setDose(String dose) {

    }

    @Override
    public long getDoseKey() {
        return 0;
    }

    public int getUnit() {
        return mUnit;
    }

    public String getNote() {
        return mNote;
    }

    @Override
    public double getDistance() {
        return 0;
    }
}
