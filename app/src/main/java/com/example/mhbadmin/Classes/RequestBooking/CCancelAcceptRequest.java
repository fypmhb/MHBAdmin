package com.example.mhbadmin.Classes.RequestBooking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mhbadmin.Activities.DashBoardActivity;
import com.example.mhbadmin.Classes.Models.CRequestBookingData;
import com.example.mhbadmin.Notification.APIService;
import com.example.mhbadmin.Notification.Sending.Client;
import com.example.mhbadmin.Notification.Sending.Data;
import com.example.mhbadmin.Notification.Sending.MyResponse;
import com.example.mhbadmin.Notification.Sending.Sender;
import com.example.mhbadmin.Notification.Token;
import com.example.mhbadmin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;

public class CCancelAcceptRequest {

    private Context context = null;

    private boolean listActivityFlag = false;

    private String sUserId = null,
            sHallId = null,
            sHallMarquee = null,
            sRefAddress = null;

    private APIService apiService = null;

    private boolean loopBreakFlag = false;

    private CRequestBookingData cRequestBookingData = null;

    private ProgressDialog progressDialog = null;

    private FirebaseDatabase firebaseDatabase = null;

    private SharedPreferences sp = null;

    public CCancelAcceptRequest(Context context, boolean listActivityFlag, String sUserId, CRequestBookingData cRequestBookingData, String sRefAddress) {
        this.context = context;
        this.listActivityFlag = listActivityFlag;
        this.sUserId = sUserId;
        this.cRequestBookingData = cRequestBookingData;
        this.sRefAddress = sRefAddress;

        connectivity();

        checkFireBaseState();
    }

    private void connectivity() {

        apiService = Client.getClient("https://fcm.googleapis.com/")
                .create(APIService.class);

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

        if (sRefAddress.equals("Accepted Requests"))
            checkPreviousBookings();
        else
            deleteBookingRequestData();
    }

    private void checkPreviousBookings() {

        final DatabaseReference databaseReference = firebaseDatabase
                .getReference(sRefAddress)
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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!loopBreakFlag)
                                deleteBookingRequestData();
                        }
                    }, 5000);
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
                        addAcceptedCancelDetail();
                    }
                });
    }

    private void addAcceptedCancelDetail() {

        String sAcceptanceTiming = Calendar.getInstance().getTime().toString();
        cRequestBookingData.setsAcceptDeniedTiming(sAcceptanceTiming);

        firebaseDatabase.getReference(sRefAddress)
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
                        sendNotification();
                    }
                });
    }


    private void sendNotification() {

        firebaseDatabase.getReference("Tokens")
                .child(sHallId)
                .child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            String sToken = dataSnapshot.getValue(String.class);
                            Token token = new Token(sToken);

                            String sTitle = null;
                            if (sRefAddress.equals("Canceled Requests"))
                                sTitle = "Request Denied";
                            else if (sRefAddress.equals("Accepted Requests"))
                                sTitle = "Request Accepted";
                            Data data = new Data(sHallId, R.drawable.ic_add_picture_, sTitle,
                                    cRequestBookingData.getsFunctionTiming() + " " + cRequestBookingData.getsFunctionDate(),
                                    sUserId, cRequestBookingData.getsSubHallId(), new Gson().toJson(userData),
                                    new Gson().toJson(cRequestBookingData));

                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                assert response.body() != null;
                                                if (response.body().success != 1) {
                                                    Toast.makeText(context, "Notification not sent", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, "Notification Sent", Toast.LENGTH_SHORT).show();
                                                    intentToNextActivity();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<MyResponse> call, Throwable t) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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