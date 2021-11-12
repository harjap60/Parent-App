package com.cmpt276.parentapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityChildListBinding;
import com.cmpt276.parentapp.databinding.ChildListItemBinding;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildDao;
import com.cmpt276.parentapp.model.ParentAppDatabase;

import java.util.List;

import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Child List Activity - This activity will display a list of all the
 * children added by the user. The user will also have the ability to
 * - add more children to the list and
 * - edit an existing child in the list
 */
public class ChildListActivity extends AppCompatActivity {

    ActivityChildListBinding binding;

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

        ChildDao childDao = ParentAppDatabase.getInstance(this).childDao();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.listChildren.setLayoutManager(layoutManager);

        childDao.getAll().subscribeOn(Schedulers.newThread())
                .subscribe((List<Child> list) -> {

                    if (list.size() == 0) {
                        return;
                    }
                    ChildListAdapter adapter = new ChildListAdapter(list);
                    binding.listChildren.setAdapter(adapter);
                });

    }

    /**
     * Implemented Adapter with help from https://developer.android.com/guide/topics/ui/layout/recyclerview
     */
    public class ChildListAdapter extends RecyclerView.Adapter<ChildListAdapter.ViewHolder> {

        private final List<Child> list;

        public ChildListAdapter(List<Child> list) {

            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ChildListItemBinding binding = ChildListItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Child child = list.get(position);
            holder.setChild(child);

        }

        @Override
        public int getItemCount() {

            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ChildListItemBinding binding;
            private Child child;

            public ViewHolder(ChildListItemBinding binding) {
                super(binding.getRoot());

                itemView.setOnClickListener(view -> {
                    if (child == null) {
                        Toast.makeText(ChildListActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent i = ChildActivity.getIntentForExistingChild(ChildListActivity.this, child.getUid());
                    startActivity(i);
                });

                this.binding = binding;
            }

            public void setChild(Child child) {
                this.child = child;
                binding.childNameTextView.setText(child.getName());
            }
        }
    }

}