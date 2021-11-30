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
import android.view.View;
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

    private int numBreaths;
    int breathsTaken = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBreatheBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupBeginButton();
        setupBreatheButtonToChangeSize();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.breatheToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.breathe);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupBeginButton(){
        binding.noOfBreaths.setEnabled(true);
        binding.beginButton.setVisibility(View.VISIBLE);
        binding.breatheButton.setVisibility(View.INVISIBLE);
        binding.textViewTotalBreathsTaken.setVisibility(View.INVISIBLE);
        binding.beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // need to add a check that will make sure that the user has selected
                // a specific no of breaths and only then do the following things
                // might not need to add the check if we are using a spinner instead of edit text
                // and in that case, we can select a default value of the spinner and we do not
                // need to add the check
                binding.noOfBreaths.setEnabled(false);

                // get the number of breaths
                numBreaths = Integer.parseInt(String.valueOf(binding.noOfBreaths.getText()));

                binding.beginButton.setVisibility(View.INVISIBLE);
                binding.textViewTotalBreathsTaken.setVisibility(View.VISIBLE);
                binding.textViewTotalBreathsTaken.setText(getString(R.string.text_view_breaths_taken_count, breathsTaken));
                binding.breatheButton.setVisibility(View.VISIBLE);
                binding.breatheButton.setText("Breathe IN");
            }
        });
    }

    /*private void takeBreaths() {

        //Toast.makeText(this, "Reached here", Toast.LENGTH_SHORT).show();
        while (breathsTaken < numBreaths) {

            if (breathTaken()) {
                // make a toast that shows the number of breaths taken
                //setupButtonToChangeSize();
                breathsTaken++;
            }
        }
    }

    private boolean breathTaken() {
        // if button is held for more than 3 seconds, then return true
        // otherwise return false

        setupButtonToChangeSize();
        return false;
    }*/

    private void updateBreathCountTextView () {
        binding.textViewTotalBreathsTaken.setText(getString(R.string.text_view_breaths_taken_count, breathsTaken));
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void setupBreatheButtonToChangeSize(){

        AnimatorSet scaleUp = new AnimatorSet();
        Handler handler = new Handler();

        binding.breatheButton.setOnTouchListener((view, motionEvent) -> {

            if (breathsTaken < numBreaths) {

                binding.breatheButton.setText("Breathe IN");
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
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

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    handler.removeCallbacksAndMessages(null);
                    scaleUp.cancel();
                    lastDuration = System.currentTimeMillis() - lastDown;

                    if (TimeUnit.MILLISECONDS.toSeconds(lastDuration) >= TIME_BREATHE_GOOD) {
                        binding.breatheButton.setText("Breathe OUT");

                        breathsTaken++;
                        updateBreathCountTextView();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // also 'might' want to disable the onTouchListener on the button for
                                // the next 3 seconds while the user exhales out

                                if (breathsTaken == numBreaths) {
                                    binding.breatheButton.setText("Good Job");
                                }
                                else {
                                    binding.breatheButton.setText("Breathe IN");
                                }
                            }
                        }, 3000);
                    }

                    if (TimeUnit.MILLISECONDS.toSeconds(lastDuration) < TIME_BREATHE_GOOD) {
                        resetSize(binding.breatheButton);
                        scaleUp.cancel();
                    }
                    binding.breatheButton.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryVariant));
                    Toast.makeText(
                            this,
                            getResources().getString(R.string.button_time_held, TimeUnit.MILLISECONDS.toSeconds(lastDuration)),
                            Toast.LENGTH_SHORT).show();
                }

            }return true;
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