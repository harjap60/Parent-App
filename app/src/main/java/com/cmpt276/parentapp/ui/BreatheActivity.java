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

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityBreatheBinding;

import java.util.concurrent.TimeUnit;

/**
 * Help user to relax and calm down if they feel the need to
 * <p>
 * Current state:
 * Just has a textview and button
 * <p>
 */
public class BreatheActivity extends AppCompatActivity {

    private final float BUTTON_SIZE_MAX = 2f;
    private final float BUTTON_DEFAULT_SIZE = 1f;
    private final int MAX_ANIMATION_DURATION = 10000;
    private final int TIME_BREATHE_GOOD = 3;
    private final int MIN_NUM_BREATHS = 0;
    private final int MAX_NUM_BREATHS = 10;
    private ActivityBreatheBinding binding;
    private long buttonPressedTimerStart;
    private long timeButtonHeldFor;

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

    private void setupBeginButton() {
        binding.noOfBreaths.setEnabled(true);
        binding.beginButton.setVisibility(View.VISIBLE);
        binding.breatheButton.setVisibility(View.INVISIBLE);
        binding.textViewTotalBreathsTaken.setVisibility(View.INVISIBLE);
        binding.beginButton.setOnClickListener(view -> {
            // get the number of breaths
            if (binding.noOfBreaths.getText().toString().equals("")) {
                Toast.makeText(BreatheActivity.this,
                        getString(R.string.breathe_value_null),
                        Toast.LENGTH_SHORT).show();
            } else {

                numBreaths = Integer.parseInt(binding.noOfBreaths.getText().toString());

                //Check if number enter is valid
                if (numBreaths > MIN_NUM_BREATHS && numBreaths <= MAX_NUM_BREATHS) {

                    // need to add a check that will make sure that the user has selected
                    // a specific no of breaths and only then do the following things
                    // might not need to add the check if we are using a spinner instead of edit text
                    // and in that case, we can select a default value of the spinner and we do not
                    // need to add the check
                    binding.noOfBreaths.setEnabled(false);


                    binding.beginButton.setVisibility(View.INVISIBLE);
                    binding.textViewTotalBreathsTaken.setVisibility(View.VISIBLE);
                    binding.textViewTotalBreathsTaken.setText(getString(R.string.text_view_breaths_taken_count, breathsTaken, numBreaths));
                    binding.breatheButton.setVisibility(View.VISIBLE);
                    binding.breatheButton.setText(R.string.breathe_in_button_text);
                } else {
                    Toast.makeText(
                            BreatheActivity.this,
                            getString(
                                    R.string.breaths_entered_invalid,
                                    MIN_NUM_BREATHS,
                                    MAX_NUM_BREATHS
                            ),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateBreathCountTextView() {
        binding.textViewTotalBreathsTaken.setText(getString(R.string.text_view_breaths_taken_count, breathsTaken, numBreaths));
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void setupBreatheButtonToChangeSize() {

        AnimatorSet scaleUp = new AnimatorSet();
        Handler handler = new Handler();

        binding.breatheButton.setOnTouchListener((view, motionEvent) -> {

            if (breathsTaken < numBreaths) {

                binding.breatheButton.setText(R.string.breathe_in_button_text);
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    buttonPressedTimerStart = System.currentTimeMillis();
                    binding.breatheButton.setBackgroundColor(Color.BLACK);

                    //Animation for button size increase
                    ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(binding.breatheButton, "scaleX", BUTTON_SIZE_MAX);
                    ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(binding.breatheButton, "scaleY", BUTTON_SIZE_MAX);
                    scaleUpX.setDuration(MAX_ANIMATION_DURATION);
                    scaleUpY.setDuration(MAX_ANIMATION_DURATION);
                    scaleUp.play(scaleUpX).with(scaleUpY);
                    scaleUp.start();

                    //Tell user to let go of button after 10s
                    handler.postDelayed(
                            () -> Toast.makeText(
                                    BreatheActivity.this,
                                    getResources().getString(R.string.toast_10s_button_held),
                                    Toast.LENGTH_SHORT)
                                    .show(),
                            MAX_ANIMATION_DURATION);

                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    handler.removeCallbacksAndMessages(null);
                    scaleUp.cancel();

                    timeButtonHeldFor = System.currentTimeMillis() - buttonPressedTimerStart;

                    //Button held for more than 3 seconds
                    if (TimeUnit.MILLISECONDS.toSeconds(timeButtonHeldFor) >= TIME_BREATHE_GOOD) {
                        binding.breatheButton.setText(R.string.breathe_out_button_text);

                        breathsTaken++;
                        updateBreathCountTextView();

                        buttonExhale(binding.breatheButton.getScaleX(), binding.breatheButton.getScaleY());
                    }
                    // Button released before 3 seconds ,
                    // i.e., the breathe is incomplete and need to breathe again
                    else {
                        resetSize(binding.breatheButton);
                    }

                    // not sure if we need the following toast
                    Toast.makeText(
                            this,
                            getResources().getString(R.string.button_time_held, TimeUnit.MILLISECONDS.toSeconds(timeButtonHeldFor)),
                            Toast.LENGTH_SHORT).show();
                }

            }
            return true;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void buttonExhale(float buttonX, float buttonY) {

        // basically we want to show the exhale animation for 10 seconds (at least 3 seconds)
        AnimatorSet scaleDown = new AnimatorSet();
        Handler handler = new Handler();

        binding.breatheButton.setScaleX(buttonX);
        binding.breatheButton.setScaleY(buttonY);
        binding.breatheButton.setBackgroundColor(Color.RED);

        //Animation for button size increase
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(binding.breatheButton, "scaleX", BUTTON_DEFAULT_SIZE);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(binding.breatheButton, "scaleY", BUTTON_DEFAULT_SIZE);
        scaleDownX.setDuration(MAX_ANIMATION_DURATION);
        scaleDownY.setDuration(MAX_ANIMATION_DURATION);
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();

        // todo: should we disable touchListener/ button for the
        //  3 seconds while the user is breathing out

        // changes the text of the button after 3 seconds of exhaling out
        handler.postDelayed(() -> {
            // also 'might' want to disable the onTouchListener on the button for
            // the next 3 seconds while the user exhales out

            if (breathsTaken == numBreaths) {
                binding.breatheButton.setText(R.string.all_breaths_taken);
            } else {
                binding.breatheButton.setText(R.string.breathe_in_button_text);
            }
        }, 3000);

    }

    private void resetSize(Button btn) {
        btn.setScaleX(BUTTON_DEFAULT_SIZE);
        btn.setScaleY(BUTTON_DEFAULT_SIZE);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, BreatheActivity.class);
    }
}