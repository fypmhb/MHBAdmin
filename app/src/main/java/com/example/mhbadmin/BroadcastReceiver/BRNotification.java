package com.example.mhbadmin.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mhbadmin.Classes.Models.CRequestBookingData;
import com.example.mhbadmin.Classes.Models.CUserData;
import com.example.mhbadmin.Classes.Models.ObjectBox.DBBookings;
import com.example.mhbadmin.Classes.Models.ObjectBox.DBBookings_;
import com.example.mhbadmin.Classes.Models.ObjectBox.DBObjectBox;
import com.example.mhbadmin.Notification.OfflineNotification.CSendNotification;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;

public class BRNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Date currentDate = new Date();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        String sDate = dateFormat.format(currentDate);

        //offLine dataBase
        Box<DBBookings> bookingsBox = DBObjectBox.getBoxStore()
                .boxFor(DBBookings.class);

        List<DBBookings> dbBookingsList = bookingsBox.query()
                .equal(DBBookings_.sFunctionDate, sDate)
                .build()
                .find();

        for (int i = 0; i < dbBookingsList.size(); i++) {

            Gson gson = new Gson();

            String sUserData = dbBookingsList.get(i).UserData;
            String sRequestBookingData = dbBookingsList.get(i).RequestBooking;

            CUserData cUserData = gson.fromJson(sUserData, CUserData.class);
            CRequestBookingData cRequestBookingData = gson.fromJson(sRequestBookingData, CRequestBookingData.class);

            new CSendNotification(context, cUserData, cRequestBookingData)
                    .sendNotification(i);
        }
    }
}