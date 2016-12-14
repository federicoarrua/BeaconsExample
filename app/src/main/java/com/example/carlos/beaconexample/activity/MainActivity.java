package com.example.carlos.beaconexample.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.carlos.beaconexample.ApplicationBeacon;
import com.example.carlos.beaconexample.R;
import com.example.carlos.beaconexample.classesBeacon.BeaconModel;
import com.example.carlos.beaconexample.servertasks.BeaconsGetTask;
import com.example.carlos.beaconexample.utils.BeaconJsonUtils;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("Main Activity","Main Activity \r\n");

        prefs = getSharedPreferences("con.example.carlos.beaconexample",MODE_PRIVATE);

        ProgressBar pg = (ProgressBar) findViewById(R.id.progressBar);
        pg.setVisibility(View.INVISIBLE);

        Button bBeacon = (Button) findViewById(R.id.buttonBeacon);
        bBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBeacon(v);
            }
        });

        Button bList = (Button) findViewById(R.id.buttonList);
        bList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                launchList(v);
            }
        });

        Button bSync = (Button) findViewById(R.id.buttonSync);
        bSync.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                syncWithDatabase(v);
            }
        });
    }

    private void launchBeacon(View v){
        Intent i = new Intent(this, BeaconActivity.class);
        startActivity(i);
    }

    private void launchList(View v){
        Intent i = new Intent(this, ListRangeActivity.class);
        startActivity(i);
    }

    private void syncWithDatabase(View v){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar pg = (ProgressBar) findViewById(R.id.progressBar);
                pg.setVisibility(View.VISIBLE);
            }
        });
        try {
            String beaconsJson = (String) new BeaconsGetTask().execute().get();
            prefs.edit().putString("beacons", beaconsJson).commit();
            int duration = Toast.LENGTH_SHORT;
            Log.d(TAG,"Se sincronizó");
            Toast toast = Toast.makeText(this, "App Sincronizada", duration);
            toast.show();
        }
        catch (InterruptedException ie){
            ie.printStackTrace();
        }
        catch(ExecutionException ee){
            ee.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar pg = (ProgressBar) findViewById(R.id.progressBar);
                pg.setVisibility(View.INVISIBLE);
            }
        });
    }


    public List<Region> createRegions(String json){
        BeaconModel[] beaconArray = BeaconJsonUtils.JsonToBeaconArray(json);
        List<Region> regionList = new ArrayList<Region>();

        for(int i=0;i<beaconArray.length;i++) {
            Region region = new Region(beaconArray[i].getId().toString(),
                    null, Identifier.parse(beaconArray[i].getMajor_id().toString()), Identifier.parse(beaconArray[i].getMinor_id().toString()));
            regionList.add(region);
            Log.d(TAG,"Region con id "+ region.getUniqueId()+" añadida");
        }
        return regionList;
    }
}