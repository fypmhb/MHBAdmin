package com.example.mhbadmin.Classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.mhbadmin.AdapterClasses.RequestBookingAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;

public class CGetRequestBookingDataFromFireBase {

    private Context context = null;

    private List<CRequestBookingData> lRequestBooking = null;
    private List<CUserData> lUserData = null;
    private RequestBookingAdapter requestBookingAdapter = null;

    //FireBase Work
    private SharedPreferences sp = null;

    private ProgressDialog progressDialog = null;

    private FirebaseFirestore firebaseFirestore = null;

    private String sUserId = null,
            sSubHallDocumentId = null,
            sHallMarquee = null;

    public CGetRequestBookingDataFromFireBase(Context context, List<CRequestBookingData> lRequestBooking,
                                              List<CUserData> lUserData,
                                              RequestBookingAdapter requestBookingAdapter, String sSubHallDocumentId) {
        this.context = context;
        this.lRequestBooking = lRequestBooking;
        this.lUserData = lUserData;
        this.requestBookingAdapter = requestBookingAdapter;
        this.sSubHallDocumentId = sSubHallDocumentId;

        connectivity();

        getDateFromFireBase();
    }

    private void connectivity() {

        //FireBase Work
        progressDialog = new ProgressDialog(context);

        firebaseFirestore = FirebaseFirestore.getInstance();
        sUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        if (sp.getString(META_DATA, null) != null)
            sHallMarquee = sp.getString(META_DATA, null);

    }

    private void getDateFromFireBase() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        final CollectionReference collectionReference = firebaseFirestore.collection("Booking Requests")
                .document(sHallMarquee)
                .collection(sUserId)
                .document("Sub Halls")
                .collection(sSubHallDocumentId);

        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final QuerySnapshot document = task.getResult();
                            assert document != null;
                            if (!document.isEmpty()) {
                                for (DocumentSnapshot documentSnapshot : document) {
                                    final String sClientId = documentSnapshot.getId();
                                    collectionReference.document(sClientId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot documentSnapshot1 = task.getResult();
                                                        final CRequestBookingData cRequestBookingData =
                                                                documentSnapshot1.toObject(CRequestBookingData.class);
                                                        firebaseFirestore.collection("Users")
                                                                .document(sClientId)
                                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                                if (documentSnapshot.exists()) {
                                                                    CUserData cUserData = documentSnapshot.toObject(CUserData.class);
                                                                    cUserData.setsUserID(sClientId);
                                                                    lRequestBooking.add(cRequestBookingData);
                                                                    lUserData.add(cUserData);
                                                                    requestBookingAdapter.notifyDataSetChanged();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                                progressDialog.dismiss();
                            } else {
                                //why this is executing?
                                progressDialog.dismiss();
                            }
                        }
                    }
                });
    }

}