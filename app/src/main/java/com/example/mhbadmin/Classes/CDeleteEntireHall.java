package com.example.mhbadmin.Classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;
import static com.example.mhbadmin.Activities.DashBoardActivity.SUB_HALL_COUNTER;

public class CDeleteEntireHall {

    private Context context = null;

    private SharedPreferences sp = null;

    private SharedPreferences.Editor editor = null;

    private ProgressDialog progressDialog = null;

    private CCustomToast cCustomToast = null;

    private FirebaseFirestore firebaseFirestore = null;

    private String userId = null,
            sHallMarquee = null;

    private int noOfTabs = 0;

    private String sSubHallDocumentId = null;

    private CSubHallData cSubHallData = null;

    public CDeleteEntireHall(Context context) {

        this.context = context;

        sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        editor = sp.edit();

        progressDialog = new ProgressDialog(context);

        cCustomToast = new CCustomToast();

        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        checkFireBaseState();
    }

    private void checkFireBaseState() {

        progressDialog.setMessage("Deleting Sub Halls...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if (sp.getString(META_DATA, null) != null)
            this.sHallMarquee = sp.getString(META_DATA, null);


        if (sp.getInt(SUB_HALL_COUNTER, 0) != 0)
            noOfTabs = sp.getInt(SUB_HALL_COUNTER, 0);

        assert sHallMarquee != null;
        firebaseFirestore.collection(sHallMarquee)
                .document(userId)
                .collection("Sub Hall info")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    int i = 1;
                    for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                        editor.putString("sSubHallDocumentId" + i, snapshots.getId());
                        editor.commit();
                        i += 1;
                    }
                }
            }
        });

        deleteSubHalls();
    }

    private void getDataFromFireBaseForSubHall() {

        final DocumentReference documentReference1 = firebaseFirestore
                .collection(sHallMarquee)
                .document(userId)
                .collection("Sub Hall info")
                .document(sSubHallDocumentId);

        documentReference1.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            cSubHallData = documentSnapshot.toObject(CSubHallData.class);
                            assert cSubHallData != null;
                            new CDeleteSubHall(context, false, sSubHallDocumentId, cSubHallData.getsLGetAddHallImagesDownloadUri());
                            progressDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                cCustomToast.makeText(context, e.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void deleteSubHalls() {
        for (int i = 1; i <= noOfTabs; i++) {
            sSubHallDocumentId = sp.getString("sSubHallDocumentId" + i, null);
            getDataFromFireBaseForSubHall();
        }

        deleteMainHall();
    }

    private void deleteMainHall() {
        new CDeleteMainHall(context);
    }

}