package com.pillhelper.DAO;

import com.pillhelper.utils.UnitConverter;

import java.text.DecimalFormat;

public class Measures {
    private float pDose;
    private int pUnit;

    public Measures(float dose, int unit) {
        pDose = dose;
        pUnit = unit;
    }

    public float getStoredDose() {
        return pDose;
    }

    public float getDose(int unit) {
        float dose = pDose;
        if (unit == UnitConverter.UNIT_LBS) {
            dose = UnitConverter.KgtoLbs(pDose);
        }
        return dose;
    }

    public int getStoredUnit() {
        return pUnit;
    }

    public String toString() {
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        return numberFormat.format(pDose);
    }

    public String getDoseStr(int unit) {
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        return numberFormat.format(getDose(unit));
    }
}
