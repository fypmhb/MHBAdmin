package com.example.mhbadmin.Classes.Upload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.mhbadmin.Activities.AddSubHallActivity;
import com.example.mhbadmin.Activities.DashBoardActivity;
import com.example.mhbadmin.Classes.CCustomToast;
import com.example.mhbadmin.Classes.Models.CSignUpData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class CUploadSignUpData {

    private CCustomToast cCustomToast = null;

    private CSignUpData cSignUpData = null;

    private SharedPreferences sp = null;

    private String sHallMarqueeEmail = null;

    private ProgressDialog progressDialog = null;
    private Context context = null;
    private StorageReference storageReference = null;
    private Uri uriManagerProfile = null;
    private FirebaseDatabase firebaseDatabase;

    private String sPassword = null,
            srbHallMarquee = null,
            sManagerProfileName = null,
            sManagerProfileDownloadUri = null;

    private String userId = null;

    private List<String> sLHallEntranceImagesUri = null,
            sLHallEntranceImageNames = null,
            sLGetHallEntranceImagesDownloadUri = null;

    public CUploadSignUpData(Context context, CSignUpData cSignUpData, Uri uriManagerProfile,
                             String sPassword, String srbHallMarquee, String sManagerProfileName,
                             List<String> sLHallEntranceImagesUri,
                             List<String> sLHallEntranceImageNames) {

        this.context = context;
        this.cSignUpData = cSignUpData;
        this.uriManagerProfile = uriManagerProfile;
        this.sPassword = sPassword;

        //this sp is used to store srbHallMarquee
        sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        this.srbHallMarquee = srbHallMarquee;
        this.sHallMarqueeEmail = cSignUpData.getsEmail();
        this.sManagerProfileName = sManagerProfileName;
        this.sLHallEntranceImagesUri = sLHallEntranceImagesUri;
        this.sLHallEntranceImageNames = sLHallEntranceImageNames;
        this.sLGetHallEntranceImagesDownloadUri = new ArrayList<>();

        this.progressDialog = new ProgressDialog(context);
        this.storageReference = getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();

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

            //check previously uploaded images for UpdateHallInfoActivity
            if (!sLHallEntranceImagesUri.get(i).contains("firebasestorage.googleapis.com")) {

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
                                    hallEntrancesImagesUploaded();
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
            } else {
                sLGetHallEntranceImagesDownloadUri.add(sLHallEntranceImagesUri.get(i));

                if (sLGetHallEntranceImagesDownloadUri.size() == sLHallEntranceImagesUri.size() - 1) {

                    hallEntrancesImagesUploaded();
                }
            }
        }
    }

    private void hallEntrancesImagesUploaded() {

        cSignUpData.setsLHallEntranceDownloadImagesUri(sLGetHallEntranceImagesDownloadUri);

        progressDialog.dismiss();
        uploadManagerProfileImage();
    }

    private void uploadManagerProfileImage() {

        progressDialog.setMessage("Uploading Manager Profile...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        //check previously uploaded images for UpdateHallInfoActivity
        if (!sManagerProfileName.equals("mProfile")) {

            storageReference.child("Profile")
                    .child(sManagerProfileName)
                    .putFile(uriManagerProfile)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                            while (!uriTask.isSuccessful()) ;

                            final Uri downloadUri = uriTask.getResult();
                            sManagerProfileDownloadUri = downloadUri.toString();

                            managerProfileUploaded();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //error. get and show error message
                    cCustomToast.makeText(context, e.getMessage());
                    progressDialog.dismiss();
                }
            });
        } else {
            sManagerProfileDownloadUri = uriManagerProfile.toString();
            managerProfileUploaded();
        }
    }

    private void managerProfileUploaded() {
        cSignUpData.setsManagerProfileImageUri(sManagerProfileDownloadUri);
        progressDialog.dismiss();

        uploadData();
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


        firebaseDatabase.getReference(srbHallMarquee)
                .child(userId)
                .child(srbHallMarquee + " info")
                .setValue(cSignUpData)
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
        firebaseDatabase
                .getReference("Meta Data")
                .child(userId)
                .child("meta data")
                .setValue(srbHallMarquee)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
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