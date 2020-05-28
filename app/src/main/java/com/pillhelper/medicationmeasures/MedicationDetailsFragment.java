package com.pillhelper.medicationmeasures;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.pillhelper.BtnClickListener;
import com.pillhelper.DAO.Profile;
import com.pillhelper.DAO.medicationdosemeasures.MedicationDose;
import com.pillhelper.DAO.medicationdosemeasures.MedicationDoseMeasure;
import com.pillhelper.DAO.medicationdosemeasures.DAOMedicationMeasure;
import com.pillhelper.DatePickerDialogFragment;
import com.pillhelper.MainActivity;
import com.pillhelper.R;
import com.pillhelper.graph.Graph;
import com.pillhelper.utils.DateConverter;
import com.pillhelper.utils.ExpandedListView;
import com.pillhelper.utils.Keyboard;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MedicationDetailsFragment extends Fragment {
    Button addButton = null;
    EditText measureEdit = null;
    EditText dateEdit = null;
    ExpandedListView measureList = null;
    Toolbar MedicationToolbar = null;
    DatePickerDialogFragment mDateFrag = null;
    private String name;
    private int mMedicationPartID;
    private LineChart mChart = null;
    private Graph mGraph = null;
    private DAOMedicationMeasure mMedicationMeasureDb = null;
    private DatePickerDialog.OnDateSetListener dateSet = (view, year, month, day) -> dateEdit.setText(DateConverter.dateToString(year, month + 1, day));
    private OnClickListener clickDateEdit = v -> showDatePickerFragment();
    private OnFocusChangeListener focusDateEdit = (v, hasFocus) -> {
        if (hasFocus) {
            showDatePickerFragment();
        }
    };
    private BtnClickListener itemClickDeleteRecord = this::showDeleteDialog;
    private OnClickListener onClickAddMeasure = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!measureEdit.getText().toString().isEmpty()) {

                Date date = DateConverter.editToDate(dateEdit.getText().toString());

                mMedicationMeasureDb.addMedicationMeasure(date, mMedicationPartID, Float.valueOf(measureEdit.getText().toString()), getProfile().getId());
                refreshData();
                measureEdit.setText("");

                Keyboard.hide(getContext(), v);
            } else {
                KToast.errorToast(getActivity(), "Please enter a measure", Gravity.BOTTOM, KToast.LENGTH_SHORT);

            }
        }
    };
    private OnItemLongClickListener itemlongclickDeleteRecord = (listView, view, position, id) -> {

        // Get the cursor, positioned to the corresponding row in the result set
        //Cursor cursor = (Cursor) listView.getItemAtPosition(position);

        final long selectedID = id;

        String[] profilListArray = new String[1]; // Nur eine Wahl bitte
        profilListArray[0] = getActivity().getResources().getString(R.string.DeleteLabel);

        AlertDialog.Builder itemActionBuilder = new AlertDialog.Builder(getActivity());
        itemActionBuilder.setTitle("").setItems(profilListArray, (dialog, which) -> {

            switch (which) {
                // Delete
                case 0:
                    mMedicationMeasureDb.deleteMeasure(selectedID);
                    refreshData();
                    KToast.infoToast(getActivity(), getActivity().getResources().getText(R.string.removedid).toString() + " " + selectedID, Gravity.BOTTOM, KToast.LENGTH_SHORT);
                    break;
                default:
            }
        });
        itemActionBuilder.show();

        return true;
    };

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static MedicationDetailsFragment newInstance(int MedicationPartID, boolean showInput) {
        MedicationDetailsFragment f = new MedicationDetailsFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("MedicationPartID", MedicationPartID);
        args.putBoolean("showInput", showInput);
        f.setArguments(args);

        return f;
    }

    private void showDatePickerFragment() {
        if (mDateFrag == null) {
            mDateFrag = DatePickerDialogFragment.newInstance(dateSet);
        }

        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        mDateFrag.show(ft, "dialog");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.medicationtracking_details_fragment, container, false);

        addButton = view.findViewById(R.id.buttonAddMedication);
        measureEdit = view.findViewById(R.id.editMedication);
        dateEdit = view.findViewById(R.id.profilEditDate);
        measureList = view.findViewById(R.id.listMedicationProfil);
        MedicationToolbar = view.findViewById(R.id.bodyTrackingDetailsToolbar);
        CardView c = view.findViewById(R.id.addMeasureCardView);

        /* Initialisation MedicationDose */
        mMedicationPartID = getArguments().getInt("MedicationPartID", 0);
        MedicationDose mMedicationDose = new MedicationDose(mMedicationPartID);

        // Hide Input if needed.
        if (!getArguments().getBoolean("showInput", true))
            c.setVisibility(View.GONE);

        /* Initialisation des boutons */
        addButton.setOnClickListener(onClickAddMeasure);
        dateEdit.setOnClickListener(clickDateEdit);
        dateEdit.setOnFocusChangeListener(focusDateEdit);
        measureList.setOnItemLongClickListener(itemlongclickDeleteRecord);

        /* Initialisation des evenements */

        // Add the other graph
        mChart = view.findViewById(R.id.graphChart);
        mChart.setDescription(null);
        mGraph = new Graph(getContext(), mChart, "");
        mMedicationMeasureDb = new DAOMedicationMeasure(view.getContext());

        // Set Initial text
        dateEdit.setText(DateConverter.currentDate());

        ((MainActivity) getActivity()).getActivityToolbar().setVisibility(View.GONE);
        MedicationToolbar.setTitle(getContext().getString(mMedicationDose.getProfileID()));
        MedicationToolbar.setNavigationIcon(R.drawable.ic_back);
        MedicationToolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        refreshData();
    }

    private void DrawGraph(List<MedicationDoseMeasure> valueList) {

        // Recupere les enregistrements
        if (valueList.size() < 1) {
            mChart.clear();
            return;
        }

        ArrayList<Entry> yVals = new ArrayList<>();

        float minMedicationMeasure = -1;

        for (int i = valueList.size() - 1; i >= 0; i--) {
            Entry value = new Entry((float) DateConverter.nbDays(valueList.get(i).getDate().getTime()), valueList.get(i).getMedicationMeasure());
            yVals.add(value);
            if (minMedicationMeasure == -1) minMedicationMeasure = valueList.get(i).getMedicationMeasure();
            else if (valueList.get(i).getMedicationMeasure() < minMedicationMeasure)
                minMedicationMeasure = valueList.get(i).getMedicationMeasure();
        }

        mGraph.draw(yVals);
    }

    /*  */
    private void FillRecordTable(List<MedicationDoseMeasure> valueList) {
        Cursor oldCursor = null;

        if (valueList.isEmpty()) {
            //Toast.makeText(getActivity(), "No records", Toast.LENGTH_SHORT).show();
            measureList.setAdapter(null);
        } else {
            // ...
            if (measureList.getAdapter() == null) {
                MedicationMeasureCursorAdapter mTableAdapter = new MedicationMeasureCursorAdapter(this.getView().getContext(), mMedicationMeasureDb.getCursor(), 0, itemClickDeleteRecord);
                measureList.setAdapter(mTableAdapter);
            } else {
                oldCursor = ((MedicationMeasureCursorAdapter) measureList.getAdapter()).swapCursor(mMedicationMeasureDb.getCursor());
                if (oldCursor != null)
                    oldCursor.close();
            }
        }
    }

    public String getName() {
        return getArguments().getString("name");
    }

    private void refreshData() {
        View fragmentView = getView();
        if (fragmentView != null) {
            if (getProfile() != null) {
                List<MedicationDoseMeasure> valueList = mMedicationMeasureDb.getMedicationMeasuresList(mMedicationPartID, getProfile());
                DrawGraph(valueList);
                // update table
                FillRecordTable(valueList);
            }
        }
    }

    private void showDeleteDialog(final long idToDelete) {

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    mMedicationMeasureDb.deleteMeasure(idToDelete);
                    refreshData();
                    Toast.makeText(getActivity(), getResources().getText(R.string.removedid) + " " + idToDelete, Toast.LENGTH_SHORT)
                        .show();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getText(R.string.DeleteRecordDialog)).setPositiveButton(getResources().getText(R.string.global_yes), dialogClickListener)
            .setNegativeButton(getResources().getText(R.string.global_no), dialogClickListener).show();

    }

    private Profile getProfile() {
        return ((MainActivity) getActivity()).getCurrentProfil();
    }

    public Fragment getFragment() {
        return this;
    }

/*
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) refreshData();
    }
*/
}
