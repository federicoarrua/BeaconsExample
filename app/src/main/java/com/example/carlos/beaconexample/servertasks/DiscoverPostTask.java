package com.example.carlos.beaconexample.servertasks;

import android.os.AsyncTask;
import com.example.carlos.beaconexample.classesBeacon.Discover;
import com.google.gson.Gson;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Carlos on 08/12/2016.
 */

public class DiscoverPostTask extends AsyncTask{
    @Override
    protected Object doInBackground(Object[] params) {
        Discover discover = new Discover();
        discover.setDevice_id(133);
        discover.setBeacon_id(1);
        discover.setMajor_id(1);
        discover.setMinor_id(1);

        Gson g = new Gson();
        String json = g.toJson(discover);

        try {
            HttpPost httpPost = new HttpPost("http://afternoon-coast-28639.herokuapp.com/devices.json");
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