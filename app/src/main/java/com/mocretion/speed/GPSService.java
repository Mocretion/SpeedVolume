package com.mocretion.speed;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ForegroundServiceStartNotAllowedException;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.Console;
import java.util.Formatter;
import java.util.Locale;

public class GPSService extends Service {

    public static boolean running = false;

    PowerManager.WakeLock wakeLock;
    PowerManager pm;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        running = true;

        final String CHANNEL_ID = "Foreground Service GPS";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentText("")
                .setContentTitle("Speed Volume")
                .setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            notification.setFlag(Notification.FLAG_ONGOING_EVENT, true);
        }

        startForeground(1001, notification.build());

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        acquireWakeLock();
    }

    @Override
    public void onDestroy(){
        releaseWakelock();
        super.onDestroy();
    }

    public void acquireWakeLock() {
        if(PowerManagement.wakelock!=null){
            PowerManagement.wakelock.release();
            PowerManagement.wakelock=null;
        }
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "speed:speedvolumetracker");
        wakeLock.acquire();
        PowerManagement.wakelock=this.wakeLock;
    }
    public void releaseWakelock() {
        if(PowerManagement.wakelock!=null){
            PowerManagement.wakelock.release();
            PowerManagement.wakelock=null;
        }

        wakeLock.release();
    }
}
