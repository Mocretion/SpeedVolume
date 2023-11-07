package com.mocretion.speed;

import android.app.ActivityManager;
import android.content.Context;
import android.location.LocationManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

public class GPSTileService extends TileService {

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

        Tile tile = getQsTile();
        tile.getState();
        tile.setState(tile.getState() == Tile.STATE_ACTIVE ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE);


        if(tile.getState() != Tile.STATE_ACTIVE)
            MainActivity.removeGPSUpdates();
        else
            MainActivity.onUserGPSPermissionSet();

        tile.updateTile();
    }

    // Called when the user removes your tile.
    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

}
