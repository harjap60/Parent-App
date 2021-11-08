package com.cmpt276.parentapp.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityTimerBinding;

public class TimerActivity extends AppCompatActivity {


    public static final String TIMER_DURATION_TAG = "TIMER_DURATION_TAG";
    public static final String TIMER_RUNNING_TAG = "TIMER_RUNNING";
    public static final String TAG = "TIMER_ACTIVITY";

    private ActivityTimerBinding binding;

    private long initialMillisUntilFinished;
    private boolean isRunning;

    private BroadcastReceiver receiver;
    private TimerService service;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            TimerService.LocalBinder localBinder = (TimerService.LocalBinder) binder;
            TimerActivity.this.service = localBinder.getService();
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            TimerActivity.this.service = null;
            updateUI();
        }
    };

    public static Intent getIntentForNewTimer(Context context, int minutes) {
        Intent i = new Intent(context, TimerActivity.class);
        i.putExtra(
                TIMER_DURATION_TAG,
                (long) minutes *
                        TimerService.SECONDS_IN_MINUTE *
                        TimerService.COUNT_DOWN_INTERVAL
        );

        return i;
    }

    public static Intent getIntentForRunningTimer(Context context, long millisUntilFinished) {
        Intent i = new Intent(context, TimerActivity.class);
        i.putExtra(
                TIMER_DURATION_TAG,
                millisUntilFinished
        );
        i.putExtra(TIMER_RUNNING_TAG, true);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimerBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        extractDurationFromIntent();
        setupPauseResumeButton();
        setupResetTimerButton();
        setupBroadcastReceiver();
        setupTimerService();
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
                updateUI();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerService.TIMER_TICK_BROADCAST_ACTION);
        registerReceiver(receiver, filter);
    }

    private void setupResetTimerButton() {
        binding.rightImageButton.setOnClickListener(v -> {
            if (this.service != null) {
                this.service.reset();
            }
        });
    }

    private void setupPauseResumeButton() {
        binding.startPauseResumeButton.setOnClickListener(v -> {
            if (this.service == null) {
                setupTimerService();
                return;
            }

            if (this.service.isRunning()) {
                this.service.pause();
            } else {
                this.service.resume();
            }
        });
    }

    private void updateUI() {
        int pauseButtonString = R.string.btn_timer_start;

        if (this.service != null) {
            pauseButtonString = this.service.isRunning() ? R.string.btn_timer_pause : R.string.btn_timer_resume;
            binding.timerLive.setText(this.service.getRemainingTimeString());
            binding.timerBar.setProgress(this.service.getProgress());
        }

        binding.startPauseResumeButton.setText(getString(pauseButtonString));
    }

    private void extractDurationFromIntent() {
        isRunning = this.getIntent().getBooleanExtra(TIMER_RUNNING_TAG, false);
        initialMillisUntilFinished = this.getIntent().getLongExtra(TIMER_DURATION_TAG, 0);
    }

    private void setupTimerService() {
        Intent intent = TimerService.getIntentWithDuration(
                this,
                initialMillisUntilFinished
        );
        if (!isRunning) {
            stopService(intent);
            startService(intent);
        }
        bindService(intent, serviceConnection, 0);
    }
}