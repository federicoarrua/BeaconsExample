package com.example.carlos.beaconexample.servertasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.carlos.beaconexample.Constants;
import com.example.carlos.beaconexample.classesBeacon.Beacon;
import com.example.carlos.beaconexample.utils.BeaconJsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Carlos on 10/12/2016.
 */

public class BeaconByRegionGetTask extends AsyncTask<HashMap<String,Integer>,Void,Object> {
    @Override
    protected Object doInBackground(HashMap<String,Integer>... params) {
        HashMap<String,Integer> regions = params[0];

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(Constants.URL+"/beacons/showregion.json?major_id="+regions.get("major_id")+"&minor_id="+regions.get("minor_id"));
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();// In this buffer i save the whole response
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
                Log.d("Response: ", "> " + line);
            }

            return buffer.toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
