package com.example.theschedule_finalproject;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * @author Agam Toledano
 * @version 1.0
 * @since 24/02/2022
 * short description - AlarmReceiver
 */
public class AlarmReceiver extends BroadcastReceiver {
    /**
     * onReceive.
     * Short description - Create an alert.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmReceiver_intent = new Intent(context, DailyScheduleView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, alarmReceiver_intent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "SpringTime")
                .setSmallIcon(R.drawable.baseline_event)
                .setContentTitle("SPRING TIME - REMINDER")
                .setContentText("It's Time For Your Event")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManagerCompat.notify(123, notificationBuilder.build());
    }
}
