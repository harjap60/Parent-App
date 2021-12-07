package com.cmpt276.parentapp.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityBreatheBinding;

/**
 * Help user to relax and calm down if they feel the need to
 * <p>
 * Current state:
 * Just has a textview and button
 * <p>
 */
public class BreatheActivity extends AppCompatActivity {

    public static final String SHARED_PREFS_NAME = "Shared Prefs";
    public static final String BREATHES = "Breathes";
    private final float BUTTON_DEFAULT_SIZE = 1f;

    private final int MAX_ANIMATION_DURATION_MILLISECONDS = 10000;

    private final int TIME_BREATHE_GOOD_MILLISECONDS = 3000;

    SharedPreferences sharedPreferences;

    private ActivityBreatheBinding binding;
    private long buttonPressedTimerStart;
    private final Integer[] optionsNumOfBreaths = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    private MediaPlayer calmMusic;
    private AnimatorSet scaleUp;
    private AnimatorSet scaleDown;


    private int numBreathsChoice;
    int breathsTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBreatheBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupBeginButton();
        getPrevNumBreaths();
        setupBreatheButtonToChangeSize();
        setupSpinner();
    }

    private void getPrevNumBreaths() {
        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        numBreathsChoice = sharedPreferences.getInt(BREATHES, 1);
    }

    private void setupSpinner() {
        ArrayAdapter<Integer> adapter = new CustomSpinner();
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        binding.numberSpinner.setAdapter(adapter);
        binding.numberSpinner.setSelection(numBreathsChoice - 1);
    }

    public class CustomSpinner extends ArrayAdapter<Integer> {
        public CustomSpinner() {
            super(BreatheActivity.this, R.layout.simple_spinner_item, optionsNumOfBreaths);
        }

        @Override
        public View getView(int position, @Nullable View convertView,
                            @androidx.annotation.NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(
                        R.layout.simple_spinner_item,
                        parent,
                        false
                );
            }

            int currentInt = optionsNumOfBreaths[position];
            TextView intToShow = itemView.findViewById(R.id.breathe_spinner);
            intToShow.setText(String.valueOf(currentInt));

            return itemView;
        }
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
            numBreathsChoice = (binding.numberSpinner.getSelectedItemPosition() + 1);
            binding.numberSpinner.setEnabled(false);

            binding.beginButton.setVisibility(View.INVISIBLE);
            binding.textViewTotalBreathsTaken.setVisibility(View.VISIBLE);
            binding.textViewTotalBreathsTaken.setText(getString(
                    R.string.text_view_breaths_taken_count, breathsTaken, numBreathsChoice
            ));
            binding.breatheButton.setVisibility(View.VISIBLE);
            binding.breatheInfoTextView.setVisibility(View.VISIBLE);
            binding.breatheInfoTextView.setText(R.string.breathe_in_text);
        });
    }

    private void updateBreathCountTextView() {
        binding.textViewTotalBreathsTaken.setText(getString(
                R.string.text_view_breaths_taken_count, breathsTaken, numBreathsChoice
        ));
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void setupBreatheButtonToChangeSize() {

        scaleUp = new AnimatorSet();
        Handler handler = new Handler();

        binding.breatheButton.setOnTouchListener((view, motionEvent) -> {
            // the onTouchListener will only be set only until there are more breaths remaining.
            if (breathsTaken < numBreathsChoice) {
                binding.breatheInfoTextView.setText(R.string.breathe_in_text);

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // the button is pressed
                    buttonPressed(scaleUp, handler);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // the button is released
                    // the button is released
                    buttonReleased(scaleUp, handler);
                }
            }
            return true;
        });
    }

    private void buttonPressed(AnimatorSet scaleUp, Handler handler) {
        calmMusic = MediaPlayer.create(this, R.raw.windy_sea_loop);

        buttonPressedTimerStart = System.currentTimeMillis();
        binding.breatheButton.setBackgroundResource(R.drawable.green_circle);

        //Animation for button size increase
        float BUTTON_SIZE_MAX = 1.5f;
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

        scaleUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (scaleDown.isRunning()) {
                    scaleDown.end();
                }
                calmMusic.start();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                calmMusic.stop();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        // change the colour of the circle to red after 3 seconds of breathing in
        handler.postDelayed(
                () -> binding.breatheButton.setBackgroundResource(R.drawable.red_circle),
                TIME_BREATHE_GOOD_MILLISECONDS
        );

        //Tell user to let go of button after 10s
        handler.postDelayed(() -> {
            Toast.makeText(
                    BreatheActivity.this,
                    getResources().getString(R.string.toast_10s_button_held),
                    Toast.LENGTH_SHORT)
                    .show();
            calmMusic.pause();
        }, MAX_ANIMATION_DURATION_MILLISECONDS);
    }

    private void buttonReleased(AnimatorSet scaleUp, Handler handler) {
        binding.breatheButton.setEnabled(false);

        handler.removeCallbacksAndMessages(null);
        scaleUp.cancel();

        long timeButtonHeldFor = System.currentTimeMillis() - buttonPressedTimerStart;

        //Button held for more than 3 seconds
        if (timeButtonHeldFor >= TIME_BREATHE_GOOD_MILLISECONDS) {
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
    }

    @SuppressLint("ClickableViewAccessibility")
    private void buttonExhale(float buttonX, float buttonY) {

        // basically we want to show the exhale animation for 10 seconds (at least 3 seconds)
        scaleDown = new AnimatorSet();
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

        scaleDownX.setDuration(MAX_ANIMATION_DURATION_MILLISECONDS);
        scaleDownY.setDuration(MAX_ANIMATION_DURATION_MILLISECONDS);
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();

        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                calmMusic.start();
                if (scaleUp.isRunning()) {
                    scaleUp.end();
                }

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                calmMusic.stop();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                calmMusic.stop();

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        // changes the text of the button after 3 seconds of exhaling out
        handler.postDelayed(() -> {
            if (breathsTaken == numBreathsChoice) {
                saveChoice(numBreathsChoice);
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

    private void saveChoice(int choice) {
        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(BREATHES, choice);
        editor.apply();
    }
}