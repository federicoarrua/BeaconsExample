package com.example.carlos.beaconexample.utils;

import android.util.Log;

import com.example.carlos.beaconexample.classesBeacon.Beacon;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carlos on 08/12/2016.
 */

public class BeaconJsonUtils {


    /*
        Returns the json in a Beacon Array
     */
    public static Beacon[] JsonToBeaconArray(String json){
        Gson g = new Gson();
        Beacon[] b = g.fromJson(json,Beacon[].class);

        return b;
    }

    /*
        Returns the json in a list of Beacons
     */
    public static List<Beacon> JsonToBeaconList(String json){
        Gson gson = new Gson();
        Type beaconListType = new TypeToken<ArrayList<Beacon>>(){}.getType();
        List<Beacon> b = gson.fromJson(json, beaconListType);

        return b;
    }

    public static Beacon JsonToBeacon(String json){
        Gson gson = new Gson();
        Beacon b = gson.fromJson(json,Beacon.class);
        return b;
    }
}
