package com.example.carlos.beaconexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.altbeacon.beacon.Beacon;

public class MainActivity extends AppCompatActivity {

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
        Intent i = new Intent(this,DetectActivity.class);
        startActivity(i);
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
