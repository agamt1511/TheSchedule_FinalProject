package com.example.theschedule_finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class NetworkConnectionReceiver extends BroadcastReceiver {

    //שליחה לפעולת בדיקת חיבור לאינטרנט והצגת הודעה מתאימה
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (!(isConnected(context))){
                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                adb.setTitle("No Network Connection");
                adb.setMessage("Connect to the network to use this app.");
                AlertDialog ad = adb.create();
                ad.show();
            }
        }
        catch (NullPointerException nullPointerException){
            nullPointerException.printStackTrace();
        }
    }

    //בדיקת חיבור לאינטרנט ושליחת ערך בוליאני כתשובה
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