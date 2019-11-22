package com.example.ventus.networksignal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.List;
import java.util.Random;

public class GraphScreen extends AppCompatActivity {
    private final static String TAG = "GraphScreen";

    Button btnBack;

    private LineChart mChart;
    private Thread thread;
    //private boolean plotData = true;
    private boolean running = true;

    WifiManager wifi;
    WifiScanReceiver wifiReceiver;

    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifi.getScanResults();
            //txtWifiInfo.setText("");
            for(int i = 0; i < wifiScanList.size(); i++){
                //String info = ("SSID: "+(wifiScanList.get(i)).SSID+" "+"RSSI: "+(wifiScanList.get(i)).level+" "+"Częstotliwość: "+(wifiScanList.get(i)).frequency+" "+"Szerokość kanału: "+(wifiScanList.get(i)).channelWidth);
                //String info = ((wifiScanList.get(i)).toString());
                //Log.i(TAG, "Wykryto siec: "+wifiScanList.get(i).BSSID);
                addEntry(wifiScanList.get(i));
                //txtWifiInfo.append(info+"\n\n");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_screen);
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregisterReceiver(GraphScreen.this.wifiReceiver);
                running = false;
                GraphScreen.this.thread.interrupt();
                finish();
            }
        });

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiScanReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.startScan();

        mChart = findViewById(R.id.chart1);

        // enable description text
        mChart.getDescription().setText("Poziom odbieranego sygnału w dBm");
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setTextColor(Color.argb(255,255,255,255));

        // enable touch gestures
        mChart.setTouchEnabled(false);

        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // set an alternative background color
        mChart.setBackgroundColor(Color.BLACK);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setWordWrapEnabled(true);
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(-20f);
        leftAxis.setAxisMinimum(-100f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);

        feedMultiple();

    }

    private void addEntry(ScanResult result) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByLabel(result.SSID+"("+result.BSSID+")", true);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet(result.SSID+"("+result.BSSID+")");
                data.addDataSet(set);
            }
            Log.i(TAG, "przed set entry count: "+Integer.toString(set.getEntryCount()));
            Log.i(TAG, "przed data entry count: "+Integer.toString(data.getEntryCount()));
            Log.i(TAG, "przed max data entry count: "+Integer.toString(data.getMaxEntryCountSet().getEntryCount()));
            Log.i(TAG, "~~~~~~~~~~przed set entry count: "+Integer.toString(set.getEntryCount()));
//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
            if(set.getEntryCount()<data.getMaxEntryCountSet().getEntryCount()){
                data.addEntry(new Entry(data.getMaxEntryCountSet().getEntryCount()-1, result.level), data.getIndexOfDataSet(set));}
            else {data.addEntry(new Entry(set.getEntryCount(), result.level), data.getIndexOfDataSet(set));}
            data.notifyDataChanged();
            Log.i(TAG, "po set entry count: "+Integer.toString(set.getEntryCount()));
            Log.i(TAG, "po data entry count: "+Integer.toString(data.getEntryCount()));
            Log.i(TAG, "po max data entry count: "+Integer.toString(data.getMaxEntryCountSet().getEntryCount()));
            Log.i(TAG, "~~~~~~~~~~po set entry count: "+Integer.toString(set.getEntryCount()));
            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(10);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    private LineDataSet createSet(String label) {

        LineDataSet set = new LineDataSet(null, label);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        set.setColor(color);
        set.setHighlightEnabled(false);
        set.setDrawValues(true);
        int colortext = Color.argb(255,255,255,255);
        set.setValueTextColor(colortext);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (running){
                    //plotData = true;
                    wifi.startScan();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        thread.interrupt();
        super.onDestroy();
    }

}
