package com.example.mhbadmin.Classes.Models.ObjectBox;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.multidex.MultiDex;

import com.example.mhbadmin.BroadcastReceiver.BRNotification;

import java.util.Calendar;
import java.util.Date;

public class MHBAdminDB extends Application {


//    see in build.gradle (Module:app) implementation 'com.android.support:multidex:1.0.3' and here attachBaseContext
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //create database object
        DBObjectBox.init(this);

        Calendar notificationTime = Calendar.getInstance();
        //Calendar currentTime = Calendar.getInstance();

        //offline daily notification at 09:00 AM
        notificationTime.set(Calendar.HOUR_OF_DAY, 9);
        notificationTime.set(Calendar.MINUTE, 0);
        notificationTime.set(Calendar.SECOND, 0);

       /* if (currentTime.after(notificationTime)) {
            notificationTime.add(Calendar.DATE, 1);
        }*/

        Intent intent = new Intent(this, BRNotification.class);

        int j = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, j, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
