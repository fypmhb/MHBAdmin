package com.example.mhbadmin;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

public class Utils extends Application {

    public void onCreate() {
        super.onCreate();
    }

    public static void copyText(Context context, String data) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", data);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied...", Toast.LENGTH_SHORT).show();
    }

    public static void reportUser(Context context, String sMailId, String sMailBody) {
        //call email share method
        String gMailPackage = "com.google.android.gm";
        // return true if gMail is installed
        boolean isGMailInstalled = isAppInstalled(context, gMailPackage);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, sMailId);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Report: " + sMailBody);
        if (isGMailInstalled) {
            intent.setType("text/html");
            intent.setPackage(gMailPackage);
            context.startActivity(intent);
        } else {
            // allow user to choose a different app to send email with
            intent.setType("message/rfc822");
            context.startActivity(Intent.createChooser(intent, "choose an email client"));
        }
    }

    public static void sendEmail(Context context, String hisEmail) {
        //call email share method
        String gMailPackage = "com.google.android.gm";
        // return true if gmail is installed
        boolean isGMailInstalled = isAppInstalled(context, gMailPackage);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{hisEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        if (isGMailInstalled) {
            intent.setType("text/html");
            intent.setPackage(gMailPackage);
            context.startActivity(intent);
        } else {
            // allow user to choose a different app to send email with
            intent.setType("message/rfc822");
            context.startActivity(Intent.createChooser(intent, "choose an email client"));
        }
    }


    // Method to check if app is installed
    private static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void dial(Context context, String number) {

        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + number));

        if (callIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(callIntent);
        }
    }

    public static void sms(Context context, String number, String sMessage) {

        Intent messageIntent = new Intent();
        messageIntent.setType("text/plain");
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("address", number);
        messageIntent.putExtra(Intent.EXTRA_TEXT, sMessage);

        // Verify that the intent will resolve to an activity
        if (messageIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(messageIntent);
        }
    }
}