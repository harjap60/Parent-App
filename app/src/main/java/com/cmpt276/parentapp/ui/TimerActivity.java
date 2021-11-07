package com.cmpt276.parentapp.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityTimerBinding;

public class TimerActivity extends AppCompatActivity {

    public static final int SECONDS_IN_MINUTE = 60;
    public static final int MINUTES_IN_HOUR = 60;

    public static final String TIMER_DURATION_TAG = "TIMER_DURATION_TAG";
    public static final String TIMER_RUNNING_TAG = "TIMER_RUNNING";
    public static final String TAG = "TIMER_ACTIVITY";

    private ActivityTimerBinding binding;

    private long initialMillisUntilFinished;
    private long millisUntilFinished;
    private boolean isRunning;

    private BroadcastReceiver receiver;
    private TimerService service;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            TimerService.LocalBinder localBinder = (TimerService.LocalBinder) binder;
            TimerActivity.this.service = localBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            TimerActivity.this.service = null;
        }
    };

    public static Intent getIntentForNewTimer(Context context, int minutes) {
        Intent i = new Intent(context, TimerActivity.class);
        i.putExtra(
                TIMER_DURATION_TAG,
                (long) minutes *
                        SECONDS_IN_MINUTE *
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
                TimerActivity.this.millisUntilFinished = intent
                        .getLongExtra(
                                TimerService.TIMER_DURATION_TAG,
                                0
                        );
                updateUI();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerService.BROADCAST_ACTION);
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

            if(this.service.isRunning()){
                this.service.pause();
            } else {
                this.service.resume();
            }
        });
    }

    private void updateUI() {
        int pauseButtonString = R.string.btn_timer_start;
        if (initialMillisUntilFinished != millisUntilFinished) {
            pauseButtonString = this.service != null && this.service.isRunning() ? R.string.btn_timer_resume : R.string.btn_timer_pause;
        }

        binding.startPauseResumeButton.setText(getString(pauseButtonString));
        binding.timerLive.setText(getRemainingTimeString(millisUntilFinished));
        binding.timerBar.setProgress((int) ((millisUntilFinished * 100) / initialMillisUntilFinished));
    }

    private void extractDurationFromIntent() {
        isRunning = this.getIntent().getBooleanExtra(TIMER_RUNNING_TAG, false);
        initialMillisUntilFinished = this.getIntent().getLongExtra(TIMER_DURATION_TAG, 0);
        millisUntilFinished = initialMillisUntilFinished;
    }

    private void setupTimerService() {
        Intent intent = TimerService.getIntentWithDuration(
                this,
                initialMillisUntilFinished,
                millisUntilFinished
        );
        if (!isRunning) {
            stopService(intent);
            startService(intent);
        }
        bindService(intent,serviceConnection,0);
    }

    @NonNull
    private String getRemainingTimeString(long millis) {
        long totalSeconds = millis / TimerService.COUNT_DOWN_INTERVAL;

        long minutes = totalSeconds / SECONDS_IN_MINUTE;
        long hours = minutes / MINUTES_IN_HOUR;
        long seconds = totalSeconds % SECONDS_IN_MINUTE;

        return String.format(getString(R.string.timer_activity_hh_mm_ss), hours, minutes, seconds);
    }
}