package com.example.mhbadmin.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mhbadmin.AdapterClasses.RequestBookingHistoryAdapter;
import com.example.mhbadmin.Classes.CNetworkConnection;
import com.example.mhbadmin.Classes.GetData.CGetRequestBookingHistoryData;
import com.example.mhbadmin.Classes.Models.CRequestBookingData;
import com.example.mhbadmin.Classes.Models.FilterLists;
import com.example.mhbadmin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.example.mhbadmin.Activities.DashBoardActivity.BOOKING_BADGE;
import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;
import static com.example.mhbadmin.Activities.DashBoardActivity.REQUEST_BADGES;
import static com.example.mhbadmin.Activities.DashBoardActivity.S_SUB_HALL_DOCUMENT_ID;

public class FRequestBookingHistoryList extends Fragment implements View.OnClickListener{

    private View fragmentView = null;
    private Context context = null;

    private Button btnReturnToTop = null;

    private SharedPreferences sp = null;

    private SwipeRefreshLayout mySwipeRefreshLayout = null;

    private RequestBookingHistoryAdapter requestBookingHistoryAdapter = null,
            historyAcceptedRequestsAdapter = null;

    private List<FilterLists> filterLists = null;

    private RecyclerView rvRequestBookingList = null;

    private CRequestBookingData cRequestBookingData = null;

    private String sSubHallDocumentId = null,
            sRequestBookingHistory = null,
            sHistoryAcceptedRequests = null,
            sHallMarquee = null,
            sHallId = null,
            sClient = null;

    private ProgressDialog progressDialog = null;

    public FRequestBookingHistoryList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            sSubHallDocumentId = getArguments().getString(S_SUB_HALL_DOCUMENT_ID);
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_request_booking_history_list, container, false);

        connectivity();

        if (sRequestBookingHistory.equals("Booking Requests")) {
            removeBadges();
            deleteOldRequests();
        } else {
            removeBadges();
            getDataFromFireBase();
        }

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        if (sRequestBookingHistory.equals("Booking Requests")) {
                            filterLists.clear();

                            requestBookingHistoryAdapter.notifyDataSetChanged();
                            deleteOldRequests();
                        } else {
                            filterLists.clear();

                            if (sHistoryAcceptedRequests != null) {
                                historyAcceptedRequestsAdapter.notifyDataSetChanged();
                            }

                            requestBookingHistoryAdapter.notifyDataSetChanged();
                            getDataFromFireBase();
                        }
                    }
                });

        mySwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //set click and scroll listener
        btnReturnToTop.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rvRequestBookingList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY)
                        btnReturnToTop.setVisibility(View.VISIBLE);
                    else
                        btnReturnToTop.setVisibility(View.GONE);
                }
            });
        }

        return fragmentView;
    }

    private void connectivity() {

        context = getActivity();

        sHallId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //FireBase Work
        progressDialog = new ProgressDialog(context);

        assert context != null;
        sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        if (sp.getString(META_DATA, null) != null)
            sHallMarquee = sp.getString(META_DATA, null);

        btnReturnToTop = (Button) fragmentView.findViewById(R.id.btn_return_to_top);

        if (sp.getString("sRequestBookingHistory", null) != null)
            sRequestBookingHistory = sp.getString("sRequestBookingHistory", null);

        if (sp.getString("sHistoryAcceptedRequests", null) != null)
            sHistoryAcceptedRequests = sp.getString("sHistoryAcceptedRequests", null);

        mySwipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipe_refresh);

        //RecyclerView
        rvRequestBookingList = (RecyclerView) fragmentView.findViewById(R.id.rv_request_booking_list);
        rvRequestBookingList.setHasFixedSize(true);

        filterLists = new ArrayList<>();

        requestBookingHistoryAdapter = new RequestBookingHistoryAdapter(context, sRequestBookingHistory, filterLists);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);

        rvRequestBookingList.setLayoutManager(mLayoutManager);
        rvRequestBookingList.setAdapter(requestBookingHistoryAdapter);
    }

    private void deleteOldRequests() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference(sRequestBookingHistory)
                .child("User Ids");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        sClient = dataSnapshot1.getKey();

                        assert sClient != null;
                        final DatabaseReference databaseReference1 = databaseReference
                                .child(sClient)
                                .child(sHallMarquee + " Ids")
                                .child(sHallId)
                                .child("Sub Hall Ids")
                                .child(sSubHallDocumentId)
                                .child("Timing");

                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {

                                    for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {

                                        String sTiming = dataSnapshot2.getKey();

                                        assert sTiming != null;
                                        final DatabaseReference databaseReference2 = databaseReference1
                                                .child(sTiming);

                                        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.exists()) {

                                                    cRequestBookingData = dataSnapshot.getValue(CRequestBookingData.class);

                                                    Date currentData = new Date();

                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                                                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

                                                    assert cRequestBookingData != null;
                                                    String sFunctionDate = cRequestBookingData.getsFunctionDate();

                                                    try {
                                                        Date functionDate = dateFormat.parse(sFunctionDate);

                                                        Date dayFunctionEndTime = timeFormat.parse("09:00:00");

                                                        Date nightFunctionEndTime = timeFormat.parse("15:00:00");

                                                        String sCurrentTime = timeFormat.format(currentData);

                                                        Date currentTime = timeFormat.parse(sCurrentTime);

                                                        assert functionDate != null;
                                                        if (currentData.compareTo(functionDate) > 0 &&
                                                                !sFunctionDate.equals(dateFormat.format(currentData))) {

                                                            databaseReference2.removeValue()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            addCanceledDetail();
                                                                        }
                                                                    });
                                                        }
                                                        if (sFunctionDate.equals(dateFormat.format(currentData))) {

                                                            assert dayFunctionEndTime != null;
                                                            if (cRequestBookingData.getsFunctionTiming().equals("Day")
                                                                    && dayFunctionEndTime.compareTo(currentTime) < 0) {

                                                                databaseReference2.removeValue()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                addCanceledDetail();
                                                                            }
                                                                        });

                                                            } else {
                                                                assert nightFunctionEndTime != null;
                                                                if (cRequestBookingData.getsFunctionTiming().equals("Night")
                                                                        && nightFunctionEndTime.compareTo(currentTime) < 0) {

                                                                    databaseReference2.removeValue()
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    addCanceledDetail();
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        }

                                                    } catch (Exception e) {
                                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                } else {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            getDataFromFireBase();
                                        }
                                    }, 5000);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            getDataFromFireBase();
                        }
                    }, 5000);
                } else {
                    progressDialog.dismiss();
                    getDataFromFireBase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addCanceledDetail() {

        String sCancelTiming = Calendar.getInstance().getTime().toString();
        cRequestBookingData.setsAcceptDeniedTiming(sCancelTiming);
        cRequestBookingData.setsSubHallId(sSubHallDocumentId);

        FirebaseDatabase.getInstance()
                .getReference("Canceled Requests")
                .child("User Ids")
                .child(sClient)
                .child(sHallMarquee + " Ids")
                .child(sHallId)
                .child("Sub Hall Ids")
                .child(sSubHallDocumentId)
                .child("Timing")
                .child(sCancelTiming)
                .setValue(cRequestBookingData);
    }

    private void removeBadges() {

        SharedPreferences.Editor editor = sp.edit();
        if (sRequestBookingHistory.equals("Booking Requests")) {

            editor.remove(REQUEST_BADGES);
            editor.commit();

            ShortcutBadger.removeCount(context);

            if (sp.getInt(BOOKING_BADGE, 0) != 0) {
                int badge = sp.getInt(BOOKING_BADGE, 0);
                ShortcutBadger.applyCount(context, badge);
            }
            /*
            try {
                Badges.removeBadge(context);

                editor.remove(REQUEST_BADGES);
                editor.commit();

                if (sp.getInt(BOOKING_BADGE, 0) != 0) {
                    int badge = sp.getInt(BOOKING_BADGE, 0);
                    Badges.setBadge(context, badge);
                }
            } catch (BadgesNotSupportedException e) {
                e.printStackTrace();
            }*/
        } else if (sRequestBookingHistory.equals("Accepted Requests")) {

            editor.remove(BOOKING_BADGE);
            editor.commit();

            ShortcutBadger.removeCount(context);

            if (sp.getInt(REQUEST_BADGES, 0) != 0) {
                int badge = sp.getInt(REQUEST_BADGES, 0);
                ShortcutBadger.applyCount(context, badge);
            }
            /*try {
                Badges.removeBadge(context);

                editor.remove(BOOKING_BADGE);
                editor.commit();

                if (sp.getInt(REQUEST_BADGES, 0) != 0) {
                    int badge = sp.getInt(REQUEST_BADGES, 0);
                    Badges.setBadge(context, badge);
                }

            } catch (BadgesNotSupportedException e) {
                e.printStackTrace();
            }*/
        }
    }

    private void getDataFromFireBase() {

        //check Internet Connection
        if (!checkInternetConnection()) {
            return;
        }

        if (sHistoryAcceptedRequests != null) {

            historyAcceptedRequestsAdapter = new RequestBookingHistoryAdapter(context, sHistoryAcceptedRequests, filterLists);

            CGetRequestBookingHistoryData cGetRequestBookingHistoryData = new CGetRequestBookingHistoryData(context,
                    filterLists, historyAcceptedRequestsAdapter, sSubHallDocumentId);

            cGetRequestBookingHistoryData.getHistoryAcceptedRequestsData("Accepted Requests");
        }

        if (sRequestBookingHistory != null) {
            CGetRequestBookingHistoryData cGetRequestBookingHistoryData = new CGetRequestBookingHistoryData(context,
                    filterLists, requestBookingHistoryAdapter, sSubHallDocumentId);

            cGetRequestBookingHistoryData.getRequestBookingDeniedData(sRequestBookingHistory);
        }

        mySwipeRefreshLayout.setRefreshing(false);
    }

    private boolean checkInternetConnection() {
        // if there is no internet connection
        CNetworkConnection CNetworkConnection = new CNetworkConnection();
        if (CNetworkConnection.isConnected(context)) {
            CNetworkConnection.buildDialog(context).show();
            return false;
        }
        return true;
    }

    public static FRequestBookingHistoryList newInstance(String sSubHallDocumentId) {
        FRequestBookingHistoryList fragment = new FRequestBookingHistoryList();
        Bundle args = new Bundle();
        args.putString(S_SUB_HALL_DOCUMENT_ID, sSubHallDocumentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.search_bar, menu);

        MenuItem item = menu.findItem(R.id.search_action);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // filter as you type
                getData(newText);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void getData(CharSequence s) {
        //FILTER AS YOU TYPE
        try {
            requestBookingHistoryAdapter.getFilter().filter(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_return_to_top) {
            if (rvRequestBookingList != null)
                rvRequestBookingList.smoothScrollToPosition(0);
        }
    }
}