package com.example.mhbadmin.Classes.Delete;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mhbadmin.Activities.DashBoardActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;
import static com.example.mhbadmin.Activities.DashBoardActivity.SUB_HALL_COUNTER;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class CDeleteSubHall {

    private Context context = null;

    private String sHallMarquee = null,
            sSubHallDocumentId = null,
            sSubHallObjectId = null;

    private int subHallCounter = 0;

    private boolean deleteSubHallHallFlag = false;

    //FireBase Work

    private List<String> sLGetAddHallImagesDownloadUri = null;

    private ProgressDialog progressDialog = null;

    private SharedPreferences sp = null;

    private String userId = null;

    private FirebaseDatabase firebaseDatabase = null;

    private boolean loopBreakFlag = false;

    public CDeleteSubHall(Context context, boolean deleteSubHallHallFlag, String sSubHallDocumentId,
                          String sSubHallObjectId, List<String> sLGetAddHallImagesDownloadUri) {

        this.context = context;
        this.deleteSubHallHallFlag = deleteSubHallHallFlag;
        this.sSubHallDocumentId = sSubHallDocumentId;
        this.sSubHallObjectId = sSubHallObjectId;
        this.sLGetAddHallImagesDownloadUri = sLGetAddHallImagesDownloadUri;

        progressDialog = new ProgressDialog(context);

        this.sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        if (sp.getString(META_DATA, null) != null)
            this.sHallMarquee = sp.getString(META_DATA, null);

        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        this.firebaseDatabase = FirebaseDatabase.getInstance();

        checkBookings();
    }

    private void checkBookings() {

        progressDialog.setMessage("Checking Bookings...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        final DatabaseReference databaseReference = firebaseDatabase
                .getReference("Accepted Requests")
                .child("User Ids");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        if (loopBreakFlag)
                            break;

                        String sClientId = dataSnapshot1.getKey();
                        assert sClientId != null;
                        databaseReference.child(sClientId)
                                .child(sHallMarquee + " Ids")
                                .child(userId)
                                .child("Sub Hall Ids")
                                .child(sSubHallDocumentId)
                                .child("Timing")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            loopBreakFlag = true;
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
                            if (loopBreakFlag) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "You cannot delete this sub hall because it is reserved", Toast.LENGTH_SHORT).show();
                            } else {
                                deleteSubHallImages();
                            }
                        }
                    }, 4000);
                } else {
                    deleteSubHallImages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteSubHallImages() {

        progressDialog.setMessage("Deleting Sub Hall Images...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        // Delete images
        for (int i = 0; i < sLGetAddHallImagesDownloadUri.size(); i++) {
            getInstance().getReferenceFromUrl(sLGetAddHallImagesDownloadUri.get(i))
                    .delete();
        }

        progressDialog.dismiss();
        deleteSubHallData();
    }

    private void deleteSubHallData() {

        progressDialog.setMessage("Deleting Sub Hall...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseDatabase
                .getReference(sHallMarquee)
                .child(userId)
                .child("Sub Hall info")
                .child(sSubHallDocumentId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteBookingRequests();
                    }
                });
    }

    private void deleteBookingRequests() {

        final DatabaseReference databaseReference = firebaseDatabase
                .getReference("Booking Requests")
                .child("User Ids");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String sClientId = dataSnapshot1.getKey();
                        assert sClientId != null;
                        databaseReference.child(sClientId)
                                .child(sHallMarquee + " Ids")
                                .child(userId)
                                .child("Sub Hall Ids")
                                .child(sSubHallDocumentId)
                                .removeValue();

                        //send notification to user that this hall is deleted whom he requests.
                        updateSubHallCounter();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateSubHallCounter() {

        if (sp.getInt(SUB_HALL_COUNTER, 0) != 0) {
            subHallCounter = sp.getInt(SUB_HALL_COUNTER, 0);

            subHallCounter -= 1;

            firebaseDatabase
                    .getReference(sHallMarquee)
                    .child(userId)
                    .child("Sub Hall Counter")
                    .setValue(Integer.toString(subHallCounter))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            SharedPreferences.Editor editor = sp.edit();

                            //remove Deleted Sub Hall From Shared Preferences

                            editor.remove(sSubHallDocumentId + subHallCounter);
                            editor.remove(sSubHallObjectId + subHallCounter);
                            editor.remove("sSubHallObject" + subHallCounter);

                            if (subHallCounter != 0) {
                                editor.putInt(SUB_HALL_COUNTER, subHallCounter);
                                editor.commit();
                            } else {
                                firebaseDatabase
                                        .getReference(sHallMarquee)
                                        .child(userId)
                                        .child("Sub Hall Counter")
                                        .removeValue();
                            }

                            if (deleteSubHallHallFlag)
                                context.startActivity(new Intent(context, DashBoardActivity.class));

                            progressDialog.dismiss();
                        }
                    });
        } else {

            firebaseDatabase
                    .getReference(sHallMarquee)
                    .child(userId)
                    .child("Sub Hall Counter")
                    .removeValue();
            progressDialog.dismiss();
        }
    }
}