package com.example.carlos.beaconexample.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.carlos.beaconexample.R;
import com.example.carlos.beaconexample.classesBeacon.BeaconModel;
import com.example.carlos.beaconexample.servertasks.BeaconsGetTask;
import com.example.carlos.beaconexample.utils.BeaconJsonUtils;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

/**
 * Created by Federico on 13/12/2016.
 * MainActivity vista principal de la aplicación contiene los botones que dirigen a la funcionalidad
 * de la aplicación.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    private SharedPreferences prefs;
    private BeaconModel[] beaconModelArray;
    private BeaconManager beaconManager;

    private final int MY_PERMISSIONS_REQUEST = 1;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 2;
    private final int MY_PERMISSIONS_READ_CONTACTS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("Main Activity","Main Activity \r\n");

        this.prefs = getSharedPreferences("con.example.carlos.beaconexample",MODE_PRIVATE);
        beaconModelArray = BeaconJsonUtils.JsonToBeaconArray(prefs.getString("beacons",null));

        ProgressBar pg = (ProgressBar) findViewById(R.id.progressBar);
        pg.setVisibility(View.INVISIBLE);

        Button bBeacon = (Button) findViewById(R.id.buttonBeacon);
        bBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBeacon();
            }
        });

        Button bList = (Button) findViewById(R.id.buttonList);
        bList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                launchList();
            }
        });

        Button bSync = (Button) findViewById(R.id.buttonSync);
        bSync.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                syncWithDatabase();
            }
        });

        //Inicialización Beacon Monitor
        this.beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        startMonitorRegions();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onStart();

        //Chequeo de permisos requeridos por la app en AndroidManifest.xml
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.GET_ACCOUNTS)!= PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.GET_ACCOUNTS,Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_REQUEST);
        else{
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.GET_ACCOUNTS)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.GET_ACCOUNTS},MY_PERMISSIONS_READ_CONTACTS);
            }
            else{
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
                }
            }
        }

    }

    /*
        launchBeacon Método del botón modo beacon
     */
    private void launchBeacon(){
        Intent i = new Intent(this, BeaconActivity.class);
        startActivity(i);
    }

    /*
        launchList Método del botón Lista de Beacons
     */
    private void launchList(){
        Intent i = new Intent(this, ListRangeActivity.class);
        startActivity(i);
    }

    /*
        syncWithDatabase Método del botón sincronizar
     */
    private void syncWithDatabase(){
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
        catch (Exception e){
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar pg = (ProgressBar) findViewById(R.id.progressBar);
                pg.setVisibility(View.INVISIBLE);
            }
        });
    }

    /*
        startMonitorRegions Método privado para iniciar regiones de monitoreo
     */
    private void startMonitorRegions(){
        for(int i=0;i<beaconModelArray.length;i++) {
            Region region = new Region(beaconModelArray[i].getName(),null, Identifier.parse(beaconModelArray[i].getMajor_region_id().toString()), Identifier.parse(beaconModelArray[i].getMinor_region_id().toString()));
            try {
                beaconManager.startMonitoringBeaconsInRegion(region);
            }
            catch(RemoteException re){
                re.printStackTrace();
            }
        }
    }
}