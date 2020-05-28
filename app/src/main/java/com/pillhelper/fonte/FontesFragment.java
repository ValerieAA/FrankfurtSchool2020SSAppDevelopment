package com.pillhelper.fonte;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.pillhelper.*;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.pillhelper.DAO.DAOFonte;
import com.pillhelper.DAO.DAOLongTerm;
import com.pillhelper.DAO.DAOMedication;
import com.pillhelper.DAO.DAORecord;
import com.pillhelper.DAO.Fonte;
import com.pillhelper.DAO.IRecord;
import com.pillhelper.DAO.Medication;
import com.pillhelper.DAO.Profile;
import com.pillhelper.DAO.medicationdosemeasures.DAOMedicationMeasure;
import com.pillhelper.specifications.MedicationCursorAdapter;
import com.pillhelper.specifications.MedicationDetailsPager;
import com.pillhelper.utils.DateConverter;
import com.pillhelper.utils.ImageUtil;
import com.pillhelper.utils.UnitConverter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FontesFragment extends Fragment {

    MainActivity mActivity = null;
    Profile mProfile = null;
    EditText dateEdit = null;
    AutoCompleteTextView medicationEdit = null;
    MedicationArrayAdapter medicationEditAdapter = null;

    EditText serieEdit = null;
    EditText intakeEdit = null;
    EditText doseEdit = null;
    LinearLayout detailsLayout = null;
    Button addButton = null;
    ExpandedListView recordList = null;
    String[] medicationListArray = null;
    ImageButton medicationListButton = null;
    Spinner unitSpinner = null;
    ImageButton detailsExpandArrow = null;
    EditText restTimeEdit = null;
    CheckBox restTimeCheck = null;

    DatePickerDialogFragment mDateFrag = null;
    TimePickerDialogFragment mDurationFrag = null;

    CircularImageView medicationImage = null;
    TextView minText = null;
    TextView maxText = null;
    int lTableColor = 1;

    AlertDialog medicationListDialog;
    LinearLayout minMaxLayout = null;

    // Selection part
    LinearLayout medicationTypeSelectorLayout = null;
    TextView MedicationSelector = null;
    TextView LongTermSelector = null;
    int selectedType = DAOMedication.TYPE_FONTE;

    // LongTerm Part
    LinearLayout MedicationLayout = null;
    LinearLayout LongTermLayout = null;
    LinearLayout restTimeLayout = null;
    EditText distanceEdit = null;
    EditText durationEdit = null;

    public static FontesFragment newInstance(String fontes, int i) {
        return null;
    }

    public TimePickerDialog.OnTimeSetListener timeSet = (view, hourOfDay, minute) -> {

        // Do something with the time chosen by the user

        String strMinute = "00";
        String strHour = "00";

        if (minute < 10) strMinute = "0" + Integer.toString(minute);
        else strMinute = Integer.toString(minute);
        if (hourOfDay < 10) strHour = "0" + Integer.toString(hourOfDay);
        else strHour = Integer.toString(hourOfDay);

        String date = strHour + ":" + strMinute;
        durationEdit.setText(date);

    };
    private DAOFonte mDbMedications = null;
    private DAOLongTerm mDbLongTerm = null;
    private DAORecord mDb = null;
    private DAOMedicationMeasure mDbMedication = null;
    private OnClickListener collapseDetailsClick = v -> {
        detailsLayout.setVisibility(detailsLayout.isShown() ? View.GONE : View.VISIBLE);
        detailsExpandArrow.setImageResource(detailsLayout.isShown() ? R.drawable.baseline_keyboard_arrow_up_black_36 : R.drawable.baseline_keyboard_arrow_down_black_36);

    };}
