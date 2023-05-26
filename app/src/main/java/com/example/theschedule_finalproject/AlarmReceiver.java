package com.example.theschedule_finalproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
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

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            DailyScheduleEdit.allowed = false;
            return;
        }
        else {
            DailyScheduleEdit.allowed = true;
            notificationManagerCompat.notify(123, notificationBuilder.build());
        }
    }
}
