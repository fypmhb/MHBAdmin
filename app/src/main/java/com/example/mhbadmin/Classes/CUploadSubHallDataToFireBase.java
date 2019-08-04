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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;

import static com.example.mhbadmin.Activities.DashBoardActivity.SUB_HALL_COUNTER;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class CUploadSubHallDataToFireBase {

    private Context context = null;

    private CCustomToast cCustomToast = null;

    private boolean intentFlag = false;

    private SharedPreferences sp = null;

    //this int is used to check whether this class
    //is instantiated from addSubHallActivity (=0) or FUpdateSubHAllInfo (=noOfTabs).
    private int noOfTabs = 0;

    private String sSubHallName = null,
            sSubHallFloorNo = null,
            sSubHallCapacity = null,
            sChickenRate = null,
            sMuttonRate = null,
            sBeefRate = null,
            srbSweetDish = null,
            srbSalad = null,
            srbDrink = null,
            srbNan = null,
            srbRise = null;

    //FireBase work
    private String userId = null,
            sHallMarquee = null;

    private String sSubHallDocumentId = null;

    private int addHallCount = 1;

    private List<String> sLAddHallImagesUri = null,
            sLAddHallImageNames = null,
            sLGetAddHallImagesDownloadUri = null;


    private ProgressDialog progressDialog = null;

    //storage reference and database reference..
    private StorageReference storageReference = null;
    private FirebaseFirestore firebaseFirestore = null;

    public CUploadSubHallDataToFireBase(Context context, boolean intentFlag, int noOfTabs, String sSubHallName,
                                        String sSubHallFloorNo, String sSubHallCapacity, String sChickenRate,
                                        String sMuttonRate, String sBeefRate, String srbSweetDish,
                                        String srbSalad, String srbDrink, String srbNan, String srbRise,
                                        String userId, List<String> sLAddHallImagesUri,
                                        List<String> sLAddHallImageNames,
                                        List<String> sLGetAddHallImagesDownloadUri, String sHallMarquee) {

        this.context = context;
        this.intentFlag = intentFlag;
        this.noOfTabs = noOfTabs;
        this.sSubHallName = sSubHallName;
        this.sSubHallFloorNo = sSubHallFloorNo;
        this.sSubHallCapacity = sSubHallCapacity;
        this.sChickenRate = sChickenRate;
        this.sMuttonRate = sMuttonRate;
        this.sBeefRate = sBeefRate;
        this.srbSweetDish = srbSweetDish;
        this.srbSalad = srbSalad;
        this.srbDrink = srbDrink;
        this.srbNan = srbNan;
        this.srbRise = srbRise;
        this.userId = userId;
        this.sLAddHallImagesUri = sLAddHallImagesUri;
        this.sLAddHallImageNames = sLAddHallImageNames;
        this.sLGetAddHallImagesDownloadUri = sLGetAddHallImagesDownloadUri;
        this.sHallMarquee = sHallMarquee;

        fireBaseWork();
    }

    private void fireBaseWork() {

        //FireBase Work
        sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(context);

        storageReference = getInstance().getReference();

        firebaseFirestore = FirebaseFirestore.getInstance();

        cCustomToast = new CCustomToast();

        //get Sub Hall Document Id to Store Sub Hall Images and FireStore Data
        DocumentReference documentReference = firebaseFirestore.collection(sHallMarquee)
                .document(userId)
                .collection("Sub Hall info")
                .document();

        sSubHallDocumentId = documentReference.getId();

        checkFireBaseState();
    }

    private void checkFireBaseState() {
        // this int is used to check whether this class is instantiated
        // from addSubHallActivity (=0 {but =0 is not "Sub Hall Counter})
        // or FUpdateSubHallInfo (=noOfTabs).
        if (noOfTabs != 0) {
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
        DocumentReference documentReference = firebaseFirestore
                .collection(sHallMarquee)
                .document(userId);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        if (document.getString("Sub Hall Counter") != null)
                            addHallCount = Integer.parseInt(document.getString("Sub Hall Counter")) + 1;
                    }
                    uploadDataToFireBase();
                }
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

        StorageReference newStorageReference = storageReference
                .child(sSubHallDocumentId)
                .child("Images");

        for (int i = 0; i < (sLAddHallImagesUri.size() - 1); i++) {
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
                                uploadDataFireStore();
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

    private void uploadDataFireStore() {

        //setting progress bar title
        progressDialog.setMessage("Adding Sub Hall...");
        //show progress dialog
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        CSubHallData CSubHallData = null;

        if (!sSubHallFloorNo.equals("0")) {
            CSubHallData = new CSubHallData(sSubHallName, sSubHallFloorNo, sSubHallCapacity, sChickenRate,
                    sMuttonRate, sBeefRate, srbSweetDish, srbSalad, srbDrink, srbNan, srbRise,
                    sLGetAddHallImagesDownloadUri);
        } else {
            CSubHallData = new CSubHallData(sSubHallName, sSubHallCapacity, sChickenRate, sMuttonRate, sBeefRate,
                    srbSweetDish, srbSalad, srbDrink, srbNan, srbRise, sLGetAddHallImagesDownloadUri);
        }

        firebaseFirestore.collection(sHallMarquee)
                .document(userId)
                .collection("Sub Hall info")
                .document(sSubHallDocumentId)
                .set(CSubHallData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        if (noOfTabs == 0)
                            addSubHallCounterToFireBase();
                        else
                            intentToNextActivity();

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

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Sub Hall Counter", Integer.toString(addHallCount));

        firebaseFirestore.collection(sHallMarquee)
                .document(userId)
                .set(hashMap)
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