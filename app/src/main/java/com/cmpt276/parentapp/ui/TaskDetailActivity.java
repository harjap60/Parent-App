package com.cmpt276.parentapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.databinding.ActivityTaskDetailBinding;
import com.cmpt276.parentapp.model.ParentAppDatabase;
import com.cmpt276.parentapp.model.TaskDao;
import com.cmpt276.parentapp.model.TaskWithChildren;

public class TaskDetailActivity extends AppCompatActivity {

    public static final String TASK_ID_EXTRA = "TASK_ID_EXTRA";
    public static final int DEFAULT_VALUE = -1;
    private ActivityTaskDetailBinding binding;
    private TaskDao taskDao;
    private TaskWithChildren taskWithChildren;

    public static Intent getIntent(Context context, int taskId) {
        Intent i = new Intent(context, TaskListActivity.class);
        i.putExtra(TASK_ID_EXTRA, taskId);
        return new Intent(context, TaskListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskDetailBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        int id = getIntent().getIntExtra(TASK_ID_EXTRA, DEFAULT_VALUE);
        taskDao = ParentAppDatabase.getInstance(this).taskDao();

        setupToolbar();
        setupTask(id);
    }

    private void setupTask(int id) {
        new Thread(() -> {
            taskWithChildren = taskDao
                    .getTaskWithChildren(id)
                    .blockingGet();

            runOnUiThread(this::updateUI);
        }).start();
    }

    private void updateUI() {
        if (taskWithChildren == null) {
            return;
        }

        Toast.makeText(this, "Updated", Toast.LENGTH_LONG).show();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }


}