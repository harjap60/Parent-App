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
import com.cmpt276.parentapp.databinding.ActivityChildBinding;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildDao;
import com.cmpt276.parentapp.model.ChildTaskCrossRef;
import com.cmpt276.parentapp.model.ParentAppDatabase;
import com.cmpt276.parentapp.model.Task;
import com.cmpt276.parentapp.model.TaskDao;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Add Child Activity - This activity lets you add a new child to the list.
 * Or edit an existing child in the list.
 * The user sets the name of the child but has a restriction to it
 * - the name of the new/edit child cannot be empty
 */
public class ChildActivity extends AppCompatActivity {

    private static final String EXTRA_FOR_INDEX =
            "com.cmpt276.parentapp.ui.AddChildActivity.childId";
    private static final int NEW_CHILD_INDEX = -1;

    private int childId;
    private Child child;
    private ChildDao childDao;
    private ActivityChildBinding binding;

    public static Intent getIntentForNewChild(Context context) {
        return getIntentForExistingChild(context, NEW_CHILD_INDEX);
    }

    public static Intent getIntentForExistingChild(Context context, int index) {
        Intent intent = new Intent(context, ChildActivity.class);
        intent.putExtra(EXTRA_FOR_INDEX, index);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        childId = getIntent().getIntExtra(EXTRA_FOR_INDEX, NEW_CHILD_INDEX);
        childDao = ParentAppDatabase.getInstance(this).childDao();

        setupChild(childId);
        setUpToolbar(childId);
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(
                childId == NEW_CHILD_INDEX ?
                        R.menu.menu_child :
                        R.menu.menu_child_edit,
                menu
        );

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_child_save:
                saveChild();
                return true;

            case R.id.btn_child_delete:
                showDeleteChildDialog();
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

        showUpConfirmationDialog();

    }

    private void setupChild(int id) {

        if (id == NEW_CHILD_INDEX) {
            return;
        }

        childDao.get(id)
                .subscribeOn(Schedulers.newThread())
                .subscribe((Child child) -> {
                    this.child = child;
                    updateUI();
                });
    }

    private void setUpToolbar(int id) {
        binding.toolbar.setTitle(
                getString(id == NEW_CHILD_INDEX ?
                        R.string.add_child_title :
                        R.string.edit_child_title)
        );

        setSupportActionBar(binding.toolbar);

        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setDisplayHomeAsUpEnabled(true);
    }

    private void updateUI() {
        if (child == null) {
            return;
        }

        binding.txtName.setText(child.getName());
    }

    private void showUpConfirmationDialog() {
        new AlertDialog.Builder(ChildActivity.this)
                .setTitle(R.string.up_alert_title)
                .setNegativeButton(R.string.no, null)
                .setMessage(
                        getString(child == null ?
                                R.string.warning_change_happened_for_add_child :
                                R.string.warning_change_happened_for_edit_child)
                ).setPositiveButton(R.string.yes, (dialogInterface, i) -> finish())
                .create()
                .show();
    }

    private void showDeleteChildDialog() {
        new AlertDialog.Builder(ChildActivity.this)
                .setTitle(R.string.delete_alert_title)
                .setNegativeButton(R.string.no, null)
                .setMessage(getString(
                        R.string.confirm_delete_child_dialog_box_message,
                        child.getName()
                ))
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> deleteChild())
                .create()
                .show();
    }

    private void saveChild() {

        String name = this.binding.txtName.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(
                    this,
                    R.string.empty_child_name_message,
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        new Thread(() -> {
            if (child == null) {
                int coinFlipOrder = childDao.getNextCoinFlipOrder().blockingGet();
                Long id = childDao.insert(new Child(name, coinFlipOrder)).blockingGet();

                TaskDao taskDao = ParentAppDatabase.getInstance(ChildActivity.this).taskDao();

                List<Task> tasks = taskDao.getAll().blockingGet();
                for (Task task : tasks) {
                    int order = taskDao.getNextOrder(task.getTaskId()).blockingGet();
                    taskDao.insertRef(new ChildTaskCrossRef(
                            task.getTaskId(),
                            id.intValue(),
                            order
                    )).blockingAwait();
                }
            } else {
                child.setName(name);
                childDao.update(child).blockingAwait();
            }
            runOnUiThread(this::finish);
        }).start();
    }

    private void deleteChild() {

        if (child == null) {
            return;
        }

        childDao.delete(this.child)
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::finish);
    }

    private AlertDialog.Builder getAlertDialogBox() {
        return new AlertDialog.Builder(ChildActivity.this)
                .setTitle(R.string.warning_message)
                .setNegativeButton(R.string.no, null);
    }


    private boolean isClean() {
        String name = binding.txtName.getText().toString();
        return (child == null && name.isEmpty()) ||
                (child != null && name.equals(child.getName()));
    }

}