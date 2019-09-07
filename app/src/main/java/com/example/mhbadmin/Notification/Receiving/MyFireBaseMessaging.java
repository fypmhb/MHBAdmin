package com.example.mhbadmin.Notification.Receiving;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.mhbadmin.Activities.RequestBookingDetailActivity;
import com.example.mhbadmin.Classes.Models.CUserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.net.URL;
import java.util.Date;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.example.mhbadmin.Activities.DashBoardActivity.REQUEST_BADGES;

public class MyFireBaseMessaging extends FirebaseMessagingService {

    private SharedPreferences sp = null;

    @Override
    public void onMessageReceived(@NonNull final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        sp = getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        final String receiver = remoteMessage.getData().get("receiver");

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert receiver != null;
        if (firebaseUser != null && receiver.equals(firebaseUser.getUid())) {
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        String userId = remoteMessage.getData().get("userId");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        String sCUserData = remoteMessage.getData().get("cUserData");
        String sCRequestBookingData = remoteMessage.getData().get("cBookingData");

        Gson gson = new Gson();

        CUserData cUserData = gson.fromJson(sCUserData, CUserData.class);

        //for cancel or accept classes
        cUserData.setsUserID(userId);

        sCUserData = gson.toJson(cUserData);

        int j = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        Intent intent = new Intent(this, RequestBookingDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("sActivityName", "Booking Requests");
        bundle.putString("requestBookingData", sCRequestBookingData);
        bundle.putString("userData", sCUserData);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        try {

            URL url = new URL(cUserData.getsUserProfileImageUri());

            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            Bitmap largeIcon = getCircleBitmap(image);

            assert icon != null;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                    .setSmallIcon(Integer.parseInt(icon))
                    .setLargeIcon(largeIcon)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //Notification Badge
            if (sp.getInt(REQUEST_BADGES, 0) != 0) {
                int badge = sp.getInt(REQUEST_BADGES, 0);
                badge += 1;
                createBadge(badge);
            } else {
                createBadge(1);
            }

            assert notificationManager != null;
            notificationManager.notify(j, builder.build());
        } catch (
                Exception e) {
            Toast.makeText(this, "Notification failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void createBadge(int badge) {

        ShortcutBadger.applyCount(this, badge);

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(REQUEST_BADGES, badge);
        editor.commit();
       /* try {
            Badges.setBadge(this, badge);

            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(REQUEST_BADGES, badge);
            editor.commit();
        } catch (BadgesNotSupportedException b) {
            b.printStackTrace();
        }*/
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
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
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }
    }
}