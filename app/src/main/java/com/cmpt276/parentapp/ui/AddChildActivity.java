package com.cmpt276.parentapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildManager;
import com.cmpt276.parentapp.model.FlipHistoryManager;
import com.cmpt276.parentapp.model.PrefConfig;

import java.util.Objects;
/**
 * Add Child Activity - This activity lets you add a new child to the list.
 * Or edit an existing child in the list.
 * The user sets the name of the child but has a restriction to it
 * - the name of the new/edit child cannot be empty
 */
public class AddChildActivity extends AppCompatActivity {

    private EditText childNameInput;
    private Button addChildButton;
    private ChildManager manager;
    private boolean addChild;
    private int positionForEditChild;
    private static final String EXTRA_FOR_INDEX =
            "com.cmpt276.parentapp.ui.AddChildActivity - the index";
    private static final int DEFAULT_VALUE_FOR_ADD_CHILD_FOR_INTENT = -1;

    private String initialString = "";

    public static Intent makeIntentForAddChild(Context context) {
        return makeIntentForEditChild(context, DEFAULT_VALUE_FOR_ADD_CHILD_FOR_INTENT);
    }

    public static Intent makeIntentForEditChild(Context context, int index) {
        Intent intent = new Intent(context, AddChildActivity.class);
        intent.putExtra(EXTRA_FOR_INDEX, index);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        // instantiating the manager
        manager = ChildManager.getInstance(AddChildActivity.this);

        extractDataFromIntent();
        setUpInitialString();
        setupAddChildButton();
        setUpEditTextChildName();
        setUpToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu
        if (addChild) {
            getMenuInflater().inflate(R.menu.menu_add_child, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_edit_child, menu);
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_child_button:
                addOrDiscardChildName();
                return true;

            case R.id.action_edit_child_button:
                editChildName();
                return true;

            case R.id.action_delete_child_button:
                deleteChild();
                return true;

            case android.R.id.home: // up button
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (changeHappened()) {
            AlertDialog.Builder builder = getAlertDialogBox();
            builder.setMessage(
                    addChild ?
                            getString(R.string.warning_change_happened_for_add_child) :
                            getString(R.string.warning_change_happened_for_edit_child)
            );

            builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> finish());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } else {
            finish();
        }
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        positionForEditChild = intent.getIntExtra(EXTRA_FOR_INDEX, 0);
        addChild = positionForEditChild < 0;
    }

    private void setUpEditTextChildName() {
        childNameInput = findViewById(R.id.child_name_edit_text);
        childNameInput.setText(initialString);
        int deviceWidth = getResources().getDisplayMetrics().widthPixels;
        childNameInput.setTextSize((deviceWidth/20f));

    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(
                addChild ?
                        getString(R.string.add_child_activity_toolbar_label) :
                        getString(R.string.edit_child_activity_toolbar_label)
        );

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupAddChildButton() {
        addChildButton = findViewById(R.id.add_child_button);
        addChildButton.setText(
                addChild ?
                        getString(R.string.add_child_button_text) :
                        getString(R.string.edit_child_button_text)
        );

        if (addChild) {
            addChildButton.setOnClickListener(view -> addOrDiscardChildName());
        } else {
            addChildButton.setOnClickListener(view -> editChildName());
        }
    }

    private void editChildName() {
        if (!childNameIsEmpty()) {
            if (changeHappened()) {
                // ----------change happened--------------
                // set alert dialog box that confirms if the user really wants to change the name
                AlertDialog.Builder builder = getAlertDialogBox();
                builder.setMessage(getString(
                        R.string.confirm_edit_child_dialog_box_message,
                        initialString,
                        childNameInput.getText()
                ));
                builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    changeChildName();
                    saveChildListToSharedPrefs();
                    finish();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                // ---------- change didn't happen -------------
                Toast.makeText(AddChildActivity.this,
                        getString(R.string.did_not_change_name_text_for_dialog_box),
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }
        } else {
            // show a toast that says did not add name because child name was empty
            Toast.makeText(
                    AddChildActivity.this,
                    R.string.empty_name,
                    Toast.LENGTH_SHORT
            ).show();
            finish();
        }
    }

    private void deleteChild() {
        AlertDialog.Builder builder = getAlertDialogBox();
        builder.setMessage(getString(
                R.string.confirm_delete_child_dialog_box_message,
                childNameInput.getText()
        ));
        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {

            FlipHistoryManager historyManager = FlipHistoryManager.getInstance(AddChildActivity.this);
            historyManager.deleteFlipHistoryOfChild(manager.getChild(positionForEditChild));

            PrefConfig.writeFlipHistoryInPref(getApplicationContext(), historyManager.getFullHistory());

            manager.removeChild(positionForEditChild);
            saveChildListToSharedPrefs();
            finish();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void addOrDiscardChildName() {
        if (!childNameIsEmpty()) {
            addChildInfo();
            saveChildListToSharedPrefs();
        } else {
            // show a toast that says did not add name because it was empty
            Toast.makeText(
                    AddChildActivity.this,
                    R.string.empty_name,
                    Toast.LENGTH_SHORT
            ).show();
        }
        finish();
    }

    private boolean childNameIsEmpty() {
        return String.valueOf(childNameInput.getText()).equals("");
    }

    private void addChildInfo() {
        String childName = childNameInput.getText().toString();
        manager.addChild(new Child(childName));
        Toast.makeText(
                AddChildActivity.this,
                getString(R.string.toast_has_been_added_to_list, childName),
                Toast.LENGTH_SHORT
        ).show();
    }


    private AlertDialog.Builder getAlertDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddChildActivity.this);
        builder.setTitle(R.string.warning_message);
        builder.setNegativeButton(R.string.no, null);

        return builder;
    }

    private void changeChildName() {
        String childNameAfterChange = String.valueOf(childNameInput.getText());
        Child child = manager.getChild(positionForEditChild);
        child.setChildName(childNameAfterChange);
    }

    private void setUpInitialString() {
        if (!addChild) {
            initialString = manager.getChild(positionForEditChild).getChildName();
        }
    }

    private boolean changeHappened() {
        return (!initialString.equals(String.valueOf(childNameInput.getText())));
    }

    private void saveChildListToSharedPrefs() {
        PrefConfig.writeChildListInPref(getApplicationContext(), manager.getAllChildren());
    }
}