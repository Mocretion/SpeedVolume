package com.mocretion.speed;

import android.content.Context;
import android.location.LocationManager;
import android.service.quicksettings.TileService;
import android.util.Log;

public class GPSTileService extends TileService {

    boolean enabled = true;

    // Called when the user adds your tile.
    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    // Called when your app can update your tile.
    @Override
    public void onStartListening() {
        super.onStartListening();
    }

    // Called when your app can no longer update your tile.
    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    // Called when the user taps on your tile in an active or inactive state.
    @Override
    public void onClick() {

        super.onClick();
        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(enabled)
            manager.removeUpdates(MainActivity.mainActivity);
        else
            MainActivity.onUserGPSPermissionSet();

        enabled = !enabled;
    }

    // Called when the user removes your tile.
    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

}
