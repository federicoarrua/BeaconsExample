package com.example.carlos.beaconexample;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.carlos.beaconexample.activity.DetectActivity;
import com.example.carlos.beaconexample.activity.MainActivity;
import com.example.carlos.beaconexample.activity.RangingActivity;
import com.example.carlos.beaconexample.classesBeacon.Discover;
import com.example.carlos.beaconexample.servertasks.DevicePostTask;
import com.example.carlos.beaconexample.servertasks.DiscoverPostTask;
import com.example.carlos.beaconexample.simbeacon.TimedBeaconSimulator;
import com.example.carlos.beaconexample.utils.UserEmailFetcher;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by Carlos on 31/10/2016.
 */

public class ApplicationBeacon extends Application implements BootstrapNotifier {

    private static final String TAG = " ApplicationBeacon";
    private static final int NOTIFICATION_ID = 123;
    private RegionBootstrap regionBootstrap;
    private SharedPreferences prefs;

    public void onCreate() {
        super.onCreate();
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

        //For iBeacon uncomment this line
        //beaconManager.getBeaconParsers().add(new BeaconParser("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        Log.d(TAG, "setting up background monitoring for beacons and power saving");

        //Wake up when a beacon is seen
        regionBootstrap = new RegionBootstrap(this, createRegions());

        // If you wish to test beacon detection in the Android Emulator, you can use code like this:
        BeaconManager.setBeaconSimulator(new TimedBeaconSimulator() );
        ((TimedBeaconSimulator) BeaconManager.getBeaconSimulator()).createTimedSimulatedBeacons();

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
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "Got a didEnterRegion call");
        HashMap<String,String> p = new  HashMap<String,String> ();
        p.put("device_id",getSharedPreferences("con.example.carlos.beaconexample",MODE_PRIVATE).getString("device_id",null));
        p.put("major_id","1");
        p.put("minor_id","1");
        p.put("beacon_id","1");
        new DiscoverPostTask().execute(p);
        regionBootstrap.disable();
//        Intent i = new Intent(this, RangingActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.startActivity(i);
//        showNotification("Beacon Notification","You enter a Beacon Region");
//        Log.d(TAG,"NOTIFICACION "+region.toString());
    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
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
    public List<Region> createRegions(){
        List<Region> regionList = new ArrayList<Region>();
        Region region = new Region("backgroundRegion",
                null,null,Identifier.parse("1"));
        regionList.add(region);
        return regionList;
    }
}
