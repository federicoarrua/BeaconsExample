package com.example.carlos.beaconexample;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.carlos.beaconexample.activity.MainActivity;
import com.example.carlos.beaconexample.simbeacon.TimedBeaconSimulator;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Created by Carlos on 31/10/2016.
 */

public class ApplicationBeacon extends Application implements BootstrapNotifier {

    private String TAG = " ApplicationBeacon";
    private RegionBootstrap regionBootstrap;

    public void onCreate() {
        super.onCreate();
//        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
//
//        beaconManager.getBeaconParsers().add(new BeaconParser("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        Log.d(TAG, "setting up background monitoring for beacons and power saving");
        // wake up the app when a beacon is seen
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        // If you wish to test beacon detection in the Android Emulator, you can use code like this:
        BeaconManager.setBeaconSimulator(new TimedBeaconSimulator() );
        ((TimedBeaconSimulator) BeaconManager.getBeaconSimulator()).createTimedSimulatedBeacons();
    }

    @Override
    public void didEnterRegion(Region region) {
        regionBootstrap.disable();
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
