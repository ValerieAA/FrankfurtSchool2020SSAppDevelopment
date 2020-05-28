package com.example.pillhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecyclerListDrugs extends AppCompatActivity {
    private SQLiteDatabase mDatabase;
    private DrugAdapter mAdapter;
    private EditText mEditTextDrugs;
    private TextView mTextViewAmount;
    private int mAmount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_list_drugs);

        MedicationDBHelper dbHelper = new MedicationDBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.setLayoutManager(new LinearLayout(this));
        mAdapter = new DrugAdapter(this, getAllItems());
        recyclerView.setAda


        mEditTextDrugs = findViewById(R.id.edittext_drugs);
        mTextViewAmount = findViewById(R.id.textview_amount);

        Button buttonIncrease = findViewById(R.id.button_increase);
        Button buttonDecrease = findViewById(R.id.button_decrease);
        Button buttonAdd = findViewById(R.id.button_add);

        buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase();
            }
        });
        buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrease();
            }
        });
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

    }

    private void increase() {
        mAmount++;
        mTextViewAmount.setText(String.valueOf(mAmount));
    }

    private void decrease() {
        if (mAmount > 0) {
            mAmount--;
            mTextViewAmount.setText(String.valueOf(mAmount));
        }
    }

    private void addItem() {
        if (mEditTextDrugs.getText().toString().trim().length() == 0 || mAmount == 0) {
            return;
        }

        String name = mEditTextDrugs.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(MedicationContract.MedicationEntry.COLUMN_NAME, name);
        cv.put(MedicationContract.MedicationEntry.COLUMN_AMOUNT, mAmount);

        mDatabase.insert(MedicationContract.MedicationEntry.TABLE_NAME, null, cv);

        mEditTextDrugs.getText().clear();
    }
        private Cursor getAllItems() {
        return mDatabase.query(
                MedicationContract.MedicationEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MedicationContract.MedicationEntry.COLUMN_TIMESTAMP + "DESC"
        );
        }

    }
