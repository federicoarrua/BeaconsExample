package com.example.carlos.beaconexample;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.carlos.beaconexample.activity.MainActivity;
import com.example.carlos.beaconexample.activity.RangingActivity;
import com.example.carlos.beaconexample.classesBeacon.BeaconModel;
import com.example.carlos.beaconexample.servertasks.BeaconsGetTask;
import com.example.carlos.beaconexample.servertasks.DevicePostTask;
import com.example.carlos.beaconexample.servertasks.DiscoverPostTask;
import com.example.carlos.beaconexample.simbeacon.TimedBeaconSimulator;
import com.example.carlos.beaconexample.utils.BeaconJsonUtils;
import com.example.carlos.beaconexample.utils.UserEmailFetcher;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Carlos on 31/10/2016.
 */

public class ApplicationBeacon extends Application implements BootstrapNotifier {

    private static final String TAG = " ApplicationBeacon";
    private static final int NOTIFICATION_ID = 123;
    public RegionBootstrap regionBootstrap;
    private SharedPreferences prefs;

    public void onCreate() {
        super.onCreate();
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

        //For iBeacon uncomment this line
        beaconManager.getBeaconParsers().add(new BeaconParser("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        Log.d(TAG, "setting up background monitoring for beacons and power saving");

        prefs = getSharedPreferences("con.example.carlos.beaconexample",MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            Log.d(TAG,"Firts Run");
            prefs.edit().putBoolean("firstrun", false).commit();

            HashMap<String,String> p = new HashMap<String,String>();
            String device_id =UserEmailFetcher.getEmail(getApplicationContext());
            p.put("device_id",device_id);
            prefs.edit().putString("device_id",device_id).commit();
            Log.d(TAG,device_id);

            new DevicePostTask().execute(p);

        }
        try {
            String beaconsJson = (String) new BeaconsGetTask().execute().get();
            prefs.edit().putString("beacons",beaconsJson).commit();
            //regionBootstrap = new RegionBootstrap(this,new Region("todos",null,null,null));
            regionBootstrap = new RegionBootstrap(this,createRegions(beaconsJson));

            //Simula Beacons
            BeaconManager.setBeaconSimulator( new TimedBeaconSimulator());
            ((TimedBeaconSimulator) BeaconManager.getBeaconSimulator()).createTimedSimulatedBeacons();

            Log.d(TAG,"Inititialization Completed!");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "Got a didEnterRegion call");

        BeaconModel b = new BeaconModel();
        b.setMajor_id(region.getId2().toInt());
        b.setMinor_id(region.getId3().toInt());
        b.setDescription(region.getUniqueId());

        HashMap<String,String> ids = new HashMap<String,String>();
        ids.put("device_id",prefs.getString("device_id",null));
        ids.put("major_id",b.getMajor_id().toString());
        ids.put("minor_id",b.getMinor_id().toString());
        new DiscoverPostTask().execute(ids);

        showNotification("Beacon Notification","You enter a Beacon Region",b);
        Log.d(TAG,"NOTIFICACION "+region.toString());

        //regionBootstrap.disable();
    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }

    public void showNotification(String title, String message , BeaconModel b) {
        Intent notifyIntent = new Intent(this, RangingActivity.class);
        notifyIntent.putExtra("beacon",b);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    //Obtain region list from rails server with GET method
    public List<Region> createRegions(String json){
        BeaconModel[] beaconArray = BeaconJsonUtils.JsonToBeaconArray(json);
        List<Region> regionList = new ArrayList<Region>();

        for(int i=0;i<beaconArray.length;i++) {
            Region region = new Region(beaconArray[i].getId().toString(),
                    null, Identifier.parse(beaconArray[i].getMajor_id().toString()), Identifier.parse(beaconArray[i].getMinor_id().toString()));
            regionList.add(region);
        }
        return regionList;
    }

    public void startBeaconMonitoring() {
        if (regionBootstrap == null) {
            Region region = new Region("backgroundRegion", null, null, null);
            regionBootstrap = new RegionBootstrap(this, region);
        }
    }

    public void stopBeaconMonitoring() {
        regionBootstrap.disable();
    }
}
