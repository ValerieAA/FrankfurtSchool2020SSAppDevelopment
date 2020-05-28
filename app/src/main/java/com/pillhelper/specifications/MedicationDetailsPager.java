package com.pillhelper.specifications;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.pillhelper.DAO.DAOMedication;
import com.pillhelper.DAO.DAOProfil;
import com.pillhelper.DAO.DAORecord;
import com.pillhelper.DAO.IRecord;
import com.pillhelper.DAO.Medication;
import com.pillhelper.DAO.Profile;
import com.pillhelper.MainActivity;
import com.pillhelper.R;
import com.pillhelper.fonte.FonteHistoryFragment;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.List;

public class MedicationDetailsPager extends Fragment {

    Toolbar top_toolbar = null;
    long medicationIdArg = 0;
    long medicationProfilIdArg = 0;
    FragmentPagerItemAdapter pagerAdapter = null;
    ViewPager mViewPager = null;
    SmartTabLayout viewPagerTab = null;
    ImageButton medicationDelete = null;
    ImageButton medicationSave = null;
    MaterialFavoriteButton medicationFavorite = null;
    Medication medication = null;
    boolean isFavorite = false;
    boolean toBeSaved = false;
    DAOMedication mDbMedication = null;
    DAORecord mDbRecord = null;

    private String name;
    private int id;
    private View.OnClickListener onClickToolbarItem = v -> {
        // Handle presses on the action bar items
        switch (v.getId()) {
            case R.id.action_medication_save:
                saveMedication();
                getActivity().findViewById(R.id.tab_medication_details).requestFocus();
                break;
            case R.id.action_medication_delete:
                deleteMedication();
                break;
            default:
                saveMedicationDialog();
        }
    };

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static MedicationDetailsPager newInstance(long medicationId, long medicationProfile) {
        MedicationDetailsPager f = new MedicationDetailsPager();

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

        View view = inflater.inflate(R.layout.medication_pager, container, false);

        // Locate the viewpager in activity_main.xml
        mViewPager = view.findViewById(R.id.pager);

        if (mViewPager.getAdapter() == null) {

            Bundle args = this.getArguments();
            medicationIdArg = args.getLong("medicationID");
            medicationProfilIdArg = args.getLong("medicationProfile");

            pagerAdapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(this.getContext())
                .add("Medication", MedicationDetailsFragment.class, args)
                .add("History", FonteHistoryFragment.class, args)
                .create());

            mViewPager.setAdapter(pagerAdapter);

            viewPagerTab = view.findViewById(R.id.viewpagertab);
            viewPagerTab.setViewPager(mViewPager);

            viewPagerTab.setOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    Fragment frag1 = pagerAdapter.getPage(position);
                    if (frag1 != null)
                        frag1.onHiddenChanged(false); // Refresh data
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }

        mDbRecord = new DAORecord(getContext());
        mDbMedication = new DAOMedication(getContext());

        ((MainActivity) getActivity()).getActivityToolbar().setVisibility(View.GONE);
        top_toolbar = view.findViewById(R.id.actionToolbarMedication);
        top_toolbar.setNavigationIcon(R.drawable.ic_back);
        top_toolbar.setNavigationOnClickListener(onClickToolbarItem);

        medicationDelete = view.findViewById(R.id.action_medication_delete);
        medicationSave = view.findViewById(R.id.action_medication_save);
        medicationFavorite = view.findViewById(R.id.favButton);
        medicationFavorite.setOnClickListener(v -> {
            MaterialFavoriteButton mFav = (MaterialFavoriteButton) v;
            boolean t = mFav.isFavorite();
            mFav.setFavoriteAnimated(!t);
            isFavorite = !t;
            requestForSave();
        });
        medication = mDbMedication.getMedication(medicationIdArg);
        // TODO wir erschaffen die Medikamente wenn sie noch nicht da sind
        medicationFavorite.setFavorite(medication.getFavorite());

        medicationSave.setOnClickListener(onClickToolbarItem);
        medicationSave.setVisibility(View.GONE); // Hide Save button by default

        medicationDelete.setOnClickListener(onClickToolbarItem);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void requestForSave() {
        toBeSaved = true; // setting state
        medicationSave.setVisibility(View.VISIBLE);
    }

    private void saveMedicationDialog() {
        if (getMedicationFragment().toBeSaved() || toBeSaved) {
            // Confirm
            AlertDialog.Builder backDialogBuilder = new AlertDialog.Builder(getActivity());

            backDialogBuilder.setTitle(getActivity().getResources().getText(R.string.global_confirm));
            backDialogBuilder.setMessage(getActivity().getResources().getText(R.string.backDialog_confirm_text));

            // Yes
            backDialogBuilder.setPositiveButton(getResources().getString(R.string.global_yes), (dialog, which) -> {
                if (saveMedication()) {
                    getActivity().onBackPressed();
                }
            });

            backDialogBuilder.setNegativeButton(getResources().getString(R.string.global_no), (dialog, which) -> getActivity().onBackPressed());

            AlertDialog backDialog = backDialogBuilder.create();
            backDialog.show();

        } else {
            getActivity().onBackPressed();
        }
    }

    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    private boolean saveMedication() {
        boolean result = false;
        final Medication initialMedication = medication;
        final Medication newMedication = getMedicationFragment().getMedication();
        final String lMedicationName = newMedication.getName(); // Potentiel nouveau nom dans le EditText

        // Si le nom est different du nom actuel
        if (lMedicationName.equals("")) {
            KToast.warningToast(getActivity(), getResources().getText(R.string.name_is_required).toString(), Gravity.BOTTOM, KToast.LENGTH_SHORT);
        } else if (!initialMedication.getName().equals(lMedicationName)) {
            final Medication medicationWithSameName = mDbMedication.getMedication(lMedicationName);
            // Si une medication existe avec le meme nom => Merge
            if (medicationWithSameName != null && newMedication.getId() != medicationWithSameName.getId() && newMedication.getType() != medicationWithSameName.getType()) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());

                dialogBuilder.setTitle(getActivity().getResources().getText(R.string.global_warning));
                dialogBuilder.setMessage(R.string.renameMedication_error_text2);
                dialogBuilder.setPositiveButton(getResources().getText(R.string.global_yes), (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            } else if (medicationWithSameName != null && newMedication.getId() != medicationWithSameName.getId() && newMedication.getType() == medicationWithSameName.getType()) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());

                dialogBuilder.setTitle(getActivity().getResources().getText(R.string.global_warning));
                dialogBuilder.setMessage(getActivity().getResources().getText(R.string.renameMedication_warning_text));
                // Si oui, supprimer la base de donnee et refaire un Start.
                dialogBuilder.setPositiveButton(getResources().getText(R.string.global_yes), (dialog, which) -> {
                    // Rename all the records with that medication and rename them
                    DAORecord lDbRecord = new DAORecord(getView().getContext());
                    DAOProfil mDbProfil = new DAOProfil(getView().getContext());
                    Profile lProfile = mDbProfil.getProfil(medicationProfilIdArg);

                    List<IRecord> listRecords = lDbRecord.getAllRecordByMedicationArray(lProfile, initialMedication.getName()); // Recupere tous les records de la medication courante
                    for (IRecord record : listRecords) {
                        record.setMedication(newMedication.getName()); // Change avec le nouveau nom. Normalement pas utile.
                        record.setMedicationKey(medicationWithSameName.getId()); // Met l'ID de la nouvelle medication
                        lDbRecord.updateRecord(record); // Met a jour
                    }

                    mDbMedication.delete(initialMedication); // Supprime l'ancienne medication

                    toBeSaved = false;
                    medicationSave.setVisibility(View.GONE);
                    getActivity().onBackPressed();
                });

                dialogBuilder.setNegativeButton(getResources().getText(R.string.global_no), (dialog, which) -> {
                    // Do nothing but close the dialog
                    dialog.dismiss();
                });

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            } else {
                newMedication.setFavorite(medicationFavorite.isFavorite());
                this.mDbMedication.updateMedication(newMedication);

                // Rename all the records with that medication and rename them
                DAORecord lDbRecord = new DAORecord(getContext());
                DAOProfil mDbProfil = new DAOProfil(getContext());
                Profile lProfile = mDbProfil.getProfil(medicationProfilIdArg);
                List<IRecord> listRecords = lDbRecord.getAllRecordByMedicationArray(lProfile, initialMedication.getName()); // Recupere tous les records de la medication courante
                for (IRecord record : listRecords) {
                    record.setMedication(lMedicationName); // Neuer Name
                    lDbRecord.updateRecord(record); // Update tag
                }

                medicationSave.setVisibility(View.GONE);
                toBeSaved = false;
                getMedicationFragment().medicationSaved();
                result = true;
            }
        } else {
            // Wenn wir den Namen verändern wollen
            newMedication.setFavorite(medicationFavorite.isFavorite());
            mDbMedication.updateMedication(newMedication);

            medicationSave.setVisibility(View.GONE);
            toBeSaved = false;
            getMedicationFragment().medicationSaved();
            result = true;
        }
        return result;
    }

    private void deleteMedication() {
        // Kreire einen Alert
        AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(this.getActivity());

        deleteDialogBuilder.setTitle(getActivity().getResources().getText(R.string.global_confirm));
        deleteDialogBuilder.setMessage(getActivity().getResources().getText(R.string.deleteMedication_confirm_text));

        deleteDialogBuilder.setPositiveButton(this.getResources().getString(R.string.global_yes), (dialog, which) -> {
            // Suppress the medication
            mDbMedication.delete(medication);
            // Suppress the associated Fontes records
            deleteRecordsAssociatedToMedication();
            getActivity().onBackPressed();
        });

        deleteDialogBuilder.setNegativeButton(this.getResources().getString(R.string.global_no), (dialog, which) -> {
            // Do nothing
            dialog.dismiss();
        });

        AlertDialog deleteDialog = deleteDialogBuilder.create();
        deleteDialog.show();
    }

    private void deleteRecordsAssociatedToMedication() {
        DAORecord mDbRecord = new DAORecord(getContext());
        DAOProfil mDbProfil = new DAOProfil(getContext());

        Profile lProfile = mDbProfil.getProfil(this.medicationProfilIdArg);

        List<IRecord> listRecords = mDbRecord.getAllRecordByMedicationArray(lProfile, medication.getName());
        for (IRecord record : listRecords) {
            mDbRecord.deleteRecord(record.getId());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.medication_details_menu, menu);

        MenuItem item = menu.findItem(R.id.action_medication_save);
        item.setVisible(toBeSaved);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public MedicationDetailsFragment getMedicationFragment() {
        MedicationDetailsFragment mpMedicationFrag;
        mpMedicationFrag = (MedicationDetailsFragment) pagerAdapter.getPage(0);
        return mpMedicationFrag;
    }

    public FonteHistoryFragment getHistoricFragment() {
        FonteHistoryFragment mpHistoryFrag;
        mpHistoryFrag = (FonteHistoryFragment) pagerAdapter.getPage(1);
        return mpHistoryFrag;
    }

    public ViewPager getViewPager() {
        return (ViewPager) getView().findViewById(R.id.pager);
    }

    public FragmentPagerItemAdapter getViewPagerAdapter() {
        return (FragmentPagerItemAdapter) ((ViewPager) (getView().findViewById(R.id.pager))).getAdapter();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            // rafraichit le fragment courant

            if (getViewPagerAdapter() != null) {
                // Moyen de rafraichir tous les fragments. Attention, les View des fragments peuvent avoir ete detruit.
                // Il faut donc que cela soit pris en compte dans le refresh des fragments.
                Fragment frag1;
                for (int i = 0; i < 3; i++) {
                    frag1 = getViewPagerAdapter().getPage(i);
                    if (frag1 != null)
                        frag1.onHiddenChanged(false); // Refresh data
                }
            }
        }
    }
}
