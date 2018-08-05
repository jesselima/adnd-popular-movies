package com.udacity.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Objects;

public class NetworkUtils {

    // Private empty constructor
    private NetworkUtils() {
    }

    public static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    /**
     * This method when called it verify the connection status. If the device is connected return true, otherwise return false.
     *
     * @return a boolean true if there is internet connection.
     */
    public static boolean isDeviceConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}

