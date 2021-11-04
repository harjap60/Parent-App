package com.cmpt276.parentapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cmpt276.parentapp.R;

public class FlipHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_history);
    }

    public static Intent getIntent(Context context){
        return new Intent(context, FlipHistoryActivity.class);
    }
}