package com.vpnmaster.vpnmasterinc.Activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Utility {

    private static final String TAG = "Utility";

    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            return nInfo != null && nInfo.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}


