package com.cmpt276.parentapp.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TimerService extends Service {

    public static final String TIMER_DURATION_TAG = "com.cmpt276.parentapp.TimerService.TIMER_DURATION";
    public static final String INITIAL_TIMER_DURATION_TAG = "com.cmpt276.parentapp.TimerService.INITIAL_DURATION";
    public static final String BROADCAST_ACTION = "com.cmpt276.parent.TIMER_NOTIFICATION";
    public static final String NOTIFICATION_CHANNEL_ID = "TIMER_SERVICE";

    public static final int NOTIFICATION_ID = 1;
    public static final int COUNT_DOWN_INTERVAL = 1000;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int MINUTES_IN_HOUR = 60;

    private long initialDurationMillis;
    private long millisUntilFinished;
    private boolean isRunning = false;

    private CountDownTimer timer;
    private final LocalBinder binder = new LocalBinder();

    public static Intent getIntentWithDuration(Context context, long initialDurationMillis) {
        Intent i = new Intent(context, TimerService.class);

        i.putExtra(INITIAL_TIMER_DURATION_TAG, initialDurationMillis);
        return i;
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, TimerService.class);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.initialDurationMillis = intent.getLongExtra(INITIAL_TIMER_DURATION_TAG, 0);
        this.millisUntilFinished = this.initialDurationMillis;

        this.startTimer();

        return START_STICKY;
    }

    private void startTimer() {
        timer = new CountDownTimer(millisUntilFinished, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimerService.this.millisUntilFinished = millisUntilFinished;
                TimerService.this.broadcast();
            }

            @Override
            public void onFinish() {
                stopSelf();
            }
        }.start();

        this.isRunning = true;
    }

    private void broadcast() {
        Intent i = new Intent();
        i.setAction(BROADCAST_ACTION);
        i.putExtra(TIMER_DURATION_TAG, millisUntilFinished);
        sendBroadcast(i);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.pause();
    }

    public long getInitialDurationMillis() {
        return TimerService.this.initialDurationMillis;
    }

    public void pause() {

        if (this.timer == null) {
            return;
        }

        this.timer.cancel();
        this.timer = null;
        this.isRunning = false;
        this.broadcast();
    }

    public void resume() {
        this.startTimer();
        this.broadcast();
    }

    public void reset() {
        this.pause();

        this.millisUntilFinished = this.initialDurationMillis;
        this.isRunning = false;

        this.broadcast();
        this.stopSelf();
    }

    public int getProgress() {
        return (int) ((millisUntilFinished * 100) / initialDurationMillis);
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @NonNull
    public String getRemainingTimeString() {
        long totalSeconds = millisUntilFinished / TimerService.COUNT_DOWN_INTERVAL;

        long minutes = totalSeconds / SECONDS_IN_MINUTE;
        long hours = minutes / MINUTES_IN_HOUR;
        long seconds = totalSeconds % SECONDS_IN_MINUTE;

        return String.format(
                this.getString(R.string.timer_activity_hh_mm_ss),
                hours,
                minutes,
                seconds
        );
    }

    public class LocalBinder extends Binder {

        public TimerService getService() {
            TimerService.this.broadcast();
            return TimerService.this;
        }

    }
}