package com.cmpt276.parentapp.ui;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    boolean isTimerServiceBound = false;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

            TimerService service = ((TimerService.LocalBinder) binder).getService();
            setupResumeTimerButton(service.getInitialDurationMillis());
            isTimerServiceBound = true;
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isTimerServiceBound = false;
            updateUI();
        }
    };

    private void setupResumeTimerButton(long initialDurationMillis) {
        binding.btnResumeTimer.setOnClickListener(v -> {
            Intent i = TimerActivity.getIntentForRunningTimer(this,initialDurationMillis);
            startActivity(i);
        });
    }

    private void updateUI() {

        binding.btnResumeTimer
                .setVisibility(
                        isTimerServiceBound ?
                                View.VISIBLE :
                                View.INVISIBLE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        setupFlipButton();
        setupTimerButton();
        setupChildButton();
        setupNotificationChannel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindTimerService();
        updateUI();
    }

    private void bindTimerService() {

        Intent i = TimerService.getIntent(this);
        bindService(i, serviceConnection, 0);
    }

    private void setupChildButton() {
        binding.startChildList.setOnClickListener(view ->
                startActivity(
                        ChildListActivity.getIntent(this)
                )
        );
    }

    private void setupTimerButton() {
        binding.btnStartTimer.setOnClickListener(view -> showTimerDurationDialog());
    }

    private void setupFlipButton() {
        binding.startFlip.setOnClickListener(view ->
                startActivity(FlipActivity.getIntent(this))
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void showTimerDurationDialog() {
        String[] options = this.getResources().getStringArray(R.array.durations);
        String custom_duration_item = this.getResources().getString(R.string.custom_duration_item);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(this.getString(R.string.dialog_duration_title))
                .setItems(options, (dialog, index) -> {
                    String selection = options[index];

                    if (selection.equals(custom_duration_item)) {
                        showCustomDurationDialog();
                        return;
                    }

                    String[] optionParts = selection.split(" ", 2);
                    int duration = Integer.parseInt(optionParts[0]);

                    Intent i = TimerActivity.getIntentForNewTimer(
                            this,
                            duration
                    );
                    this.startActivity(i);

                }).create()
                .show();
    }

    private void setupNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(TimerService.NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showCustomDurationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = this.getLayoutInflater().inflate(R.layout.dialog_custom_duration, null);

        builder.setView(v)
                .setTitle(R.string.dialog_custom_duration_title)
                .setPositiveButton(R.string.dialog_custom_duration_title, (dialog, id) -> {
                    try {
                        EditText tvTimerDuration = v.findViewById(R.id.tvTimerDuration);
                        int duration = Integer.parseInt(tvTimerDuration.getText().toString());
                        Intent i = TimerActivity.getIntentForNewTimer(
                                this,
                                duration
                        );
                        this.startActivity(i);
                    } catch (Exception e) {
                        Toast.makeText(
                                this,
                                this.getString(R.string.dialog_custom_duration_invalid_input),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton(R.string.dialog_custom_duration_dialog_cancel, null)
                .create()
                .show();
    }

}