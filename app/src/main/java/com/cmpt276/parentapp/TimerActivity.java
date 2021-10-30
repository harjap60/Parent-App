package com.cmpt276.parentapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    public static final int COUNT_DOWN_INTERVAL = 1000;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final String TIMER_DURATION = "TIMER_DURATION_TAG";
    private CountDownTimer timer = null;

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
        timer = new CountDownTimer(duration * COUNT_DOWN_INTERVAL, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished / COUNT_DOWN_INTERVAL;

                long minutes = totalSeconds / SECONDS_IN_MINUTE;
                long seconds = totalSeconds % SECONDS_IN_MINUTE;

                tvTimerRemaining.setText(String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimerRemaining.setText(R.string.timer_activity_end_message);
            }

        };
        timer.start();
    }
}