package com.example.carlos.beaconexample.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.carlos.beaconexample.ApplicationBeacon;
import com.example.carlos.beaconexample.R;
import com.example.carlos.beaconexample.classesBeacon.BeaconModel;
import com.example.carlos.beaconexample.simbeacon.TimedBeaconSimulator;
import com.example.carlos.beaconexample.utils.BeaconJsonUtils;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static android.R.attr.type;

/**
 * Created by Carlos on 13/12/2016.
 */

public class ListRangeActivity extends ListActivity implements BeaconConsumer {

    private String TAG = "RangingDetectBeacon";
    private BeaconManager beaconManager;

    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;

    //Colección de beacons que encuentro
    Collection<Beacon> beaconCollection;

    //Arreglo de beacons registrados en la base de datos
    BeaconModel[] beaconModelArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listItems);
        setListAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("con.example.carlos.beaconexample",MODE_PRIVATE);
        beaconModelArray = BeaconJsonUtils.JsonToBeaconArray(prefs.getString("beacons",null));

        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int i =0;
                Beacon b;
                BeaconModel bm = new BeaconModel();
                for (Iterator iterator = beaconCollection.iterator(); iterator.hasNext();) {
                    if(i == position) {
                        b = (Beacon) iterator.next();
                        bm.setMinor_id(b.getId3().toInt());
                        bm.setMajor_id(b.getId2().toInt());
                        bm.setDescription("Alguna descripción");
                    }
                    else
                        iterator.next();
                    i++;
                }

                Intent intent = new Intent(ListRangeActivity.this,RangingActivity.class);
                intent.putExtra("beacon",bm);
                startActivity(intent);
            }
        });

        this.beaconManager = BeaconManager.getInstanceForApplication(this);
        //((ApplicationBeacon)this.getApplication()).stopBeaconMonitoring();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                beaconCollection = beacons;
                if (beacons.size()>0 ){
                    addItems(beacons);
                }
                else{
                    Log.i(TAG,"No beacons in this region.\r\n");
                    noItems();
                }
            }
        });
        try{
            beaconManager.startRangingBeaconsInRegion(new Region("Ranging region",null, null,null));
        }
        catch (Exception e){}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    private void addItems(Collection<Beacon> beacons) {
        listItems=new ArrayList<String>();
        String item;
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listItems);
        Integer i=0;

        for (Beacon beacon: beacons) {
            Beacon b = beacon;
            item= getNameBeacon(b.getId2().toInt(),b.getId3().toInt()) + " :\r\n" +i.toString()+". UUID: "+ b.getId1()+" Major: "+b.getId2()+" Minor: "+b.getId3()+" Distancia: " +b.getDistance();
            listItems.add(item);
            i++;
        }
        runOnUiThread(new Runnable() {
            public void run() {
                setListAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void noItems() {
        listItems=new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listItems);
        runOnUiThread(new Runnable() {
            public void run() {
                setListAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }
    private String getNameBeacon(int major_id,int minor_id){
        for(int i=0; i<beaconModelArray.length;i++){
            if(beaconModelArray[i].getMinor_id()==minor_id && beaconModelArray[i].getMajor_id()==major_id )
                return "Nombre: " + beaconModelArray[i].getName();
        }
        return "El beacon no está en la base de datos";
    }
}
