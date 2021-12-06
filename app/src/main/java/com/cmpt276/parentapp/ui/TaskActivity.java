package com.cmpt276.parentapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

/**
 * Adding new Task Activity - This activity lets you add a new task to the list.
 * This Activity also takes care of confirming the next child's turn
 */

public class TaskActivity extends AppCompatActivity {

    private static final String EXTRA_FOR_INDEX =
            "com.cmpt276.parentapp.ui.AddChildActivity.childId";
    private static final long NEW_TASK_INDEX = -1L;
    private Task task;
    private TaskDao taskDao;
    private ActivityTaskBinding binding;

    public static Intent getIntentForNewTask(Context context) {
        return getIntentForExistingTask(context, NEW_TASK_INDEX);
    }

    public static Intent getIntentForExistingTask(Context context, long index) {
        Intent intent = new Intent(context, TaskActivity.class);
        intent.putExtra(EXTRA_FOR_INDEX, index);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        long id = getIntent().getLongExtra(EXTRA_FOR_INDEX, NEW_TASK_INDEX);

        setupDB();
        setupTask(id);
        setuptoolbar(id);
        enableUpOnToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.btn_task_save:
                saveTask();
                return false;

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

        new AlertDialog.Builder(TaskActivity.this)
                .setTitle(R.string.warning_message)
                .setNegativeButton(R.string.no, null)
                .setMessage(
                        getString(task == null ?
                                R.string.warning_change_happened_for_add_task :
                                R.string.warning_change_happened_for_edit_task)
                ).setPositiveButton(R.string.yes, (dialogInterface, i) -> finish())
                .create()
                .show();

    }

    private void setupDB() {

        taskDao = ParentAppDatabase.getInstance(this).taskDao();
    }

    private void setupTask(long id) {

        if (id == NEW_TASK_INDEX) {
            return;
        }

        taskDao.getTaskWithNextChild(id)
                .subscribeOn(Schedulers.newThread())
                .subscribe((TaskWithChild task) -> {
                    this.task = task.task;
                    updateUI();
                });
    }

    private void setuptoolbar(long id) {
        binding.toolbar.setTitle(
                getString(id == NEW_TASK_INDEX ?
                        R.string.add_task_title :
                        R.string.edit_task_title)
        );

        setSupportActionBar(binding.toolbar);

        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setDisplayHomeAsUpEnabled(true);
    }

    private void enableUpOnToolbar() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void updateUI() {
        if (task == null) {
            return;
        }

        binding.txtName.setText(task.getName());
    }

    private void saveTask() {
        String name = this.binding.txtName.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(
                    this,
                    "Task Name cannot be empty.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        new Thread(() -> {
            if (task == null) {
                ChildDao childDao = ParentAppDatabase
                        .getInstance(TaskActivity.this)
                        .childDao();

                long id = taskDao.insert(new Task(name)).blockingGet();

                List<Child> children = childDao.getAll().blockingGet();

                for (long i = 0L; i < children.size(); i++) {
                    ChildTaskCrossRef ref = new ChildTaskCrossRef(
                            id,
                            children.get((int) i).getChildId(),
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

    private boolean isClean() {
        String name = binding.txtName.getText().toString();
        return (task == null && name.isEmpty()) ||
                (task != null && name.equals(task.getName()));
    }
}