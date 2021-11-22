package com.cmpt276.parentapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.cmpt276.parentapp.databinding.ActivityChildBinding;
import com.cmpt276.parentapp.databinding.ActivityTaskBinding;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildDao;
import com.cmpt276.parentapp.model.Task;
import com.cmpt276.parentapp.model.TaskDao;

public class TaskActivity extends AppCompatActivity {

    private Task task;
    private TaskDao taskDao;
    private ActivityTaskBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());
    }
}