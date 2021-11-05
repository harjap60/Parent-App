package com.cmpt276.parentapp.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class TimerService extends Service {

    public static final int COUNT_DOWN_INTERVAL = 1000;
    public static final String TIMER_DURATION_TAG = "com.cmpt276.parentapp.TimerService.TIMER_DURATION";
    public static final String BROADCAST_ACTION = "com.cmpt276.parent.TIMER_NOTIFICATION";

    private CountDownTimer timer;
    private long millisUntilFinished;

    public static Intent getIntentWithDuration(Context context, long duration) {
        Intent i = new Intent(context, TimerService.class);
        i.putExtra(TIMER_DURATION_TAG, duration);
        return i;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.millisUntilFinished = intent.getLongExtra(TIMER_DURATION_TAG, 0);
        timer = new CountDownTimer(this.millisUntilFinished, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimerService.this.millisUntilFinished = millisUntilFinished;
                TimerService.this.broadcast();
                Log.i("TIMER_SERVICE", "Running " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                stopSelf();
            }
        }.start();
        return START_STICKY;
    }

    private void broadcast() {
        Intent i = new Intent();
        i.setAction(BROADCAST_ACTION);
        i.putExtra(TIMER_DURATION_TAG, this.millisUntilFinished);
        sendBroadcast(i);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}