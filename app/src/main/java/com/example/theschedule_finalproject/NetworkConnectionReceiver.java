package com.example.theschedule_finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AlertDialog;

/**
 * @author Agam Toledano
 * @version 1.0
 * @since 13/12/2022
 * short description - Network Connection Receiver
 */
public class NetworkConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * Sending to the Internet connection test operation and displaying an appropriate message.
         * <p>
         */
        try {
            if (!(isConnected(context))){
                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                adb.setTitle("No Network Connection");
                adb.setMessage("Please connect to the network.\nWithout a network connection the application will not work properly.");
                AlertDialog ad = adb.create();
                ad.show();
            }
        }
        catch (NullPointerException nullPointerException){
            nullPointerException.printStackTrace();
        }
    }

    /**
     * Is connected boolean.
     * Checking internet connection and sending boolean value as reply.
     * @param context the context
     * @return the boolean
     * <p>
     */
    public boolean isConnected(Context context){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo =connectivityManager.getActiveNetworkInfo();
            return (networkInfo!=null && networkInfo.isConnected());
        }
        catch (NullPointerException nullPointerException){
            nullPointerException.printStackTrace();
            return false;
        }
    }

}