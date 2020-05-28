package com.pillhelper.DAO;

import java.util.Date;

/* DataBase Object */
public class ARecord implements IRecord {
    protected long id;
    protected Date mDate;
    protected String mMedication;
    protected long mMedicationId;
    protected Profile mProfile;
    protected String mTime; // Time in HH:MM:SS
    protected int mType; // Time in HH:MM:SS


    public ARecord() {
        super();
    }

    public ARecord(Date pDate, String pMedication, Profile pProfile, long pMedicationKey, String pTime, int pType) {
        super();
        this.mDate = pDate;
        this.mMedication = pMedication;
        this.mProfile = pProfile;
        this.mMedicationId = pMedicationKey;
        this.mTime = pTime;
        this.mType = pType;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Date getDate() {
        return mDate;
    }

    @Override
    public String getMedication() {
        return mMedication;
    }

    @Override
    public void setMedication(String medication) {
        this.mMedication = medication;
    }

    @Override
    public long getMedicationKey() {
        return mMedicationId;
    }

    @Override
    public float getDose() {
        return 0;
    }

    @Override
    public void setDose(String dose) {

    }

    @Override
    public long getDoseKey() {
        return 0;
    }

    @Override
    public void setMedicationKey(long id) {
        this.mMedicationId = id;
    }

    @Override
    public Profile getProfil() {
        return mProfile;
    }

    @Override
    public long getProfilKey() {
        return mProfile.getId();
    }

    @Override
    public String getTime() {
        return mTime;
    }

    @Override
    public int getType() {
        return mType;
    }

    public double getDistance() {
        return 0;
    }
}
