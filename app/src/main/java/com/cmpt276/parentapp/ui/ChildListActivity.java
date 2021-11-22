package com.cmpt276.parentapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityChildListBinding;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildDao;
import com.cmpt276.parentapp.model.ParentAppDatabase;

import java.util.List;

/**
 * Child List Activity - This activity will display a list of all the
 * children added by the user. The user will also have the ability to
 * - add more children to the list and
 * - edit an existing child in the list
 */
public class ChildListActivity extends AppCompatActivity {

    private ActivityChildListBinding binding;

    public static Intent getIntent(Context context) {
        return new Intent(context, ChildListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildListBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        setUpToolbar();
        enableUpOnToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu:
        getMenuInflater().inflate(R.menu.menu_child_list, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_child_button_for_child_list) {
            startActivity(
                    ChildActivity.getIntentForNewChild(ChildListActivity.this)
            );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateListView();
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void enableUpOnToolbar() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void populateListView() {
        new Thread(()->{
            ChildDao childDao = ParentAppDatabase.getInstance(this).childDao();

            List<Child> list = childDao.getAll().blockingGet();

            if (list.size() == 0) {
                return;
            }

            ChildListAdapter adapter = new ChildListAdapter(list);
            runOnUiThread(() -> binding.listChildren.setAdapter(adapter));
        }).start();

    }

    public class ChildListAdapter extends ArrayAdapter<Child> {

        private final List<Child> children;

        public ChildListAdapter(List<Child> children) {
            super(ChildListActivity.this,
                    R.layout.child_list_item,
                    children
            );
            this.children = children;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater()
                        .inflate(
                                R.layout.child_list_item,
                                parent,
                                false
                        );
            }
            Child child = children.get(position);

            TextView childNameTextView = itemView.findViewById(R.id.child_name_text_view);
            childNameTextView.setText(child.getName());

            // Child Image
            ImageView childImage = itemView.findViewById(R.id.item_icon_child);

            // if the user has specified a picture for the child, then set the image of the child
            // otherwise just display the default image for the child
            if (child.getImage() != null) {
                childImage.setImageBitmap(child.getImage());
            }

            itemView.setOnClickListener(v -> {
                Intent i = ChildActivity.getIntentForExistingChild(ChildListActivity.this, child.getChildId());
                startActivity(i);
            });

            return itemView;
        }
    }

}