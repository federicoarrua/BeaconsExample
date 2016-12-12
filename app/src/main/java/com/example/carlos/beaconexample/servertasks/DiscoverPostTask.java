package com.example.carlos.beaconexample.servertasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.carlos.beaconexample.Constants;
import com.example.carlos.beaconexample.classesBeacon.Discover;
import com.example.carlos.beaconexample.utils.BeaconJsonUtils;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by Carlos on 08/12/2016.
 */

public class DiscoverPostTask extends AsyncTask<HashMap<String,String>,Void,Object>{

    private static final String TAG ="DISCOVER_POST_TASK";

    @Override
    protected Object doInBackground(HashMap<String,String>... params) {
        HashMap<String,String> p = params[0];

        Discover discover = new Discover();
        discover.setDevice_id(p.get("device_id"));
        discover.setMajor_id(Integer.parseInt(p.get("major_id")));
        discover.setMinor_id(Integer.parseInt(p.get("minor_id")));

        String json = BeaconJsonUtils.DiscoverToJson(discover);

        Log.d(TAG,json);


        try {
            HttpPost httpPost = new HttpPost(Constants.URL+"/discovers.json");
            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            return new DefaultHttpClient().execute(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}