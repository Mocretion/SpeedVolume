package com.mocretion.speed;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ForegroundServiceStartNotAllowedException;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import java.io.Console;

public class GPSService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String CHANNEL_ID = "Foreground Service GPS";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentText("")
                .setContentTitle("Speed Volume");
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
        startForeground();
    }

    @SuppressLint("NewApi")
    private void startForeground(){

        try {
            Notification.Builder builder = new Notification.Builder(this, "moc_gps")
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("test")
                    .setAutoCancel(true);

            Notification notification = builder.build();
            startForeground(568744985, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);

        } catch (Exception e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    e instanceof ForegroundServiceStartNotAllowedException
            ) {
                // App not in a valid state to start foreground service
                // (e.g started from bg)
                e.printStackTrace();
            }
            e.printStackTrace();
            // ...
        }

    }
}
