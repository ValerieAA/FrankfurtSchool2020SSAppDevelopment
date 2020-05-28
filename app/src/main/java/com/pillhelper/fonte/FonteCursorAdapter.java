package com.pillhelper.fonte;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pillhelper.BtnClickListener;
import com.pillhelper.DAO.DAOUtils;
import com.pillhelper.R;
import com.pillhelper.utils.UnitConverter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FonteCursorAdapter extends CursorAdapter {

    BtnClickListener mDeleteClickListener = null;
    private LayoutInflater mInflater;
    private int mFirstColorOdd = 0;
    private Context mContext = null;

    public FonteCursorAdapter(Context context, Cursor c, int flags, BtnClickListener clickList) {
        super(context, c, flags);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDeleteClickListener = clickList;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (cursor.getPosition() % 2 == mFirstColorOdd) {
            view.setBackgroundColor(context.getResources().getColor(R.color.background_odd));
        } else {
            view.setBackgroundColor(context.getResources().getColor(R.color.background_even));
        }

        TextView t1 = view.findViewById(R.id.DATE_CELL);
        Date date;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = dateFormat.parse(cursor.getString(1));

            DateFormat dateFormat3 = android.text.format.DateFormat.getDateFormat(mContext.getApplicationContext());
            dateFormat3.setTimeZone(TimeZone.getTimeZone("GMT"));
            t1.setText(dateFormat3.format(date));
        } catch (ParseException e) {
            t1.setText("");
            e.printStackTrace();
        }

        TextView t10 = view.findViewById(R.id.TIME_CELL);
        t10.setText(cursor.getString(10));


        TextView t2 = view.findViewById(R.id.MED_CELL);
        //t2.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));
        t2.setText(cursor.getString(2));

        TextView t3 = view.findViewById(R.id.INTAKE_CELL);
        t3.setText(cursor.getString(3));

        TextView t4 = view.findViewById(R.id.DOSE_CELL);
        t4.setText(cursor.getString(4));

        TextView t5 = view.findViewById(R.id.MED_CELL);
        String unit = mContext.getString(R.string.mgUnitLabel);
        float dose = cursor.getFloat(5);
        if (cursor.getInt(6) == UnitConverter.UNIT_LBS) {
            dose = UnitConverter.KgtoLbs(dose);
            unit = mContext.getString(R.string.gUnitLabel);
        }
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        t5.setText(numberFormat.format(dose) + unit);

        ImageView deletImg = view.findViewById(R.id.deleteButton);
        deletImg.setTag(cursor.getLong(0));
        deletImg.setOnClickListener(v -> {
            if (mDeleteClickListener != null)
                mDeleteClickListener.onBtnClick((long) v.getTag());
        });

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.row_fonte, parent, false);
    }

    public void setFirstColorOdd(int pColor) {
        mFirstColorOdd = pColor;
    }
}
