package com.example.carlos.beaconexample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.carlos.beaconexample.Constants;
import com.example.carlos.beaconexample.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Arrays;

/**
 * Created by Federico on 24/10/2016.
 * Instancia el modo beacon, e inicia transmisión
 */

public class BeaconActivity extends Activity {

    private String TAG = "Beacon Activity";
    private BeaconTransmitter bt;
    private Beacon beacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detect_layout);

        TextView text = (TextView) findViewById(R.id.textView2);

        //Trato de transmitir como beacon con esas especificaciones
        try {
            beacon = new Beacon.Builder().setId1(Constants.UUID_DEV)
                    .setId2("1")
                    .setId3("2")
                    .setManufacturer(0x004C)
                    .setTxPower(-59)
                    .setDataFields(Arrays.asList(new Long[] {0l}))
                    .build();
            BeaconParser bp = new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24");

            bt = new BeaconTransmitter(getApplicationContext(),bp);
            bt.startAdvertising(beacon);

            Log.d(TAG,"Transmitiendo como beacon");
            text.setText("Beacon mode funcionando");
        }
        //En caso de no poder muestro en pantalla que no es posible
        catch(Exception e){
            e.printStackTrace();
            text.setText("Beacon mode no funciona. \r\n Chequear Bluetooth o versión de Android.");
            Log.d(TAG,"Error al encender la transmisión como beacon.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Al salir de este activity dejo de transmitir como beacon
        bt.stopAdvertising();
    }
}
