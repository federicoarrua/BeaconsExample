package com.example.carlos.beaconexample.notifier;

import android.app.Application;
import android.content.Intent;

import com.example.carlos.beaconexample.activity.MainActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Created by Carlos on 31/10/2016.
 */

public class ApplicationBeaconNotifier extends Application implements BootstrapNotifier {

    private String TAG = " ApplicationBeaconNotifier";
    private RegionBootstrap regionBootstrap;

    @Override
    public void onCreate() {
        super.onCreate();

        BeaconManager bm = BeaconManager.getInstanceForApplication(this);
        bm.getBeaconParsers().add(new BeaconParser("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        Region region = new Region("Region",null,null,null);

        regionBootstrap = new RegionBootstrap(this,region);
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
