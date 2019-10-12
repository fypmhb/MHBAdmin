package com.example.mhbadmin.Classes.Delete;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.mhbadmin.Activities.SplashScreenActivity;
import com.example.mhbadmin.Classes.Models.CSignUpData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

class CDeleteMainHall {

    private Context context = null;
    private String sHallMarquee = null;

    //FireBase Work
    private ProgressDialog progressDialog = null;

    private SharedPreferences sp = null;

    private CSignUpData cSignUpData = null;

    private String userId = null;

    private FirebaseDatabase firebaseDatabase = null;

    CDeleteMainHall(Context context) {
        this.context = context;

        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        this.sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        if (sp.getString(META_DATA, null) != null) {
            this.sHallMarquee = sp.getString(META_DATA, null);

            getDataFromFireBase();
        }
    }

    private void getDataFromFireBase() {

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseDatabase.getReference(sHallMarquee)
                .child(userId)
                .child(sHallMarquee + " info")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            cSignUpData = dataSnapshot.getValue(CSignUpData.class);
                            deleteMainHallImages();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void deleteMainHallImages() {

        progressDialog.setMessage("Deleting Hall Entrance Images...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        List<String> getsLHallEntranceDownloadImagesUri = cSignUpData.getsLHallEntranceDownloadImagesUri();
        // Delete images
        for (int i = 0; i < getsLHallEntranceDownloadImagesUri.size(); i++) {
            getInstance().getReferenceFromUrl(getsLHallEntranceDownloadImagesUri.get(i))
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            deleteProfileImage();
                        }
                    });
        }
    }

    private void deleteProfileImage() {

        progressDialog.setMessage("Deleting Manager Profile Image...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        getInstance().getReferenceFromUrl(cSignUpData.getsManagerProfileImageUri())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        progressDialog.dismiss();

                        deleteHall();
                    }
                });
    }

    private void deleteHall() {

        progressDialog.setMessage("Deleting Hall...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseDatabase.getReference(sHallMarquee)
                .child(userId)
                .child(sHallMarquee + " info")
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteSubHallCounter();
                    }
                });
    }

    private void deleteSubHallCounter() {
        firebaseDatabase
                .getReference(sHallMarquee)
                .child(userId)
                .child("Sub Hall Counter")
                .removeValue();

        deleteMetaData();
    }

    private void deleteMetaData() {

        firebaseDatabase.getReference("Meta Data")
                .child(userId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteUsersToken();
                    }
                });
    }

    private void deleteUsersToken() {

        firebaseDatabase
                .getReference("Tokens")
                .child(userId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteHallsFavourite();
                    }
                });
    }

    private void deleteHallsFavourite() {
        final DatabaseReference databaseReference = firebaseDatabase
                .getReference("Favourites")
                .child("User Ids");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        String sClientId = dataSnapshot1.getKey();

                        assert sClientId != null;
                        databaseReference.child(sClientId)
                                .child(sHallMarquee+" Ids")
                                .child(userId)
                                .removeValue();
                    }
                    deleteCurrentUser();
                } else {
                    deleteCurrentUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteCurrentUser() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        user.delete().
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.clear();
                            editor.commit();
                            context.startActivity(new Intent(context, SplashScreenActivity.class));
                            ((Activity) context).finish();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}