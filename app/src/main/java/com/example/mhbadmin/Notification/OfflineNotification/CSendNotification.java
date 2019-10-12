package com.example.mhbadmin.Notification.OfflineNotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.example.mhbadmin.Activities.RequestBookingDetailActivity;
import com.example.mhbadmin.Classes.Models.CRequestBookingData;
import com.example.mhbadmin.Classes.Models.CUserData;
import com.example.mhbadmin.R;
import com.google.gson.Gson;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.example.mhbadmin.Activities.DashBoardActivity.BOOKING_BADGE;

public class CSendNotification {

    private Context context = null;

    private CUserData cUserData = null;

    private CRequestBookingData cRequestBookingData = null;

    private SharedPreferences sp = null;


    public CSendNotification(Context context, CUserData cUserData, CRequestBookingData cRequestBookingData) {
        this.context = context;
        this.cUserData = cUserData;
        this.cRequestBookingData = cRequestBookingData;

        this.sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);
    }

    public void sendNotification(int j) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        Gson gson = new Gson();

        String sCUserData = gson.toJson(cUserData);
        String sCRequestBookingData = gson.toJson(cRequestBookingData);

        String title = cUserData.getsUserFirstName() + " " + cUserData.getsUserLastName();

        String body = "Today at " + cRequestBookingData.getsFunctionDate() + " " +
                cRequestBookingData.getsFunctionTiming() + " a function will be held";

        Intent intent = new Intent(context, RequestBookingDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("sActivityName", "Accepted Requests");
        bundle.putString("requestBookingData", sCRequestBookingData);
        bundle.putString("userData", sCUserData);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, j, intent,
                PendingIntent.FLAG_ONE_SHOT);

//PendingIntent pendingIntent = PendingIntent.getActivity(context, j, intent,
//              PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.ic_add_picture_)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Notification Badge
        if (sp.getInt(BOOKING_BADGE, 0) != 0) {
            int badge = sp.getInt(BOOKING_BADGE, 0);
            badge += 1;
            createBadge(badge);
        } else {
            createBadge(1);
        }

        assert notificationManager != null;
        notificationManager.notify(j, builder.build());
    }

    private void createBadge(int badge) {

        ShortcutBadger.applyCount(context, badge);

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(BOOKING_BADGE, badge);
        editor.commit();

        /*try {
            Badges.setBadge(context, badge);

            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(BOOKING_BADGE, badge);
            editor.commit();
        } catch (BadgesNotSupportedException b) {
            Toast.makeText(context, "CSendNotification sorry", Toast.LENGTH_SHORT).show();
            b.printStackTrace();
        }*/
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
