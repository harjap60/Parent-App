package com.cmpt276.parentapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

        manager = ChildManager.getInstance();
        setupAddChildButton();

    }


    private void setupAddChildButton(){
        Button addChildButton = findViewById(R.id.add_child_button);
        addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = findViewById(R.id.child_name_edit_text);
                String childName = text.getText().toString();
                manager.addKid(new Child(childName));
                Toast.makeText(
                        AddChildActivity.this,
                        childName +" has been added to the list.",
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }
        });
    }

}