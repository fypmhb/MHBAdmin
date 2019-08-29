package com.example.mhbadmin.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.example.mhbadmin.Classes.Models.CRequestBookingData;
import com.example.mhbadmin.Classes.Models.CUserData;
import com.example.mhbadmin.Notification.OfflineNotification.CSendNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SNotification extends Service {


    static int delay = 30000,
            period = 3000;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), delay, period);


        return START_STICKY;
    }

    // timer class for image swipe
    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            getDate();
        }
    }

    private void getDate() {

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        Date date = new Date();

        String sCurrentTime = timeFormat.format(date);

        //if time is 9 O'clock morning send notification
        if (sCurrentTime.equals("10:06")) {

            DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy");
            //only day
            DateFormat dayFormat = new SimpleDateFormat("dd");

            String sDate = dateFormat.format(date);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -1);
            Date newDate = calendar.getTime();

            int day = Integer.parseInt(dayFormat.format(newDate));

            getDataFromFireBase(sDate, day);
        }
    }

    private void getDataFromFireBase(final String sDate, final int day) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Accepted Requests")
                .child("User Ids");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        final String sClient = dataSnapshot1.getKey();

                        assert sClient != null;
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(sClient)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {

                                            final CUserData cUserData = dataSnapshot.getValue(CUserData.class);

                                            final DatabaseReference databaseReference1 = databaseReference
                                                    .child(sClient)
                                                    .child("Hall Ids")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child("Sub Hall Ids");

                                            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.exists()) {

                                                        for (final DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {

                                                            final String sSubHallId = dataSnapshot2.getKey();

                                                            assert sSubHallId != null;
                                                            final DatabaseReference databaseReference2 = databaseReference1
                                                                    .child(sSubHallId)
                                                                    .child("Timing");

                                                            databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.exists()) {

                                                                        for (DataSnapshot dataSnapshot3 : dataSnapshot.getChildren()) {
                                                                            final String sTiming = dataSnapshot3.getKey();

                                                                            assert sTiming != null;
                                                                            databaseReference2.child(sTiming)
                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                            if (dataSnapshot.exists()) {

                                                                                                CRequestBookingData cRequestBookingData = dataSnapshot.getValue(CRequestBookingData.class);

                                                                                                assert cRequestBookingData != null;
                                                                                                String sFunctionDate = cRequestBookingData.getsFunctionDate();

                                                                                                int beforeFunctionDay = 0;
                                                                                                if (sFunctionDate.charAt(1) != '-')
                                                                                                    beforeFunctionDay = Integer.parseInt(sFunctionDate.substring(0, 2)) - 1;
                                                                                                else
                                                                                                    beforeFunctionDay = Integer.parseInt(sFunctionDate.substring(0, 1));


                                                                                                if (cRequestBookingData.getsFunctionDate().equals(sDate) && day == beforeFunctionDay) {

                                                                                                    assert cUserData != null;
                                                                                                    cUserData.setsUserID(sClient);
                                                                                                    cUserData.setsSubHallId(sSubHallId);
                                                                                                    cRequestBookingData.setsAcceptDeniedTiming(sTiming);

                                                                                                    new CSendNotification(SNotification.this, cUserData, cRequestBookingData)
                                                                                                            .sendNotification();
                                                                                                }

                                                                                                if (cRequestBookingData.getsFunctionDate().equals(sDate)) {

                                                                                                    assert cUserData != null;
                                                                                                    cUserData.setsUserID(sClient);
                                                                                                    cUserData.setsSubHallId(sSubHallId);
                                                                                                    cRequestBookingData.setsAcceptDeniedTiming(sTiming);

                                                                                                    new CSendNotification(SNotification.this, cUserData, cRequestBookingData)
                                                                                                            .sendNotification();
                                                                                                }

                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}