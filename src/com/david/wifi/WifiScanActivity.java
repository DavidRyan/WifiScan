package com.david.wifi;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;

 public class WifiScanActivity extends Activity {
    
    private final static String TAG = "DEBUG";

    private static final int SCAN_BARCODE = 0;
    private static final int QR_REQUEST = 1;
    private static final String WIFI_PREFIX = "@@";

    Button mScanButton;
    TextView mNetworkName;
    TextView mNetworkStatus;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mNetworkName = (TextView) findViewById(R.id.network_name); 
        mNetworkStatus = (TextView) findViewById(R.id.network_status); 
        mScanButton = (Button) findViewById(R.id.scan_button);
        mScanButton.setOnClickListener(listener);

    }

    private void launchQrScanner() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage("com.google.zxing.client.android");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, SCAN_BARCODE);
    } 

    public void onActivityResult(int aRequestCode, int aResultCode, Intent aIntent) {
        if (aRequestCode == SCAN_BARCODE) {
            if (aResultCode == RESULT_OK) {

                String contents = aIntent.getStringExtra("SCAN_RESULT");
                parseResult(contents);
            }
        } 
    }

    private void parseResult(String aResult) {
        
        if(aResult.startsWith(WIFI_PREFIX)) {
            String subString = aResult.substring(3);
            Log.d(TAG, subString);
            String[] split = subString.split(":");       
            Log.d(TAG, split[0]);
            Log.d(TAG, split[1]);

            mNetworkName.setText("Network: " + split[0]);
            saveInformationToWireless(split);

        } else {
            float sz = 20;
            mNetworkName.setTextSize(sz);
            mNetworkName.setText("QR Code Invalid"); 
        }
    }

    private void saveInformationToWireless(String[] aInfo) {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wc = new WifiConfiguration(); 
        wc.SSID = "\"" + aInfo[0] + "\"";
        wc.hiddenSSID = false;
        wc.status = WifiConfiguration.Status.DISABLED;     
        wc.priority = 40;
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN); 
        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

        wc.preSharedKey = "\"" + aInfo[1] + "\"";
        wc.wepTxKeyIndex = 0;

        WifiManager  wifiManag = (WifiManager) this.getSystemService(WIFI_SERVICE);
        boolean res1 = wifiManag.setWifiEnabled(true);
        int res = wifi.addNetwork(wc);
        Log.d(TAG, "add Network returned " + res );
        boolean es = wifi.saveConfiguration();
        Log.d(TAG, "saveConfiguration returned " + es );
        boolean b = wifi.enableNetwork(res, true);   
        Log.d(TAG, "enableNetwork returned " + b ); 
        if(b) { 
            mNetworkStatus.setText("Status: Connected");
        } else {
            mNetworkStatus.setText("Status: Unable to connect.");
        }
    }

    public OnClickListener listener = new OnClickListener() {
        public void onClick(View aView) {
            switch (aView.getId()) {
                case R.id.scan_button:
                    launchQrScanner();
                    break;
                default:
                    break;
            }
        }
    };
}

