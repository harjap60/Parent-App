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

    private final float BUTTON_SIZE_MAX = 2f;
    private final int MAX_ANIMATION_DURATION = 10000;
    private final int TIME_BREATHE_GOOD = 3;
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
        setupButtonToChangeSize();
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void setupButtonToChangeSize(){
        AnimatorSet scaleUp = new AnimatorSet();
        Handler handler = new Handler();

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
                handler.postDelayed(
                        () -> Toast.makeText(
                                BreatheActivity.this,
                                getResources().getString(R.string.toast_10s_button_held),
                                Toast.LENGTH_SHORT)
                                .show(),
                        MAX_ANIMATION_DURATION);

            } else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                handler.removeCallbacksAndMessages(null);
                scaleUp.pause();
                lastDuration = System.currentTimeMillis() - lastDown;
                if(TimeUnit.MILLISECONDS.toSeconds(lastDuration) < TIME_BREATHE_GOOD){
                    resetSize(binding.breatheButton);
                    scaleUp.cancel();
                }
                binding.breatheButton.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryVariant));
                Toast.makeText(
                        this,
                        getResources().getString(R.string.button_time_held, TimeUnit.MILLISECONDS.toSeconds(lastDuration)),
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void resetSize(Button btn){
        final float BUTTON_DEFAULT_SIZE = 1f;
        btn.setScaleX(BUTTON_DEFAULT_SIZE);
        btn.setScaleY(BUTTON_DEFAULT_SIZE);
    }


    public static Intent getIntent(Context context) {
        return new Intent(context, BreatheActivity.class);
    }
}