package com.cmpt276.parentapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.model.ChildManager;
import com.cmpt276.parentapp.model.FlipHistoryManager;
import com.cmpt276.parentapp.model.CoinFlip;

import java.time.format.DateTimeFormatter;

public class FlipHistoryActivity extends AppCompatActivity {

    FlipHistoryManager flipHistoryManager;
    ChildManager childManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_history);

        flipHistoryManager = FlipHistoryManager.getInstance();
        childManager = ChildManager.getInstance(FlipHistoryActivity.this);
        populateListView();
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, FlipHistoryActivity.class);
    }

    private void populateListView() {
        ArrayAdapter<CoinFlip> adapter = new MyListAdapter();
        ListView historyList = findViewById(R.id.flip_history_list);
        historyList.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<CoinFlip> {
        public MyListAdapter() {
            super(
                    FlipHistoryActivity.this,
                    R.layout.flip_history_view,
                    flipHistoryManager.getFullHistory()
            );
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(
                        R.layout.flip_history_view, parent, false
                );
            }

            CoinFlip currentFlip = flipHistoryManager.getFlip(position);

            TextView dateText = itemView.findViewById(R.id.game_date_flip_information_tv);
            String date = DateTimeFormatter.ofPattern("MMM-dd@KK:mma").format(currentFlip.getFlipTime());
            dateText.setText(date);

            TextView childNameText = itemView.findViewById(R.id.child_name_flip_information_tv);
            if(currentFlip.getChild() == null){
                childNameText.setText("");
            }
            else {
                childNameText.setText(currentFlip.getChild().getChildName());
            }

            TextView childChoiceText = itemView.findViewById(R.id.child_choice_flip_information_tv);
            childChoiceText.setText(getString(R.string.child_choice_for_flip, currentFlip.getChoice(),
                    currentFlip.isWinner() ? "won" : "lost"));

            ImageView guessImage = itemView.findViewById(R.id.child_guess_image_flip_information_iv);
            if (currentFlip.isWinner()) {
                guessImage.setImageResource(R.drawable.child_guess_correct);
            } else {
                guessImage.setImageResource(R.drawable.child_guess_wrong);
            }
            return itemView;
        }
    }

}