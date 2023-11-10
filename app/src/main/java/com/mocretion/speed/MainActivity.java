package com.mocretion.speed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements LocationListener {

    public static MainActivity mainActivity;

    private static LocationManager locationManager;
    private static AudioManager audioManager;
    public static NotificationManager notificationManager;

    private static TextView tv_speed;
    private static EditText maxSpeed;
    private static TextWatcher maxSpeedWatcher;
    private static EditText minSpeed;
    private static TextWatcher minSpeedWatcher;
    private static EditText maxVolume;
    private static TextWatcher maxVolumeWatcher;
    private static EditText minVolume;
    private static TextWatcher minVolumeWatcher;

    private static int iMaxSpeed = 100;
    private static int iMinSpeed = 10;
    private static int iMaxVolume = 13;
    private static int iMinVolume = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(mainActivity == null) {
            mainActivity = this;
        }

        removeTextListeners();

        tv_speed = findViewById(R.id.tv_speed);
        maxSpeed = findViewById(R.id.maxSpeedInput);
        minSpeed = findViewById(R.id.minSpeedInput);
        maxVolume = findViewById(R.id.maxVolumeInput);
        minVolume = findViewById(R.id.minVolumeInput);

        maxSpeed.setText(Integer.toString(iMaxSpeed));
        minSpeed.setText(Integer.toString(iMinSpeed));
        maxVolume.setText(Integer.toString(iMaxVolume));
        minVolume.setText(Integer.toString(iMinVolume));

        initTextListeners();

        if(locationManager == null)
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if(audioManager == null)
            audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);

        if(notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        ActivityManager.RunningServiceInfo service = foregroundServiceRunning();

        if(service == null && GPSService.running == false) {
            Intent serviceIntent = new Intent(this, GPSService.class);
            serviceIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startForegroundService(serviceIntent);
            GPSService.running = true;
        }

        // Check for GPS Permission
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.MODIFY_AUDIO_SETTINGS}, 102);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 103);
            }else{  // Permission granted
             //   onUserBackgroundGPSPermissionSet();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void removeTextListeners(){
        if(maxSpeedWatcher == null)
            return;

        maxSpeed.removeTextChangedListener(maxSpeedWatcher);
        minSpeed.removeTextChangedListener(minSpeedWatcher);
        maxVolume.removeTextChangedListener(maxVolumeWatcher);
        minVolume.removeTextChangedListener(minVolumeWatcher);
    }

    private void initTextListeners(){
        maxSpeedWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(maxSpeed.getText().toString().isEmpty())
                    return;

                iMaxSpeed = Integer.parseInt(maxSpeed.getText().toString());
            }
        };
        maxSpeed.addTextChangedListener(maxSpeedWatcher);

        minSpeedWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(minSpeed.getText().toString().isEmpty())
                    return;

                iMinSpeed = Integer.parseInt(minSpeed.getText().toString());
            }
        };
        minSpeed.addTextChangedListener(minSpeedWatcher);

        maxVolumeWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(maxVolume.getText().toString().isEmpty())
                    return;

                iMaxVolume = Integer.parseInt(maxVolume.getText().toString());
            }
        };
        maxVolume.addTextChangedListener(maxVolumeWatcher);

        minVolumeWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(minVolume.getText().toString().isEmpty())
                    return;

                iMinVolume = Integer.parseInt(minVolume.getText().toString());
            }
        };
        minVolume.addTextChangedListener(minVolumeWatcher);
    }

    private ActivityManager.RunningServiceInfo foregroundServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (GPSService.class.getName().equals(service.service.getClassName())) {
                return service;
            }
        }
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location != null){
            updateSpeed(location);
        }
    }

    @SuppressLint("MissingPermission")
    public static void onUserGPSPermissionSet(){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, mainActivity);
    }

    public static void removeGPSUpdates(){
        locationManager.removeUpdates(mainActivity);
    }

    private void updateSpeed(Location location){
        float currentSpeed = 0;
        if(location != null){
            currentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", currentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", "0");

        if(maxVolume.getText().toString().isEmpty() ||
                minVolume.getText().toString().isEmpty() ||
                maxSpeed.getText().toString().isEmpty() ||
                minSpeed.getText().toString().isEmpty())
            return;

        /*iMaxVolume = Integer.parseInt(maxVolume.getText().toString());
        iMinVolume = Integer.parseInt(minVolume.getText().toString());
        iMaxSpeed = Integer.parseInt(maxSpeed.getText().toString());
        iMinSpeed = Integer.parseInt(minSpeed.getText().toString());*/

        if(iMinVolume < 0)
            iMinVolume = 0;

        if(iMaxVolume > 15)
            iMaxVolume = 15;

        if(iMinSpeed < 0)
            iMinSpeed = 0;

        if(iMaxSpeed > 110)
             iMaxSpeed = 110;

        if(iMaxSpeed < iMinSpeed)
            iMaxSpeed = iMinSpeed;

        if(iMaxVolume < iMinVolume)
            iMaxVolume = iMinVolume;

        tv_speed.setText(strCurrentSpeed + " km/h");
        if(audioManager != null) {
            float percent = (float)((iMaxVolume - iMinVolume)) / (float)((iMaxSpeed - iMinSpeed));
            int volume = Math.round(percent * (currentSpeed - iMinSpeed) + iMinVolume);

            if(volume < iMinVolume)
                volume = iMinVolume;
            if(volume > iMaxVolume)
                volume = iMaxVolume;

            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        }
    }
}