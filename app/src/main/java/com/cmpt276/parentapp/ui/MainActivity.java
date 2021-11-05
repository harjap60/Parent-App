package com.cmpt276.parentapp.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.cmpt276.parentapp.R;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupFlipButton();
        setupTimerButton();
        setupChildButton();
    }

    private void setupChildButton() {
        Button child = findViewById(R.id.start_child_list);
        child.setOnClickListener(view ->
                startActivity(
                        ChildListActivity.getIntent(this)
                )
        );
    }

    private void setupTimerButton() {
        Button timer = findViewById(R.id.create_timer);
        timer.setOnClickListener(view -> showTimerDurationDialog());
    }

    private void showTimerDurationDialog() {
        String[] options = this.getResources().getStringArray(R.array.durations);
        String custom_duration_item = this.getResources().getString(R.string.custom_duration_item);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(this.getString(R.string.dialog_duration_title))
                .setItems(options, (dialog, index) -> {
                    String selection = options[index];

                    if (selection.equals(custom_duration_item)) {
                        showCustomDurationDialog();
                        return;
                    }

                    String[] optionParts = selection.split(" ", 2);
                    int duration = Integer.parseInt(optionParts[0]);

                    Intent i = TimerActivity.getIntentWithDurationMinutes(
                            this,
                            duration
                    );
                    this.startActivity(i);

                }).create()
                .show();
    }

    private void showCustomDurationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = this.getLayoutInflater().inflate(R.layout.dialog_custom_duration, null);

        builder.setView(v)
                .setTitle(R.string.dialog_custom_duration_title)
                .setPositiveButton(R.string.dialog_custom_duration_title, (dialog, id) -> {
                    try {
                        EditText tvTimerDuration = v.findViewById(R.id.tvTimerDuration);
                        int duration = Integer.parseInt(tvTimerDuration.getText().toString());
                        Intent i = TimerActivity.getIntentWithDurationMinutes(
                                this,
                                duration
                        );
                        this.startActivity(i);
                    } catch (Exception e) {
                        Toast.makeText(
                                this,
                                this.getString(R.string.dialog_custom_duration_invalid_input),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton(R.string.dialog_custom_duration_dialog_cancel, null)
                .create()
                .show();
    }

    private void setupFlipButton() {
        Button flip = findViewById(R.id.start_flip);
        flip.setOnClickListener(view ->
                startActivity(FlipActivity.getIntent(this))
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}