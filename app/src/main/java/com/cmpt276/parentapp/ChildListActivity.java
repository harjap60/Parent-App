package com.cmpt276.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ChildListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_list);
    }
}