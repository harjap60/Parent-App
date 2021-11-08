package com.cmpt276.parentapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.cmpt276.parentapp.R;

public class TimerActivity extends AppCompatActivity {
    public static final int STARTING_MILLI_SECONDS = 100000;
    public static final int MILLISECONDS_IN_HOUR = 3600000;
    public static final int MILLISECOND_TO_SECOND = 1000;
    CountDownTimer timer;
    long timeRemaining = 0;
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        setupStartButton();


        //// Testing ImageButton connected to TimerOptionsActivity ////
        ImageButton lockButton = findViewById(R.id.left_image_button);
        lockButton.setOnClickListener(view -> {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.dimAmount=1.0f;
            getWindow().setAttributes(lp);
            Intent intent = new Intent(TimerActivity.this, TimerOptionsActivity.class);
            startActivity(intent);
        });
    }

    private void setupStartButton() {
        startButton = findViewById(R.id.start_pause_resume_button);
        startButton.setOnClickListener(view -> {
            if (startButton.getText().equals("START")) {
                startButton.setText("PAUSE");
                testCircle(STARTING_MILLI_SECONDS);
                timer.start();
            } else if (startButton.getText().equals("PAUSE")) {
                startButton.setText("RESUME");
                timer.cancel();
            } else {
                testCircle(timeRemaining);
                timer.start();
                startButton.setText("PAUSE");
            }
        });
    }

    /** +
     * @param milliseconds - Sets up the countdown timer for the specified milliseconds.
     * Updates progress bar alongside the UI timer
     */
    private void testCircle(long milliseconds) {
        final double[] counter = {0};
        ProgressBar circle = findViewById(R.id.timerBar);
        circle.setMax(100); // May change later on to see smaller increments in the circular progress bar

        TextView timerText = findViewById(R.id.timer_live);
        timer = new CountDownTimer(milliseconds, 1000) { // testing 1 minute and 30 seconds
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                setTimerTime(millisUntilFinished, timerText);
                counter[0]++;
                circle.setProgress((int) (100 - (counter[0] * 100 / milliseconds * 1000)));
            }

            @Override
            public void onFinish() {
                //TODO: notification and alarm sound/vibrate
                counter[0]++;
                circle.setProgress(0);
                startButton.setText("START");
            }
        };
    }

    /** +
     * @param milliseconds - Calculates correct text based on milliseconds
     * @param text - Changes text in UI
     */
    private void setTimerTime(long milliseconds, TextView text) {
        long hour = (milliseconds / MILLISECOND_TO_SECOND)/ 60 / 60;
        long min = ((milliseconds - hour * MILLISECONDS_IN_HOUR) / MILLISECOND_TO_SECOND) / 60;
        long sec = (milliseconds / MILLISECOND_TO_SECOND) % 60;
        if (hour == 0) {
            if (sec < 10) {
                text.setText("" + min + ":0" + sec);
            } else text.setText("" + min + ":" + sec);
        } else {
            if (sec < 10) {
                text.setText(hour + ":" + min + ":0" + sec);
            } else {
                text.setText(hour + ":" + min + ":" + sec);
            }
        }
    }
}