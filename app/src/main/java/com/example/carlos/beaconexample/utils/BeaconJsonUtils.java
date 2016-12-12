package com.example.carlos.beaconexample.utils;

import com.example.carlos.beaconexample.classesBeacon.BeaconModel;
import com.example.carlos.beaconexample.classesBeacon.Device;
import com.example.carlos.beaconexample.classesBeacon.Discover;
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
    public static BeaconModel[] JsonToBeaconArray(String json){
        Gson g = new Gson();
        BeaconModel[] b = g.fromJson(json,BeaconModel[].class);

        return b;
    }

    /*
        Returns the json in a list of Beacons
     */
    public static List<BeaconModel> JsonToBeaconList(String json){
        Gson gson = new Gson();
        Type beaconListType = new TypeToken<ArrayList<BeaconModel>>(){}.getType();
        List<BeaconModel> b = gson.fromJson(json, beaconListType);

        return b;
    }

    public static BeaconModel JsonToBeacon(String json){
        Gson gson = new Gson();
        BeaconModel b = gson.fromJson(json,BeaconModel.class);
        return b;
    }

    public static String DiscoverToJson(Discover d){
        Gson g = new Gson();
        String json = g.toJson(d);

        return json;
    }

    public static String DeviceToJson(Device d){
        Gson g = new Gson();
        String json = g.toJson(d);

        return json;
    }
}
