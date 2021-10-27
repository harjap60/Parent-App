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
    public static final int MILLIS_IN_FUTURE = 10000;
    public static final int SECONDS_IN_MINUTE = 60;
    private CountDownTimer timer;

    public static Intent getIntent(Context context) {
        return new Intent(context, TimerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        setupTimer();
    }

    private void setupTimer() {
        TextView tvTimerRemaining = this.findViewById(R.id.tvTimeRemaining);

        timer = new CountDownTimer(MILLIS_IN_FUTURE, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished / COUNT_DOWN_INTERVAL;

                long minutes = totalSeconds / SECONDS_IN_MINUTE;
                long seconds = totalSeconds % SECONDS_IN_MINUTE;

                tvTimerRemaining.setText(String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimerRemaining.setText("Done");
            }
        };

        timer.start();
    }


}