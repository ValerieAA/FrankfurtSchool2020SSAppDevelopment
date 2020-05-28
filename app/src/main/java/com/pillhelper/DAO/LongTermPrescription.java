package com.pillhelper.DAO;

import java.util.Date;

/* DataBase Object */
public class LongTermPrescription extends ARecord {

    private float mDose;
    private long mDuration;

    public LongTermPrescription(Date pDate, String pMedication, float pDose, long pDuration, Profile pProfile) {
        this.mDate = pDate;
        this.mMedication = pMedication;
        this.mDose = pDose;
        this.mDuration = pDuration;
        this.mProfile = pProfile;
        this.mType = DAOMedication.TYPE_LONGTERM;
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

    public long getDuration() {
        return mDuration;
    }

}

