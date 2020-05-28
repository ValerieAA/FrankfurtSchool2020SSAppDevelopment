package com.pillhelper.specifications;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.pillhelper.DAO.DAOMedication;
import com.pillhelper.DAO.Medication;
import com.pillhelper.R;
import com.pillhelper.utils.ImageUtil;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

public class MedicationCursorAdapter extends CursorAdapter implements Filterable {

    DAOMedication mDbMedication = null;
    MaterialFavoriteButton iFav = null;
    private LayoutInflater mInflater;

    public MedicationCursorAdapter(Context context, Cursor c, int flags, DAOMedication pDbMedication) {
        super(context, c, flags);
        mDbMedication = pDbMedication;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView t0 = view.findViewById(R.id.LIST_MEDICATION_ID);
        t0.setText(cursor.getString(cursor.getColumnIndex(DAOMedication.KEY)));

        TextView t1 = view.findViewById(R.id.LIST_MEDICATION_NAME);
        t1.setText(cursor.getString(cursor.getColumnIndex(DAOMedication.NAME)));

        TextView t2 = view.findViewById(R.id.LIST_MEDICATION_SHORT_DESCRIPTION);
        t2.setText(cursor.getString(cursor.getColumnIndex(DAOMedication.DESCRIPTION)));

        ImageView i0 = view.findViewById(R.id.LIST_MEDICATION_PHOTO);
        String lPath = cursor.getString(cursor.getColumnIndex(DAOMedication.PICTURE));

        int lType = cursor.getInt(cursor.getColumnIndex(DAOMedication.TYPE));

        if (lPath != null && !lPath.isEmpty()) {
            try {
                ImageUtil imgUtil = new ImageUtil();
                String lThumbPath = imgUtil.getThumbPath(lPath);
                ImageUtil.setThumb(i0, lThumbPath);
            } catch (Exception e) {
                if (lType == DAOMedication.TYPE_FONTE)
                    i0.setImageResource(R.drawable.ic_filter_vintage_black_24dp);
                else
                    i0.setImageResource(R.drawable.ic_filter_vintage_black_24dp);
                e.printStackTrace();
            }
        } else {
            if (lType == DAOMedication.TYPE_FONTE)
                i0.setImageResource(R.drawable.ic_filter_vintage_black_24dp);
            else
                i0.setImageResource(R.drawable.ic_filter_vintage_black_24dp);
        }

        iFav = view.findViewById(R.id.LIST_MEDICATION_FAVORITE);
        boolean bFav = cursor.getInt(6) == 1;
        iFav.setFavorite(bFav);
        iFav.setRotationDuration(500);
        iFav.setAnimateFavorite(true);
        iFav.setTag(cursor.getLong(0));

        iFav.setOnClickListener(v -> {
            MaterialFavoriteButton mFav = (MaterialFavoriteButton) v;
            boolean t = mFav.isFavorite();
            mFav.setFavoriteAnimated(!t);
            if (mDbMedication != null) {
                Medication m = mDbMedication.getMedication((long) mFav.getTag());
                m.setFavorite(!t);
                mDbMedication.updateMedication(m);
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.medicationlist_row, parent, false);

    }

}
