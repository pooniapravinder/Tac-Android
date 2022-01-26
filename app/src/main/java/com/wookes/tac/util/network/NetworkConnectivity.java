package com.wookes.tac.util.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnectivity {
    public static boolean a(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE || networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }
}
