package com.cmpt276.parentapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityTaskHistoryBinding;

public class TaskHistoryActivity extends AppCompatActivity {

    public static final String TASK_ID_EXTRA = "com.cmpt276.parentapp.TaskHistory.TASK_ID_EXTRA";

    ActivityTaskHistoryBinding binding;

    public static Intent getIntent(Context context, int taskId) {
        Intent i = new Intent(context, TaskHistoryActivity.class);
        i.putExtra(TASK_ID_EXTRA, taskId);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTaskHistoryBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }
}