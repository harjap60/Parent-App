package com.cmpt276.parentapp.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityBreatheBinding;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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

        binding.breatheButton.setOnTouchListener((view, motionEvent)->{
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                lastDown = System.currentTimeMillis();
                binding.breatheButton.setBackgroundColor(Color.BLACK);

                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(binding.breatheButton, "scaleX", 1.5f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(binding.breatheButton, "scaleY", 1.5f);                scaleDownX.setDuration(10000);
                scaleDownX.setDuration(5000);
                scaleDownY.setDuration(5000);

                AnimatorSet scaleUp = new AnimatorSet();
                scaleUp.play(scaleDownX).with(scaleDownY);
                scaleUp.start();
                Toast.makeText(this, "Button timer start", Toast.LENGTH_SHORT).show();

            } else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                lastDuration = System.currentTimeMillis() - lastDown;
                binding.breatheButton.setBackgroundColor(getResources().getColor(R.color.primaryVariant));
                Toast.makeText(this, "Button Pressed for: "+ TimeUnit.MILLISECONDS.toSeconds(lastDuration) +"s", Toast.LENGTH_SHORT).show();
                resetSize(binding.breatheButton);
            }else if((System.currentTimeMillis() - lastDown) == 1000){
                Toast.makeText(this, "Button held for 1000ms", Toast.LENGTH_SHORT).show();
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