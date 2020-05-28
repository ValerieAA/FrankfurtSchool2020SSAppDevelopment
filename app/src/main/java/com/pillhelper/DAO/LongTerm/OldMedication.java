package com.pillhelper.DAO.LongTerm;

import com.pillhelper.DAO.Profile;

import java.util.Date;

/* unser Datenbankobjekt */

public class OldMedication {

    // Die ID wird auf long gesetzt

    private long id;
    private Date mDate;
    private String mMedication;
    private float mDose;
    private long mDuration;
    private Profile mProfile;
    private String mTime;
    private String mDistance;

    public OldMedication(Date pDate, String pMedication, float pDose, long pDuration, Profile pProfile, float mTime, String mTime1, String mDistance) {
        super();
        this.mDate = pDate;
        this.mMedication = pMedication;
        this.mDose = pDose;
        this.mDuration = pDuration;
        this.mProfile = pProfile;

        this.mTime = mTime1;
        this.mDistance = mDistance;
    }

    public OldMedication(Date date, String string, float aFloat, long aLong, Profile setId) {
    }
   // public OldMedication(Date date, String string, float aFloat, long aLong, Profile lProfile) {
   // }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return mDate;
    }

    public String getMedication() {
        return mMedication;
    }

    public float getDose() {
        return mDose;
    }

    public long getDuration() {
        return mDuration;
    }

    public Profile getProfil() {
        return mProfile;
    }

    public String getTime() { return mTime;}

    public float getDistance() { return Float.parseFloat(mDistance);}
    }

