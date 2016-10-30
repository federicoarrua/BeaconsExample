package com.example.carlos.beaconexample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.w3c.dom.Text;

import java.util.Collection;

/**
 * Created by Carlos on 24/10/2016.
 */

public class RangingActivity extends Activity implements BeaconConsumer {

    private String TAG = "RangingDetectBeacon";
    private BeaconManager beaconManager;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.range_layout);

        this.beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            private String text ="No beacons in this region";

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size()>0){
                    Log.i(TAG,"The First beacon i see is "+beacons.iterator().next().getDistance()+"meters away.\r\n");
                    Log.i(TAG,"The UUID is: "+beacons.iterator().next().getId1()+"\r\n");
                    Log.i(TAG,"The Major is: "+beacons.iterator().next().getId2()+"\r\n");
                    Log.i(TAG,"The Minor is: "+beacons.iterator().next().getId3()+"\r\n");
                    text = "UUID:"+beacons.iterator().next().getId1()+"\r\nMajor: "+beacons.iterator().next().getId2()+"\r\n Minor: "+beacons.iterator().next().getId3()+"\r\nDistance: "+beacons.iterator().next().getDistance();

                }
                else{
                    Log.i(TAG,"No beacons in this region.\r\n");
                    text = "No beacons in this region.";
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        TextView txt = (TextView) findViewById(R.id.textView3);
                        txt.setText(text);
                    }
                });
            }
        });
        try{
            beaconManager.startRangingBeaconsInRegion(new Region("Region X",null,null,null));
        }
        catch (Exception e){}
    }
}
