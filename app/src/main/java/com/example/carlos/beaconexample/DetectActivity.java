package com.example.carlos.beaconexample;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.w3c.dom.Text;

import java.util.List;


/**
 * Created by Carlos on 22/10/2016.
 */

public class DetectActivity extends Activity implements BeaconConsumer {

    private String TAG = "MonitorDetectBeacon";
    private BeaconManager beaconManager;

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.addMonitorNotifier(new MonitorNotifier() {

            private String text;

            @Override
            public void didEnterRegion(Region region) {
                text="You enter in the beacon region";
                Log.i(TAG,"Entering to a region");
                runOnUiThread(new Runnable() {
                    public void run() {
                        TextView txt = (TextView) findViewById(R.id.textView2);
                        txt.setText(text);
                    }
                });
            }

            @Override
            public void didExitRegion(Region region) {
                text="You exit the beacon region";
                Log.i(TAG,"Exiting region");
                runOnUiThread(new Runnable() {
                    public void run() {
                        TextView txt = (TextView) findViewById(R.id.textView2);
                        txt.setText(text);
                    }
                });
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                text="I have just switched from seeing/not seeing beacons: "+i;
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+i);
                runOnUiThread(new Runnable() {
                    public void run() {
                        TextView txt = (TextView) findViewById(R.id.textView2);
                        txt.setText(text);
                    }
                });
            }
        });

        try{
            beaconManager.startMonitoringBeaconsInRegion(new Region("Monitor Region",null,null,null));
        }
        catch(RemoteException e){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detect_layout);

        this.beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    public BeaconManager getBeaconManager() {
        return beaconManager;
    }

    public void setBeaconManager(BeaconManager beaconManager) {
        this.beaconManager = beaconManager;
    }
}
