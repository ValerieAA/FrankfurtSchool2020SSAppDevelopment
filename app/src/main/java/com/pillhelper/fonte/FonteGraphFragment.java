package com.pillhelper.fonte;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.pillhelper.DAO.DAOLongTerm;
import com.pillhelper.DAO.DAOFonte;
import com.pillhelper.DAO.DAOMedication;
import com.pillhelper.DAO.Medication;
import com.pillhelper.DAO.Profile;
import com.pillhelper.DateGraphData;
import com.pillhelper.MainActivity;
import com.pillhelper.R;
import com.pillhelper.graph.Graph;
import com.pillhelper.graph.Graph.zoomType;
import com.pillhelper.utils.DateConverter;
import com.pillhelper.utils.UnitConverter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

public class FonteGraphFragment extends Fragment {

    MainActivity mActivity = null;
    ArrayAdapter<String> mAdapterMedication = null;

    //Profile mProfile = null;

    List<String> mMedicationsArray = null;
    private String name;
    private int id;
    private Spinner functionList = null;
    private Spinner medicationList = null;
    private zoomType currentZoom = zoomType.ZOOM_ALL;
    private LineChart mChart = null;
    private Graph mGraph = null;
    private DAOFonte mDbFonte = null;
    private DAOLongTerm mDbLongTerm = null;
    private DAOMedication mDbMedication = null;
    private View mFragmentView = null;
    private OnItemSelectedListener onItemSelectedList = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (parent.getId() == R.id.filterMachine)
                updateFunctionSpinner(); // Update functions only when changing medication
            drawGraph();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private OnClickListener onZoomClick = v -> {
        switch (v.getId()) {
            case R.id.allbutton:
                currentZoom = zoomType.ZOOM_ALL;
                break;
            case R.id.lastweekbutton:
                currentZoom = zoomType.ZOOM_WEEK;
                break;
            case R.id.lastmonthbutton:
                currentZoom = zoomType.ZOOM_MONTH;
                break;
            case R.id.lastyearbutton:
                currentZoom = zoomType.ZOOM_YEAR;
                break;
        }
        mGraph.setZoom(currentZoom);
    };

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static FonteGraphFragment newInstance(String name, int id) {
        FonteGraphFragment f = new FonteGraphFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("id", id);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_graph, container, false);
        mFragmentView = view;
        functionList = view.findViewById(R.id.filterGraphFunction);
        medicationList = view.findViewById(R.id.filterGraphMachine);
        Button allButton = view.findViewById(R.id.allbutton);
        Button lastyearButton = view.findViewById(R.id.lastyearbutton);
        Button lastmonthButton = view.findViewById(R.id.lastmonthbutton);
        Button lastweekButton = view.findViewById(R.id.lastweekbutton);

        /* Initialisierung der Events */
        medicationList.setOnItemSelectedListener(onItemSelectedList);
        functionList.setOnItemSelectedListener(onItemSelectedList);

        allButton.setOnClickListener(onZoomClick);
        lastyearButton.setOnClickListener(onZoomClick);
        lastmonthButton.setOnClickListener(onZoomClick);
        lastweekButton.setOnClickListener(onZoomClick);

        /* Initialisierung des Graphen */
        mChart = view.findViewById(R.id.graphChart);
        mGraph = new Graph(getContext(), mChart, getResources().getText(R.string.no_weight_available).toString());

        /* Initialisierung der Historie */
        if (mDbFonte == null) mDbFonte = new DAOFonte(getContext());
        if (mDbLongTerm == null) mDbLongTerm = new DAOLongTerm(getContext());
        if (mDbMedication == null) mDbMedication = new DAOMedication(getContext());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getProfil() != null) {
            mMedicationsArray = new ArrayList<String>(0); //Data are refreshed on show //mDbFonte.getAllMedicationStrList(getProfil());
            // lMedicationArray = prepend(lMedicationArray, "All");
            mAdapterMedication = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item,
                mMedicationsArray);
            mAdapterMedication.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            medicationList.setAdapter(mAdapterMedication);
            mDbFonte.closeCursor();
        }

        if (this.getUserVisibleHint())
            refreshData();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (MainActivity) activity;
    }

    @Override
    public void onStop() {
        super.onStop();
        // Save Shared Preferences
    }

    public MainActivity getMainActivity() {
        return this.mActivity;
    }

    private void updateFunctionSpinner() {
        if (medicationList.getSelectedItem() == null) return;  // List not yet initialized.
        String lMedicationStr = medicationList.getSelectedItem().toString();
        Medication medication = mDbMedication.getMedication(lMedicationStr);
        if (medication == null) return;

        ArrayAdapter<String> adapterFunction = null;

        if (medication.getType() == DAOMedication.TYPE_FONTE) {
            adapterFunction = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item,
                mActivity.getResources().getStringArray(R.array.graph_functions));
        } else if (medication.getType() == DAOMedication.TYPE_LONGTERM) {
            adapterFunction = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item,
                mActivity.getResources().getStringArray(R.array.graph_cardio_functions));
        }
        adapterFunction.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        functionList.setAdapter(adapterFunction);
    }

    private void drawGraph() {

        if (getProfil() == null) return;

        String lMedication = null;
        String lFunction = null;
        int lDAOFunction = 0;

        mChart.clear();
        if (medicationList.getSelectedItem() == null) {
            return;
        }// So that we don't have any problems when there is no medication registered yet
        if (functionList.getSelectedItem() == null) {
            return;
        }

        lMedication = medicationList.getSelectedItem().toString();
        lFunction = functionList.getSelectedItem().toString();

        DAOMedication mDbMedication = new DAOMedication(mActivity);
        Medication m = mDbMedication.getMedication(lMedication);
        if (m == null) return;
        ArrayList<Entry> yVals = new ArrayList<>();
        Description desc = new Description();

        if (m.getType() == DAOMedication.TYPE_FONTE) {
            if (lFunction.equals(mActivity.getResources().getString(R.string.maxIntake1))) {
                lDAOFunction = DAOFonte.MAX1_FCT;
            } else if (lFunction.equals(mActivity.getResources().getString(R.string.maxIntake5))) {
                lDAOFunction = DAOFonte.MAX5_FCT;
            } else if (lFunction.equals(mActivity.getResources().getString(R.string.sum))) {
                lDAOFunction = DAOFonte.SUM_FCT;
            }
            desc.setText(lMedication + "/" + lFunction + "(kg)");

            List<DateGraphData> valueList = mDbFonte.getMedicationFunctionRecords(getProfil(), lMedication, lDAOFunction);

            if (valueList.size() <= 0) {
                // mChart.clear(); Already cleared
                return;
            }

            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int defaultUnit = 0;
            try {
                defaultUnit = Integer.valueOf(SP.getString("defaultUnit", "0"));
            } catch (NumberFormatException e) {
                defaultUnit = 0;
            }

            for (int i = 0; i < valueList.size(); i++) {
                Entry value = null;
                if (defaultUnit == UnitConverter.UNIT_LBS) {
                    value = new Entry((float) valueList.get(i).getX(), UnitConverter.KgtoLbs((float) valueList.get(i).getY()));//-minDate)/86400000));
                } else {
                    value = new Entry((float) valueList.get(i).getX(), (float) valueList.get(i).getY());//-minDate)/86400000));
                }
                yVals.add(value);
            }
        } else if (m.getType() == DAOMedication.TYPE_LONGTERM) {

            if (lFunction.equals(mActivity.getResources().getString(R.string.sumDistance))) {
                lDAOFunction = DAOLongTerm.DISTANCE_FCT;
                desc.setText(lMedication + "/" + lFunction + "(km)");
            } else if (lFunction.equals(mActivity.getResources().getString(R.string.sumDuration))) {
                lDAOFunction = DAOLongTerm.DURATION_FCT;
                desc.setText(lMedication + "/" + lFunction + "(min)");
            } else if (lFunction.equals(mActivity.getResources().getString(R.string.speed))) {
                lDAOFunction = DAOLongTerm.SPEED_FCT;
                desc.setText(lMedication + "/" + lFunction + "(km/h)");
            }


            List<DateGraphData> valueList = mDbLongTerm.getFunctionRecords(getProfil(), lMedication, lDAOFunction);

            if (valueList.size() <= 0) {
                return;
            }

            for (int i = 0; i < valueList.size(); i++) {
                Entry value = null;
                if (lDAOFunction == DAOLongTerm.DURATION_FCT) {
                    value = new Entry((float) valueList.get(i).getX(), (float) DateConverter.nbMinutes(valueList.get(i).getY()));
                } else if (lDAOFunction == DAOLongTerm.SPEED_FCT) { // Km/h
                    value = new Entry((float) valueList.get(i).getX(), (float) valueList.get(i).getY() * (60 * 60 * 1000));
                } else {
                    value = new Entry((float) valueList.get(i).getX(), (float) valueList.get(i).getY());
                }
                yVals.add(value);
            }
        }

        mGraph.getLineChart().setDescription(desc);
        mGraph.draw(yVals);
    }

    public String getName() {
        return getArguments().getString("name");
    }

    public int getFragmentId() {
        return getArguments().getInt("id", 0);
    }

    public DAOFonte getDB() {
        return mDbFonte;
    }

    private void refreshData() {
        //View fragmentView = getView();

        if (mFragmentView != null) {
            if (getProfil() != null) {
                //functionList.setOnItemSelectedListener(onItemSelectedList);
                if (mAdapterMedication == null) {
                    mMedicationsArray = mDbFonte.getAllMedicationStrList(getProfil());
                    //Data are refreshed on show
                    mAdapterMedication = new ArrayAdapter<String>(
                        getContext(), android.R.layout.simple_spinner_item,
                        mMedicationsArray);
                    mAdapterMedication.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    medicationList.setAdapter(mAdapterMedication);
                } else {
                    /* Initialisierung weiterer Spezifikationen*/
                    if (mMedicationsArray == null)
                        mMedicationsArray = mDbFonte.getAllMedicationStrList(getProfil());
                    else {
                        mMedicationsArray.clear();
                        mMedicationsArray.addAll(mDbFonte.getAllMedicationStrList(getProfil()));
                        mAdapterMedication.notifyDataSetChanged();
                        mDbFonte.closeCursor();
                    }
                }

                int position = mAdapterMedication.getPosition(this.getFontesMedication());
                if (position != -1) {
                    if (medicationList.getSelectedItemPosition() != position) {
                        medicationList.setSelection(position); // Refresh drawing
                    } else {
                        drawGraph();
                    }
                } else {
                    mChart.clear();
                }
            }
        }
    }

    private ArrayAdapter<String> getAdapterMedication() {
        ArrayAdapter<String> a;
        mMedicationsArray = new ArrayList<String>(0); //Data are refreshed on show //mDbFonte.getAllMedicationStrList(getProfil());

        mAdapterMedication = new ArrayAdapter<>(
            getContext(), android.R.layout.simple_spinner_item,
            mMedicationsArray);
        mAdapterMedication.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicationList.setAdapter(mAdapterMedication);
        return mAdapterMedication;
    }

    private Profile getProfil() {
        return mActivity.getCurrentProfil();
    }

    private String getFontesMedication() {
        return getMainActivity().getCurrentMedication();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        //medicationList
        if (!hidden) refreshData();
    }

    public void saveSharedParams(String toSave, String paramName) {
        SharedPreferences sharedPref = this.mActivity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(toSave, paramName);
        editor.apply();
    }

    public String getSharedParams(String paramName) {
        SharedPreferences sharedPref = this.mActivity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(paramName, "");
    }

}
