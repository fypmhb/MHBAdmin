package com.example.mhbadmin.Classes.RequestBooking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mhbadmin.Activities.DashBoardActivity;
import com.example.mhbadmin.Classes.Models.CRequestBookingData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;

public class CAcceptRequest {

    private Context context = null;

    private boolean listActivityFlag = false;

    private String sUserId = null,
            sHallId = null,
            sHallMarquee = null;

    private boolean loopBreakFlag = false;

    private CRequestBookingData cRequestBookingData = null;

    private ProgressDialog progressDialog = null;

    private FirebaseDatabase firebaseDatabase = null;

    private SharedPreferences sp = null;

    public CAcceptRequest(Context context, boolean listActivityFlag, String sUserId, CRequestBookingData cRequestBookingData) {
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

//        checkPreviousBookings();

        deleteBookingRequestData();
    }

    private void checkPreviousBookings() {

        final DatabaseReference databaseReference = firebaseDatabase
                .getReference("Accepted Requests")
                .child("User Ids");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        if (loopBreakFlag)
                            break;

                        String sClientId = dataSnapshot1.getKey();

                        assert sClientId != null;
                        final DatabaseReference databaseReference1 = databaseReference
                                .child(sClientId)
                                .child(sHallMarquee + " Ids")
                                .child(sHallId)
                                .child("Sub Hall Ids")
                                .child(cRequestBookingData.getsSubHallId())
                                .child("Timing");

                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {

                                        if (loopBreakFlag)
                                            break;

                                        String sAcceptTiming = dataSnapshot2.getKey();

                                        assert sAcceptTiming != null;
                                        databaseReference1.child(sAcceptTiming)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {

                                                            CRequestBookingData cRequestBookingData1 =
                                                                    dataSnapshot
                                                                            .getValue(CRequestBookingData.class);

                                                            assert cRequestBookingData1 != null;
                                                            if (cRequestBookingData.getsFunctionDate().equals(cRequestBookingData1.getsFunctionDate()) &&
                                                                    cRequestBookingData.getsFunctionTiming().equals(cRequestBookingData1.getsFunctionTiming())) {

                                                                loopBreakFlag = true;
                                                                Toast.makeText(context, "This sub hall is already booked on this timing", Toast.LENGTH_LONG).show();
                                                                progressDialog.dismiss();
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
                    Toast.makeText(context, "" + loopBreakFlag, Toast.LENGTH_SHORT).show();
                    if (!loopBreakFlag)
                        deleteBookingRequestData();
                } else {
                    deleteBookingRequestData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void deleteBookingRequestData() {


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
                        addAcceptedRequestsBookingsDetail();
                    }
                });
    }

    private void addAcceptedRequestsBookingsDetail() {

        String sAcceptanceTiming = Calendar.getInstance().getTime().toString();

        cRequestBookingData.setsAcceptDeniedTiming(sAcceptanceTiming);

        firebaseDatabase.getReference("Accepted Requests")
                .child("User Ids")
                .child(sUserId)
                .child(sHallMarquee + " Ids")
                .child(sHallId)
                .child("Sub Hall Ids")
                .child(cRequestBookingData.getsSubHallId())
                .child("Timing")
                .child(sAcceptanceTiming)
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