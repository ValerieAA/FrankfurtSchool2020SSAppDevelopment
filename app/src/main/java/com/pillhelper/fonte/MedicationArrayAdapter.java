package com.pillhelper.fonte;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pillhelper.DAO.Medication;
import com.pillhelper.R;

import java.util.ArrayList;

public class MedicationArrayAdapter extends ArrayAdapter<Medication> implements View.OnClickListener {

    Context mContext;
    private int lastPosition = -1;

    public MedicationArrayAdapter(ArrayList<Medication> data, Context context) {
        super(context, R.layout.medication_measure, data);
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Medication dataModel = getItem(position);
        //Snackbar.make(v, "Click:" + dataModel.getId(), Snackbar.LENGTH_SHORT);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        //MedicationDose dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.simplemedication_row, parent, false);
            viewHolder.txtID = convertView.findViewById(R.id.LIST_MEDICATION_ID);
            viewHolder.txtName = convertView.findViewById(R.id.LIST_MACHINE_NAME);
            //viewHolder.btFavorite = convertView.findViewById(R.id.LIST_MACHINE_FAVORITE);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtID;
        TextView txtName;
        ImageView btFavorite;
    }
}
