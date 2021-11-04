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
import com.cmpt276.parentapp.model.HistoryOfFlips;
import com.cmpt276.parentapp.model.SingleFlipInformation;

import java.time.format.DateTimeFormatter;

public class FlipHistoryActivity extends AppCompatActivity {

    HistoryOfFlips historyOfFlips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_history);

        historyOfFlips = HistoryOfFlips.getInstance();
        populateListView();
    }

    public static Intent getIntent(Context context){
        return new Intent(context, FlipHistoryActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI(){
        populateListView();
    }

    private void populateListView(){
        ArrayAdapter<SingleFlipInformation> adapter = new MyListAdapter();
        ListView historyList = findViewById(R.id.flip_history_list);
        historyList.setAdapter(adapter);
    }
    private class MyListAdapter extends  ArrayAdapter<SingleFlipInformation>{
        public MyListAdapter(){
            super(
                    FlipHistoryActivity.this,
                    R.layout.single_flip_information,
                    historyOfFlips.getFullHistory()
            );
        }
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(
                        R.layout.single_flip_information, parent ,false
                );
            }
            SingleFlipInformation currentFlip = historyOfFlips.getFlip(position);

            TextView dateText = itemView.findViewById(R.id.game_date_flip_information_tv);
            String date = DateTimeFormatter.ofPattern("MMM-dd@KK:mma").format(currentFlip.getFlipTime());
            dateText.setText(date);

            TextView childNameText = itemView.findViewById(R.id.child_name_flip_information_tv);
            childNameText.setText(currentFlip.getChildName());

            TextView childChoiceText = itemView.findViewById(R.id.child_choice_flip_information_tv);
            childChoiceText.setText("Chose: "+ currentFlip.getChoice());

            TextView isWinText = itemView.findViewById(R.id.did_win_flip_information_tv);
            isWinText.setText(String.valueOf(currentFlip.isWinner()));

            ImageView guessImage = itemView.findViewById(R.id.child_guess_image_flip_information_iv);
            if(currentFlip.isWinner()){
                guessImage.setImageResource(R.drawable.child_guess_correct);
            }else{
                guessImage.setImageResource(R.drawable.child_guess_wrong);
            }
            return itemView;
        }
    }

}