package com.cmpt276.parentapp.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityTimerBinding;

/**
 * Timer Activity provides the UI to start and update Timer Service. It can bind to an existing
 * timer service and allow interactions with it.
 */
public class TimerActivity extends AppCompatActivity {

    public static final String TIMER_DURATION_TAG = "TIMER_DURATION_TAG";
    public static final String TIMER_RUNNING_TAG = "TIMER_RUNNING";

    private ActivityTimerBinding binding;

    private long initialMillisUntilFinished;
    private boolean settingRunningService;

    private BroadcastReceiver receiver;
    private TimerService service;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            TimerService.LocalBinder localBinder = (TimerService.LocalBinder) binder;
            TimerActivity.this.service = localBinder.getService();
            updateUI();
            setTotalTimeUI();
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

        setupToolbar();
        extractDurationFromIntent();
        setupPauseResumeButton();
        setupResetTimerButton();
        setupBroadcastReceiver();
        setupCancelTimerButton();
        setupTimerService();
        updateUI();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.timer_activity_toolbar_label);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return true;
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (this.service == null) {
            return true;
        }
        switch (id) {
            case R.id.speed25:
                this.service.setTimerSpeed(0.25);
                binding.speedPercentage.setText(R.string.twenty_five);
                return true;

            case R.id.speed50:
                this.service.setTimerSpeed(0.5);
                binding.speedPercentage.setText(R.string.fifty);
                return true;

            case R.id.speed75:
                this.service.setTimerSpeed(0.75);
                binding.speedPercentage.setText(R.string.seventy_five);
                return true;

            case R.id.speed100:
                this.service.setTimerSpeed(1.0);
                binding.speedPercentage.setText(R.string.one_hundred);
                return true;

            case R.id.speed200:
                this.service.setTimerSpeed(2.0);
                binding.speedPercentage.setText(R.string.two_hundred);
                return true;

            case R.id.speed300:
                this.service.setTimerSpeed(3.0);
                binding.speedPercentage.setText(R.string.three_hundred);
                return true;

            case R.id.speed400:
                this.service.setTimerSpeed(4.0);
                binding.speedPercentage.setText(R.string.four_hundred);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
        binding.btnResetTimer.setOnClickListener(v -> {
            if (this.service != null) {
                this.service.reset();
            }
        });
    }

    private void setupCancelTimerButton() {
        binding.btnCancelTimer.setOnClickListener(v -> {
            if (this.service != null) {
                this.service.reset();
            }
            finish();
        });
    }

    private void setupPauseResumeButton() {
        binding.btnPauseResume.setOnClickListener(v -> {
            if (this.service == null) {
                setupTimerService();
                binding.speedPercentage.setText(R.string.one_hundred);
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

            int visibility = this.service.isRunning() ? View.INVISIBLE : View.VISIBLE;
            binding.btnResetTimer.setVisibility(visibility);
            binding.btnCancelTimer.setVisibility(visibility);
            binding.btnPauseResume.setVisibility(this.service.isFinished() ? View.INVISIBLE : View.VISIBLE);

            binding.timerLive.setText(this.service.getRemainingTimeString());
            binding.timerBar.setProgress(this.service.getProgress());
            binding.timeElapsed.setText(String.format(getString(R.string.time_elapsed), this.service.getElapsedTimeString()));
            binding.speedPercentage.setText(this.service.getSpeed());
        } else {
            binding.speedPercentage.setText(R.string.one_hundred);
        }
        binding.btnPauseResume.setText(getString(pauseButtonString));
    }

    private void setTotalTimeUI() {
        binding.timeTotal.setText(String.format(getString(R.string.initial_time), this.service.getTotalTimeString()));
    }

    private void extractDurationFromIntent() {
        settingRunningService = this.getIntent().getBooleanExtra(TIMER_RUNNING_TAG, false);
        initialMillisUntilFinished = this.getIntent().getLongExtra(TIMER_DURATION_TAG, 0);
    }

    private void setupTimerService() {
        Intent intent = TimerService.getIntentWithDuration(
                this,
                initialMillisUntilFinished
        );
        if (!settingRunningService) {
            stopService(intent);
            startService(intent);
        }
        settingRunningService = false;
        bindService(intent, serviceConnection, 0);
    }
}