package main.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import main.model.ParentAppDatabase;
import main.model.TaskDao;
import main.model.TaskHistory;
import main.model.TaskWithChild;
import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityTaskDetailBinding;

import java.time.LocalDateTime;

public class TaskDetailActivity extends AppCompatActivity {

    public static final String TASK_ID_EXTRA = "TASK_ID_EXTRA";
    public static final int DEFAULT_VALUE = -1;
    public static final long MIN_ORDER = 0L;

    private long taskId;
    private ActivityTaskDetailBinding binding;
    private TaskDao taskDao;
    private TaskWithChild taskWithChild;

    public static Intent getIntent(Context context, long taskId) {
        Intent i = new Intent(context, TaskDetailActivity.class);
        i.putExtra(TASK_ID_EXTRA, taskId);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskDetailBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        taskId = getIntent().getLongExtra(TASK_ID_EXTRA, DEFAULT_VALUE);
        taskDao = ParentAppDatabase.getInstance(this).taskDao();

        setupToolbar();
        setupConfirmButton();
        setupCancelButton();
    }

    private void setupCancelButton() {
        binding.btnCancel.setOnClickListener(v -> this.finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupTask();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.btn_task_edit:
                showTaskActivity();
                return true;
            case R.id.btn_task_delete:
                showDeleteTaskDialog();
                return true;
            case R.id.btn_task_history:
                startActivity(TaskHistoryActivity.getIntent(this, taskId));
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

    private void setupConfirmButton() {
        binding.btnConfirmTask.setOnClickListener((v) -> new Thread(() -> {
            Long order = taskDao.getNextOrder(taskId).blockingGet();

            taskDao.updateOrder(taskId, taskWithChild.child.getChildId(), order).blockingAwait();
            taskDao.decrementOrder(taskId, MIN_ORDER).blockingAwait();

            TaskHistory history = new TaskHistory(
                    taskWithChild.child.getChildId(),
                    taskId,
                    LocalDateTime.now()
            );

            taskDao.insertHistory(history).blockingAwait();

            setupTask();
        }).start());
    }

    private void setupTask() {
        new Thread(() -> {
            taskWithChild = taskDao
                    .getTaskWithNextChild(taskId)
                    .blockingGet();

            runOnUiThread(this::updateUI);
        }).start();
    }

    private void showTaskActivity() {
        Intent i = TaskActivity.getIntentForExistingTask(
                this,
                taskWithChild.task.getTaskId()
        );
        startActivity(i);
    }

    private void showDeleteTaskDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.warning_message)
                .setNegativeButton(R.string.no, null)
                .setMessage(getString(
                        R.string.confirm_delete_child_dialog_box_message,
                        taskWithChild.task.getName()
                ))
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> deleteTask())
                .create()
                .show();
    }

    private void deleteTask() {
        new Thread(() -> {
            try {
                taskDao.delete(this.taskWithChild.task).blockingAwait();

                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.task_deleted_toast_message, Toast.LENGTH_SHORT).show();
                    TaskDetailActivity.this.finish();
                });

            } catch (Exception e) {
                Log.i("Task Activity deletion", e.getMessage());
            }
        }).start();
    }


    private void updateUI() {

        int deviceWidth = getResources().getDisplayMetrics().widthPixels;
        binding.btnConfirmTask.setTextSize((deviceWidth / 25f));
        binding.btnCancel.setTextSize((deviceWidth / 25f));

        if (taskWithChild == null) {
            return;
        }

        binding.toolbar.setTitle(taskWithChild.task.getName());
        if (taskWithChild.child == null) {
            binding.tvChildName.setText(R.string.no_children_configure_message);
            binding.btnConfirmTask.setVisibility(View.INVISIBLE);
            return;
        }

        binding.tvChildName.setText(taskWithChild.child.getName());
        binding.btnConfirmTask.setVisibility(View.VISIBLE);

        if (taskWithChild.child.getImagePath() != null) {
            Glide.with(this)
                    .load(taskWithChild.child.getImagePath())
                    .centerCrop()
                    .into(binding.imageView);
        } else {
            binding.imageView.setImageResource(R.drawable.child_image_icon);
        }
    }
}