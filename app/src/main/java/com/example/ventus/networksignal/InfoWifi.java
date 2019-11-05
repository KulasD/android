package com.example.ventus.networksignal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;

import de.nitri.gauge.Gauge;

public class InfoWifi extends AppCompatActivity {

    String wifiName;
    String wifiShortName;
    WifiManager wifi;
    WifiScanReceiver wifiReceiver;
    TextView txtWifi;
    Boolean showOnce = true;
    //public static final int TYPE_WIFI = 1;
    ConnectivityManager connManager;
    NetworkInfo mWifi;
    Gauge gauge;
    int curValue;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_wifi);
        txtWifi = findViewById(R.id.infoWifi);
        Intent intent = getIntent();
        wifiName = intent.getStringExtra("WIFI");
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiScanReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        wifi.startScan();
        gauge = (Gauge) findViewById(R.id.gauge);
        curValue = 0;
        gauge.setValue(curValue);
    }
    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifi.getScanResults();
            for(int i = 0; i < wifiScanList.size(); i++){
                //String info = ("SSID: "+(wifiScanList.get(i)).SSID+" "+"RSSI: "+(wifiScanList.get(i)).level+" "+"Częstotliwość: "+(wifiScanList.get(i)).frequency+" "+"Szerokość kanału: "+(wifiScanList.get(i)).channelWidth);
                String info = ((wifiScanList.get(i)).SSID+" ("+(wifiScanList.get(i)).BSSID+")");
                if (info.equals(wifiName)&&showOnce) {
                    txtWifi.setText("");
                    txtWifi.append("Nazwa sieci: "+(wifiScanList.get(i)).SSID+"\n");
                    wifiShortName = wifiScanList.get(i).SSID;
                    txtWifi.append("Adres Mac nadajnika: "+(wifiScanList.get(i)).BSSID+"\n");
                    txtWifi.append("RSSI: "+(wifiScanList.get(i)).level+"\n");
                    curValue = (wifiScanList.get(i)).level;
                    gauge.setValue(curValue);
                    txtWifi.append("Częstotliwość: "+(wifiScanList.get(i)).frequency+"MHz\n");
                    String width = Integer.toString(wifiScanList.get(i).channelWidth*20);
                    txtWifi.append("Szerokość kanału: "+width+"MHz\n");
                    String security;
                    if((wifiScanList.get(i)).capabilities.equals("[ESS]")){
                        security = "brak";
                    }
                    else{
                        security = (wifiScanList.get(i)).capabilities;
                    }
                    txtWifi.append("Zabezpieczenia: "+security+"\n");
                    if (mWifi.isConnected()) {
                        showMore();
                    }
                    //showOnce = false;
                }
            }
            wifi.startScan();
        }
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(InfoWifi.this.wifiReceiver);
        super.onDestroy();
    }
    public void showMore(){
        WifiInfo connection = wifi.getConnectionInfo();
        String tempName = connection.getSSID();
        tempName = tempName.substring(1, tempName.length()-1);
        if (wifiShortName.equals(tempName)) {
            txtWifi.append("Adres IP urządzenia: " + intToIp(wifi.getDhcpInfo().ipAddress) + "\n");
            txtWifi.append("Adres IP nadajnika: " + intToIp(wifi.getDhcpInfo().gateway) + "\n");
            txtWifi.append("Maska sieci: " + intToIp(wifi.getDhcpInfo().netmask) + "\n");
            txtWifi.append("DNS 1: " + intToIp(wifi.getDhcpInfo().dns1) + "\n");
            txtWifi.append("DNS 2: " + intToIp(wifi.getDhcpInfo().dns2) + "\n");
            txtWifi.append("Prędkość: " + wifi.getConnectionInfo().getLinkSpeed() + "Mb/s\n");
        }
    }
    public String intToIp(int addr) {
        return  ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }
}
