package com.example.mhbadmin.Notification.OfflineNotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mhbadmin.Activities.RequestBookingDetailActivity;
import com.example.mhbadmin.Classes.Models.CRequestBookingData;
import com.example.mhbadmin.Classes.Models.CUserData;
import com.example.mhbadmin.R;
import com.google.gson.Gson;

public class CSendNotification {

    private Context context = null;

    private CUserData cUserData = null;

    private CRequestBookingData cRequestBookingData = null;

    public CSendNotification(Context context, CUserData cUserData, CRequestBookingData cRequestBookingData) {
        this.context = context;
        this.cUserData = cUserData;
        this.cRequestBookingData = cRequestBookingData;
    }

    public void sendNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        Gson gson = new Gson();

        String sCUserData = gson.toJson(cUserData);
        String sCRequestBookingData = gson.toJson(cRequestBookingData);

        String title = cUserData.getsUserFirstName() + " " + cUserData.getsUserLastName();
        String body = "At " + cRequestBookingData.getsFunctionDate() + " a function will be held";
        String userId = cUserData.getsUserID();

        int j = Integer.parseInt(userId.replaceAll("[\\D]", ""));
        Intent intent = new Intent(context, RequestBookingDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("sActivityName", "Accepted Requests");
        bundle.putString("requestBookingData", sCRequestBookingData);
        bundle.putString("userData", sCUserData);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, j, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.ic_add_picture_)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);


        int i = 0;

        if (j > 0) {
            i = j;
        }
        notificationManager.notify(i, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RequestBooking Chanel";
            String description = "My Chanel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }
    }
}