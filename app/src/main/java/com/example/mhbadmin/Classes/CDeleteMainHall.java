package com.example.mhbadmin.Classes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.mhbadmin.Activities.SplashScreenActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
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

    private FirebaseFirestore firebaseFirestore = null;

    CDeleteMainHall(Context context) {
        this.context = context;

        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        this.sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        if (sp.getString(META_DATA, null) != null) {
            this.sHallMarquee = sp.getString(META_DATA, null);
            checkFireBaseState();
        }
    }

    private void checkFireBaseState() {

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        getDataFromFireBase();
    }

    private void getDataFromFireBase() {
        final DocumentReference documentReference = firebaseFirestore
                .collection(sHallMarquee)
                .document(userId)
                .collection(sHallMarquee + " info")
                .document(sHallMarquee + " Document");

        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            cSignUpData = documentSnapshot.toObject(CSignUpData.class);
                            deleteMainHallImages();
                        }
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
            StorageReference storageReference = getInstance().getReferenceFromUrl(getsLHallEntranceDownloadImagesUri.get(i));
            storageReference.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            deleteProfileImage();
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void deleteProfileImage() {
        progressDialog.setMessage("Deleting Manager Profile Image...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        StorageReference storageReference = getInstance().getReferenceFromUrl(cSignUpData.getsManagerProfileImageUri());
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteHallFromFireStore();
                        progressDialog.dismiss();
                    }
                });
    }

    private void deleteHallFromFireStore() {
        progressDialog.setMessage("Deleting Hall...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        final DocumentReference documentReference = firebaseFirestore
                .collection(sHallMarquee)
                .document(userId)
                .collection("Hall info")
                .document("Hall Document");


        documentReference.delete().
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteMetaData();
                    }
                });
    }

    private void deleteMetaData() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("meta data", FieldValue.delete());

        final DocumentReference documentReference = firebaseFirestore
                .collection("Meta Data")
                .document(userId);

        documentReference.update(hashMap);
        deleteUserId();
    }

    private void deleteUserId() {
        final DocumentReference documentReference = firebaseFirestore
                .collection("Meta Data")
                .document(userId);

        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final DocumentReference documentReference = firebaseFirestore
                        .collection(sHallMarquee)
                        .document(userId);

                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteCurrentUser();
                    }
                });
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
                            FirebaseAuth.getInstance().signOut();
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