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
import com.example.mhbadmin.Classes.Models.CSubHallData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import static com.example.mhbadmin.Activities.DashBoardActivity.SUB_HALL_COUNTER;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class CUploadSubHallData {

    private Context context = null;

    private CCustomToast cCustomToast = null;

    private CSubHallData cSubHallData = null;

    private boolean intentFlag = false;

    private SharedPreferences sp = null;

    //this int is used to check whether this class
    //is instantiated from addSubHallActivity (=0) or FUpdateSubHAllInfo (=noOfTabs).
    private int noOfTabs = 0;

    //FireBase work
    private String userId = null,
            sHallMarquee = null;

    private String sSubHallDocumentId = null;

    private int addHallCount = 1;

    private List<String> sLAddHallImagesUri = null,
            sLAddHallImageNames = null;

    private ProgressDialog progressDialog = null;

    //storage reference and database reference..
    private StorageReference storageReference = null;

    private FirebaseDatabase firebaseDatabase = null;

    public CUploadSubHallData(Context context, CSubHallData cSubHallData, boolean intentFlag,
                              int noOfTabs, String sSubHallDocumentId, String userId,
                              List<String> sLAddHallImagesUri,
                              List<String> sLAddHallImageNames, String sHallMarquee) {

        this.context = context;
        this.cSubHallData = cSubHallData;
        this.intentFlag = intentFlag;
        this.noOfTabs = noOfTabs;
        this.sSubHallDocumentId = sSubHallDocumentId;
        this.userId = userId;
        this.sLAddHallImagesUri = sLAddHallImagesUri;
        this.sLAddHallImageNames = sLAddHallImageNames;
        this.sHallMarquee = sHallMarquee;

        connectivity();

        fireBaseWork();
    }

    private void connectivity() {
        cCustomToast = new CCustomToast();

        //FireBase Work
        sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(context);

        storageReference = getInstance().getReference();

        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void fireBaseWork() {

        if (sSubHallDocumentId == null) {
            //get Sub Hall Document Id to Store Sub Hall Images and FireStore Data

            DatabaseReference databaseReference = firebaseDatabase
                    .getReference(sHallMarquee)
                    .child(userId)
                    .child("Sub Hall info")
                    .push();

            sSubHallDocumentId = databaseReference.getKey();
        }

        checkFireBaseState();
    }

    private void checkFireBaseState() {
        // this int is used to check whether this class is instantiated
        // from addSubHallActivity (=0 {but =0 is not "Sub Hall Counter})
        // or FUpdateSubHallInfo (=noOfTabs).
        if (noOfTabs != 0) {
            //update data to FireBase.
            addHallCount = noOfTabs;
            uploadDataToFireBase();
        } else
            getSubHallCounter();

    }

    private void getSubHallCounter() {
        if (sp.getInt(SUB_HALL_COUNTER, 0) != 0) {
            addHallCount = sp.getInt(SUB_HALL_COUNTER, 0) + 1;
            uploadDataToFireBase();
        } else
            getSubHallCounterFromFireBase();
    }

    private void getSubHallCounterFromFireBase() {

        final DatabaseReference databaseReference = firebaseDatabase
                .getReference(sHallMarquee)
                .child(userId)
                .child("Sub Hall Counter");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
//                    if (dataSnapshot.getValue() != null)
                    addHallCount = Integer.parseInt(dataSnapshot.getValue(String.class)) + 1;
                }
                uploadDataToFireBase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadDataToFireBase() {
        //create storageReference
        storageReference = this.storageReference.child(sHallMarquee)
                .child(userId)
                .child("Sub Hall info");
        uploadAddHallImages();
    }

    private void uploadAddHallImages() {

        progressDialog.setMessage("Uploading Sub Hall Images...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        final List<String> sLGetAddHallImagesDownloadUri = cSubHallData
                .getsLGetAddHallImagesDownloadUri();

        StorageReference newStorageReference = storageReference
                .child(sSubHallDocumentId)
                .child("Images");

        for (int i = 0; i < (sLAddHallImagesUri.size() - 1); i++) {

            //check previously uploaded images for UpdateHallInfoActivity
            if (!sLAddHallImagesUri.get(i).contains("firebasestorage.googleapis.com")) {

                //adding addOnSuccessListener to storageReference
                newStorageReference.child(sLAddHallImageNames.get(i))
                        .putFile(Uri.parse(sLAddHallImagesUri.get(i)))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful()) ;

                                final String downloadUri = uriTask.getResult().toString();
                                sLGetAddHallImagesDownloadUri.add(downloadUri);

                                if (sLGetAddHallImagesDownloadUri.size() == sLAddHallImagesUri.size() - 1) {
                                    progressDialog.dismiss();
                                    uploadDataFireBase();
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
                sLGetAddHallImagesDownloadUri.add(sLAddHallImagesUri.get(i));

                if (sLGetAddHallImagesDownloadUri.size() == sLAddHallImagesUri.size() - 1) {

                    progressDialog.dismiss();
                    uploadDataFireBase();
                }
            }
        }
    }

    private void uploadDataFireBase() {

        //setting progress bar title
        progressDialog.setMessage("Adding Sub Hall...");
        //show progress dialog
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseDatabase.getReference(sHallMarquee)
                .child(userId)
                .child("Sub Hall info")
                .child(sSubHallDocumentId)
                .setValue(cSubHallData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        if (noOfTabs == 0)
                            addSubHallCounterToFireBase();
                        else {
                            //no need to update sub hall in shared preference because it will
                            //be updated from dash board.
                            intentToNextActivity();
                        }
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

    private void addSubHallCounterToFireBase() {

        firebaseDatabase
                .getReference(sHallMarquee)
                .child(userId)
                .child("Sub Hall Counter")
                .setValue(Integer.toString(addHallCount))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt(SUB_HALL_COUNTER, addHallCount);
                        editor.commit();

                        intentToNextActivity();
                    }
                });
    }

    private void intentToNextActivity() {
        if (intentFlag) {
            context.startActivity(new Intent(context, DashBoardActivity.class));
            ((Activity) context).finish();
        } else {
            context.startActivity(new Intent(context, AddSubHallActivity.class));
            ((Activity) context).finish();
        }
    }
}