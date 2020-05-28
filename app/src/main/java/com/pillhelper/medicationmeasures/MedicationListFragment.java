package com.pillhelper.medicationmeasures;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.pillhelper.DAO.Profile;
import com.pillhelper.DAO.medicationdosemeasures.MedicationDose;
import com.pillhelper.DAO.medicationdosemeasures.DAOMedicationMeasure;
import com.pillhelper.MainActivity;
import com.pillhelper.R;

import java.util.ArrayList;

public class MedicationListFragment extends Fragment {
    Spinner typeList = null;
    Spinner medicationList = null;
    EditText description = null;
    ImageButton renameMedicationButton = null;
    ArrayList<MedicationDose> dataModels;
    ListView measureList = null;
    private String name;
    private int id;
    private DAOMedicationMeasure mDbBodyMeasures = null;
    private OnItemClickListener onClickListItem = (parent, view, position, id) -> {

        TextView textView = view.findViewById(R.id.LIST_MEDICATION_ID);
        int bodyPartID = Integer.parseInt(textView.getText().toString());

        MedicationDetailsFragment fragment = MedicationDetailsFragment.newInstance(bodyPartID, true);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment, MainActivity.BODYTRACKINGDETAILS);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    };

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static MedicationListFragment newInstance(String name, int id) {
        MedicationListFragment f = new MedicationListFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("id", id);
        f.setArguments(args);

        return f;
    }

    private static String[] prepend(String[] a, String el) {
        String[] c = new String[a.length + 1];
        c[0] = el;
        System.arraycopy(a, 0, c, 1, a.length);
        return c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_medicationtracking, container, false);

        DAOMedicationMeasure mdbMeasure = new DAOMedicationMeasure(this.getContext());

        measureList = view.findViewById(R.id.listBodyMeasures);

        dataModels = new ArrayList<>();

        // Reference back to the original Table, Überblick
        //protected CharSequence[] _medication = {"Cardiovascular", "Respiratory", "Gastrointestinal", "Renal", "Neurological", "Antibiotics", "Dermatologic"};
        //    protected boolean[] _selections = new boolean[_medication.length];

        //     dataModels.add(new MedicationDose(MedicationDose.CARDIOVASCULAR, mdbMeasure.getLastMedicationMeasures(MedicationDose.CARDIOVASCULAR, ((MainActivity) getActivity()).getCurrentProfil())));
        //   dataModels.add(new MedicationDose(MedicationDose.RESPIRATORY, mdbMeasure.getLastMedicationMeasures(MedicationDose.RESPIRATORY, ((MainActivity) getActivity()).getCurrentProfil())));
        //  dataModels.add(new MedicationDose(MedicationDose.GASTROINTESTINAL, mdbMeasure.getLastMedicationMeasures(MedicationDose.GASTROINTESTINAL, ((MainActivity) getActivity()).getCurrentProfil())));
        //  dataModels.add(new MedicationDose(MedicationDose.DOSE, mdbMeasure.getLastMedicationMeasures(MedicationDose.DOSE, ((MainActivity) getActivity()).getCurrentProfil())));
   //     dataModels.add(new MedicationDose(MedicationDose.RENAL, mdbMeasure.getLastBodyMeasures(MedicationDose.RENAL, ((MainActivity) getActivity()).getCurrentProfil())));

        MedicationListAdapter adapter = new MedicationListAdapter(dataModels, getContext());

        measureList.setAdapter(adapter);
        measureList.setOnItemClickListener((parent, view1, position, id) -> {
            MedicationDose dataModel = dataModels.get(position);
        });

        measureList.setOnItemClickListener(onClickListItem);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public String getName() {
        return getArguments().getString("name");
    }

    public int getFragmentId() {
        return getArguments().getInt("id", 0);
    }

    public MedicationListFragment getThis() {
        return this;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

    }

    private Profile getProfil() {
        return ((MainActivity) getActivity()).getCurrentProfil();
    }

}
