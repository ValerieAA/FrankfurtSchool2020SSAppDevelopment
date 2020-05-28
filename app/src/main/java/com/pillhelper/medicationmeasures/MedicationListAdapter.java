package com.pillhelper.medicationmeasures;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pillhelper.DAO.medicationdosemeasures.MedicationDose;
import com.pillhelper.R;

import java.util.ArrayList;

public class MedicationListAdapter extends ArrayAdapter<MedicationDose> implements View.OnClickListener {

    Context mContext;
    private ArrayList<MedicationDose> dataSet;
    private int lastPosition = -1;

    public MedicationListAdapter(ArrayList<MedicationDose> data, Context context) {
        super(context, R.layout.medication_measure, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        MedicationDose dataModel = (MedicationDose) object;

/*
        switch (v.getId()) {
            case R.id.item_info:
                Snackbar.make(v, "Release date " + dataModel.getFeature(), Snackbar.LENGTH_LONG)
                    .setAction("No action", null).show();
                break;
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //

        // Get the data item for this position
        MedicationDose dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.medication_measure, parent, false);
            viewHolder.txtID = convertView.findViewById(R.id.LIST_MEDICATION_ID);
            viewHolder.txtName = convertView.findViewById(R.id.LIST_MEDICATION);
            viewHolder.txtLastMeasure = convertView.findViewById(R.id.LIST_MEDICATION_RECORD);
            viewHolder.logo = convertView.findViewById(R.id.LIST_MEDICATION_LOGO);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.txtID.setText(String.valueOf(dataModel.getProfileID()));
       viewHolder.txtName.setText(this.getContext().getResources().getText(dataModel.getResourceNameID()));
       if (dataModel == dataModel.getLastMeasure()) {
           viewHolder.txtLastMeasure.setText("-");
       } else {
            viewHolder.txtLastMeasure.setText(String.valueOf(dataModel.getLastMeasure()));
        }
         viewHolder.logo.setImageResource(dataModel.getResourceLogoID());
         Return the completed view to render on screen
        return convertView;
    }

        // View lookup cache
        private static class ViewHolder {
            TextView txtID;
            TextView txtName;
            TextView txtLastMeasure;
            ImageView logo;
        }
    }

    */}}
