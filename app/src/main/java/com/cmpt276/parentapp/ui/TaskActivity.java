package com.cmpt276.parentapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityTaskBinding;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildDao;
import com.cmpt276.parentapp.model.ChildTaskCrossRef;
import com.cmpt276.parentapp.model.ParentAppDatabase;
import com.cmpt276.parentapp.model.Task;
import com.cmpt276.parentapp.model.TaskDao;
import com.cmpt276.parentapp.model.TaskWithChild;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.schedulers.Schedulers;

public class TaskActivity extends AppCompatActivity {

    public static final int MIN_ORDER = 0;
    private static final String EXTRA_FOR_INDEX =
            "com.cmpt276.parentapp.ui.AddChildActivity.childId";
    private static final int NEW_TASK_INDEX = -1;
    private Task task;
    private Child child;
    private TaskDao taskDao;
    private ActivityTaskBinding binding;

    public static Intent getIntentForNewTask(Context context) {
        return getIntentForExistingTask(context, NEW_TASK_INDEX);
    }

    public static Intent getIntentForExistingTask(Context context, int index) {
        Intent intent = new Intent(context, TaskActivity.class);
        intent.putExtra(EXTRA_FOR_INDEX, index);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        int id = getIntent().getIntExtra(EXTRA_FOR_INDEX, NEW_TASK_INDEX);// may need to change task_index value

        setupDB();
        setupTask(id);
        setUpToolbar(id);
        setupSaveButton();
        setupCancelButton();
        configureUI(id);
    }

    private void setupCancelButton() {
        Button cancelBtn = binding.cancelTaskBtn;
        cancelBtn.setOnClickListener(view -> finish());
    }

    private void configureUI(int id) {
        if (id == NEW_TASK_INDEX) {
            binding.confirmTurnBtn.setVisibility(View.GONE);
            binding.cancelTaskBtn.setVisibility(View.GONE);
            binding.btnSave.setVisibility(View.VISIBLE);
            binding.childTaskImage.setVisibility(View.GONE);
        } else {
            binding.taskEditName.setEnabled(false);
            binding.taskEditName.setClickable(false);
            binding.btnSave.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (task != null) {
            getMenuInflater().inflate(R.menu.menu_edit_task, menu);// change to task menu
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_task_button:
                showDeleteTaskDialog();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (isClean()) {
            finish();
            return;
        }

        AlertDialog.Builder builder = getAlertDialogBox();
        builder.setMessage(
                getString(task == null ?
                        R.string.warning_change_happened_for_add_task :
                        R.string.warning_change_happened_for_edit_task)
        );

        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> finish());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void setupDB() {
        taskDao = ParentAppDatabase.getInstance(this).taskDao();
    }

    private void setupTask(int id) {

        if (id == NEW_TASK_INDEX) {
            return;
        }

        taskDao.getTaskWithNextChild(id)
                .subscribeOn(Schedulers.newThread())
                .subscribe((TaskWithChild task) -> {
                    this.task = task.task;
                    this.child = task.child;
                    updateUI();
                    setupConfirmButton();
                });
    }

    private void setupSaveButton() {

        binding.btnSave.setOnClickListener(view -> saveTask());
    }

    private void setUpToolbar(int id) {
        binding.toolbar2.setTitle(
                getString(id == NEW_TASK_INDEX ?
                        R.string.add_task_title :
                        R.string.edit_task_title)
        );

        setSupportActionBar(binding.toolbar2);

        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setDisplayHomeAsUpEnabled(true);
    }

    private void updateUI() {
        if (task == null) {
            return;
        }

        binding.taskEditName.setText(task.getName());
        if (child != null) {
            binding.nameNextChild.setText(child.getName());
        }
    }

    private void handleUnsavedChanges() {
        if (isClean()) {
            return;
        }

        getAlertDialogBox()
                .setMessage(getString(
                        R.string.confirm_edit_task_dialog_box_message,
                        task.getName(),
                        binding.taskEditName.getText()
                ))
                .setPositiveButton(R.string.yes, (dialog, which) -> saveTask())
                .create()
                .show();
    }

    private void showDeleteTaskDialog() {
        getAlertDialogBox()
                .setMessage(getString(
                        R.string.confirm_delete_child_dialog_box_message,
                        task.getName()
                ))
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> deleteTask())
                .create()
                .show();
    }

    private void saveTask() {
        new Thread(() -> {

            String name = this.binding.taskEditName.getText().toString();

            if (task == null) {
                ChildDao childDao = ParentAppDatabase
                        .getInstance(TaskActivity.this)
                        .childDao();

                Long id = taskDao.insert(new Task(name)).blockingGet();

                List<Child> children = childDao.getAll().blockingGet();

                for (int i = 0; i < children.size(); i++) {
                    ChildTaskCrossRef ref = new ChildTaskCrossRef(
                            id.intValue(),
                            children.get(i).getChildId(),
                            i
                    );

                    taskDao.insertRef(ref).blockingAwait();
                }
            } else {
                task.setName(name);
                taskDao.update(task).blockingAwait();
            }
            runOnUiThread(this::finish);
        }).start();
    }

    private void deleteTask() {
        if (task == null) {
            return;
        }

        taskDao.delete(this.task)
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::finish);
    }

    private void setupConfirmButton() {
        binding.confirmTurnBtn.setOnClickListener((v) -> {
            new Thread(() -> {
                int taskId = task.getTaskId();
                int order = taskDao.getNextOrder(taskId).blockingGet();

                taskDao.updateOrder(taskId, child.getChildId(), order).blockingAwait();
                taskDao.decrementOrder(taskId, MIN_ORDER).blockingAwait();

                setupTask(taskId);
            }).start();
        });
    }

    private AlertDialog.Builder getAlertDialogBox() {
        return new AlertDialog.Builder(TaskActivity.this)
                .setTitle(R.string.warning_message)
                .setNegativeButton(R.string.no, null);
    }


    private boolean isClean() {
        String name = binding.taskEditName.getText().toString();
        return (task == null && name.isEmpty()) ||
                (task != null && name.equals(task.getName()));
    }
}