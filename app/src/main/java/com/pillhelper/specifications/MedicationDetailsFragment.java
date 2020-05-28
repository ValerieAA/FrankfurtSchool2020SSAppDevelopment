package com.pillhelper.specifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

import com.pillhelper.BtnClickListener;
import com.pillhelper.DAO.DAOMedication;
import com.pillhelper.DAO.DAORecord;
import com.pillhelper.DAO.Medication;
import com.pillhelper.R;
import com.pillhelper.utils.ImageUtil;
import com.pillhelper.utils.RealPathUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MedicationDetailsFragment extends Fragment {
    public final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 101;
    // http://labs.makemachine.net/2010/03/android-multi-selection-dialogs/
    protected CharSequence[] _medication = {"Cardiovascular", "Respiratory", "Gastrointestinal", "Renal", "Neurological", "Antibiotics", "Dermatologic"};
    protected boolean[] _selections = new boolean[_medication.length];
    Spinner typeList = null;
    EditText medicationList = null;
    EditText medicationName = null;
    EditText medicationDescription = null;
    ImageView medicationPhoto = null;
    FloatingActionButton medicationAction = null;
    LinearLayout medicationPhotoLayout = null;

    // Selection part
    LinearLayout medicationTypeSelectorLayout = null;
    TextView medicationSelector = null;
    TextView LongTermSelector = null;
    int selectedType = DAOMedication.TYPE_FONTE;
    String medicationNameArg = null;
    long medicationIdArg = 0;
    long medicationProfilIdArg = 0;
    boolean isImageFitToScreen = false;
    MedicationDetailsPager pager = null;
    ArrayList selectMedicationList = new ArrayList();
    DAOMedication mDbMedication = null;
    DAORecord mDbRecord = null;
    Medication mMedication;

    View fragmentView = null;

    ImageUtil imgUtil = null;
    boolean isCreateMedicationDialogActive = false;
    String mCurrentPhotoPath = null;
    private boolean toBeSaved;
    public TextWatcher watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
            requestForSave();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    private BtnClickListener itemClickDeleteRecord = this::showDeleteDialog;
    private OnClickListener onClickMedicationList = v -> CreateMedicationDialog();
    private OnLongClickListener onLongClickMedicationPhoto = v -> CreatePhotoSourceDialog();
    private OnClickListener onClickMedicationPhoto = v -> CreatePhotoSourceDialog();
    private OnFocusChangeListener onFocusMedicationList = (arg0, arg1) -> {
        if (arg1) {
            CreateMedicationDialog();
        }
    };

    // Get the cursor, positioned to the corresponding row in the result set
    //Cursor cursor = (Cursor) listView.getItemAtPosition(position);

    private OnClickListener clickMedicationTypeSelector = v -> {
        switch (v.getId()) {
            case R.id.LongTermSelection:
                LongTermSelector.setBackgroundColor(getResources().getColor(R.color.background_odd));
                medicationSelector.setBackgroundColor(getResources().getColor(R.color.background));
                selectedType = DAOMedication.TYPE_LONGTERM;
                break;
            case R.id.medication_description:
            default:
                LongTermSelector.setBackgroundColor(getResources().getColor(R.color.background));
                medicationSelector.setBackgroundColor(getResources().getColor(R.color.background_odd));
                selectedType = DAOMedication.TYPE_FONTE;
                break;
        }
    };

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static MedicationDetailsFragment newInstance(long medicationId, long medicationProfile) {
        MedicationDetailsFragment f = new MedicationDetailsFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putLong("medicationID", medicationId);
        args.putLong("medicationProfile", medicationProfile);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.medication_details, container, false);
        fragmentView = view;

        // Initialisation de l'historique
        mDbMedication = new DAOMedication(view.getContext());
        mDbRecord = new DAORecord(view.getContext());

        medicationName = view.findViewById(R.id.medication_name);
        medicationDescription = view.findViewById(R.id.medication_description);
        medicationList = view.findViewById(R.id.medication_dose);
        medicationPhoto = view.findViewById(R.id.medication_photo);

        medicationPhotoLayout = view.findViewById(R.id.medication_photo_layout);
        medicationSelector = view.findViewById(R.id.MedicationSelection);
        LongTermSelector = view.findViewById(R.id.LongTermSelection);
        medicationTypeSelectorLayout = view.findViewById(R.id.MedicationTypeSelectionLayout);

        medicationAction = view.findViewById(R.id.actionCamera);

        imgUtil = new ImageUtil(medicationPhoto);

        buildMedicationTable();

        Bundle args = this.getArguments();

        medicationIdArg = args.getLong("medicationID");
        medicationProfilIdArg = args.getLong("medicationProfile");

        // set events

        //medicationFavorite.setOnClickListener(onClickFavoriteItem);

        medicationList.setOnClickListener(onClickMedicationList);
        medicationList.setOnFocusChangeListener(onFocusMedicationList);

        //MedicationSelector.setOnClickListener(clickMedicationTypeSelector);
        //LongTermSelector.setOnClickListener(clickMedicationTypeSelector);

        medicationPhoto.setOnLongClickListener(onLongClickMedicationPhoto);
        medicationPhoto.setOnClickListener(v -> {
            if (isImageFitToScreen) {
                isImageFitToScreen = false;
                medicationPhoto.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                medicationPhoto.setAdjustViewBounds(true);
                medicationPhoto.setMaxHeight((int) (getView().getHeight() * 0.2));
                medicationPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                if (mCurrentPhotoPath != null && !mCurrentPhotoPath.isEmpty()) {
                    File f = new File(mCurrentPhotoPath);
                    if (f.exists()) {

                        isImageFitToScreen = true;

                        // Get the dimensions of the bitmap
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        bmOptions.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                        float photoW = bmOptions.outWidth;
                        float photoH = bmOptions.outHeight;

                        // Determine how much to scale down the image
                        int scaleFactor = (int) (photoW / (medicationPhoto.getWidth())); //Math.min(photoW/targetW, photoH/targetH);medicationPhoto.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        medicationPhoto.setAdjustViewBounds(true);
                        medicationPhoto.setMaxHeight((int) (photoH / scaleFactor));
                        medicationPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    }
                }
            }
        });
        medicationAction.setOnClickListener(onClickMedicationPhoto);

        mMedication = mDbMedication.getMedication(medicationIdArg);
        medicationNameArg = mMedication.getName();

        if (medicationNameArg.equals("")) {
            requestForSave();
        }

        medicationName.setText(medicationNameArg);
        medicationDescription.setText(mMedication.getDescription());
        medicationList.setText(this.getInputFromDBString(mMedication.getMedicationParts()));
        mCurrentPhotoPath = mMedication.getPicture();
        medicationTypeSelectorLayout.setVisibility(View.GONE);

        if (mMedication.getType() == DAOMedication.TYPE_LONGTERM) {
            LongTermSelector.setBackgroundColor(getResources().getColor(R.color.background_odd));
            medicationSelector.setVisibility(View.GONE);
            medicationSelector.setBackgroundColor(getResources().getColor(R.color.background));
            selectedType = mMedication.getType();
            view.findViewById(R.id.medication_dose).setVisibility(View.GONE);
            view.findViewById(R.id.medication_dose_textview).setVisibility(View.GONE);
        } else {
            LongTermSelector.setBackgroundColor(getResources().getColor(R.color.background));
            LongTermSelector.setVisibility(View.GONE);
            medicationSelector.setBackgroundColor(getResources().getColor(R.color.background_odd));
            selectedType = mMedication.getType();
        }

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once :
                fragmentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Here you can get the size :)

                if (mCurrentPhotoPath != null && !mCurrentPhotoPath.isEmpty()) {
                    ImageUtil.setPic(medicationPhoto, mCurrentPhotoPath);
                } else {
                    if (mMedication.getType() == DAOMedication.TYPE_FONTE) {
                        imgUtil.getView().setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_accessibility_black_24dp));
                    } else {
                        imgUtil.getView().setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_accessibility_black_24dp));
                    }
                    medicationPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
                medicationPhoto.setMaxHeight((int) (getView().getHeight() * 0.2)); // Taille initiale
            }
        });

        medicationName.addTextChangedListener(watcher);
        medicationDescription.addTextChangedListener(watcher);
        medicationList.addTextChangedListener(watcher);

        imgUtil.setOnDeleteImageListener(imgUtil -> {
            imgUtil.getView().setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_accessibility_black_24dp));
            mCurrentPhotoPath = null;
            requestForSave();
        });

        if (getParentFragment() instanceof MedicationDetailsPager) {
            pager = (MedicationDetailsPager) getParentFragment();
        }

        return view;
    }

    private void buildMedicationTable() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void showDeleteDialog(final long idToDelete) {

        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText(getString(R.string.DeleteRecordDialog))
            .setContentText(getResources().getText(R.string.areyousure).toString())
            .setCancelText(getResources().getText(R.string.global_no).toString())
            .setConfirmText(getResources().getText(R.string.global_yes).toString())
            .showCancelButton(true)
            .setConfirmClickListener(sDialog -> {
                mDbRecord.deleteRecord(idToDelete);
                KToast.infoToast(getActivity(), getResources().getText(R.string.removedid).toString(), Gravity.BOTTOM, KToast.LENGTH_LONG);
                sDialog.dismissWithAnimation();
            })
            .show();
    }

    private boolean CreateMedicationDialog() {
        if (isCreateMedicationDialogActive)
            return true;

        isCreateMedicationDialogActive = true;

        AlertDialog.Builder newProfilBuilder = new AlertDialog.Builder(this.getActivity());

        newProfilBuilder.setTitle(this.getResources().getString(R.string.selectPrescriptionDialogLabel));
        newProfilBuilder.setMultiChoiceItems(_medication, _selections, (arg0, arg1, arg2) -> {
            if (arg2) {
                // If user select a item then add it in selected items
                selectMedicationList.add(arg1);
            } else if (selectMedicationList.contains(arg1)) {
                // if the item is already selected then remove it
                selectMedicationList.remove(Integer.valueOf(arg1));
            }
        });

        // Set an EditText view to get user input
        newProfilBuilder.setPositiveButton(getResources().getString(R.string.global_ok), (dialog, whichButton) -> {
            StringBuilder msg = new StringBuilder();
            int i = 0;
            boolean firstSelection = true;
            // ( Select your Medication Dose)
            for (i = 0; i < _selections.length; i++) {
                if (_selections[i] && firstSelection) {
                    msg = new StringBuilder(_medication[i].toString());
                    firstSelection = false;
                } else if (_selections[i] && !firstSelection) {
                    msg.append(";").append(_medication[i]);
                }
            }
            //}
            setMuscleText(msg.toString());
            isCreateMedicationDialogActive = false;
        });
        newProfilBuilder.setNegativeButton(getResources().getString(R.string.global_cancel), (dialog, whichButton) -> isCreateMedicationDialogActive = false);

        newProfilBuilder.show();

        return true;
    }

    private boolean CreatePhotoSourceDialog() {
        if (imgUtil == null)
            imgUtil = new ImageUtil();

        return imgUtil.CreatePhotoSourceDialog(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ImageUtil.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    mCurrentPhotoPath = imgUtil.getFilePath();
                    ImageUtil.setPic(medicationPhoto, mCurrentPhotoPath);
                    ImageUtil.saveThumb(mCurrentPhotoPath);
                    imgUtil.galleryAddPic(this, mCurrentPhotoPath);
                    requestForSave();
                }
                break;
            case ImageUtil.REQUEST_PICK_GALERY_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    String realPath;
                    realPath = RealPathUtil.getRealPath(this.getContext(), data.getData());

                    ImageUtil.setPic(medicationPhoto, realPath);
                    ImageUtil.saveThumb(realPath);
                    mCurrentPhotoPath = realPath;
                    requestForSave();
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == Activity.RESULT_OK) {
                    Uri resultUri = result.getUri();
                    String realPath;
                    realPath = RealPathUtil.getRealPath(this.getContext(), resultUri);

                    // Wir legen den File bei uns an
                    File SourceFile = new File(realPath);

                    File storageDir = null;
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + timeStamp + ".jpg";
                    String state = Environment.getExternalStorageState();
                    if (!Environment.MEDIA_MOUNTED.equals(state)) {
                        return;
                    } else {
                        //We use the Pillhelper directory for saving our .csv file.
                        storageDir = Environment.getExternalStoragePublicDirectory("/pillhelper/Camera/");
                        if (!storageDir.exists()) {
                            storageDir.mkdirs();
                        }
                    }
                    File DestinationFile = null;

                    try {
                        DestinationFile = imgUtil.moveFile(SourceFile, storageDir);
                        Log.v("Moving", "Moving file successful.");
                        realPath = DestinationFile.getPath();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.v("Moving", "Moving file failed.");
                    }

                    ImageUtil.setPic(medicationPhoto, realPath);
                    ImageUtil.saveThumb(realPath);
                    mCurrentPhotoPath = realPath;
                    requestForSave();
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
                break;
        }
    }

    public void setMuscleText(String t) {
        medicationList.setText(t);
    }

    public MedicationDetailsFragment getThis() {
        return this;
    }

    private void requestForSave() {
        toBeSaved = true; // setting state
        if (pager != null) pager.requestForSave();
    }
    //Reference back to the original Table, Überblick
    //protected CharSequence[] _medication = {"Cardiovascular", "Respiratory", "Gastrointestinal", "Renal", "Neurological", "Antibiotics", "Dermatologic"};
    //   protected boolean[] _selections = new boolean[_medication.length];
/*
    public void buildMedicationTable() {
        _medication[0] = getActivity().getResources().getString(R.string.Paracetamol);
        _medication[1] = getActivity().getResources().getString(R.string.Respiratory);
        _medication[2] = getActivity().getResources().getString(R.string.Gastrointestinal);
        _medication[3] = getActivity().getResources().getString(R.string.Renal);
        _medication[4] = getActivity().getResources().getString(R.string.Neurological);
        _medication[5] = getActivity().getResources().getString(R.string.Antibiotics);
        _medication[6] = getActivity().getResources().getString(R.string.Dermatologic);
    }*/

    /*
     * @return the name of the Medication depending on the language
     */

    public String getMedicationNameFromId(int id) {
        String ret = "";
        try {
            ret = _medication[id].toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /*
     * @return the name of the Medication depending on the language
     */

    public int getMedicationIdFromName(String pName) {
        for (int i = 0; i < _medication.length; i++) {
            if (_medication[i].toString().equals(pName)) return i;
        }
        return -1;
    }

    /*
     * @return the name of the Medication depending on the language
     */

    public String getDBStringFromInput(String pInput) {
        String[] data = pInput.split(";");
        StringBuilder output = new StringBuilder();

        if (pInput.isEmpty()) return "";

        int i = 0;
        if (data.length > 0) {
            output = new StringBuilder(String.valueOf(getMedicationIdFromName(data[i])));
            for (i = 1; i < data.length; i++) {
                output.append(";").append(getMedicationIdFromName(data[i]));
            }
        }

        return output.toString();
    }


    /*
     * @return the name of the Medication depending on the language
     */

    public String getInputFromDBString(String pDBString) {
        String[] data = pDBString.split(";");
        StringBuilder output = new StringBuilder();

        int i = 0;

        try {
            if (data.length > 0) {
                if (data[0].isEmpty()) return "";

                if (!data[i].equals("-1")) {
                    output = new StringBuilder(getMedicationNameFromId(Integer.valueOf(data[i])));
                    _selections[Integer.valueOf(data[i])] = true;
                    for (i = 1; i < data.length; i++) {
                        if (!data[i].equals("-1")) {
                            output.append(";").append(getMedicationNameFromId(Integer.valueOf(data[i])));
                            _selections[Integer.valueOf(data[i])] = true;
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            output = new StringBuilder();
            e.printStackTrace();
        }

        return output.toString();
    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            //Log.i("RotateImage", "Exif orientation: " + orientation);
            //Log.i("RotateImage", "Rotate value: " + rotate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public boolean toBeSaved() {
        return toBeSaved;
    }

    public void medicationSaved() {
        toBeSaved = false;
    }

    public Medication getMedication() {
        Medication m = mMedication;
        m.setName(medicationName.getText().toString());
        m.setDescription(medicationDescription.getText().toString());
        m.setBodyParts(getDBStringFromInput(this.medicationList.getText().toString()));
        m.setPicture(mCurrentPhotoPath);
        m.setFavorite(false);
        m.setType(selectedType);
        return m;
    }
}




