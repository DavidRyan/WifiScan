package com.david.wifi;

import android.app.Activity;
import android.os.Bundle;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

 public class WifiScanActivity extends Activity {
    
    private final static String TAG = "DEBUG";

    private static final int SCAN_BARCODE = 0;
    private static final int QR_REQUEST = 1;
    private static final String WIFI_PREFIX = "@@";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        launchQrScanner();
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
            //Log.d(TAG, subString);
            String[] split = subString.split(":");
            Log.d(TAG, split[0]);
            Log.d(TAG, split[1]);
            split[0] = "Cam loves anal beads, as well";
            split[1] = "Crookshanks";

            saveInformationToWireless(split);

        } else {
            //toast it failed
        }
    }

    private void saveInformationToWireless(String[] aInfo) {

        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wc = new WifiConfiguration(); 
        wc.SSID = "\"" + Info[0] + "\"";
        wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.DISABLED;     
        wc.priority = 40;
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN); 
        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

        wc.wepKeys[0] = "\"" + aInfo[1] + "\"";
        wc.wepTxKeyIndex = 0;

        WifiManager  wifiManag = (WifiManager) this.getSystemService(WIFI_SERVICE);
        boolean res1 = wifiManag.setWifiEnabled(true);
        int res = wifi.addNetwork(wc);
        Log.d(TAG, "add Network returned " + res );
        boolean es = wifi.saveConfiguration();
        Log.d(TAG, "saveConfiguration returned " + es );
        boolean b = wifi.enableNetwork(res, true);   
        Log.d(TAG, "enableNetwork returned " + b );  
    }
}

