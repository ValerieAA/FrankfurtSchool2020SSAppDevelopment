package com.pillhelper.DAO;

import java.util.Date;

/* DataBase Object */
public class ProfileDose {


    private long id;
    private Date mDate;
    private float mDose;
    private long mProfil_id;

    public static final String KEY = "_id";
    public static final String DOSE = "dose";
    public static final String DATE = "date";
    public static final String PROFIL_KEY = "profil_id";

    public ProfileDose(long id, Date pDate, float pDose, long pProfil_id) {
        super();
        this.id = id;
        this.mDate = pDate;
        this.mDose = pDose;
        this.mProfil_id = pProfil_id;
    }

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
}
