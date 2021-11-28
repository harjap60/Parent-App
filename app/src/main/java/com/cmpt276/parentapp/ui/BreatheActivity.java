package com.cmpt276.parentapp.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityBreatheBinding;

import java.util.concurrent.TimeUnit;

/**
 * Help user to relax and calm down if they feel the need to
 *
 * Current state:
 *          Just has a textview and button
 *
 *TODO:
 *      Make button increase size the longer it is held
 *      When released reset to normal size
 */
public class BreatheActivity extends AppCompatActivity {

    public static final float BUTTON_SIZE_MAX = 2f;
    public static final int MAX_ANIMATION_DURATION = 10000;
    public static final int TIME_BREATHE_GOOD = 3;
    private ActivityBreatheBinding binding;
    private long lastDown;
    private long lastDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBreatheBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupBreatheButton();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.breatheToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.breathe);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupBreatheButton(){
        binding.breatheButton.setOnClickListener(view -> Toast.makeText(
                BreatheActivity.this,
                "Nice you did the breathe",
                Toast.LENGTH_SHORT
        ).show());
        setupButtonToChangeSize();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupButtonToChangeSize(){
        AnimatorSet scaleUp = new AnimatorSet();

        binding.breatheButton.setOnTouchListener((view, motionEvent)->{
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                lastDown = System.currentTimeMillis();
                binding.breatheButton.setBackgroundColor(Color.BLACK);

                //Animation for button size increase
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(binding.breatheButton, "scaleX", BUTTON_SIZE_MAX);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(binding.breatheButton, "scaleY", BUTTON_SIZE_MAX);
                scaleDownX.setDuration(MAX_ANIMATION_DURATION);
                scaleDownY.setDuration(MAX_ANIMATION_DURATION);
                scaleUp.play(scaleDownX).with(scaleDownY);
                scaleUp.start();

                //Tell user to let go of button after 10s
                new Handler().postDelayed(
                        () -> Toast.makeText(
                                BreatheActivity.this,
                                "LET GO OF ME!!!",
                                Toast.LENGTH_SHORT)
                                .show(),
                        MAX_ANIMATION_DURATION);

            } else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                scaleUp.pause();
                lastDuration = System.currentTimeMillis() - lastDown;
                if(TimeUnit.MILLISECONDS.toSeconds(lastDuration) < TIME_BREATHE_GOOD){
                    resetSize(binding.breatheButton);
                    scaleUp.cancel();
                }
                binding.breatheButton.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryVariant));
                Toast.makeText(
                        this,
                        "Held button for: "+
                                TimeUnit.MILLISECONDS.toSeconds(lastDuration) +"s",
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void resetSize(Button btn){
        btn.setScaleX(1f);
        btn.setScaleY(1f);
    }


    public static Intent getIntent(Context context) {
        return new Intent(context, BreatheActivity.class);
    }
}