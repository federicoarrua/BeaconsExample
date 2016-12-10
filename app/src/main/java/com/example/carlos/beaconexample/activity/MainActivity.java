package com.example.carlos.beaconexample.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.carlos.beaconexample.R;
import com.example.carlos.beaconexample.servertasks.BeaconByRegionGetTask;
import com.example.carlos.beaconexample.servertasks.DiscoverPostTask;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("Main Activity","Main Activity \r\n");

        Button bDetect = (Button) findViewById(R.id.buttonMonitor);
        bDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMonitorBeacon(v);
            }
        });

        Button bRange = (Button) findViewById(R.id.buttonRange);
        bRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRangeBeacon(v);
            }
        });

        Button bBeacon = (Button) findViewById(R.id.buttonBeacon);
        bBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBeacon(v);
            }
        });
    }

    private void launchMonitorBeacon(View v){
//        Intent i = new Intent(this,DetectActivity.class);
//        startActivity(i);
        HashMap<String,Integer> m = new  HashMap<String,Integer> ();
        m.put("major_id",4660);
        m.put("minor_id",64001);

        try {
            String p = new BeaconByRegionGetTask().execute(m).get().toString();
        }
        catch(Exception e){}

    }

    private void launchRangeBeacon(View v){
        Intent i = new Intent(this,RangingActivity.class);
        startActivity(i);
    }

    private void launchBeacon(View v){
        Intent i = new Intent(this, BeaconActivity.class);
        startActivity(i);
    }

}