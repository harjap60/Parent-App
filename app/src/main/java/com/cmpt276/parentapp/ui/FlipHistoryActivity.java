package com.cmpt276.parentapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityFlipHistoryBinding;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildCoinFlip;
import com.cmpt276.parentapp.model.CoinFlip;
import com.cmpt276.parentapp.model.CoinFlipDao;
import com.cmpt276.parentapp.model.ParentAppDatabase;

import java.time.format.DateTimeFormatter;
import java.util.List;

import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Activity to print list of all flips that have happened
 */

public class FlipHistoryActivity extends AppCompatActivity {

    private ActivityFlipHistoryBinding binding;

    public static Intent getIntent(Context context) {
        return new Intent(context, FlipHistoryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlipHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        populateListView();
        setUpToolbar();
    }

    private void populateListView() {
        CoinFlipDao dao = ParentAppDatabase.getInstance(this).coinFlipDao();
        dao.GetAllChildCoinFlips()
                .subscribeOn(Schedulers.io())
                .subscribe((list) -> {
                    FlipHistoryAdaptor adaptor = new FlipHistoryAdaptor(list);
                    binding.flipHistoryList.setAdapter(adaptor);
                });
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.flip_history_activity_toolbar_label);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private class FlipHistoryAdaptor extends ArrayAdapter<ChildCoinFlip> {
        private final List<ChildCoinFlip> list;
        private final String DATE_FORMAT = "yyyy-MM-dd @ KK:mma";

        public FlipHistoryAdaptor(List<ChildCoinFlip> list) {
            super(FlipHistoryActivity.this,
                    R.layout.list_item_flip_history,
                    list
            );
            this.list = list;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater()
                        .inflate(
                                R.layout.list_item_flip_history,
                                parent,
                                false
                        );
            }

            CoinFlip coinFlip = list.get(position).coinFlip;
            Child child = list.get(position).child;

            TextView dateText = itemView.findViewById(R.id.game_date_flip_information_tv);
            dateText.setText(
                    DateTimeFormatter
                            .ofPattern(DATE_FORMAT)
                            .format(coinFlip.getDate())
            );

            TextView childNameText = itemView.findViewById(R.id.child_name_flip_information_tv);
            childNameText.setText(child.getName());

            TextView childChoiceText = itemView.findViewById(R.id.child_choice_flip_information_tv);
            childChoiceText.setText(
                    getString(
                            R.string.child_choice_for_flip,
                            coinFlip.getChoice().toString(),
                            coinFlip.isWinner() ?
                                    getString(R.string.child_won) :
                                    getString(R.string.child_lost)
                    )
            );

            ImageView guessImage = itemView.findViewById(R.id.child_guess_image_flip_information_iv);
            guessImage.setImageResource(
                    coinFlip.isWinner()
                            ? R.drawable.child_guess_correct
                            : R.drawable.child_guess_wrong
            );

            return itemView;
        }
    }

}