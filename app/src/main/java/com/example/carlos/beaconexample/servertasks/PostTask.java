package com.example.carlos.beaconexample.servertasks;

import android.os.AsyncTask;

import com.example.carlos.beaconexample.classesBeacon.Beacon;
import com.google.gson.Gson;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Carlos on 02/12/2016.
 */

public class PostTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] params) {
        Beacon b = new Beacon();
        b.setMajor_id("10");
        b.setMinor_id("9");

        Gson gson = new Gson();
        String json = gson.toJson(b);

        try {
            HttpPost httpPost = new HttpPost("http://afternoon-coast-28639.herokuapp.com/beacons.json");
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
