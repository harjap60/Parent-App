package com.cmpt276.parentapp.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.model.ChildManager;
import com.cmpt276.parentapp.model.FlipHistoryManager;
import com.cmpt276.parentapp.model.CoinFlip;

import java.util.Random;

/**
 * When the activity starts a coin will appear on the screen with the heads side.
 * When the coin is flipped, a side (heads or tails) will randomly be chosen
 * as the new side facing "up"
 */

public class FlipActivity extends AppCompatActivity {

    private final int ANIMATION_REPEAT_COUNT = 100;
    //50 = 2.5 seconds
    private final int ANIMATION_DURATION = 50;

    ImageView coinImage;
    boolean isHeads;
    Button userChoiceHeads;
    Button userChoiceTails;
    Button historyButton;

    ChildManager childNames;
    int currChildIndex;
    int prevChildIndex;

    CoinFlip flip;
    FlipHistoryManager flipHistoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip);

        childNames = ChildManager.getInstance(FlipActivity.this);
        flipHistoryManager = FlipHistoryManager.getInstance();

        coinImage = findViewById(R.id.coin_image_view);


        setupHistoryButton();

        updateTextView();

        getChildIndex();

        setChoiceButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTextView();
    }

    private void setupHistoryButton() {
        historyButton = findViewById(R.id.flip_history_button);
        historyButton.setOnClickListener(view ->
                startActivity(FlipHistoryActivity.getIntent(this)));
    }


    //Break into 2 methods
    private void setChoiceButtons() {

        //Heads
        userChoiceHeads = findViewById(R.id.user_choice_heads_button);
        userChoiceHeads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {flip = new CoinFlip();
                if (childNames.size() == 0) {
                    flip.setChildNameIndex(childNames.size());
                } else {
                    flip.setChildNameIndex(currChildIndex);
                }
                flip.setChoice(userChoiceHeads.getText().toString());
                flip.startFlip();
                disableAllButtons();
                flipCoin();
            }
        });

        //For tails
        userChoiceTails = findViewById(R.id.user_choice_tails_button);
        userChoiceTails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flip = new CoinFlip();
                if (childNames.size() == 0) {
                    flip.setChildNameIndex(childNames.size());
                } else {
                    flip.setChildNameIndex(currChildIndex);
                }
                flip.setChoice(userChoiceTails.getText().toString());
                flip.startFlip();
                disableAllButtons();
                flipCoin();
            }
        });
    }

    private void disableAllButtons(){
        userChoiceHeads.setEnabled(false);
        userChoiceTails.setEnabled(false);
        historyButton.setEnabled(false);
    }
    private void enableAllButtons(){
        userChoiceHeads.setEnabled(true);
        userChoiceTails.setEnabled(true);
        historyButton.setEnabled(true);
    }


    private void flipCoin() {
        startCoinSound();

        //Source: https://stackoverflow.com/questions/46111262/card-flip-animation-in-android
        final ObjectAnimator firstAnimation = ObjectAnimator.ofFloat(coinImage, "scaleY", 1f, 0f);
        final ObjectAnimator secondAnimation = ObjectAnimator.ofFloat(coinImage, "scaleY", 0f, 1f);

        firstAnimation.setDuration(ANIMATION_DURATION);
        firstAnimation.setRepeatCount(ANIMATION_REPEAT_COUNT);
        firstAnimation.setInterpolator(new DecelerateInterpolator());
        secondAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        firstAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                coinImage.setImageResource(determineSide());
                secondAnimation.start();
                checkWin();
                flipHistoryManager.addFlip(flip);
                incrementChildIndex();
                enableAllButtons();
            }
        });
        firstAnimation.start();
    }

    private void startCoinSound() {
        final MediaPlayer coinFlip = MediaPlayer.create(this, R.raw.coinflip);
        coinFlip.start();
    }

    private void checkWin() {
        if (isHeads && flip.getChoice().equals("Heads")) {
            flip.setIsWinner(true);
        } else if (isHeads && flip.getChoice().equals("Tails")) {
            flip.setIsWinner(false);
        }
        if (!isHeads && flip.getChoice().equals("Heads")) {
            flip.setIsWinner(false);
        } else if (!isHeads && flip.getChoice().equals("Tails")) {
            flip.setIsWinner(true);
        }

    }

    private void getChildIndex() {
        currChildIndex = flipHistoryManager.getCurrentFlipIndex();
        prevChildIndex = flipHistoryManager.getPreviousFlipIndex();
    }

    private void updateTextView() {
        TextView view = findViewById(R.id.flip_index_tv);
        view.setText("Current index " + currChildIndex + "\nPrevious Index " + prevChildIndex);
    }

    private void incrementChildIndex() {
        prevChildIndex = currChildIndex;
        currChildIndex++;
        currChildIndex %= childNames.size();

        flipHistoryManager.setCurrentFlipIndex(currChildIndex);
        flipHistoryManager.setPreviousFlipIndex(prevChildIndex);

        updateTextView();

    }

    //Determine the side of the coin which will be shown
    private int determineSide() {
        Random random = new Random();
        final int NUM_SIDES_COIN = 2;
        int randomNum = random.nextInt(NUM_SIDES_COIN);

        final int HEADS = 1;
        if (randomNum == HEADS) {
            isHeads = true;
            return R.drawable.heads;
        } else {
            isHeads = false;
            return R.drawable.tails;
        }
    }
}