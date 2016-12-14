package com.example.carlos.beaconexample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.carlos.beaconexample.ApplicationBeacon;
import com.example.carlos.beaconexample.R;
import com.example.carlos.beaconexample.classesBeacon.BeaconModel;
import com.example.carlos.beaconexample.servertasks.BeaconByRegionGetTask;
import com.example.carlos.beaconexample.utils.BeaconJsonUtils;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.w3c.dom.Text;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static android.R.attr.description;

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

        ((ApplicationBeacon)this.getApplication()).stopBeaconMonitoring();

        beacon = (BeaconModel) getIntent().getSerializableExtra("beacon");
        ((ApplicationBeacon) this.getApplication()).stopBeaconMonitoring();

        HashMap<String,Integer> hm = new HashMap<String,Integer>();
        hm.put("minor_id",beacon.getMinor_id());
        hm.put("major_id",beacon.getMajor_id());

        try {
            String json =(String) new BeaconByRegionGetTask().execute(hm).get();
            TextView txtDesc = (TextView) findViewById(R.id.textViewDesc);
            TextView txtName = (TextView) findViewById(R.id.textViewName);

            if(json != null) {
                BeaconModel b = BeaconJsonUtils.JsonToBeacon(json);
                txtName.setText(b.getName());
                txtDesc.setText(b.getDescription());
            }
            else{
                txtName.setText("No hay nombre");
                txtDesc.setText("El beacon no está en la base de datos.");
            }
        }
        catch(InterruptedException ie){
            ie.printStackTrace();
        }
        catch (ExecutionException ee){
            ee.printStackTrace();
        }
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
                    logToDisplay(Double.toString(beacons.iterator().next().getDistance()));
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

    private void logToDisplay( final String dist) {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView distance = (TextView) findViewById(R.id.textViewDist);
                distance.setText("Esta a "+dist+"m de distancia");
            }
        });
    }

    private void logToDisplayNoBeacon() {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView distance = (TextView) findViewById(R.id.textViewDist);
                distance.setText("Saliste de la región del beacon.");
            }
        });
    }
}
