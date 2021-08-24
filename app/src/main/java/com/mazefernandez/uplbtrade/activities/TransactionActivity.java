package com.mazefernandez.uplbtrade.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mazefernandez.uplbtrade.R;

/* Shows the user the progress of their transaction */

public class TransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
    }
}
