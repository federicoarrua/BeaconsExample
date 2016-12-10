package com.example.carlos.beaconexample.servertasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.carlos.beaconexample.Constants;
import com.example.carlos.beaconexample.classesBeacon.Beacon;
import com.example.carlos.beaconexample.utils.BeaconJsonUtils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

/**
 * Created by Carlos on 08/12/2016.
 */

public class BeaconsGetTask extends AsyncTask{

    @Override
    protected Object doInBackground(Object[] params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(Constants.URL+"/beacons.json");
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
                Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
            }

            Beacon[] b = BeaconJsonUtils.JsonToBeaconArray(buffer.toString());
            List<Beacon> bl = BeaconJsonUtils.JsonToBeaconList(buffer.toString());

            for(int i=0 ; i< b.length;i++){
                Log.d("ASYNCTASK",b[i].getMajor_id()+" "+b[i].getMinor_id()+" "+b[i].getDescription());
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
