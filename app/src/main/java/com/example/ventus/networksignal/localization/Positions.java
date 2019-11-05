package com.example.ventus.networksignal.localization;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ventus.networksignal.R;
import com.zechassault.zonemap.listener.ItemClickListener;
import com.zechassault.zonemap.util.BitmapUtils;
import com.zechassault.zonemap.view.ImageMapView;
import android.net.wifi.ScanResult;

public class Positions extends Activity implements ItemClickListener<PinItem> {

    private Button calibrate;
    private Button finish;
    private TextView positionName;
    private int readingCount = 30;
    private TextView results;
    private String resultsText;
    private ListView positionsList;
    ArrayList<String> positions;
    ArrayAdapter arrayAdapter;
    Timer timer;
    TimerTask myTimerTask;
    int positionCount;
    DatabaseHelper db;
    private Boolean isLearning = true;
    static final int SCAN_REQUEST = 0;
    Button friendlyWifisButton;
    PinItem holder;

    private List<PositionData> positionsData;
    private PositionData positionData;
    private String building;
    private List<ResultData> resultsData;
    WifiManager wifi;
    private int currentCount;
    String currentPositionName;

    int pinID = R.drawable.pin_gray_small;
    int pinIDGold = R.drawable.pin_gold_small;
    String[] names = new String[]{
            "C102",
            "C106",
            "C105",
            "Schody 1",
            "C100*",
            "Schody 2",
            "Ubikacja niepełnosprawnych",
            "Ubikacja damska",
            "Winda",
            "Ubikacja męska",
            "C109",
            "C112"
    };
    private PointF[] pointFs = new PointF[]{
            new PointF(0.33f, 0.40f),  //C102
            new PointF(0.53f, 0.40f),  //C106
            new PointF(0.47f, 0.60f),  //C105
            new PointF(0.55f, 0.60f),  //Schody 1
            new PointF(0.16f, 0.55f),  //C100
            new PointF(0.10f, 0.45f),  //Schody 2
            new PointF(0.60f, 0.35f),  //Ubikacja niepełnosprawnych
            new PointF(0.65f, 0.35f),  //Ubikacja damska
            new PointF(0.695f, 0.35f), //Winda
            new PointF(0.74f, 0.35f),  //Ubikacja męska
            new PointF(0.65f, 0.60f),  //C109
            new PointF(0.80f, 0.60f)   //C112
    };
    /* Item Map view*/
    private ImageMapView imageMapView;

    /*List of item to show on map view*/
    private List<PinItem> items;
    public static final int NB_ELEMENTS = 12;

    @SuppressWarnings("null")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Confirm...");
        alertDialog.setMessage("Scanning requires WiFi.");
        alertDialog.setPositiveButton("Turn on WiFi",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Activity transfer to wifi settings
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        alertDialog.setCancelable(false);
        if(!wifi.isWifiEnabled()) {
            alertDialog.show();
        }
        setContentView(R.layout.positions);
        friendlyWifisButton= (Button) findViewById(R.id.friendly_wifis_button);
        friendlyWifisButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FriendlyWifis.class);
                intent.putExtra("BUILDING_NAME", building);
                startActivity(intent);
            }
        });
        positionName = findViewById(R.id.position_name);
        calibrate = (Button) findViewById(R.id.calibratebutton);
        resultsText = "";

        positionCount = 0;
        positionsData = new ArrayList<PositionData>();
        building = "PWSZ_1";
        db = new DatabaseHelper(this);
        positions = db.getPositions(building);
        positionName.setText("");
        calibrate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if(db.getFriendlyWifis(building).isEmpty()) {
                    Context context = getApplicationContext();
                    CharSequence text = "Select one or more Friendly WiFi";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    calibrate.setEnabled(false);
                    positionName.setText("Please wait...");
                    resultsData = new ArrayList<ResultData>();
                    currentCount = 0;
                    timer = new Timer();
                    myTimerTask = new TimerTask() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            refresh();
                        }
                    };
                    timer.schedule(myTimerTask, 0, 1000);
                }
            }
        });

        items = new ArrayList<>();
        for (int i = 0; i < NB_ELEMENTS; i++) {
            items.add(new PinItem(names[i], pointFs[i]
                    , BitmapUtils.resAsBitmap(ContextCompat.getDrawable(getApplicationContext(), pinID))
                    , BitmapUtils.resAsBitmap(ContextCompat.getDrawable(getApplicationContext(), pinIDGold))));
        }
        imageMapView = (ImageMapView) findViewById(R.id.imageMapViewFront);
        imageMapView.setScaleToBackground(false);
        imageMapView.setAllowTransparent(false);
        imageMapView.setAdapter(new PinAdapter(items));
        imageMapView.getAdapter().setItemClickListener(this);

    }

    @Override
    protected void onResume() {
        positions = db.getPositions(building);
        positionName.setText("");
        calibrate.setEnabled(false);
        super.onResume();
    }
    @Override
    public void onMapItemClick(PinItem item) {
        holder = item;
        positionName.setText(item.text);
        currentPositionName = item.text;
        calibrate.setEnabled(true);
    }
    public class ResultData {
        private Router router;

        public Router getRouter() {
            return this.router;
        }

        public List<Integer> values;

        public ResultData(Router router) {
            // TODO Auto-generated constructor stub
            this.router = router;
            values = new ArrayList<Integer>();
        }
    }
    private void refresh() {
        // TODO Auto-generated method stub
        if (currentCount >= readingCount) {
            if (myTimerTask != null)
                myTimerTask.cancel();

        }
        currentCount++;
        wifi.startScan();
        List<ScanResult> results = wifi.getScanResults();
        for (int i = 0; i < results.size(); i++) {
            // System.out.println("test2");
            String ssid0 = results.get(i).SSID;
            String bssid = results.get(i).BSSID;

            int rssi0 = results.get(i).level;
            boolean found = false;
            for (int pos = 0; pos < resultsData.size(); pos++) {
                if (resultsData.get(pos).getRouter().getBSSID().equals(bssid)) {
                    found = true;
                    resultsData.get(pos).values.add(rssi0);
                    break;
                }
            }
            if (!found) {

                ResultData data = new ResultData(new Router(ssid0, bssid));
                data.values.add(rssi0);
                resultsData.add(data);
            }
        }
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                // result.setText("here"+currentCount);
                if (currentCount > readingCount) {
                    returnResults();
                }
            }
        });

    }
    private void returnResults() {
        // TODO Auto-generated method stub

        positionData = new PositionData(currentPositionName);
        for (int length = 0; length < resultsData.size(); length++) {

            int sum = 0;
            for (int l = 0; l < resultsData.get(length).values.size(); l++) {
                sum += resultsData.get(length).values.get(l);

            }
            int average = sum / resultsData.get(length).values.size();

            positionData.addValue(resultsData.get(length).getRouter(), average);
        }
        db.addReadings(building, positionData);
        //((PinAdapter) imageMapView.getAdapter()).pickItem(holder);
        ((PinAdapter) imageMapView.getAdapter()).pickItem(((PinAdapter) imageMapView.getAdapter()).getItemWithName(currentPositionName));
        positionName.setText("Select next pin to calibrate");
    }
}
