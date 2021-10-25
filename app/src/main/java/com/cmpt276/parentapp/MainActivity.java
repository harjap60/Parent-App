package com.cmpt276.parentapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cmpt276.parentapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
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
        Button child = (Button) findViewById(R.id.start_child_list);
        child.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ChildListActivity.class);
            startActivity(intent);
        });
    }

    private void setupTimerButton() {
        Button timer = (Button) findViewById(R.id.start_timer);
        timer.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TimerActivity.class);
            startActivity(intent);
        });
    }

    private void setupFlipButton() {
        Button flip = (Button) findViewById(R.id.start_flip);
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