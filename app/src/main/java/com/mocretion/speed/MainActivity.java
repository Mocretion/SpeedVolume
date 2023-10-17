package com.mocretion.speed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private TextView tv_speed;
    private LocationManager locationManager;
    private AudioManager audioManager;

    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = this;
        setContentView(R.layout.activity_main);

        tv_speed = findViewById(R.id.tv_speed);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);

        // Check for GPS Permission
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }else{  // Permission granted
                onUserGPSPermissionSet();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.MODIFY_AUDIO_SETTINGS}, 102);
            }else{  // Permission granted
                onUserVolumePermissionSet();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 103);
            }else{  // Permission granted
                onUserBackgroundGPSPermissionSet();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        updateSpeed(null);

        Intent serviceIntent = new Intent(this, GPSService.class);
        startForegroundService(serviceIntent);

    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(GPSService.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }

        return false;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location != null){
            updateSpeed(location);
        }
    }

    @SuppressLint("MissingPermission")
    public static void onUserGPSPermissionSet(){
        mainActivity.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, mainActivity);
    }

    @SuppressLint("MissingPermission")
    private void onUserVolumePermissionSet(){
       // Toast.makeText(this, "Volume permission set!", Toast.LENGTH_SHORT).show();
    }

    private void onUserBackgroundGPSPermissionSet(){
        if(locationManager.isLocationEnabled()){
            //startLocationWork();
        }else{
            Toast.makeText(this, "GPS is turned off!", Toast.LENGTH_SHORT).show();
        }
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

        tv_speed.setText(strCurrentSpeed + " km/h");
        if(audioManager != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Math.round(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 100f * currentSpeed), AudioManager.FLAG_SHOW_UI);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(locationManager.isLocationEnabled()){
                    onUserGPSPermissionSet();
                }else{
                    Toast.makeText(this, "GPS is turned off!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}