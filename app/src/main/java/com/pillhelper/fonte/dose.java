package com.pillhelper.fonte;

import com.pillhelper.utils.UnitConverter;

import java.text.DecimalFormat;

class dose {
        private float pdose;
        private int pUnit;

        public dose(float dose, int unit) {
            pdose = dose;
            pUnit = unit;
        }

        public float getStoredDose() {
            return pdose;
        }

        public float getDose(int unit) {
            float dose = pdose;
            if (unit == UnitConverter.UNIT_LBS) {
                dose = UnitConverter.KgtoLbs(pdose);
            }
            return dose;
        }

        public int getStoredUnit() {
            return pUnit;
        }

        public String toString() {
            DecimalFormat numberFormat = new DecimalFormat("#.##");
            return numberFormat.format(pdose);
        }

        public String getWeightStr(int unit) {
            DecimalFormat numberFormat = new DecimalFormat("#.##");
            return numberFormat.format(getDose(unit));
        }
    }


