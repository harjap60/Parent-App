/**
 * Child List Activity - This activity will display a list of all the
 * children added by the user. The user will also have the ability to
 * - add more children to the list and
 * - edit an existing child in the list
 */
package com.cmpt276.parentapp.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChildListActivity extends AppCompatActivity {

    ChildManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_list);

        // instantiating the manager
        manager = ChildManager.getInstance(ChildListActivity.this);

        setUpToolbar();
        enableUpOnToolbar();
        setUpAddNewChildButton();
        populateListView();
        registerClickCallback();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu:
        getMenuInflater().inflate(R.menu.menu_child_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_child_button_for_child_list:
                startActivity(
                        AddChildActivity.makeIntentForAddChild(ChildListActivity.this)
                );
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        // updateUI
        populateListView();
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getText(R.string.child_list_activity_toolbar_label));
    }

    private void enableUpOnToolbar(){
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setUpAddNewChildButton() {
        FloatingActionButton addNewChildFabButton = findViewById(R.id.add_child_fab);
        addNewChildFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        AddChildActivity.makeIntentForAddChild(ChildListActivity.this)
                );
            }
        });
    }

    private void populateListView(){
        // Build the adapter
        ArrayAdapter<Child> adapter = new MyListAdapter();

        // Configure the list view
        ListView childList = findViewById(R.id.child_list_view);
        childList.setAdapter(adapter);
    }

    private void registerClickCallback(){
        ListView list = (ListView) findViewById(R.id.child_list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                startActivity(
                        AddChildActivity.makeIntentForEditChild(
                                ChildListActivity.this, position)
                );
            }
        });
    }

    // MyListAdapter that will help make the complex list view
    private class MyListAdapter extends  ArrayAdapter<Child>{
        public MyListAdapter(){
            super(ChildListActivity.this,
                    R.layout.child_name_view, manager.getAllChildren());
        }
        public View getView(int position, View convertView, ViewGroup parent){

            // make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater()
                        .inflate(R.layout.child_name_view, parent, false);
            }

            // Find the child to work with
            Child currentChild = manager.getChild(position);

            // Fill the view
            TextView childNameText = itemView.findViewById(R.id.child_name_text_view);
            childNameText.setText(currentChild.getChildName());

            return itemView;
        }
    }
}