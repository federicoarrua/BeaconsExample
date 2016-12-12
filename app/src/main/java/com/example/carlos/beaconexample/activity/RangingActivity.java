package com.example.carlos.beaconexample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.carlos.beaconexample.R;
import com.example.carlos.beaconexample.classesBeacon.BeaconModel;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

/**
 * Created by Carlos on 24/10/2016.
 */

public class RangingActivity extends Activity implements BeaconConsumer {

    private String TAG = "RangingDetectBeacon";
    private BeaconManager beaconManager;
    private BeaconModel beacon;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.range_layout);

        beacon = (BeaconModel) getIntent().getSerializableExtra("beacon");

        this.beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size()>0){
                    logToDisplay(region.getUniqueId(),Double.toString(beacons.iterator().next().getDistance()));
                }
                else{
                    Log.i(TAG,"No beacons in this region.\r\n");
                    logToDisplayNoBeacon();
                }

            }
        });
        try{
            beaconManager.startRangingBeaconsInRegion(new Region(beacon.getDescription(),null, Identifier.parse(beacon.getMajor_id().toString()),Identifier.parse(beacon.getMinor_id().toString())));
        }
        catch (Exception e){}
    }

    private void logToDisplay(final String desc, final String dist) {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView description = (TextView) findViewById(R.id.textViewDesc);
                TextView distance = (TextView) findViewById(R.id.textViewDist);
                description.setText(desc);
                distance.setText("Esta a "+dist+"m de distancia");
            }
        });
    }

    private void logToDisplayNoBeacon() {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView description = (TextView) findViewById(R.id.textViewDesc);
                TextView distance = (TextView) findViewById(R.id.textViewDist);
                description.setText("Saliste de la región del beacon.");
            }
        });
    }
}
