package com.cmpt276.parentapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.databinding.ActivityTimerRunningBinding;

public class TimerActivity extends AppCompatActivity {

    public static final int COUNT_DOWN_INTERVAL = 1000;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int MINUTES_IN_HOUR = 60;

    public static final String TIMER_DURATION = "TIMER_DURATION_TAG";

    private ActivityTimerRunningBinding binding;
    private long initialMillisUntilFinished;
    private long millisUntilFinished;
    private CountDownTimer timer;

    public static Intent getIntentWithDurationMinutes(Context context, int duration) {
        Intent i = new Intent(context, TimerActivity.class);
        i.putExtra(TIMER_DURATION, duration * SECONDS_IN_MINUTE * COUNT_DOWN_INTERVAL);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimerRunningBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        extractDurationFromIntent();
        setupPauseResumeButton();
        setupResetTimerButton();
        createAndStartTimer();
        updateUI();
    }

    private void extractDurationFromIntent() {
        initialMillisUntilFinished = (long) this.getIntent().getIntExtra(TIMER_DURATION, 0);
        millisUntilFinished = initialMillisUntilFinished;
    }

    private void createAndStartTimer() {
        timer = new CountDownTimer(millisUntilFinished, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimerActivity.this.millisUntilFinished = millisUntilFinished;
                updateUI();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void pauseTimer() {
        if(timer == null){
            return;
        }

        timer.cancel();
        timer = null;
    }

    private void setupResetTimerButton() {
        binding.btnTimerReset.setOnClickListener(v -> {
            pauseTimer();
            millisUntilFinished = initialMillisUntilFinished;
            updateUI();
        });
    }

    private void setupPauseResumeButton() {
        binding.btnTimerPause.setOnClickListener(v -> {
            if (timer != null) {
                pauseTimer();
            } else {
                createAndStartTimer();
            }
            updateUI();
        });
    }

    private void updateUI() {
        int pauseButtonString = R.string.btn_timer_start;
        if (initialMillisUntilFinished != millisUntilFinished) {
            pauseButtonString = timer == null ? R.string.btn_timer_resume : R.string.btn_timer_pause;
        }
        binding.btnTimerPause.setText(getString(pauseButtonString));

        binding.tvTimeRemaining.setText(getRemainingTimeString(this.millisUntilFinished));
    }

    @NonNull
    private String getRemainingTimeString(long millisUntilFinished) {
        long totalSeconds = millisUntilFinished / COUNT_DOWN_INTERVAL;

        long minutes = totalSeconds / SECONDS_IN_MINUTE;
        long hours = minutes / MINUTES_IN_HOUR;
        long seconds = totalSeconds % SECONDS_IN_MINUTE;

        return String.format(getString(R.string.timer_activity_hh_mm_ss), hours, minutes, seconds);
    }
}