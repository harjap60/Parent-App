package com.cmpt276.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.util.Random;

/**
 *When the activity starts a coin will appear on the screen with the heads side.
 * When the coin is flipped, a side (heads or tails) will randomly be chosen
 * as the new side facing "up"
 *
 */

public class FlipActivity extends AppCompatActivity {

    private final int ANIMATION_REPEAT_COUNT = 100;
    private final int ANIMATION_DURATION = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip);

        flipCoin();
    }

    private void flipCoin() {
        ImageView imageView = findViewById(R.id.coin_image_view);
        final MediaPlayer coinFlip = MediaPlayer.create(this, R.raw.coinflip);

        //Source: https://stackoverflow.com/questions/46111262/card-flip-animation-in-android
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                coinFlip.start();
                final ObjectAnimator firstAnimation = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 0f);
                final ObjectAnimator secondAnimation = ObjectAnimator.ofFloat(imageView, "scaleY", 0f, 1f);

                //50 = 2.5 seconds
                firstAnimation.setDuration(ANIMATION_DURATION);
                //Number of flips the coin does in the duration
                firstAnimation.setRepeatCount(ANIMATION_REPEAT_COUNT);

                firstAnimation.setInterpolator(new DecelerateInterpolator());
                secondAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

                firstAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        imageView.setImageResource(determineSide());
                        secondAnimation.start();
                    }
                });
                firstAnimation.start();
            }
        });
    }

    //Determine the side of the coin which will be shown
    private int determineSide() {
        Random random = new Random();
        int NUM_SIDES_COIN = 2;
        int randomNum = random.nextInt(NUM_SIDES_COIN);

        int HEADS = 1;
        if (randomNum == HEADS) {
            return R.drawable.heads;
        } else {
            return R.drawable.tails;
        }

    }
}