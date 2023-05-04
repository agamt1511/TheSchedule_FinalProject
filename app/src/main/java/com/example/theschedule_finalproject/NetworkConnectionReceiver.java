package com.example.theschedule_finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkConnectionReceiver extends BroadcastReceiver {

    //שליחה לפעולת בדיקת חיבור לאינטרנט והצגת הודעה מתאימה
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