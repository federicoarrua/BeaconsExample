package com.example.carlos.beaconexample.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import com.example.carlos.beaconexample.R;
import com.example.carlos.beaconexample.classesBeacon.BeaconModel;
import com.example.carlos.beaconexample.servertasks.BeaconByRegionGetTask;
import com.example.carlos.beaconexample.servertasks.DiscoverPostTask;
import com.example.carlos.beaconexample.utils.BeaconJsonUtils;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Federico on 24/10/2016.
 */

public class RangingActivity extends Activity implements BeaconConsumer {

    private String TAG = "RangingDetectBeacon";

    private BeaconManager beaconManager;

    private SharedPreferences prefs;

    //Beacon del cual muestro la información
    private BeaconModel beacon;

    //Usada al principio para iniciar monitoreo
    private BeaconModel[] beaconModelArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.range_layout);

        //Recibo el beacon a visualizar desde la vista anterior
        beacon = (BeaconModel) getIntent().getSerializableExtra("beacon");

        //Descomentar si el regionBootstrap esta inicializado en la aplicación
        //((ApplicationBeacon)this.getApplication()).stopBeaconMonitoring();

        //Obtengo lista de beacons en base de datos
        prefs = getSharedPreferences("con.example.carlos.beaconexample",MODE_PRIVATE);
        beaconModelArray = BeaconJsonUtils.JsonToBeaconArray(prefs.getString("beacons",null));

        //HashMap usado para pasar a la AsyncTask que recupera al beacon
        HashMap<String,Integer> hm = new HashMap<>();
        hm.put("minor_region_id",beacon.getMinor_region_id());
        hm.put("major_region_id",beacon.getMajor_region_id());

        try {
            //Recupero el beacon desde el servidor
            String json =(String) new BeaconByRegionGetTask().execute(hm).get();
            TextView txtDesc = (TextView) findViewById(R.id.textViewDesc);
            TextView txtName = (TextView) findViewById(R.id.textViewName);

            //Si json es null significa que el beacon no esta en la base de datos
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
        catch(Exception e){
            e.printStackTrace();
        }

        //Inicialización de beaconManager
        this.beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    /*
       onBeaconServiceConnect() encargado de iniciar lectura de beacons
       añade un rangeNotifier para la región de la variable beacon y monitorea en las
       regiones de la base de datos para publicar descubrimientos
     */
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size()>0){
                    logToDisplay(Float.toString((float)beacons.iterator().next().getDistance()));
                }
                else{
                    Log.i(TAG,"No beacons in this region.\r\n");
                    logToDisplayNoBeacon();
                }

            }
        });
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.d(TAG, "Got a didEnterRegion call");

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

            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });
        try{
            beaconManager.startRangingBeaconsInRegion(new Region(beacon.getDescription(),null, Identifier.parse(beacon.getMajor_region_id().toString()),Identifier.parse(beacon.getMinor_region_id().toString())));
            startMonitorRegions();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
        logToDisplay método privado para graficar la distancia al beacon
     */
    private void logToDisplay( final String dist) {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView distance = (TextView) findViewById(R.id.textViewDist);
                distance.setText("Esta a "+dist+"m de distancia");
            }
        });
    }

    /*
        logToDisplayNoBeacon método privado para gráficar que no estoy cerca del beacon
     */
    private void logToDisplayNoBeacon() {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView distance = (TextView) findViewById(R.id.textViewDist);
                distance.setText("Saliste de la región del beacon.");
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
