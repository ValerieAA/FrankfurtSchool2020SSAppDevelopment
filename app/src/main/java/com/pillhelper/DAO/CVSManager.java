package com.pillhelper.DAO;

import android.content.Context;
import android.os.Environment;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.pillhelper.DAO.LongTerm.OldMedication;
import com.pillhelper.DAO.medicationdosemeasures.DAOMedicationMeasure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

    public class CVSManager<MedicationMeasure> {

    static private String TABLE_HEAD = "table";
    static private String ID_HEAD = "id";

    private Context mContext = null;

    public CVSManager(Context pContext) {
        mContext = pContext;
    }

    public boolean exportDatabase(Profile pProfile) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        /**First of all we check if the external storage of the device is available for writing.
         * Remember that the external storage is not necessarily the sd card. Very often it is
         * the device storage.
         */
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_H_m_s_");
            Date date = new Date();

            //We use the Pillhelper directory for saving our .csv file.
            File exportDir = Environment.getExternalStoragePublicDirectory("/pillhelper/export/" + dateFormat.format(date) + pProfile.getName());
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            PrintWriter printWriter = null;
            try {
                exportFontes(exportDir, pProfile);
                exportLongTerm(exportDir, pProfile);
                exportProfileDose(exportDir, pProfile);
                exportMedicationMeasures(exportDir, pProfile);
                exportMedication(exportDir, pProfile);
            } catch (Exception e) {
                //if there are any exceptions, return false
                e.printStackTrace();
                return false;
            } finally {
                if (printWriter != null) printWriter.close();
            }

            //If there are no errors, return true.
            return true;
        }
    }

    public boolean exportFontes(File exportDir, Profile pProfile) {
        try {
            // FONTE
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_H_m_s");
            Date date = new Date();

            CsvWriter csvOutputFonte = new CsvWriter(exportDir.getPath() + "/" + "EF_" + pProfile.getName() + "_Medication_" + dateFormat.format(date) + ".csv", ',', Charset.forName("UTF-8"));

            /**This is our database connector class that reads the data from the database.
             * The code of this class is omitted for brevity.
             */
            DAOFonte dbcFonte = new DAOFonte(mContext);
            dbcFonte.open();

            /**Let's read the first table of the database.
             * getFirstTable() is a method in our DBCOurDatabaseConnector class which retrieves a Cursor
             * containing all records of the table (all fields).
             * The code of this class is omitted for brevity.
             */
            List<Fonte> records = null;
            records = dbcFonte.getAllMedicationRecordsByProfileArray(pProfile);

            //Write the name of the table and the name of the columns (comma separated values) in the .csv file.
            csvOutputFonte.write(TABLE_HEAD);
            csvOutputFonte.write(ID_HEAD);
            csvOutputFonte.write(DAOFonte.DATE);
            csvOutputFonte.write(DAOFonte.MEDICATION);
            csvOutputFonte.write(DAOFonte.DOSE);
            csvOutputFonte.write(DAOFonte.INTAKE);
            csvOutputFonte.write(DAOFonte.SERIE);
            csvOutputFonte.write(DAOFonte.PROFIL_KEY);
            csvOutputFonte.write(DAOFonte.UNIT);
            csvOutputFonte.write(DAOFonte.NOTES);
            csvOutputFonte.endRecord();

            for (int i = 0; i < records.size(); i++) {
                csvOutputFonte.write(DAOFonte.TABLE_NAME);
                csvOutputFonte.write(Long.toString(records.get(i).getId()));

                Date dateRecord = records.get(i).getDate();

                SimpleDateFormat dateFormatcsv = new SimpleDateFormat(DAOUtils.DATE_FORMAT);

                csvOutputFonte.write(dateFormatcsv.format(dateRecord));
                csvOutputFonte.write(records.get(i).getMedication());
                csvOutputFonte.write(Float.toString(records.get(i).getDose()));
                csvOutputFonte.write(Integer.toString(records.get(i).getIntake()));
                csvOutputFonte.write(Integer.toString(records.get(i).getSerie()));
                if (records.get(i).getProfil() != null)
                    csvOutputFonte.write(Long.toString(records.get(i).getProfil().getId()));
                else csvOutputFonte.write("-1");
                csvOutputFonte.write(Integer.toString(records.get(i).getUnit()));
                if (records.get(i).getNote() == null) csvOutputFonte.write("");
                else csvOutputFonte.write(records.get(i).getNote());
                csvOutputFonte.endRecord();
            }
            csvOutputFonte.close();
            dbcFonte.closeAll();
        } catch (Exception e) {
            //if there are any exceptions, return false
            e.printStackTrace();
            return false;
        }
        //If there are no errors, return true.
        return true;
    }

    public boolean exportProfileDose(File exportDir, Profile pProfile) {
        try {
            // FONTE
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_H_m_s");
            Date date = new Date();

            // use FileWriter constructor that specifies open for appending
            CsvWriter csvOutputDose = new CsvWriter(exportDir.getPath() + "/" + "EF_" + pProfile.getName() + "_ProfilDose_" + dateFormat.format(date) + ".csv", ',', Charset.forName("UTF-8"));
            DAODose dbcDose = new DAODose(mContext);
            dbcDose.open();

            List<ProfileDose> doseRecords;
            doseRecords = (List<ProfileDose>) dbcDose.getDoseList(pProfile);

            csvOutputDose.write(TABLE_HEAD);
            csvOutputDose.write(ID_HEAD);
            csvOutputDose.write(DAODose.DOSE);
            csvOutputDose.write(DAODose.DATE);
            csvOutputDose.endRecord();

            for (int i = 0; i < doseRecords.size(); i++) {
                csvOutputDose.write(DAODose.TABLE_NAME);
                csvOutputDose.write(Long.toString(doseRecords.get(i).getId()));
                csvOutputDose.write(Float.toString(doseRecords.get(i).getDose()));

                Date dateRecord = doseRecords.get(i).getDate();
                SimpleDateFormat dateFormatcsv = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
                csvOutputDose.write(dateFormatcsv.format(dateRecord));

                csvOutputDose.endRecord();
            }
            csvOutputDose.close();
            dbcDose.close();
        } catch (Exception e) {
            //if there are any exceptions, return false
            e.printStackTrace();
            return false;
        }
        //If there are no errors, return true.
        return true;
    }

    public boolean exportMedicationMeasures(File exportDir, Profile pProfile) {
        try {
            // FONTE
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_H_m_s");
            Date date = new Date();

            // use FileWriter constructor that specifies open for appending
            CsvWriter cvsOutput = new CsvWriter(exportDir.getPath() + "/" + "EF_" + pProfile.getName() + "_MedicationMeasures_" + dateFormat.format(date) + ".csv", ',', Charset.forName("UTF-8"));
            DAOMedicationMeasure daoMedicationMeasure = new DAOMedicationMeasure( mContext);
            daoMedicationMeasure.open();

            List<MedicationMeasure> medicationMeasures = null;
            daoMedicationMeasure.getMedcationMeasuresList(pProfile);

            cvsOutput.write(TABLE_HEAD);
            cvsOutput.write(ID_HEAD);
            cvsOutput.write(daoMedicationMeasure.DATE);
            cvsOutput.write(daoMedicationMeasure.MEDICATIONPART_KEY);
            cvsOutput.write("bodypart_label");
            cvsOutput.write(daoMedicationMeasure.MEASURE);
            cvsOutput.write(daoMedicationMeasure.PROFIL_KEY);
            cvsOutput.endRecord();

                for (int i = 0; i < medicationMeasures.size(); i++) {
                cvsOutput.write(daoMedicationMeasure.TABLE_NAME);
             //   cvsOutput.write(Long.toString(medicationMeasures.get(i).getID()));
               // Date dateRecord = medicationMeasures.get(i).getDate();
             //   cvsOutput.write(DateConverter.dateToDBDateStr(dateRecord));
             //   cvsOutput.write(Long.toString(medicationMeasures.get(i).getMedicationPartID()));
              //  MedicationDose bp = new MedicationDose(medicationMeasures.get(i).getMedicationPartID());
              //      cvsOutput.write(this.mContext.getString(bp.getResourceNameID()));
             //   cvsOutput.write(Float.toString(medicationMeasures.get(i).getMedicationMeasure()));
             //   cvsOutput.write(Long.toString(medicationMeasures.get(i).getProfileID()));

                cvsOutput.endRecord();
            }
            cvsOutput.close();
            daoMedicationMeasure.close();
        } catch (Exception e) {
            //if there are any exceptions, return false
            e.printStackTrace();
            return false;
        }
        //If there are no errors, return true.
        return true;
    }

    public boolean exportLongTerm(File exportDir, Profile pProfile) {
        try {
            // FONTE
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_H_m_s");
            Date date = new Date();

            CsvWriter csvOutputLongTerm = new CsvWriter(exportDir.getPath() + "/" + "EF_" + pProfile.getName() + "_LongTerm_" + dateFormat.format(date) + ".csv", ',', Charset.forName("UTF-8"));

            /**This is our database connector class that reads the data from the database.
             * The code of this class is omitted for brevity.
             */
            DAOLongTerm dbcLongTerm = new DAOLongTerm(mContext);
            dbcLongTerm.open();

            /**Let's read the first table of the database.
             * getFirstTable() is a method in our DBCOurDatabaseConnector class which retrieves a Cursor
             * containing all records of the table (all fields).
             * The code of this class is omitted for brevity.
             */
            List<OldMedication> longtermRecords = null;
            String pMedication = null;
            longtermRecords = dbcLongTerm.getAllLongTermRecordsByProfile(pProfile, pMedication);

            //Write the name of the table and the name of the columns (comma separated values) in the .csv file.
            csvOutputLongTerm.write(TABLE_HEAD);
            csvOutputLongTerm.write(ID_HEAD);
            csvOutputLongTerm.write(DAOLongTerm.DATE);
            csvOutputLongTerm.write(DAOLongTerm.TIME);
            csvOutputLongTerm.write(DAOLongTerm.MEDICATION);
            csvOutputLongTerm.write(DAOLongTerm.DURATION);
            csvOutputLongTerm.write(DAOLongTerm.DISTANCE);
            csvOutputLongTerm.write(DAOLongTerm.PROFIL_KEY);
            csvOutputLongTerm.endRecord();

            for (int i = 0; i < longtermRecords.size(); i++) {
                csvOutputLongTerm.write(DAOLongTerm.TABLE_NAME);
                csvOutputLongTerm.write(Long.toString(longtermRecords.get(i).getId()));

                Date dateRecord = longtermRecords.get(i).getDate();

                SimpleDateFormat dateFormatcsv = new SimpleDateFormat(DAOUtils.DATE_FORMAT);

                csvOutputLongTerm.write(dateFormatcsv.format(dateRecord));
                csvOutputLongTerm.write(longtermRecords.get(i).getTime());
                csvOutputLongTerm.write(longtermRecords.get(i).getMedication());
                csvOutputLongTerm.write(Long.toString(longtermRecords.get(i).getDuration()));
                csvOutputLongTerm.write(Float.toString(longtermRecords.get(i).getDistance()));
                if (longtermRecords.get(i).getProfil() != null)
                    csvOutputLongTerm.write(Long.toString(longtermRecords.get(i).getProfil().getId()));
                else csvOutputLongTerm.write("-1");
                //write the record in the .csv file
                csvOutputLongTerm.endRecord();
            }
            csvOutputLongTerm.close();
            dbcLongTerm.close();
        } catch (Exception e) {
            //if there are any exceptions, return false
            e.printStackTrace();
            return false;
        }
        //If there are no errors, return true.
        return true;
    }

    public boolean exportMedication(File exportDir, Profile pProfile) {
        try {
            // FONTE
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_H_m_s");
            Date date = new Date();

            CsvWriter csvOutput = new CsvWriter(exportDir.getPath() + "/" + "EF_" + pProfile.getName() + "_Exercises_" + dateFormat.format(date) + ".csv", ',', Charset.forName("UTF-8"));

            /**This is our database connector class that reads the data from the database.
             * The code of this class is omitted for brevity.
             */
            DAOMedication dbcMedication = new DAOMedication(mContext);
            dbcMedication.open();

            /**Let's read the first table of the database.
             * getFirstTable() is a method in our DBCOurDatabaseConnector class which retrieves a Cursor
             * containing all records of the table (all fields).
             * The code of this class is omitted for brevity.
             */
            List<Medication> records = null;
            records = dbcMedication.getAllMedicationArray();

            //Write the name of the table and the name of the columns (comma separated values) in the .csv file.
            csvOutput.write(TABLE_HEAD);
            csvOutput.write(ID_HEAD);
            csvOutput.write(DAOMedication.NAME);
            csvOutput.write(DAOMedication.DESCRIPTION);
            csvOutput.write(DAOMedication.TYPE);
            csvOutput.write(DAOMedication.MEDICATIONPARTS);
            csvOutput.write(DAOMedication.FAVORITES);
            //csvOutput.write(DAOMedication.PICTURE);
            csvOutput.endRecord();

            for (int i = 0; i < records.size(); i++) {
                csvOutput.write(DAOMedication.TABLE_NAME);
                csvOutput.write(Long.toString(records.get(i).getId()));
                csvOutput.write(records.get(i).getName());
                csvOutput.write(records.get(i).getDescription());
                csvOutput.write(Integer.toString(records.get(i).getType()));
                csvOutput.write(records.get(i).getMedicationParts());
                csvOutput.write(Boolean.toString(records.get(i).getFavorite()));
                //write the record in the .csv file
                csvOutput.endRecord();
            }
            csvOutput.close();
            dbcMedication.close();
        } catch (Exception e) {
            //if there are any exceptions, return false
            e.printStackTrace();
            return false;
        }
        //If there are no errors, return true.
        return true;
    }

    /*
     * TODO : Reinforce the function
     */
    public boolean importDatabase(String file, Profile pProfile) {

        boolean ret = true;

        try {
            CsvReader csvRecords = new CsvReader(file, ',', Charset.forName("UTF-8"));

            csvRecords.readHeaders();

            while (csvRecords.readRecord()) {
                switch (csvRecords.get(TABLE_HEAD)) {
                    case DAOFonte.TABLE_NAME: {
                        DAOFonte dbcFonte = new DAOFonte(mContext);
                        DAOLongTerm dbcLongTerm = new DAOLongTerm(mContext);
                        DAOMedication dbcMedication = new DAOMedication(mContext);
                        dbcFonte.open();
                        Date date;
                        try {
                            date = new SimpleDateFormat(DAOUtils.DATE_FORMAT)
                                .parse(csvRecords.get(DAOFonte.DATE));

                            String medication = csvRecords.get(DAOFonte.MEDICATION);
                            if (dbcMedication.getMedication(medication).getType() == DAOMedication.TYPE_FONTE) {
                                float dose = Float.valueOf(csvRecords.get(DAOFonte.MEDICATION));
                                int intake = Integer.valueOf(csvRecords.get(DAOFonte.INTAKE));
                                int serie = Integer.valueOf(csvRecords.get(DAOFonte.SERIE));
                                int unit = 0;
                                if (!csvRecords.get(DAOFonte.UNIT).isEmpty()) {
                                    unit = Integer.valueOf(csvRecords.get(DAOFonte.UNIT));
                                }
                                String notes = csvRecords.get(DAOFonte.NOTES);
                                String time = csvRecords.get(DAOFonte.TIME);
                                dbcFonte.addMedicationRecord(date, medication, serie, intake, dose, pProfile, unit, notes, time);
                                dbcFonte.close();
                            } else {
                                String time = csvRecords.get(DAOLongTerm.TIME);
                                String longterm = csvRecords.get(DAOLongTerm.MEDICATION);
                                float distance = Float.valueOf(csvRecords.get(DAOLongTerm.DISTANCE));
                                int duration = Integer.valueOf(csvRecords.get(DAOLongTerm.DURATION));
                                dbcLongTerm.addLongTermRecord(date, time, longterm, distance, duration, pProfile);
                                dbcLongTerm.close();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            ret = false;
                        }
                        break;
                    }
                    case DAODose.TABLE_NAME: {
                        DAODose dbcDose = new DAODose(mContext);
                        dbcDose.open();
                        Date date;
                        try {
                            date = new SimpleDateFormat(DAOUtils.DATE_FORMAT)
                                .parse(csvRecords.get(DAODose.DATE));

                            float dose = Float.valueOf(csvRecords.get(DAODose.DOSE));
                            dbcDose.addDose(date, dose, pProfile);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            ret = false;
                        }
                        break;
                    }
                    case DAOProfil.TABLE_NAME:
                        // TODO : import profiles
                        break;
                    case DAOMedication.TABLE_NAME:
                        DAOMedication dbc = new DAOMedication(mContext);
                        String name = csvRecords.get(DAOMedication.NAME);
                        String description = csvRecords.get(DAOMedication.DESCRIPTION);
                        int type = Integer.valueOf(csvRecords.get(DAOMedication.TYPE));
                        boolean favorite = Boolean.valueOf(csvRecords.get(DAOMedication.FAVORITES));
                        // Check if medication exists
                        if (dbc.getMedication(name) == null) {
                            dbc.addMedication(name, description, type, "", favorite);
                        }
                        break;
                }
            }

            csvRecords.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ret = false;
        } catch (IOException e) {
            e.printStackTrace();
            ret = false;
        }

        return ret;
    }}
