package com.example.mhbadmin.Classes.Delete;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mhbadmin.Classes.Models.CSubHallData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import static com.example.mhbadmin.Activities.DashBoardActivity.SUB_HALL_COUNTER;

public class CDeleteEntireHall {

    //FireBase Work

    private Context context = null;

    private SharedPreferences sp = null;

    private ProgressDialog progressDialog = null;

    private FirebaseDatabase firebaseDatabase = null;

    private boolean loopBreakFlag = false;

    private String userId = null;

    private int noOfTabs = 0;

    private String sSubHallDocumentId = null,
            getsSubHallObjectId = null,
            getsSubHallObject = null;

    public CDeleteEntireHall(Context context) {

        this.context = context;

        sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(context);

        firebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        checkFireBaseState();
    }

    private void checkFireBaseState() {

        progressDialog.setMessage("Deleting Sub Halls...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if (sp.getInt(SUB_HALL_COUNTER, 0) != 0)
            noOfTabs = sp.getInt(SUB_HALL_COUNTER, 0);

        checkBookings();
    }

    private void checkBookings() {

        progressDialog.setMessage("Checking Bookings...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        firebaseDatabase
                .getReference("Bookings")
                .child("User Ids")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            checkSubHallBookings();
                        else
                            deleteSubHalls();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void checkSubHallBookings() {

        final DatabaseReference databaseReference = firebaseDatabase
                .getReference("Bookings")
                .child("User Ids");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            if (loopBreakFlag)
                                break;

                            for (int i = 1; i <= noOfTabs; i++) {
                                sSubHallDocumentId = sp.getString("sSubHallDocumentId" + i, null);

                                String sClientId = dataSnapshot1.getKey();
                                assert sClientId != null;
                                databaseReference.child(sClientId)
                                        .child("Hall Ids")
                                        .child(userId)
                                        .child("Sub Hall Ids")
                                        .child(sSubHallDocumentId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists())
                                                    loopBreakFlag = true;
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                        if (loopBreakFlag)
                            Toast.makeText(context, "You cannot delete this hall because it's sub hall is reserved", Toast.LENGTH_SHORT).show();
                        else
                            deleteSubHalls();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void deleteSubHalls() {
        for (int i = 1; i <= noOfTabs; i++) {
            sSubHallDocumentId = sp.getString("sSubHallDocumentId" + i, null);
            getsSubHallObjectId = sp.getString("sSubHallObjectId" + i, null);
            getsSubHallObject = sp.getString("sSubHallObject" + i, null);
            deleteSubHallFromFireBase();
        }

        deleteMainHall();
    }

    private void deleteSubHallFromFireBase() {

        CSubHallData cSubHallData = new Gson().fromJson(getsSubHallObject, CSubHallData.class);

        new CDeleteSubHall(context, true, sSubHallDocumentId, getsSubHallObjectId,
                false, cSubHallData.getsLGetAddHallImagesDownloadUri());

        progressDialog.dismiss();
    }

    private void deleteMainHall() {
        new CDeleteMainHall(context);
    }
}