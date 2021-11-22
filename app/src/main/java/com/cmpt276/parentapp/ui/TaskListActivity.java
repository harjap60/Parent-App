package com.cmpt276.parentapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityTaskListBinding;
import com.cmpt276.parentapp.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {

    private ActivityTaskListBinding binding;

    public static Intent getIntent(Context context) {
        return new Intent(context, TaskListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskListBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());
        populateTaskRecyclerView();

        setupToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btn_add_task) {
            startActivity(
                    TaskActivity.getIntentForNewTask(this)
            );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateTaskRecyclerView() {
        List<Task> taskList = new ArrayList<>();

        taskList.add(new Task("Read"));
        taskList.add(new Task("Eat"));
        taskList.add(new Task("Play"));
        taskList.add(new Task("Jump"));
        taskList.add(new Task("Run"));

        TaskListAdapter adapter = new TaskListAdapter(taskList);
        binding.rvTaskList.setAdapter(adapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    class TaskListAdapter extends ArrayAdapter<Task> {

        List<Task> taskList;

        public TaskListAdapter(List<Task> taskList) {
            super(TaskListActivity.this,
                    R.layout.task_list_item,
                    taskList);
            this.taskList = taskList;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater()
                        .inflate(
                                R.layout.task_list_item,
                                parent,
                                false
                        );
            }

            itemView.setOnClickListener(v -> Toast.makeText(
                    TaskListActivity.this,
                    "Clicked",
                    Toast.LENGTH_SHORT
            ).show());

            Task task = taskList.get(position);

            TextView childNameText = itemView.findViewById(R.id.tv_task_name);
            childNameText.setText(task.getName());

            return itemView;
        }
    }
}