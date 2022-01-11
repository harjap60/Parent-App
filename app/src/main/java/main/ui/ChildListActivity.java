package main.ui;

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

import com.bumptech.glide.Glide;
import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityChildListBinding;

import java.util.List;

import main.model.Child;
import main.model.ChildDao;
import main.model.ParentAppDatabase;

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

        setupFabAddChild();
        setupToolbar();
    }

    private void setupFabAddChild() {
        binding.floatingActionButton.setOnClickListener((v) ->
                startActivity(ChildActivity.getIntentForNewChild(ChildListActivity.this))
        );
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_child_list, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_child_button_for_child_list) {
            startActivity(ChildActivity.getIntentForNewChild(ChildListActivity.this));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateListView();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void populateListView() {
        new Thread(() -> {
            ChildDao childDao = ParentAppDatabase.getInstance(this).childDao();

            List<Child> list = childDao.getAll().blockingGet();

            ChildListAdapter adapter = new ChildListAdapter(list);
            runOnUiThread(() -> binding.listChildren.setAdapter(adapter));
        }).start();

    }

    public class ChildListAdapter extends ArrayAdapter<Child> {

        private final List<Child> children;

        public ChildListAdapter(List<Child> children) {
            super(ChildListActivity.this,
                    R.layout.list_item_child,
                    children
            );
            this.children = children;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater()
                        .inflate(
                                R.layout.list_item_child,
                                parent,
                                false
                        );
            }
            Child child = children.get(position);

            TextView childNameTextView = itemView.findViewById(R.id.child_name_text_view);
            childNameTextView.setText(child.getName());

            ImageView childImage = itemView.findViewById(R.id.item_icon_child);

            if (child.getImagePath() != null) {
                Glide.with(ChildListActivity.this)
                        .load(child.getImagePath())
                        .centerCrop()
                        .placeholder(R.drawable.child_image_icon)
                        .into(childImage);
            }

            itemView.setOnClickListener(v -> {
                Intent i = ChildActivity.getIntentForExistingChild(ChildListActivity.this, child.getChildId());
                startActivity(i);
            });

            return itemView;
        }
    }

}