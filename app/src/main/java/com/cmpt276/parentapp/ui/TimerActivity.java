package com.cmpt276.parentapp.ui;

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
import android.widget.Toast;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.submenu1:
                Toast.makeText(this, "Selected 1",
                        Toast.LENGTH_SHORT).show();
                return true;

            case R.id.submenu2:
                Toast.makeText(this, "Selected 2",
                        Toast.LENGTH_SHORT).show();
                return true;

            case R.id.submenu3:
                Toast.makeText(this, "Selected 3",
                        Toast.LENGTH_SHORT).show();
                return true;

            case R.id.submenu4:
                Toast.makeText(this, "Selected 4",
                        Toast.LENGTH_SHORT).show();
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