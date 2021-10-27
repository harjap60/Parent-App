package com.cmpt276.parentapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildManager;

public class AddChildActivity extends AppCompatActivity {

    ChildManager manager;

    public static Intent getIntent(Context context){
        return new Intent(context, AddChildActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        // instantiating the manager
        manager = ChildManager.getInstance();

        setUpToolbar();
        enableUpButtonOnToolbar();
        setupAddChildButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu
        getMenuInflater().inflate(R.menu.menu_add_child, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_child_button:
                if(validateChildName() == true){
                    addChildInfo();
                }
                else{
                    setAlertDialogBox();
                }
                return true;

            case android.R.id.home:
                Toast.makeText(this, "Going up!!!", Toast.LENGTH_SHORT).show();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getText(R.string.add_child_activity_toolbar_label));
    }

    private void enableUpButtonOnToolbar(){
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupAddChildButton(){
        Button addChildButton = findViewById(R.id.add_child_button);
        addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addChildInfo();
            }
        });
    }

    private void addChildInfo() {
        EditText childNameInput = findViewById(R.id.child_name_edit_text);
        String childName = childNameInput.getText().toString();
        manager.addChild(new Child(childName));
        Toast.makeText(
                AddChildActivity.this,
                "\"" + childName +"\" has been added to the list.",
                Toast.LENGTH_SHORT
        ).show();
        finish();
    }

    private boolean validateChildName(){
        EditText childNameInput = findViewById(R.id.child_name_edit_text);
        //String childName = String.valueOf(childNameInput.getText());

        if(String.valueOf(childNameInput.getText()).equals("")){
            return false;
        }
        else{
            return true;
        }
    }

    public void setAlertDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddChildActivity.this);
        builder.setTitle(R.string.warning_message);
        builder.setMessage(R.string.empty_name);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}