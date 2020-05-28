package com.pillhelper.specifications;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.pillhelper.DAO.DAOFonte;
import com.pillhelper.DAO.DAOMedication;
import com.pillhelper.DAO.Medication;
import com.pillhelper.DAO.Profile;
import com.pillhelper.MainActivity;
import com.pillhelper.R;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MedicationFragment extends Fragment {
    final int addId = 555;  //for add Medication menu
    Spinner typeList = null;
    Spinner musclesList = null;
    EditText description = null;
    ImageButton renameMedicationButton = null;
    ListView medicationList = null;
    Button addButton = null;
    AutoCompleteTextView searchField = null;
    MedicationCursorAdapter mTableAdapter;
    private String name;
    private int id;
    private DAOFonte mDbFonte = null;

    private DAOMedication mDbMedication = null;
    public TextWatcher onTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (charSequence.length() == 0) {
//                mTableAdapter.notifyDataSetChanged();
//                mTableAdapter = ((MedicationCursorAdapter) medicationList.getAdapter());
                refreshData();
            } else {
                mTableAdapter.getFilter().filter(charSequence);
                mTableAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };
    private OnItemClickListener onClickListItem = (parent, view, position, id) -> {
        // Get Medication Name selected
        TextView textViewID = view.findViewById(R.id.LIST_MEDICATION_ID);
        long medicationId = Long.valueOf(textViewID.getText().toString());

        MedicationDetailsPager MedicationDetailsFragment = MedicationDetailsPager.newInstance(medicationId, ((MainActivity) getActivity()).getCurrentProfil().getId());
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, MedicationDetailsFragment, MainActivity.MEDICATIONDETAILS);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    };
    private View.OnClickListener clickAddButton = v -> {

        // create a temporarily medication with name="" and open it like any other existing medication
        long new_id = -1;


        SweetAlertDialog dlg = new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE)
            .setTitleText("What type of medication ?")
            .setContentText("")
            .setCancelText(getResources().getText(R.string.LongLabel).toString())
            .setConfirmText(getResources().getText(R.string.FonteLabel).toString())
            .showCancelButton(true)
            .setConfirmClickListener(sDialog -> {
                long temp_medication_key = -1;
                String pMedication = "";
                DAOMedication lDAOMedication = new DAOMedication(getContext());
                temp_medication_key = lDAOMedication.addMedication(pMedication, "", DAOMedication.TYPE_FONTE, "", false);
                sDialog.dismissWithAnimation();

                MedicationDetailsPager MedicationDetailsFragment = MedicationDetailsPager.newInstance(temp_medication_key, ((MainActivity) getActivity()).getCurrentProfil().getId());
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.fragment_container, MedicationDetailsFragment, MainActivity.MEDICATIONDETAILS);
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();
            })
            .setCancelClickListener(sDialog -> {
                long temp_medication_key = -1;
                String pMedication = "";
                DAOMedication lDAOMedication = new DAOMedication(getContext());
                temp_medication_key = lDAOMedication.addMedication(pMedication, "", DAOMedication.TYPE_LONGTERM, "", false);
                sDialog.dismissWithAnimation();

                MedicationDetailsPager MedicationDetailsFragment = MedicationDetailsPager.newInstance(temp_medication_key, ((MainActivity) getActivity()).getCurrentProfil().getId());
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.fragment_container, MedicationDetailsFragment, MainActivity.MEDICATIONDETAILS);
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();
            });

        dlg.show();

        dlg.getButton(SweetAlertDialog.BUTTON_CONFIRM).setBackgroundResource(R.color.background_odd);
        dlg.getButton(SweetAlertDialog.BUTTON_CONFIRM).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        dlg.getButton(SweetAlertDialog.BUTTON_CANCEL).setBackgroundResource(R.color.background_odd);
        dlg.getButton(SweetAlertDialog.BUTTON_CANCEL).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);

    };
    private OnItemSelectedListener onItemSelectedList = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            //refreshData();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static MedicationFragment newInstance(String name, int id) {
        MedicationFragment f = new MedicationFragment();

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

        // activates onCreateOptionsMenu in this fragment
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_medication, container, false);

        addButton = view.findViewById(R.id.addMedication);
        addButton.setOnClickListener(clickAddButton);

        searchField = view.findViewById(R.id.searchField);
        searchField.addTextChangedListener(onTextChangeListener);

        //typeList = view.findViewById(R.id.filterDate);
        //medicationList = (Spinner) view.findViewById(R.id.filterMedication);
        //renameMedicationButton = view.findViewById(R.id.imageMedicationRename);
        medicationList = view.findViewById(R.id.listMedication);
        //musclesList = view.findViewById(R.id.listFilterRecord);

        medicationList.setOnItemClickListener(onClickListItem);

        // Initialisation de l'historique
        mDbFonte = new DAOFonte(view.getContext());
        mDbMedication = new DAOMedication(view.getContext());

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items

        switch (item.getItemId()) {
            case addId:
                clickAddButton.onClick(getView());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        mDbMedication.deleteAllEmptyMedication();
        refreshData();

        // for resetting the search field at the start:
        searchField.setText("");

        // Initialisation des evenements
        medicationList.setOnItemSelectedListener(onItemSelectedList);
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

    public MedicationFragment getThis() {
        return this;
    }

    private void refreshData() {
        Cursor c = null;
        Cursor oldCursor = null;
        ArrayList<Medication> records = null;

        View fragmentView = getView();
        if (fragmentView != null) {
            if (getProfil() != null) {

                // Version avec table Medication
                c = mDbMedication.getAllMedication();
                if (c == null || c.getCount() == 0) {
                    //Toast.makeText(getActivity(), "No records", Toast.LENGTH_SHORT).show();
                    medicationList.setAdapter(null);
                } else {
                    if (medicationList.getAdapter() == null) {
                        mTableAdapter = new MedicationCursorAdapter(this.getView().getContext(), c, 0, mDbMedication);
                        medicationList.setAdapter(mTableAdapter);
                    } else {
                        mTableAdapter = ((MedicationCursorAdapter) medicationList.getAdapter());
                        oldCursor = mTableAdapter.swapCursor(c);
                        if (oldCursor != null) oldCursor.close();
                    }

                    mTableAdapter.setFilterQueryProvider(constraint -> mDbMedication.getFilteredMedication(constraint));
                }
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) refreshData();
    }

    private Profile getProfil() {
        return ((MainActivity) getActivity()).getCurrentProfil();
    }

}
