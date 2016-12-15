package com.example.carlos.beaconexample.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.support.annotation.RequiresPermission;
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
import com.example.carlos.beaconexample.servertasks.DiscoverPostTask;
import com.example.carlos.beaconexample.utils.BeaconJsonUtils;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String TAG = "MAIN_ACTIVITY";
    private SharedPreferences prefs;
    private BeaconModel[] beaconModelArray;
    private BeaconManager beaconManager;

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
        //Descomentar si el regionBootstrap esta inicializado en la aplicación
        //((ApplicationBeacon)this.getApplication()).stopBeaconMonitoring();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        this.beaconManager.bind(this);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    /*
       onBeaconServiceConnect() encargado de iniciar lectura de beacons
       añade un MonitorNotifier que monitorea en las
       regiones de la base de datos para publicar descubrimientos
     */
    public void onBeaconServiceConnect() {
        this.beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.d(TAG, "Entré a la región");

                BeaconModel b = new BeaconModel();
                b.setMajor_region_id(region.getId2().toInt());
                b.setMinor_region_id(region.getId3().toInt());
                b.setDescription(region.getUniqueId());

                HashMap<String,String> ids = new HashMap<>();
                ids.put("device_id",prefs.getString("device_id",null));
                ids.put("major_region_id",b.getMajor_region_id().toString());
                ids.put("minor_region_id",b.getMinor_region_id().toString());
                new DiscoverPostTask().execute(ids);

                Log.d(TAG,"NOTIFICACION "+region.toString());
            }

            @Override
            public void didExitRegion(Region region) {
                Log.d(TAG, "Salí de la región");
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });
        startMonitorRegions();
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