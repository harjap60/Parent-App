package com.cmpt276.parentapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.model.Task;

public class TaskActivity extends AppCompatActivity {

    public static final String TASK_ID_EXTRA = "TASK_ID_EXTRA";
    private static int NEW_TASK_ID = -1;
    private Task task;

    public static Intent getIntentForNewTask(Context context) {
        return getIntentForExistingTask(context, NEW_TASK_ID);
    }

    public static Intent getIntentForExistingTask(Context context, int id) {
        Intent i = new Intent(context, TaskActivity.class);
        i.putExtra(TASK_ID_EXTRA, id);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        int taskId = getIntent().getIntExtra(TASK_ID_EXTRA,NEW_TASK_ID);

        setupToolbar(taskId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (task != null) {
            getMenuInflater().inflate(R.menu.menu_child, menu);
        }

        return true;
    }

    private void setupToolbar(int taskId) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);

            if(taskId != NEW_TASK_ID){
                ab.setTitle("Edit Task");
            }
        }
    }



}