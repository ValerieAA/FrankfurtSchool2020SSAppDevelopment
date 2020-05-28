package com.pillhelper.DAO.medicationdosemeasures;

import com.pillhelper.R;

/* DataBase Object */
public class MedicationDose<MedicationMeasure> {

/*    public void buildMedicationTable() {
        _medication[0] = getActivity().getResources().getString(R.string.Cardiovascular);
        _medication[1] = getActivity().getResources().getString(R.string.Respiratory);
        _medication[2] = getActivity().getResources().getString(R.string.Gastrointestinal);
        _medication[3] = getActivity().getResources().getString(R.string.Renal);
        _medication[4] = getActivity().getResources().getString(R.string.Neurological);
        _medication[5] = getActivity().getResources().getString(R.string.Antibiotics);
        _medication[6] = getActivity().getResources().getString(R.string.Dermatologic);*/
/*
    public static final int CARDIOVASCULAR = 16;
    public static final int RESPIRATORY = 17;
    public static final int GASTROINTESTINAL = 18;
    */
    public static final int DOSE = 19;
    public static final int FAT = 20;
    public static final int BONES = 21;
    public static final int WATER = 22;
    public static final int MUSCLES = 23;

    private int id;
    private MedicationMeasure mLastMeasure;

    public MedicationDose(int id) {
        super();
        this.id = id;
        this.mLastMeasure = null;
    }

    public MedicationDose(int id, MedicationMeasure lastMeasure) {
        super();
        this.id = id;
        this.mLastMeasure = lastMeasure;
    }

    private static int getBodyResourceID(int pBodyID) {
        switch (pBodyID) {
            case DOSE:
                return R.string.weightLabel;
            case FAT:
                return R.string.fatLabel;
            case BONES:
                return R.string.bonesLabel;
            case WATER:
                return R.string.waterLabel;
            case MUSCLES:
                return R.string.musclesLabel;
        }

        return 0;
    }

    public void getResourceNameID() {
    }


    public int getProfileID() { return Integer.parseInt(null);
    }
}


