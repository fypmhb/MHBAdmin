package com.example.mhbadmin.Classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.mhbadmin.Activities.DashBoardActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;
import static com.example.mhbadmin.Activities.DashBoardActivity.SUB_HALL_COUNTER;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class CDeleteSubHall {

    private Context context = null;
    private String sHallMarquee = null;
    private String sSubHallDocumentId = null;

    private int subHallCounter = 0;

    private boolean deleteSubHallHall = false;

    //FireBase Work

    private List<String> sLGetAddHallImagesDownloadUri = null;

    private ProgressDialog progressDialog = null;

    private SharedPreferences sp = null;

    private String userId = null;

    private FirebaseFirestore firebaseFirestore = null;

    public CDeleteSubHall(Context context, boolean deleteSubHallHall, String sSubHallDocumentId, List<String> sLGetAddHallImagesDownloadUri) {

        this.context = context;
        this.deleteSubHallHall = deleteSubHallHall;
        this.sSubHallDocumentId = sSubHallDocumentId;
        this.sLGetAddHallImagesDownloadUri = sLGetAddHallImagesDownloadUri;

        progressDialog = new ProgressDialog(context);

        this.sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);
        if (sp.getString(META_DATA, null) != null)
            this.sHallMarquee = sp.getString(META_DATA, null);

        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        this.firebaseFirestore = FirebaseFirestore.getInstance();

        deleteImagesFromFireBase();
    }

    private void deleteImagesFromFireBase() {

        progressDialog.setMessage("Deleting Sub Hall Images...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        // Delete images
        for (int i = 0; i < sLGetAddHallImagesDownloadUri.size(); i++) {
            StorageReference storageReference = getInstance().getReferenceFromUrl(sLGetAddHallImagesDownloadUri.get(i));
            storageReference.delete();
        }
        deleteSubHallFromFireStore();
        progressDialog.dismiss();
    }

    private void deleteSubHallFromFireStore() {
        progressDialog.setMessage("Deleting Sub Hall...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        final DocumentReference documentReference = firebaseFirestore
                .collection(sHallMarquee)
                .document(userId)
                .collection("Sub Hall info")
                .document(sSubHallDocumentId);

        documentReference.delete().
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateSubHallCounter();
                    }
                });
    }

    private void updateSubHallCounter() {

        if (sp.getInt(SUB_HALL_COUNTER, 0) != 0) {
            subHallCounter = sp.getInt(SUB_HALL_COUNTER, 0);

            subHallCounter -= 1;

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("Sub Hall Counter", Integer.toString(subHallCounter));

            firebaseFirestore.collection(sHallMarquee)
                    .document(userId)
                    .set(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            SharedPreferences.Editor editor = sp.edit();

                            //remove Deleted Sub Hall From
                            editor.remove(sSubHallDocumentId + subHallCounter);

                            editor.putInt(SUB_HALL_COUNTER, subHallCounter);
                            editor.commit();

                            if (deleteSubHallHall)
                                context.startActivity(new Intent(context, DashBoardActivity.class));

                            progressDialog.dismiss();
                        }
                    });
        } else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Sub Hall Counter", FieldValue.delete());

            firebaseFirestore.collection(sHallMarquee)
                    .document(userId)
                    .update(hashMap);
            progressDialog.dismiss();
        }
    }
}