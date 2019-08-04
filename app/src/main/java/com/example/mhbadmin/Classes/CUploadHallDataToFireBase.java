package com.example.mhbadmin.Classes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.mhbadmin.Activities.AddSubHallActivity;
import com.example.mhbadmin.Activities.DashBoardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class CUploadHallDataToFireBase {

    private CCustomToast cCustomToast = null;

    private SharedPreferences sp = null;

    private ProgressDialog progressDialog = null;
    private Context context = null;
    private StorageReference storageReference = null;
    private Uri uriManagerProfile = null;

    private String sHallMarqueeEmail = null,
            sPassword = null,
            srbHallMarquee = null,
            sManagerProfileName = null,
            sManagerProfileDownloadUri,
            sHallMarqueeName = null,
            sManagerFirstName = null,
            sManagerLastName = null,
            sHallMarqueePhoneNo = null,
            sHallMarqueeCity = null,
            sHallMarqueeLocation = null,
            sSpotLights = null,
            sMusic = null,
            sACHeater = null,
            sParking = null;

    private String userId = null;

    private List<String> sLHallEntranceImagesUri = null,
            sLHallEntranceImageNames = null,
            sLGetHallEntranceImagesDownloadUri = null;

    public CUploadHallDataToFireBase(Context context, Uri uriManagerProfile, String sHallMarqueeEmail,
                                     String sPassword, String srbHallMarquee, String sManagerProfileName,
                                     String sManagerProfileDownloadUri, String sHallMarqueeName,
                                     String sManagerFirstName, String sManagerLastName,
                                     String sHallMarqueePhoneNo, String sHallMarqueeCity,
                                     String sHallMarqueeLocation, String sSpotLights, String sMusic,
                                     String sACHeater, String sParking, List<String> sLHallEntranceImagesUri,
                                     List<String> sLHallEntranceImageNames,
                                     List<String> sLGetHallEntranceImagesDownloadUri) {

        this.context = context;
        this.uriManagerProfile = uriManagerProfile;
        this.sHallMarqueeEmail = sHallMarqueeEmail;
        this.sPassword = sPassword;

        //this sp is used to store srbHallMarquee
        sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        this.srbHallMarquee = srbHallMarquee;

        this.sManagerProfileName = sManagerProfileName;
        this.sManagerProfileDownloadUri = sManagerProfileDownloadUri;
        this.sHallMarqueeName = sHallMarqueeName;
        this.sManagerFirstName = sManagerFirstName;
        this.sManagerLastName = sManagerLastName;
        this.sHallMarqueePhoneNo = sHallMarqueePhoneNo;
        this.sHallMarqueeCity = sHallMarqueeCity;
        this.sHallMarqueeLocation = sHallMarqueeLocation;
        this.sSpotLights = sSpotLights;
        this.sMusic = sMusic;
        this.sACHeater = sACHeater;
        this.sParking = sParking;
        this.sLHallEntranceImagesUri = sLHallEntranceImagesUri;
        this.sLHallEntranceImageNames = sLHallEntranceImageNames;
        this.sLGetHallEntranceImagesDownloadUri = sLGetHallEntranceImagesDownloadUri;

        this.progressDialog = new ProgressDialog(context);
        this.storageReference = getInstance().getReference();

        if (this.sPassword != null) {
            createUserOnFireBase();
        } else {
            this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            uploadDataToFireBase();
        }

        cCustomToast = new CCustomToast();
    }

    private void createUserOnFireBase() {

        progressDialog.setMessage("Creating Admin...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(sHallMarqueeEmail, sPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            userId = task.getResult().getUser().getUid();

                            progressDialog.dismiss();

                            uploadDataToFireBase();

                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                cCustomToast.makeText(context, "Your are already registered");
                                progressDialog.dismiss();
                            } else {
                                cCustomToast.makeText(context, task.getException().getMessage());
                                progressDialog.dismiss();
                            }
                        }
                    }
                });
    }

    private void uploadDataToFireBase() {
        //create storageReference
        storageReference = this.storageReference.child(srbHallMarquee)
                .child(userId)
                .child(srbHallMarquee + " info");

        uploadHallEntranceImages();
    }

    private void uploadHallEntranceImages() {

        progressDialog.setMessage("Uploading Hall Images...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        StorageReference newStorageReference = storageReference.child("Images");

        for (int i = 0; i < (sLHallEntranceImagesUri.size() - 1); i++) {
            //adding addOnSuccessListener to storageReference
            newStorageReference.child(sLHallEntranceImageNames.get(i))
                    .putFile(Uri.parse(sLHallEntranceImagesUri.get(i)))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            final String downloadUri = uriTask.getResult().toString();
                            sLGetHallEntranceImagesDownloadUri.add(downloadUri);
                            if (sLGetHallEntranceImagesDownloadUri.size() == sLHallEntranceImagesUri.size() - 1) {
                                progressDialog.dismiss();
                                uploadManagerProfileImage();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //error. get and show error message
                    cCustomToast.makeText(context, e.getMessage());
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void uploadManagerProfileImage() {

        progressDialog.setMessage("Uploading Manager Profile...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        StorageReference newStorageReference = storageReference.child("Profile");

        //adding addOnSuccessListener to storageReference2nd
        newStorageReference.child(sManagerProfileName)
                .putFile(uriManagerProfile)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        final Uri downloadUri = uriTask.getResult();

                        sManagerProfileDownloadUri = downloadUri.toString();
                        progressDialog.dismiss();

                        uploadData();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //error. get and show error message
                cCustomToast.makeText(context, e.getMessage());
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                        .getTotalByteCount());
                progressDialog.setMessage("Uploaded "+(int)progress+"%");
            }
        });
    }

    private void uploadData() {

        //setting progress bar title
        if (sPassword != null)
            progressDialog.setMessage("Registering...");
        else
            progressDialog.setMessage("Updating...");
        //show progress dialog
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        CSignUpData CSignUpData = new CSignUpData(sHallMarqueeName, sManagerFirstName, sManagerLastName,
                sManagerProfileDownloadUri, sHallMarqueeEmail, sHallMarqueePhoneNo, sHallMarqueeCity, sHallMarqueeLocation, sSpotLights,
                sMusic, sACHeater, sParking, sLGetHallEntranceImagesDownloadUri);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(srbHallMarquee)
                .document(userId)
                .collection(srbHallMarquee + " info")
                .document(srbHallMarquee + " Document")
                .set(CSignUpData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        createMetaData();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //error. get and show error message
                        cCustomToast.makeText(context, e.getMessage());
                        progressDialog.dismiss();
                    }
                });
    }

    private void createMetaData() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("meta data", srbHallMarquee);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Meta Data")
                .document(userId)
                .set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(META_DATA, srbHallMarquee);
                editor.commit();

                intentToAddHallActivity();
            }
        });
    }

    private void intentToAddHallActivity() {
        if (sPassword != null) {
            context.startActivity(new Intent(context, AddSubHallActivity.class));
            ((Activity) context).finish();
        } else {
            context.startActivity(new Intent(context, DashBoardActivity.class));
            ((Activity) context).finish();
        }
    }
}