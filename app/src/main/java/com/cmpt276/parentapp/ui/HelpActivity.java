package com.cmpt276.parentapp.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityHelpBinding;

public class HelpActivity extends AppCompatActivity {

    private ActivityHelpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar3);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.help_activity_toolbar_label);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, HelpActivity.class);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}