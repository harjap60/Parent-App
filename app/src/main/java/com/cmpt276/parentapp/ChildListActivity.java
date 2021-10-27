package com.cmpt276.parentapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ChildListActivity extends AppCompatActivity {

    public static Intent getIntent(Context context) {
       return new Intent(context, ChildListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_list);
    }
}