package com.example.mhbadmin.Classes.RequestBooking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.mhbadmin.Activities.DashBoardActivity;
import com.example.mhbadmin.Classes.Models.CRequestBookingData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;

public class CCancelRequest {

    private Context context = null;

    private boolean listActivityFlag = false;

    private String sUserId = null,
            sHallId = null,
            sHallMarquee = null;

    private CRequestBookingData cRequestBookingData = null;

    private ProgressDialog progressDialog = null;

    private FirebaseDatabase firebaseDatabase = null;

    private SharedPreferences sp = null;

    public CCancelRequest(Context context, boolean listActivityFlag, String sUserId, CRequestBookingData cRequestBookingData) {

        this.context = context;
        this.listActivityFlag = listActivityFlag;
        this.sUserId = sUserId;
        this.cRequestBookingData = cRequestBookingData;

        connectivity();

        checkFireBaseState();
    }

    private void connectivity() {

        sHallId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        progressDialog = new ProgressDialog(context);

        firebaseDatabase = FirebaseDatabase.getInstance();
        sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);
    }

    private void checkFireBaseState() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if (sp.getString(META_DATA, null) != null)
            sHallMarquee = sp.getString(META_DATA, null);

        deleteBookingRequestData();
    }

    private void deleteBookingRequestData() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseDatabase.getReference("Booking Requests")
                .child("User Ids")
                .child(sUserId)
                .child(sHallMarquee + " Ids")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Sub Hall Ids")
                .child(cRequestBookingData.getsSubHallId())
                .child("Timing")
                .child(cRequestBookingData.getsFunctionDate() + " " + cRequestBookingData.getsFunctionTiming())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        addCanceledRequestsDetail();
                    }
                });
    }

    private void addCanceledRequestsDetail() {

        String sCancellationTiming = Calendar.getInstance().getTime().toString();
        cRequestBookingData.setsAcceptDeniedTiming(sCancellationTiming);

        firebaseDatabase.getReference("Canceled Requests")
                .child("User Ids")
                .child(sUserId)
                .child(sHallMarquee + " Ids")
                .child(sHallId)
                .child("Sub Hall Ids")
                .child(cRequestBookingData.getsSubHallId())
                .child("Timing")
                .child(sCancellationTiming)
                .setValue(cRequestBookingData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        intentToNextActivity();
                    }
                });
    }

    private void intentToNextActivity() {

        if (listActivityFlag) {
            ((Activity) context).recreate();
            progressDialog.dismiss();
        } else {
            context.startActivity(new Intent(context, DashBoardActivity.class));
            ((Activity) context).finish();
            progressDialog.dismiss();
        }
    }
}