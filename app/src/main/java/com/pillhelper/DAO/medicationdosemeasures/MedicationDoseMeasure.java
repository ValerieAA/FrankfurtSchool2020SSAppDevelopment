package com.pillhelper.DAO.medicationdosemeasures;

import java.util.Date;

/* DataBase Object */
public class MedicationDoseMeasure {

    private long id;
    private Date mDate;
    private int mMedicationpart_id;
    private float mMedication;
    private long mProfil_id;

    public MedicationDoseMeasure(long id, Date pDate, int pMedicationpart_id, float pMedication, long pProfil_id) {
        super();
        this.id = id;
        this.mDate = pDate;
        this.mMedicationpart_id = pMedicationpart_id;
        this.mMedication = pMedication;
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

    /**
     * @return long Medication Part ID
     */
    public int getMedicationPartID() {
        return mMedicationpart_id;
    }

    public float getMedicationMeasure() {
        return mMedication;
    }

    public long getProfileID() {
        return mProfil_id;
    }

}
