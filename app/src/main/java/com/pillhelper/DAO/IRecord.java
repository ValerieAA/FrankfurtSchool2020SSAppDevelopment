package com.pillhelper.DAO;

import java.util.Date;

public interface IRecord {
    long getId();

    void setId(long id);

    Date getDate();

    String getMedication();

    void setMedication(String Medication);

    long getMedicationKey();

    float getDose();

    void setDose(String dose);

    long getDoseKey();

    void setMedicationKey(long id);

    Profile getProfil();

    long getProfilKey();

    String getTime();

    int getType();
}
