package com.cmpt276.parentapp.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

/**
 * todo:
 *  - add sound effect to breathe in and out
 *  - fix the linear layout, the 2nd text view is chipped out
 *  - add shared prefs to remember the last choice of the user selected breaths
 *  - test for all screen sizes
 *  - make code better by refactoring strings and not hardcode them
 *  - might want to change colour (or their hex values)
 */
public class BreatheActivity extends AppCompatActivity {

    private final float BUTTON_SIZE_MAX = 2f;
    private final float BUTTON_DEFAULT_SIZE = 1f;

    private final int MAX_ANIMATION_DURATION_MILLISECONDS = 10000;
    private final int MAX_ANIMATION_DURATION_SECONDS = 10;

    private final int TIME_BREATHE_GOOD_MILLISECONDS = 3000;
    private final int TIME_BREATHE_GOOD_SECONDS = 3;

    private ActivityBreatheBinding binding;
    private long buttonPressedTimerStart;
    private long timeButtonHeldFor;
    private Integer[] optionsNumOfBreaths = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

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

        setupSpinner();
    }

    private void setupSpinner() {
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionsNumOfBreaths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.numberSpinner.setAdapter(adapter);
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
        breathsTaken = 0;
        binding.beginButton.setVisibility(View.VISIBLE);
        binding.breatheInfoTextView.setVisibility(View.INVISIBLE);
        binding.breatheButton.setVisibility(View.INVISIBLE);
        binding.textViewTotalBreathsTaken.setVisibility(View.INVISIBLE);
        binding.beginButton.setOnClickListener(view -> {
            // get the number of breaths
            numBreaths = (binding.numberSpinner.getSelectedItemPosition() + 1);
            binding.numberSpinner.setEnabled(false);

            binding.beginButton.setVisibility(View.INVISIBLE);
            binding.textViewTotalBreathsTaken.setVisibility(View.VISIBLE);
            binding.textViewTotalBreathsTaken.setText(getString(
                    R.string.text_view_breaths_taken_count, breathsTaken, numBreaths
            ));
            binding.breatheButton.setVisibility(View.VISIBLE);
            binding.breatheInfoTextView.setVisibility(View.VISIBLE);
            binding.breatheInfoTextView.setText(R.string.breathe_in_text);
        });
    }

    private void updateBreathCountTextView() {
        binding.textViewTotalBreathsTaken.setText(getString(
                R.string.text_view_breaths_taken_count, breathsTaken, numBreaths
        ));
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void setupBreatheButtonToChangeSize() {

        AnimatorSet scaleUp = new AnimatorSet();
        Handler handler = new Handler();

        binding.breatheButton.setOnTouchListener((view, motionEvent) -> {
            // the onTouchListener will only be set only until there are more breaths remaining.
            if (breathsTaken < numBreaths) {
                binding.breatheInfoTextView.setText(R.string.breathe_in_text);

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // the button is pressed
                    buttonPressed(scaleUp, handler);
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // the button is released
                    // the button is released
                    buttonReleased(scaleUp, handler);
                }
            }
            return true;
        });
    }

    private void buttonPressed(AnimatorSet scaleUp, Handler handler) {
        buttonPressedTimerStart = System.currentTimeMillis();
        binding.breatheButton.setBackgroundResource(R.drawable.green_circle);

        //Animation for button size increase
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(
                binding.breatheButton, "scaleX", BUTTON_SIZE_MAX
        );
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(
                binding.breatheButton, "scaleY", BUTTON_SIZE_MAX
        );
        scaleUpX.setDuration(MAX_ANIMATION_DURATION_MILLISECONDS);
        scaleUpY.setDuration(MAX_ANIMATION_DURATION_MILLISECONDS);
        scaleUp.play(scaleUpX).with(scaleUpY);
        scaleUp.start();

        // change the colour of the circle to red after 3 seconds of breathing in
        handler.postDelayed(
                () -> binding.breatheButton.setBackgroundResource(R.drawable.red_circle),
                TIME_BREATHE_GOOD_MILLISECONDS
        );

        //Tell user to let go of button after 10s
        handler.postDelayed(
                () -> Toast.makeText(
                        BreatheActivity.this,
                        getResources().getString(R.string.toast_10s_button_held),
                        Toast.LENGTH_SHORT)
                        .show(),
                MAX_ANIMATION_DURATION_MILLISECONDS);
    }

    private void buttonReleased(AnimatorSet scaleUp, Handler handler) {
        binding.breatheButton.setEnabled(false);

        handler.removeCallbacksAndMessages(null);
        scaleUp.cancel();

        timeButtonHeldFor = System.currentTimeMillis() - buttonPressedTimerStart;

        //Button held for more than 3 seconds
        if (TimeUnit.MILLISECONDS.toSeconds(timeButtonHeldFor) >= TIME_BREATHE_GOOD_SECONDS) {
            binding.breatheInfoTextView.setText(R.string.breathe_out_text);

            breathsTaken++;
            updateBreathCountTextView();

            buttonExhale(
                    binding.breatheButton.getScaleX(),
                    binding.breatheButton.getScaleY()
            );
        }
        // Button released before 3 seconds ,
        // i.e., the breathe is incomplete and need to breathe again
        else {
            resetSize(binding.breatheButton);
        }

        // TODO: not sure if we need the following toast
        Toast.makeText(
                this,
                getResources().getString(
                        R.string.button_time_held,
                        TimeUnit.MILLISECONDS.toSeconds(timeButtonHeldFor)
                ),
                Toast.LENGTH_SHORT
        ).show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void buttonExhale(float buttonX, float buttonY) {

        // basically we want to show the exhale animation for 10 seconds (at least 3 seconds)
        AnimatorSet scaleDown = new AnimatorSet();
        Handler handler = new Handler();

        binding.breatheButton.setScaleX(buttonX);
        binding.breatheButton.setScaleY(buttonY);
        binding.breatheButton.setBackgroundResource(R.drawable.red_circle);

        //Animation for button size increase
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(
                binding.breatheButton, "scaleX", BUTTON_DEFAULT_SIZE
        );
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(
                binding.breatheButton, "scaleY", BUTTON_DEFAULT_SIZE
        );

        scaleDownX.setDuration(TIME_BREATHE_GOOD_MILLISECONDS);
        scaleDownY.setDuration(TIME_BREATHE_GOOD_MILLISECONDS);
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();

        // changes the text of the button after 3 seconds of exhaling out
        handler.postDelayed(() -> {
            if (breathsTaken == numBreaths) {
                binding.breatheInfoTextView.setText(R.string.all_breaths_taken);
            } else {
                binding.breatheInfoTextView.setText(R.string.breathe_in_text);
            }
            binding.breatheButton.setBackgroundResource(R.drawable.green_circle);
            binding.breatheButton.setEnabled(true);
        }, TIME_BREATHE_GOOD_MILLISECONDS);

    }

    private void resetSize(ImageButton btn) {
        btn.setScaleX(BUTTON_DEFAULT_SIZE);
        btn.setScaleY(BUTTON_DEFAULT_SIZE);
        binding.breatheButton.setEnabled(true);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, BreatheActivity.class);
    }
}