package com.pillhelper.DAO;

/* DataBase Object */
public class Medication {

    private long id;
    private String mName;
    private int mType; // LongTermPrescription or Fonte
    private String mPicture = null;
    private String mDescription;
    private String mMedicationParts;
    private Boolean mFavorite;

    public Medication(String pName, String pDescription, int pType, String pMedicationParts, String pPicture, Boolean pFavorite) {
        super();
        this.mName = pName;
        this.mDescription = pDescription;
        this.mType = pType;
        this.mPicture = pPicture;
        this.mMedicationParts = pMedicationParts;
        this.mFavorite = pFavorite;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String desc) {
        this.mDescription = desc;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public String getPicture() {
        return mPicture;
    }

    public void setPicture(String picture) {
        this.mPicture = picture;
    }

    public String getMedicationParts() {
        if (mMedicationParts == null) return "";
        else return mMedicationParts;
    }

    public void setBodyParts(String medicationParts) {
        mMedicationParts = medicationParts;
    }

    public Boolean getFavorite() {
        return mFavorite;
    }

    public void setFavorite(Boolean favorite) {
        mFavorite = favorite;
    }

    @Override
    public String toString() {
        return getName();
    }


}
