package com.cmpt276.parentapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.databinding.ActivityTimerRunningBinding;

public class TimerActivity extends AppCompatActivity {

    public static final int SECONDS_IN_MINUTE = 60;
    public static final int MINUTES_IN_HOUR = 60;

    public static final String TIMER_DURATION_TAG = "TIMER_DURATION_TAG";

    private ActivityTimerRunningBinding binding;
    private Intent timerServiceIntent;

    private long initialMillisUntilFinished;
    private long millisUntilFinished;

    private BroadcastReceiver receiver;

    public static Intent getIntentWithDurationMinutes(Context context, int duration) {
        Intent i = new Intent(context, TimerActivity.class);
        i.putExtra(TIMER_DURATION_TAG, (long) duration * SECONDS_IN_MINUTE * TimerService.COUNT_DOWN_INTERVAL);
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
        setupBroadcastReceiver();
        createAndStartTimer();
        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    private void setupBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TimerActivity.this.millisUntilFinished = intent.getLongExtra(TimerService.TIMER_DURATION_TAG, 0);
                updateUI();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerService.BROADCAST_ACTION);
        registerReceiver(receiver, filter);
    }


    private void extractDurationFromIntent() {
        initialMillisUntilFinished = this.getIntent().getLongExtra(TIMER_DURATION_TAG, 0);
        millisUntilFinished = initialMillisUntilFinished;
    }

    private void createAndStartTimer() {
        timerServiceIntent = TimerService.getIntentWithDuration(this, millisUntilFinished);
        startService(timerServiceIntent);
    }

    private void pauseTimer() {
        if (timerServiceIntent == null) {
            return;
        }

        stopService(timerServiceIntent);
        timerServiceIntent = null;
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
            if (timerServiceIntent != null) {
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
            pauseButtonString = timerServiceIntent == null ? R.string.btn_timer_resume : R.string.btn_timer_pause;
        }
        binding.btnTimerPause.setText(getString(pauseButtonString));

        binding.tvTimeRemaining.setText(getRemainingTimeString());
    }

    @NonNull
    private String getRemainingTimeString() {
        long totalSeconds = millisUntilFinished/TimerService.COUNT_DOWN_INTERVAL;

        long minutes = totalSeconds / SECONDS_IN_MINUTE;
        long hours = minutes / MINUTES_IN_HOUR;
        long seconds = totalSeconds % SECONDS_IN_MINUTE;

        return String.format(getString(R.string.timer_activity_hh_mm_ss), hours, minutes, seconds);
    }
}