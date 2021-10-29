package com.cmpt276.parentapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildManager;
import com.cmpt276.parentapp.model.PrefConfig;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChildListActivity extends AppCompatActivity {

    ChildManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_list);

        manager = ChildManager.getInstance();
        List<Child> children = PrefConfig.readListFromPref(this);
        if(children != null){
            manager.setChildren(children);
        }

        setUpToolbar();
        setUpNewGameButton();
        populateListView();
        registerClickCallback();

    }

    @Override
    protected void onResume(){
        super.onResume();
        updateUI();
    }
    private void updateUI(){
        populateListView();
    }

    private void populateListView(){
        // Build the adapter
        ArrayAdapter<Child> adapter = new MyListAdapter();

        // Configure the list view
        ListView childList = findViewById(R.id.child_list_view);
        childList.setAdapter(adapter);
    }

    // MyListAdapter that will help make the complex list view
    private class MyListAdapter extends  ArrayAdapter<Child>{
        public MyListAdapter(){
            super(ChildListActivity.this, R.layout.child_name_view, manager.getAllChildren());
        }
        public View getView(int position, View convertView, ViewGroup parent){

            // make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.child_name_view, parent, false);
            }

            // Find the child to work with
            Child currentChild = manager.retrieveChildByIndex(position);

            // Fill the view
            TextView childNameText = itemView.findViewById(R.id.child_name_text_view);
            childNameText.setText(currentChild.getChildName());

            return itemView;
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getText(R.string.child_list_activity_toolbar_label));
    }

    private void setUpNewGameButton() {
        FloatingActionButton addNewChildFabButton = findViewById(R.id.add_child_fab);
        addNewChildFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(
                        ChildListActivity.this,
                        "This button will let you add a new child",
                        Toast.LENGTH_SHORT
                ).show();
                startActivity(AddChildActivity.makeIntentForAddChild(ChildListActivity.this));
            }
        });
    }

    private void registerClickCallback(){
        ListView list = (ListView) findViewById(R.id.child_list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                startActivity(AddChildActivity.makeIntentForEditChild(ChildListActivity.this, position));
            }
        });
    }

   /* private void storeChildListToSharedPrefs(){
        Set<String> set = new HashSet<String>();
        set.addAll(manager.getAllChildren());
        SharedPreferences prefs = getSharedPreferences("Child List Prefs String", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.put
    }*/
}