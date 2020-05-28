package com.pillhelper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.pillhelper.DAO.DAOUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static android.text.format.DateFormat.getDateFormat;

public class DoseCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public DoseCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        CardView cdView = view.findViewById(R.id.CARDVIEW);

        if (cursor.getPosition() % 2 == 1) {
            cdView.setBackgroundColor(context.getResources().getColor(R.color.background_even));
        } else {
            cdView.setBackgroundColor(context.getResources().getColor(R.color.background));
        }

        TextView t1 = view.findViewById(R.id.DATE_CELL);
        Date date;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DAOUtils.DATE_FORMAT);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = dateFormat.parse(cursor.getString(1));

            DateFormat dateFormat2 = getDateFormat(context.getApplicationContext());
            dateFormat2.setTimeZone(TimeZone.getTimeZone("GMT"));
            t1.setText(dateFormat2.format(date));
        } catch (ParseException e) {
            t1.setText("");
            e.printStackTrace();
        }


        TextView t2 = view.findViewById(R.id.MED_CELL);
        t2.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));

        TextView t3 = view.findViewById(R.id.INTAKE_CELL);
        t3.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))));

        TextView t4 = view.findViewById(R.id.DOSE_CELL);
        t4.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(4))));

        TextView t5 = view.findViewById(R.id.MED_CELL);
        t5.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5))));

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.row_fonte, parent, false);
    }

}
