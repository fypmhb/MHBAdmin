package com.example.mhbadmin.Classes.Models.ObjectBox;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;

import com.example.mhbadmin.BroadcastReceiver.BNotification;

import java.util.Calendar;
import java.util.Date;

public class MHBAdminDB extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //create database object
        DBObjectBox.init(this);

        Calendar notificationTime = Calendar.getInstance();
//        Calendar currentTime = Calendar.getInstance();

        //offline daily notification at 09:00 AM
//        notificationTime.set(Calendar.HOUR_OF_DAY, 9);
//        notificationTime.set(Calendar.MINUTE, 0);
        notificationTime.set(Calendar.SECOND, 10);

       /* if (currentTime.after(notificationTime)) {
            notificationTime.add(Calendar.DATE, 1);
        }*/

        Intent intent = new Intent(this, BNotification.class);

        int j = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, j, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), 10, pendingIntent);
    }
}
