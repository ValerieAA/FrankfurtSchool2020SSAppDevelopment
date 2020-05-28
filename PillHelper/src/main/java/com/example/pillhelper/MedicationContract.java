package com.example.pillhelper;

import android.provider.BaseColumns;

public class MedicationContract {

    private MedicationContract(){}

    public static final class   MedicationEntry implements BaseColumns {
        public static final String TABLE_NAME ="medicationList";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_TIMESTAMP ="timestamp";

}}
