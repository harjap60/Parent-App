package com.cmpt276.parentapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    public static final int COUNT_DOWN_INTERVAL = 1000;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final String TIMER_DURATION = "TIMER_DURATION_TAG";
    public static final int MINUTES_IN_HOUR = 60;

    public static Intent getIntentWithDurationMinutes(Context context, int duration) {
        Intent i = new Intent(context, TimerActivity.class);
        i.putExtra(TIMER_DURATION, duration * SECONDS_IN_MINUTE);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        setupTimer();
    }

    private void setupTimer() {
        long duration = this.getIntent().getIntExtra(TIMER_DURATION, 0);
        TextView tvTimerRemaining = this.findViewById(R.id.tvTimeRemaining);
        CountDownTimer timer = new CountDownTimer(duration * COUNT_DOWN_INTERVAL, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimerRemaining.setText(getRemainingTimeString(millisUntilFinished));
            }

            @NonNull
            private String getRemainingTimeString(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished / COUNT_DOWN_INTERVAL;

                long minutes = totalSeconds / SECONDS_IN_MINUTE;
                long seconds = totalSeconds % SECONDS_IN_MINUTE;

                String timerText;

                if (minutes >= MINUTES_IN_HOUR) {
                    long hours = minutes / MINUTES_IN_HOUR;
                    minutes = minutes % MINUTES_IN_HOUR;
                    timerText = String.format(getString(R.string.timer_activity_hh_mm_ss), hours, minutes, seconds);
                } else {
                    timerText = String.format(getString(R.string.timer_activity_mm_ss), minutes, seconds);
                }
                return timerText;
            }

            @Override
            public void onFinish() {
                tvTimerRemaining.setText(R.string.timer_activity_end_message);
            }
        };

        Button btnTimerCancel = this.findViewById(R.id.btnTimerCancel);

        btnTimerCancel.setOnClickListener(v -> {
            timer.cancel();
            finish();
        });

        timer.start();
    }
}