package com.example.ventus.networksignal;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ventus.networksignal.localization.StartingScreen;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    WifiManager wifi;
    WifiScanReceiver wifiReceiver;
    Button btnScan;
    Button btnGraph;
    Button btnLocalize;
    ArrayList<String> wifis = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    private ListView wifisList;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGraph = findViewById(R.id.btn_graph);
        btnLocalize = findViewById(R.id.btn_localize);
        btnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent graph_activity = new Intent(MainActivity.this, GraphScreen.class);
                startActivity(graph_activity);
            }
        });
        btnLocalize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent localize = new Intent(MainActivity.this, StartingScreen.class);
                startActivity(localize);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
            }
        }
        wifisList = (ListView) findViewById(R.id.wifislist);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiScanReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        btnScan = findViewById(R.id.button1);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifi.startScan();
            }
        });
        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, wifis){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.WHITE);

                // Generate ListView Item using TextView
                return view;
            }
        };
        //wifisList.setAdapter(arrayAdapter);
        wifisList
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v,
                                            int position, long id) {
                        Intent intent = new Intent(getApplicationContext(),
                                InfoWifi.class);
                        String selectedPosition = (String) parent
                                .getItemAtPosition(position);
                        intent.putExtra("WIFI", selectedPosition);
                        startActivityForResult(intent,0);
                    }
                });
    }

    protected void onPause() {
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifi.getScanResults();
            for(int i = 0; i < wifiScanList.size(); i++){
                //String info = ("SSID: "+(wifiScanList.get(i)).SSID+" "+"RSSI: "+(wifiScanList.get(i)).level+" "+"Częstotliwość: "+(wifiScanList.get(i)).frequency+" "+"Szerokość kanału: "+(wifiScanList.get(i)).channelWidth);
                String info = ((wifiScanList.get(i)).SSID+" ("+(wifiScanList.get(i)).BSSID+")");
                if (wifis==null) {
                    wifis.add(info);
                    wifisList.setAdapter(arrayAdapter);
                }
                else if (wifis.contains(info)){
                    //do nothing
                }
                else {
                    wifis.add(info);
                    wifisList.setAdapter(arrayAdapter);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {//permission was not granted

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}