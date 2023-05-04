package com.example.theschedule_finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class NetworkConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (!(isConnected(context))){
                Toast.makeText(context, "Please connect to the network to use this application.", Toast.LENGTH_LONG).show();
            }
        }
        catch (NullPointerException nullPointerException){
            nullPointerException.printStackTrace();
        }
    }

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