package com.cmpt276.parentapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

        int id = getIntent().getIntExtra(EXTRA_FOR_INDEX, NEW_CHILD_INDEX);

        setupDB();
        setupChild(id);
        setUpToolbar(id);
        setupSaveButton();
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (child != null) {
            getMenuInflater().inflate(R.menu.menu_edit_child, menu);
        }

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_child_button:
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

        AlertDialog.Builder builder = getAlertDialogBox();
        builder.setMessage(
                getString(child == null ?
                        R.string.warning_change_happened_for_add_child :
                        R.string.warning_change_happened_for_edit_child)
        );

        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> finish());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void setupDB() {

        childDao = ParentAppDatabase.getInstance(this).childDao();
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

    private void setupSaveButton() {

        binding.btnSave.setOnClickListener(view -> saveChild());
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

    private void handleUnsavedChanges() {
        if (isClean()) {
            return;
        }

        getAlertDialogBox()
                .setMessage(getString(
                        R.string.confirm_edit_child_dialog_box_message,
                        child.getName(),
                        binding.txtName.getText()
                ))
                .setPositiveButton(R.string.yes, (dialog, which) -> saveChild())
                .create()
                .show();
    }

    private void showDeleteChildDialog() {
        getAlertDialogBox()
                .setMessage(getString(
                        R.string.confirm_delete_child_dialog_box_message,
                        child.getName()
                ))
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> deleteChild())
                .create()
                .show();
    }

    private void saveChild() {
        new Thread(() -> {

            String name = this.binding.txtName.getText().toString();

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