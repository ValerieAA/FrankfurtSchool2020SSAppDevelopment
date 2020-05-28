package com.pillhelper.specifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pillhelper.DAO.Medication;
import com.pillhelper.R;
import com.pillhelper.utils.ImageUtil;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import java.util.ArrayList;

/**
 * Adapter für die Listen die den Cursor nicht benutzen können.
 */


public class MedicationArrayFullAdapter extends ArrayAdapter<Medication> {

    public MedicationArrayFullAdapter(Context context, ArrayList<Medication> medications) {
        super(context, 0, medications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Medication medication = getItem(position);
        if (medication == null) return convertView;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.medicationlist_row, parent, false);
        }
        TextView t0 = convertView.findViewById(R.id.LIST_MEDICATION_ID);
        t0.setText(String.valueOf(medication.getId()));

        TextView t1 = convertView.findViewById(R.id.LIST_MEDICATION_NAME);
        t1.setText(medication.getName());

        TextView t2 = convertView.findViewById(R.id.LIST_MEDICATION_SHORT_DESCRIPTION);
        t2.setText(medication.getDescription());

        ImageView i0 = convertView.findViewById(R.id.LIST_MEDICATION_PHOTO);
        String lPath = medication.getPicture();
        if (lPath != null && !lPath.isEmpty()) {
            try {
                ImageUtil imgUtil = new ImageUtil();
                String lThumbPath = imgUtil.getThumbPath(lPath);
                ImageUtil.setThumb(i0, lThumbPath);
            } catch (Exception e) {
                i0.setImageResource(R.drawable.ic_filter_vintage_black_24dp);
                e.printStackTrace();
            }
        } else {
            i0.setImageResource(R.drawable.ic_filter_vintage_black_24dp);
        }

        MaterialFavoriteButton iFav = convertView.findViewById(R.id.LIST_MEDICATION_FAVORITE);
        iFav.setFavorite(medication.getFavorite());
        return convertView;
    }
}

