package com.pillhelper.fonte;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.pillhelper.BtnClickListener;
import com.pillhelper.DAO.DAOMedication;
import com.pillhelper.DAO.DAORecord;
import com.pillhelper.DAO.DAOUtils;
import com.pillhelper.DAO.Medication;
import com.pillhelper.DAO.Profile;
import com.pillhelper.MainActivity;
import com.pillhelper.R;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FonteHistoryFragment extends Fragment {

    Spinner dateList = null;
    Spinner medicationList = null;

    Button paramButton = null;
    ListView filterList = null;

    TextView tSerie = null;
    TextView tIntake = null;
    TextView tDose = null;

    MainActivity mActivity = null;

    List<String> mMedicationArray = null;
    List<String> mDateArray = null;

    ArrayAdapter<String> mAdapterMedication = null;
    ArrayAdapter<String> mAdapterDate = null;

    long medicationIdArg = -1;
    long medicationProfilIdArg = -1;

    Medication selectedMedication = null;
    private DAORecord mDb = null;
    private BtnClickListener itemClickDeleteRecord = this::showDeleteDialog;
    private OnItemLongClickListener itemlongclickDeleteRecord = (listView, view, position, id) -> {

        mDb.deleteRecord(id);

        FillRecordTable(medicationList.getSelectedItem().toString(), dateList
            .getSelectedItem().toString());

        KToast.infoToast(getActivity(), getResources().getText(R.string.removedid).toString(), Gravity.BOTTOM, KToast.LENGTH_SHORT);

        return true;
    };
    private OnItemSelectedListener onItemSelectedList = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (parent.getId() == R.id.filterMachine) {

                // Save current date

                String currentDateSelection = "";

                //  Update currentSelectedMedication

                DAOMedication lDbMedication = new DAOMedication(getContext());
                Medication medication = null;
                if (!medicationList.getSelectedItem().toString().equals(getView().getResources().getText(R.string.all).toString())) {
                    selectedMedication = lDbMedication.getMedication(medicationList.getSelectedItem().toString());
                } else {
                    selectedMedication = null;
                }

                // Update associated Dates
                refreshDates(selectedMedication);
                if (dateList.getCount() > 1) {
                    dateList.setSelection(1); // Select latest date
                } else {
                    dateList.setSelection(0); // Or select "All"
                }
            }
            if (dateList.getCount() >= 1 && medicationList.getCount() >= 1) {
                FillRecordTable(medicationList.getSelectedItem().toString(), dateList
                    .getSelectedItem().toString());
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */

    public static FonteHistoryFragment newInstance(long medicationId, long medicationProfile) {
        FonteHistoryFragment f = new FonteHistoryFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putLong("medicationID", medicationId);
        args.putLong("medicationProfile", medicationProfile);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.tab_history, container, false);

        Bundle args = this.getArguments();
        medicationIdArg = args.getLong("medicationID");
        medicationProfilIdArg = args.getLong("medicationProfile");

        dateList = view.findViewById(R.id.filterDate);
        medicationList = view.findViewById(R.id.filterMachine);
        filterList = view.findViewById(R.id.listFilterRecord);

        tSerie = view.findViewById(R.id.INTAKE_CELL);
        tIntake = view.findViewById(R.id.DOSE_CELL);
        tDose = view.findViewById(R.id.MED_CELL);

        // Wir initialisieren die Historie

        mDb = new DAORecord(view.getContext());

        mMedicationArray = new ArrayList<>();
        mMedicationArray.add(getContext().getResources().getText(R.string.all).toString());
        mAdapterMedication = new ArrayAdapter<>(
            getContext(), android.R.layout.simple_spinner_item, //simple_spinner_dropdown_item
            mMedicationArray);
        mAdapterMedication.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicationList.setAdapter(mAdapterMedication);
        mDb.closeCursor();

        if (medicationIdArg != -1) {
            // Hide the spinner

            view.findViewById(R.id.tableRowFilterMachine).setVisibility(View.GONE);
            DAOMedication lDbMedication = new DAOMedication(getContext());
            selectedMedication = lDbMedication.getMedication(medicationIdArg);
            mMedicationArray.add(selectedMedication.getName());
            mAdapterMedication.notifyDataSetChanged();
            medicationList.setSelection(mAdapterMedication.getPosition(selectedMedication.getName()));
        } else {
            medicationList.setOnItemSelectedListener(onItemSelectedList);
        }

        mDateArray = new ArrayList<>();
        mDateArray.add(getContext().getResources().getText(R.string.all).toString());
        mAdapterDate = new ArrayAdapter<>(
            getContext(), android.R.layout.simple_spinner_item,
            mDateArray);
        mAdapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateList.setAdapter(mAdapterDate);

        // Initialisierung der Events
        // Implementierung eines Long Clicks
        filterList.setOnItemLongClickListener(itemlongclickDeleteRecord);
        dateList.setOnItemSelectedListener(onItemSelectedList);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.mActivity = (MainActivity) this.getActivity();
        refreshData();
    }

    public String getName() {
        return getArguments().getString("name");
    }

    public int getFragmentId() {
        return getArguments().getInt("id", 0);
    }

    public MainActivity getMainActivity() {
        return this.mActivity;
    }

    /*  */
    private void FillRecordTable(String pMedication, String pDate) {
        Cursor oldCursor = null;

        // Retransform date filter value in SQLLite date format

        if (!pDate.equals(getContext().getResources().getText(R.string.all).toString())) {
            Date date;
            try {
                DateFormat dateFormat3 = android.text.format.DateFormat.getDateFormat(getContext().getApplicationContext());
                dateFormat3.setTimeZone(TimeZone.getTimeZone("GMT"));
                date = dateFormat3.parse(pDate);
            } catch (ParseException e) {
                e.printStackTrace();
                date = new Date();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            pDate = dateFormat.format(date);
        }

        // Get Values
        Cursor c = mDb.getFilteredRecords(getProfil(), pMedication, pDate);

        if (c == null || c.getCount() == 0) {
            filterList.setAdapter(null);
        } else {
            if (filterList.getAdapter() == null) {
                RecordCursorAdapter mTableAdapter = new RecordCursorAdapter(this.getView().getContext(), c, 0, itemClickDeleteRecord, null);
                filterList.setAdapter(mTableAdapter);
            } else {
                oldCursor = ((RecordCursorAdapter) filterList.getAdapter()).swapCursor(c);
                if (oldCursor != null)
                    oldCursor.close();
            }
        }
    }

    private void refreshData() {
        View fragmentView = getView();
        if (fragmentView != null) {
            if (getProfil() != null) {
                // If the fragment is used to display record of a specific medication

                if (medicationIdArg == -1) // Refresh the list
                {
                    // Initialisation des specifications
                    mMedicationArray.clear();
                    mMedicationArray.add(getContext().getResources().getText(R.string.all).toString());
                    mMedicationArray.addAll(mDb.getAllMedicationStrList(getProfil()));
                    mAdapterMedication.notifyDataSetChanged();
                    mDb.closeCursor();

                    medicationList.setSelection(0); // Default value is "all" when there is a list
                }

                refreshDates(selectedMedication);
            }
        }
    }

    /**
     * @param m if m is null then, get the dates for all specifications
     */
    private void refreshDates(Medication m) {
        View fragmentView = getView();
        if (fragmentView != null) {
            if (getProfil() != null) {
                mDateArray.clear();
                mDateArray.add(getView().getResources().getText(R.string.all).toString());
                mDateArray.addAll(mDb.getAllDatesList(getProfil(), m));
                if (mDateArray.size() > 1) {
                    dateList.setSelection(1);
                }
                mAdapterDate.notifyDataSetChanged();
                mDb.closeCursor();
            }
        }
    }

    private Profile getProfil() {
        return mActivity.getCurrentProfil();
    }

    private String getFontesMedication() {
        return getMainActivity().getCurrentMedication();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            refreshData();
        }
    }

    private void showDeleteDialog(final long idToDelete) {

        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText(getString(R.string.DeleteRecordDialog))
            .setContentText(getResources().getText(R.string.areyousure).toString())
            .setCancelText(getResources().getText(R.string.global_no).toString())
            .setConfirmText(getResources().getText(R.string.global_yes).toString())
            .showCancelButton(true)
            .setConfirmClickListener(sDialog -> {
                mDb.deleteRecord(idToDelete);
                FillRecordTable(medicationList.getSelectedItem().toString(), dateList
                    .getSelectedItem().toString());
                KToast.infoToast(getActivity(), getResources().getText(R.string.removedid).toString(), Gravity.BOTTOM, KToast.LENGTH_LONG);
                sDialog.dismissWithAnimation();
            })
            .show();
    }
}
