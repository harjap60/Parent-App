package com.cmpt276.parentapp.ui;

import android.content.Intent;
import android.os.Bundle;

import com.cmpt276.parentapp.R;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.view.Menu;
import android.widget.Button;

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
        CardView child = (CardView) findViewById(R.id.start_child_list);
        child.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ChildListActivity.class);
            startActivity(intent);
        });
    }

    private void setupTimerButton() {
        CardView timer = (CardView) findViewById(R.id.start_timer);
        timer.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TimerActivity.class);
            startActivity(intent);
        });
    }

    private void setupFlipButton() {
        CardView flip = (CardView) findViewById(R.id.start_flip);
        flip.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FlipActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}