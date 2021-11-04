package com.cmpt276.parentapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cmpt276.parentapp.R;

public class TimerActivity extends AppCompatActivity {
    double i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        testCircle();
    }

    private void testCircle() {
        ProgressBar circle = findViewById(R.id.timerBar);
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(circle, "progress", 100, 0);
        circle.setMax(100);
        TextView timerText = findViewById(R.id.time_test);
        int seconds = 1200000;
        CountDownTimer timer = new CountDownTimer(seconds,1000) { // test 30 seconds
            @Override
            public void onTick(long millisUntilFinished) {
                long Mmin = (millisUntilFinished / 1000) / 60;
                long Ssec = (millisUntilFinished / 1000) % 60;
                if (Ssec < 10) {
                    timerText.setText("" + Mmin + ":0" + Ssec);
                } else timerText.setText("" + Mmin + ":" + Ssec);
                i++;
                circle.setProgress((int) (100 - (i * 100/seconds * 1000)));
                //circle.setProgress((int)i*100/(30000/1000));
              //  circle.setProgress((int)(i*1000)/30000 * 100);

            }

            @Override
            public void onFinish() {
                //TODO: notification and alarm sound
                i++;
                circle.setProgress(0);
            }
        };
        timer.start();
    }
}