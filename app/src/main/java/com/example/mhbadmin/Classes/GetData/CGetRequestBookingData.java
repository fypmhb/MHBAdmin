package com.example.mhbadmin.Classes.GetData;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mhbadmin.AdapterClasses.RequestBookingAdapter;
import com.example.mhbadmin.Classes.Models.CRequestBookingData;
import com.example.mhbadmin.Classes.Models.CUserData;
import com.example.mhbadmin.Classes.Models.FilterLists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;

public class CGetRequestBookingData {

    private Context context = null;

    private List<FilterLists> filterLists = null;
    private RequestBookingAdapter requestBookingAdapter = null;

    //FireBase Work
    private ProgressDialog progressDialog = null;

    private FirebaseDatabase firebaseDatabase = null;

    private String sUserId = null,
            sSubHallDocumentId = null,
            sHallMarquee = null;

    public CGetRequestBookingData(Context context, List<FilterLists> filterLists,
                                  RequestBookingAdapter requestBookingAdapter,
                                  String sSubHallDocumentId) {
        this.context = context;
        this.filterLists = filterLists;
        this.requestBookingAdapter = requestBookingAdapter;
        this.sSubHallDocumentId = sSubHallDocumentId;

        connectivity();
    }

    private void connectivity() {

        //FireBase Work
        progressDialog = new ProgressDialog(context);

        firebaseDatabase = FirebaseDatabase.getInstance();
        sUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SharedPreferences sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        if (sp.getString(META_DATA, null) != null)
            sHallMarquee = sp.getString(META_DATA, null);

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

    }

    public void getRequestsData() {

        final DatabaseReference databaseReference = firebaseDatabase
                .getReference("Booking Requests")
                .child("User Ids");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        final String sClientId = dataSnapshot1.getKey();
                        assert sClientId != null;

                        //get User Object
                        firebaseDatabase.getReference("Users")
                                .child(sClientId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {

                                            final CUserData cUserData = dataSnapshot.getValue(CUserData.class);

                                            assert cUserData != null;
                                            cUserData.setsUserID(sClientId);
                                            cUserData.setsSubHallId(sSubHallDocumentId);

                                            final DatabaseReference databaseReference1 = databaseReference.child(sClientId)
                                                    .child(sHallMarquee + " Ids")
                                                    .child(sUserId)
                                                    .child("Sub Hall Ids")
                                                    .child(sSubHallDocumentId);

                                            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {

                                                        for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                                            String sRequestTiming = dataSnapshot2.getKey();

                                                            assert sRequestTiming != null;
                                                            databaseReference1.child(sRequestTiming)
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                            final CRequestBookingData cRequestBookingData =
                                                                                    dataSnapshot.getValue(CRequestBookingData.class);

                                                                            FilterLists filterList = getFilterList(cUserData, cRequestBookingData);

                                                                            filterLists.add(filterList);
                                                                            requestBookingAdapter.notifyDataSetChanged();
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "No record found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getAcceptedRequestsData() {

        final DatabaseReference databaseReference = firebaseDatabase
                .getReference("Accepted Requests")
                .child("User Ids");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        final String sClientId = dataSnapshot1.getKey();

                        //get User Object
                        assert sClientId != null;
                        firebaseDatabase.getReference("Users")
                                .child(sClientId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {

                                            final CUserData cUserData = dataSnapshot.getValue(CUserData.class);

                                            assert cUserData != null;
                                            cUserData.setsUserID(sClientId);
                                            cUserData.setsSubHallId(sSubHallDocumentId);

                                            final DatabaseReference databaseReference1 =
                                                    databaseReference.child(sClientId)
                                                            .child(sHallMarquee + " Ids")
                                                            .child(sUserId)
                                                            .child("Sub Hall Ids")
                                                            .child(sSubHallDocumentId)
                                                            .child("Timing");

                                            databaseReference1
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {

                                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                                                    final String sAcceptTiming = dataSnapshot1.getKey();

                                                                    assert sAcceptTiming != null;
                                                                    databaseReference1.child(sAcceptTiming)
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    if (dataSnapshot.exists()) {

                                                                                        final CRequestBookingData cRequestBookingData =
                                                                                                dataSnapshot.getValue(CRequestBookingData.class);

                                                                                        assert cRequestBookingData != null;
                                                                                        cRequestBookingData.setsAcceptDeniedTiming(sAcceptTiming);

                                                                                        FilterLists filterList = getFilterList(cUserData, cRequestBookingData);

                                                                                        filterLists.add(filterList);
                                                                                        requestBookingAdapter.notifyDataSetChanged();
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "No record found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getSucceedBookingsData() {

        final DatabaseReference databaseReference = firebaseDatabase
                .getReference("Accepted Requests")
                .child("User Ids");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        final String sClientId = dataSnapshot1.getKey();

                        assert sClientId != null;
                        firebaseDatabase.getReference("Users")
                                .child(sClientId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            final CUserData cUserData = dataSnapshot.getValue(CUserData.class);
                                            assert cUserData != null;
                                            cUserData.setsUserID(sClientId);

                                            final DatabaseReference databaseReference1 =
                                                    databaseReference.child(sClientId)
                                                            .child(sHallMarquee + " Ids")
                                                            .child(sUserId)
                                                            .child("Sub Hall Ids");

                                            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {

                                                            sSubHallDocumentId = dataSnapshot2.getKey();

                                                            assert sSubHallDocumentId != null;
                                                            final DatabaseReference databaseReference2 = databaseReference1
                                                                    .child(sSubHallDocumentId)
                                                                    .child("Timing");

                                                            databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.exists()) {
                                                                        for (DataSnapshot dataSnapshot3 : dataSnapshot.getChildren()) {

                                                                            final String sSucceedTiming = dataSnapshot3.getKey();

                                                                            assert sSucceedTiming != null;
                                                                            databaseReference2.child(sSucceedTiming)
                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                            if (dataSnapshot.exists()) {

                                                                                                CRequestBookingData cRequestBookingData = dataSnapshot
                                                                                                        .getValue(CRequestBookingData.class);

                                                                                                assert cRequestBookingData != null;
                                                                                                cRequestBookingData.setsAcceptDeniedTiming(sSucceedTiming);

                                                                                                String sFunctionDate = cRequestBookingData.getsFunctionDate();

                                                                                                Date currentData = new Date();

                                                                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                                                                                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");


                                                                                                try {
                                                                                                    Date functionDate = dateFormat.parse(sFunctionDate);

                                                                                                    Date dayFunctionEndTime = timeFormat.parse("10:23:00");

                                                                                                    Date nightFunctionEndTime = timeFormat.parse("10:24:00");

                                                                                                    String sCurrentTime = timeFormat.format(currentData);

                                                                                                    Date currentTime = timeFormat.parse(sCurrentTime);

                                                                                                    assert functionDate != null;
                                                                                                    if (functionDate.compareTo(currentData) < 0) {

                                                                                                        FilterLists filterList = getFilterList(cUserData, cRequestBookingData);

                                                                                                        filterLists.add(filterList);
                                                                                                        requestBookingAdapter.notifyDataSetChanged();

                                                                                                    } else if (functionDate.compareTo(currentData) == 0) {

                                                                                                        if (cRequestBookingData.getsFunctionTiming().equals("Day") &&
                                                                                                                currentTime.compareTo(dayFunctionEndTime) > 0) {

                                                                                                            FilterLists filterList = getFilterList(cUserData, cRequestBookingData);

                                                                                                            filterLists.add(filterList);
                                                                                                            requestBookingAdapter.notifyDataSetChanged();
                                                                                                            requestBookingAdapter.notifyDataSetChanged();
                                                                                                        } else if (cRequestBookingData.getsFunctionTiming().equals("Night") &&
                                                                                                                currentTime.compareTo(nightFunctionEndTime) > 0) {

                                                                                                            FilterLists filterList = getFilterList(cUserData, cRequestBookingData);

                                                                                                            filterLists.add(filterList);
                                                                                                            requestBookingAdapter.notifyDataSetChanged();
                                                                                                        }

                                                                                                    }

                                                                                                } catch (ParseException e) {
                                                                                                    e.printStackTrace();
                                                                                                }
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                        }
                                                                                    });
                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                    progressDialog.dismiss();
                } else
                    progressDialog.dismiss();
                Toast.makeText(context, "No record found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getDeniedRequestsData() {

        final DatabaseReference databaseReference = firebaseDatabase
                .getReference("Canceled Requests")
                .child("User Ids");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        final String sClientId = dataSnapshot1.getKey();

                        assert sClientId != null;
                        firebaseDatabase.getReference("Users")
                                .child(sClientId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            final CUserData cUserData = dataSnapshot.getValue(CUserData.class);
                                            assert cUserData != null;
                                            cUserData.setsUserID(sClientId);

                                            final DatabaseReference databaseReference1 =
                                                    databaseReference.child(sClientId)
                                                            .child(sHallMarquee + " Ids")
                                                            .child(sUserId)
                                                            .child("Sub Hall Ids");

                                            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {

                                                            sSubHallDocumentId = dataSnapshot2.getKey();

                                                            assert sSubHallDocumentId != null;
                                                            final DatabaseReference databaseReference2 = databaseReference1
                                                                    .child(sSubHallDocumentId)
                                                                    .child("Timing");

                                                            databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.exists()) {
                                                                        for (DataSnapshot dataSnapshot3 : dataSnapshot.getChildren()) {
                                                                            final String sCanceledTiming = dataSnapshot3.getKey();

                                                                            assert sCanceledTiming != null;
                                                                            databaseReference2.child(sCanceledTiming)
                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                            if (dataSnapshot.exists()) {
                                                                                                CRequestBookingData cRequestBookingData = dataSnapshot
                                                                                                        .getValue(CRequestBookingData.class);

                                                                                                assert cRequestBookingData != null;
                                                                                                cRequestBookingData.setsAcceptDeniedTiming(sCanceledTiming);

                                                                                                FilterLists filterList = getFilterList(cUserData, cRequestBookingData);

                                                                                                filterLists.add(filterList);
                                                                                                requestBookingAdapter.notifyDataSetChanged();
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                    progressDialog.dismiss();
                } else
                    progressDialog.dismiss();
                Toast.makeText(context, "No record found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private FilterLists getFilterList(CUserData cUserData, CRequestBookingData cRequestBookingData) {
        return new FilterLists(cUserData.getsUserFirstName(),
                cUserData.getsUserLastName(), cUserData.getsUserProfileImageUri(),
                cUserData.getsEmail(), cUserData.getsPhoneNo(), cUserData.getsCity(),cUserData.getsCountry(),
                cUserData.getsLocation(), cUserData.getsSubHallId(), cUserData.getsUserID(),
                cRequestBookingData.getsRequestTime(), cRequestBookingData.getsSubHallName(),
                cRequestBookingData.getsFunctionDate(), cRequestBookingData.getsNoOfGuests(),
                cRequestBookingData.getsFunctionTiming(), cRequestBookingData.getsDish(),
                cRequestBookingData.getsPerHead(), cRequestBookingData.getsEstimatedBudget(),
                cRequestBookingData.getsOtherDetail(), cRequestBookingData.getsAcceptDeniedTiming());
    }
}