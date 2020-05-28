package com.example.pillhelper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DrugAdapter extends RecyclerView.Adapter<DrugAdapter.DrugViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public DrugAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;

    }

    public class  DrugViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public TextView countText;


        public DrugViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.textview_name_item);
            countText = itemView.findViewById(R.id.textview_amount);

        }
    }

    @NonNull
    @Override
    public DrugViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.medication_item, parent, false);
        return new DrugViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull DrugViewHolder holder, int position) {
        if (!mCursor.move(position)){
            return;
        }
        String name = mCursor.getString(mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_NAME));
        int amount = mCursor.getInt(mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_AMOUNT));

        holder.nameText.setText(name);
        holder.countText.setText(String.valueOf(amount));

    }

    @Override
    public int getItemCount() {

        return mCursor.getCount();
    }
    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null)
        {
        notifyDataSetChanged();
        }
    }
}
