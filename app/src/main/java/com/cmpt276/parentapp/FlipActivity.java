package com.cmpt276.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class FlipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip);

        flipCoin();
    }

    private void flipCoin(){
        ImageView imageView = findViewById(R.id.coin_image_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ObjectAnimator firstAnimation = ObjectAnimator
                        .ofFloat(imageView, "scaleY", 1f, 0f);
                final ObjectAnimator secondAnimation = ObjectAnimator
                        .ofFloat(imageView, "scaleY", 0f, 1f);

                firstAnimation.setDuration(100);
                firstAnimation.setRepeatCount(100);
                firstAnimation.setInterpolator(new DecelerateInterpolator());
                secondAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

                firstAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        imageView.setImageResource(R.drawable.tails);
                        secondAnimation.start();
                    }
                });
                firstAnimation.start();
            }
        });
    }
}