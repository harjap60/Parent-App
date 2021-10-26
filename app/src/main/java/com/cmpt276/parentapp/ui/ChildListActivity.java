package com.cmpt276.parentapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cmpt276.parentapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChildListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_list);

        setUpToolbar();
        setUpNewGameButton();
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getText(R.string.child_list_activity_toolbar_label));
    }

    private void setUpNewGameButton() {
        FloatingActionButton addNewChildFabButton = findViewById(R.id.add_child_fab);
        addNewChildFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChildListActivity.this, "This button will let you add a new child", Toast.LENGTH_SHORT).show();
            }
        });
    }
}