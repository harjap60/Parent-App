package com.cmpt276.parentapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityTaskHistoryBinding;
import com.cmpt276.parentapp.model.ParentAppDatabase;
import com.cmpt276.parentapp.model.TaskDao;
import com.cmpt276.parentapp.model.TaskHistoryWithChild;
import com.cmpt276.parentapp.model.TaskWithHistory;

import java.time.format.DateTimeFormatter;

public class TaskHistoryActivity extends AppCompatActivity {

    public static final String TASK_ID_EXTRA = "com.cmpt276.parentapp.TaskHistory.TASK_ID_EXTRA";
    public static final long INVALID_ID = -1L;

    ActivityTaskHistoryBinding binding;

    public static Intent getIntent(Context context, long taskId) {
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
        populateTaskHistoryList();
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

    private void populateTaskHistoryList() {
        long taskId = getIntent().getLongExtra(TASK_ID_EXTRA, INVALID_ID);
        if (taskId != INVALID_ID) {
            new Thread(() -> {
                TaskDao taskDao = ParentAppDatabase.getInstance(TaskHistoryActivity.this).taskDao();

                TaskWithHistory historyList = taskDao.getHistory(taskId).blockingGet();

                TaskHistoryAdaptor adaptor = new TaskHistoryAdaptor(historyList);

                runOnUiThread(() -> binding.listTaskHistory.setAdapter(adaptor));
                Log.i("HISTORY", historyList.toString());
            }).start();
        }
    }

    private class TaskHistoryAdaptor extends ArrayAdapter<TaskHistoryWithChild> {
        private final String DATE_FORMAT = "yyyy-MM-dd @ KK:mma";
        private final TaskWithHistory taskWithHistory;

        public TaskHistoryAdaptor(TaskWithHistory taskWithHistory) {
            super(TaskHistoryActivity.this,
                    R.layout.list_item_flip_history,
                    taskWithHistory.history
            );
            this.taskWithHistory = taskWithHistory;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater()
                        .inflate(
                                R.layout.list_item_task_history,
                                parent,
                                false
                        );
            }

            TaskHistoryWithChild taskHistoryWithChild = taskWithHistory.history.get(position);

            TextView dateText = itemView.findViewById(R.id.tv_task_date);
            dateText.setText(
                    DateTimeFormatter
                            .ofPattern(DATE_FORMAT)
                            .format(taskHistoryWithChild.taskHistory.getDate())
            );

            TextView childNameText = itemView.findViewById(R.id.tv_child_name);
            childNameText.setText(taskHistoryWithChild.child.getName());

            ImageView childImage = itemView.findViewById(R.id.iv_child_image);
            Glide.with(TaskHistoryActivity.this)
                    .load(taskHistoryWithChild.child.getImagePath())
                    .centerCrop()
                    .placeholder(R.drawable.child_image_icon)
                    .into(childImage);

            return itemView;
        }
    }

}