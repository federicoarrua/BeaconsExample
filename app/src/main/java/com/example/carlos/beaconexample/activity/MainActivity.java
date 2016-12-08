package com.example.carlos.beaconexample.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.carlos.beaconexample.R;
import com.example.carlos.beaconexample.servertasks.PostTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
        new PostTask().execute();
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