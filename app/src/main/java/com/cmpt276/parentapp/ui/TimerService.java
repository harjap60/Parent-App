package com.cmpt276.parentapp.ui;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.cmpt276.parentapp.R;

public class TimerService extends Service {

    public static final String TIMER_TICK_BROADCAST_ACTION = "com.cmpt276.parent.TIMER_NOTIFICATION";
    public static final String NOTIFICATION_CHANNEL_ID = "TIMER_SERVICE";
    public static final String TIMER_END_NOTIFICATION_CHANNEL_ID = "TIMER_SERVICE_END";
    public static final int COUNT_DOWN_INTERVAL = 1000;
    public static final int SECONDS_IN_MINUTE = 60;

    private static final String TIMER_DURATION_TAG = "com.cmpt276.parentapp.TimerService.TIMER_DURATION";
    private static final String INITIAL_TIMER_DURATION_TAG = "com.cmpt276.parentapp.TimerService.INITIAL_DURATION";
    private static final String TIMER_RESUME_BROADCAST_ACTION = "com.cmpt276.parent.TIMER_RESUME";
    private static final String TIMER_PAUSE_BROADCAST_ACTION = "com.cmpt276.parent.TIMER_PAUSE";
    private static final String TIMER_STOP_BROADCAST_ACTION = "com.cmpt276.parent.TIMER_CANCEL";
    private static final int NOTIFICATION_ID = 1;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int VIBRATION_REPEAT_INDEX = 0;
    private static final long[] PATTERN = {0, 1000, 500, 2000};
    private static final int PROGRESS_MULTIPLIER = 100;
    private static final int MILLIS_AT_FINISHED = 0;

    private final LocalBinder binder = new LocalBinder();
    private long initialDurationMillis;
    private long millisUntilFinished;
    private boolean isRunning = false;
    private boolean isFinished = false;

    private MediaPlayer player;
    private Vibrator vibrator;

    private BroadcastReceiver stopTimerBroadcastReceiver;

    private CountDownTimer timer;

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

        setupStopTimerBroadcastReceiver();
        this.startTimer();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.pause();
    }

    private void setupStopTimerBroadcastReceiver() {
        this.stopTimerBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case TIMER_RESUME_BROADCAST_ACTION:
                        TimerService.this.resume();
                        break;
                    case TIMER_PAUSE_BROADCAST_ACTION:
                        TimerService.this.pause();
                        break;
                    case TIMER_STOP_BROADCAST_ACTION:
                    default:
                        TimerService.this.reset();

                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(TIMER_STOP_BROADCAST_ACTION);
        intentFilter.addAction(TIMER_PAUSE_BROADCAST_ACTION);
        intentFilter.addAction(TIMER_RESUME_BROADCAST_ACTION);
        registerReceiver(stopTimerBroadcastReceiver, intentFilter);
    }

    private void startTimer() {
        timer = new CountDownTimer(millisUntilFinished, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimerService.this.millisUntilFinished = millisUntilFinished;
                TimerService.this.broadcast();
                updateForeGroundNotification();
            }

            @Override
            public void onFinish() {
                TimerService.this.isRunning = false;
                TimerService.this.isFinished = true;
                TimerService.this.millisUntilFinished = MILLIS_AT_FINISHED;
                TimerService.this.broadcast();
                startTimerEndNotification();
                playAlarmAlert();
            }
        }.start();

        this.isRunning = true;
        this.isFinished = false;
    }

    private void playAlarmAlert() {
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createWaveform(PATTERN, VIBRATION_REPEAT_INDEX));

        this.player = MediaPlayer.create(this, R.raw.alarm_sound);
        this.player.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
        );
        this.player.start();
    }

    private void updateForeGroundNotification() {
        Notification notification = getNotification(TimerService.this.getRemainingTimeString(), NOTIFICATION_CHANNEL_ID);
        startForeground(NOTIFICATION_ID, notification);
    }

    private void startTimerEndNotification() {
        Notification notification = getNotification("Time Up", TIMER_END_NOTIFICATION_CHANNEL_ID);
        startForeground(NOTIFICATION_ID, notification);
    }

    private Notification getNotification(String text, String channel) {
        Intent notificationIntent = TimerActivity.getIntentForRunningTimer(this,
                this.initialDurationMillis);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                channel)
                .setSmallIcon(R.drawable.ic_baseline_add_white_24)
                .setContentTitle("Timer")
                .setContentText(text)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(
                        R.drawable.ic_baseline_stop_24,
                        "Stop",
                        getActionPendingIntent(TIMER_STOP_BROADCAST_ACTION)
                );

        if (!this.isFinished) {
            if (this.isRunning) {
                builder.addAction(
                        R.drawable.ic_baseline_pause_24,
                        "Pause",
                        getActionPendingIntent(TIMER_PAUSE_BROADCAST_ACTION));
            } else {
                builder.addAction(
                        R.drawable.ic_baseline_play_arrow_24,
                        "Resume",
                        getActionPendingIntent(TIMER_RESUME_BROADCAST_ACTION));
            }
        }

        return builder.build();
    }

    private PendingIntent getActionPendingIntent(String action) {
        return PendingIntent.getBroadcast(this,
                0,
                new Intent(action),
                PendingIntent.FLAG_IMMUTABLE);
    }

    private void broadcast() {
        Intent i = new Intent();
        i.setAction(TIMER_TICK_BROADCAST_ACTION);
        i.putExtra(TIMER_DURATION_TAG, millisUntilFinished);
        sendBroadcast(i);
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
        this.updateForeGroundNotification();
    }

    public void resume() {
        this.startTimer();
        this.broadcast();
    }

    public void reset() {
        this.pause();

        this.millisUntilFinished = this.initialDurationMillis;

        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }

        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }

        unregisterReceiver(stopTimerBroadcastReceiver);
        this.broadcast();
        this.stopSelf();
    }

    public int getProgress() {
        final long i = (millisUntilFinished * PROGRESS_MULTIPLIER) / initialDurationMillis;
        return (int) i;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public boolean isFinished() {
        return isFinished;
    }

    @NonNull
    public String getRemainingTimeString() {
        return getTimerString(millisUntilFinished);
    }

    @NonNull
    public String getTotalTimeString() {
        return getTimerString(initialDurationMillis);
    }

    @NonNull
    public String getElapsedTimeString() {
        return getTimerString(initialDurationMillis - millisUntilFinished);
    }

    private String getTimerString(long millisSeconds) {
        long totalSeconds = millisSeconds / TimerService.COUNT_DOWN_INTERVAL;

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

            return TimerService.this;
        }

    }
}