package com.cmpt276.parentapp.ui;

import android.content.Intent;
import android.os.Bundle;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildManager;
import com.cmpt276.parentapp.model.CoinFlip;
import com.cmpt276.parentapp.model.FlipHistoryManager;
import com.cmpt276.parentapp.model.PrefConfig;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ChildManager childManager;
    FlipHistoryManager flipHistoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //childManager = ChildManager.getInstance();
        //flipHistoryManager = FlipHistoryManager.getInstance();

        setupFlipButton();
        setupTimerButton();
        setupChildButton();

        //readFlipsHistoryFromSharedPrefs();
        //readChildListFromSharedPrefs();
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

    /*private void readFlipsHistoryFromSharedPrefs(){
        List<CoinFlip> history = PrefConfig.readFlipHistoryFromPref(this);
        if(history != null){
            flipHistoryManager.setHistory(history);
        }
    }

    private void readChildListFromSharedPrefs(){
        List<Child> children = PrefConfig.readChildListFromPref(this);
        if(children != null){
            childManager.setChildren(children);
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}