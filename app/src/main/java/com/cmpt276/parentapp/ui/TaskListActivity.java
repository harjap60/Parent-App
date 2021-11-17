package com.cmpt276.parentapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityTaskListBinding;
import com.cmpt276.parentapp.databinding.TaskListItemBinding;
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
    }

    private void populateTaskRecyclerView() {
        List<Task> taskList = new ArrayList<>();

        taskList.add(new Task("Read"));
        taskList.add(new Task("Eat"));
        taskList.add(new Task("Play"));
        taskList.add(new Task("Jump"));
        taskList.add(new Task("Run"));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvTaskList.setLayoutManager(layoutManager);

        TaskListAdapter adapter = new TaskListAdapter(taskList);
        binding.rvTaskList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskHolder> {

        List<Task> taskList;

        public TaskListAdapter(List<Task> taskList) {
            this.taskList = taskList;
        }

        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TaskHolder(TaskListItemBinding.inflate(
                    LayoutInflater.from(
                            parent.getContext()
                    ),
                    parent,
                    false
            ));
        }

        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
            holder.setName(taskList.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }

        class TaskHolder extends RecyclerView.ViewHolder {

            TaskListItemBinding binding;

            public TaskHolder(TaskListItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;

                this.binding.getRoot().setOnClickListener(v -> Toast.makeText(
                        TaskListActivity.this,
                        "Clicked",
                        Toast.LENGTH_SHORT
                ).show());
            }

            public void setName(String name) {
                this.binding.tvTaskName.setText(name);
            }
        }
    }
}