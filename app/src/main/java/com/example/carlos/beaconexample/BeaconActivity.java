package com.example.carlos.beaconexample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.w3c.dom.Text;

import java.util.Arrays;

/**
 * Created by Carlos on 24/10/2016.
 */

public class BeaconActivity extends Activity {

    private String TAG = "Beacon Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detect_layout);

        TextView text = (TextView) findViewById(R.id.textView2);

        try {
            Beacon beacon = new Beacon.Builder().setId1("f7826da6-4fa2-4e98-8024-bc5b71e0893e")
                    .setId2("1")
                    .setId3("2")
                    .setManufacturer(0x0118)
                    .setTxPower(-59)
                    .setDataFields(Arrays.asList(new Long[] {0l}))
                    .build();
            BeaconParser bp = new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24");
            BeaconTransmitter bt = new BeaconTransmitter(getApplicationContext(),bp);
            bt.startAdvertising(beacon);
            text.setText("Beacon mode working");
        }
        catch(Exception e){
            Log.i(TAG,e.toString());
            text.setText("Beacon mode not working. \r\n Check Bluetooth.");
        }
    }
}
