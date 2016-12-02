package com.example.carlos.beaconexample.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.carlos.beaconexample.R;
import com.example.carlos.beaconexample.Tasks.PostTask;
import com.example.carlos.beaconexample.classesBeacon.Beacon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.R.attr.path;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("Main Activity","Main Activity \r\n");

        Button bDetect = (Button) findViewById(R.id.buttonMonitor);
        bDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMonitorBeacon(v);
            }
        });

        Button bRange = (Button) findViewById(R.id.buttonRange);
        bRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRangeBeacon(v);
            }
        });

        Button bBeacon = (Button) findViewById(R.id.buttonBeacon);
        bBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBeacon(v);
            }
        });
    }

    private void launchMonitorBeacon(View v){
//        Intent i = new Intent(this,DetectActivity.class);
//        startActivity(i);
        new PostTask().execute();
    }

    private void launchRangeBeacon(View v){
        Intent i = new Intent(this,RangingActivity.class);
        startActivity(i);
    }

    private void launchBeacon(View v){
        Intent i = new Intent(this, BeaconActivity.class);
        startActivity(i);
    }

    public static HttpResponse makeRequest(String uri, String json) {
        try {
            HttpPost httpPost = new HttpPost(uri);
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

}