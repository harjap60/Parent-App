package com.cmpt276.parentapp;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;

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
        child.setOnClickListener(view -> startActivity(ChildListActivity.getIntent(this)));
    }

    private void setupTimerButton() {
        Button timer = findViewById(R.id.start_timer);
        timer.setOnClickListener(view -> startActivity(TimerActivity.getIntent(this)));
    }

    private void setupFlipButton() {
        Button flip = findViewById(R.id.start_flip);
        flip.setOnClickListener(view -> startActivity(FlipActivity.getIntent(this)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}