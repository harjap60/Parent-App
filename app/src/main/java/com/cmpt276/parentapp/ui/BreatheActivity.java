package com.cmpt276.parentapp.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityBreatheBinding;

/**
 * Help user to relax and calm down if they feel the need to
 *
 * Current state:
 *          Just has a textview and button
 *
 *TODO:
 *      Make button increase size the longer it is held
 *      When released reset to normal size
 */
public class BreatheActivity extends AppCompatActivity {

    private ActivityBreatheBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathe);
        binding = ActivityBreatheBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupBreatheButton();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.breatheToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.breathe);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupBreatheButton(){
        binding.breatheButton.setOnClickListener(view -> Toast.makeText(
                BreatheActivity.this,
                "Nice you did the breathe",
                Toast.LENGTH_SHORT
        ).show());
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, BreatheActivity.class);
    }
}