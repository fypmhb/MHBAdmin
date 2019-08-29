package com.example.mhbadmin.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mhbadmin.AdapterClasses.RequestBookingAdapter;
import com.example.mhbadmin.Classes.CNetworkConnection;
import com.example.mhbadmin.Classes.GetData.CGetRequestBookingData;
import com.example.mhbadmin.Classes.Models.FilterLists;
import com.example.mhbadmin.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.mhbadmin.Fragments.FSubHallMarqueeDetail.S_SUB_HALL_DOCUMENT_ID;

public class FRequestBookingList extends Fragment {

    private View fragmentView = null;
    private Context context = null;

    private SwipeRefreshLayout mySwipeRefreshLayout = null;

    private RequestBookingAdapter requestBookingAdapter = null;
    private List<FilterLists> filterLists=null;

    private String sSubHallDocumentId = null,
            sRequestBooking = null;

    public FRequestBookingList() {
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
        fragmentView = inflater.inflate(R.layout.fragment_request_booking_list, container, false);

        connectivity();

        getDataFromFireBase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.

                        filterLists.clear();
                        requestBookingAdapter.notifyDataSetChanged();
                        getDataFromFireBase();
                    }
                });

        mySwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return fragmentView;
    }

    private void connectivity() {

        context = getActivity();

        mySwipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipe_refresh);

        SharedPreferences sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        if (sp.getString("Request Booking", null) != null)
            sRequestBooking = sp.getString("Request Booking", null);

        //RecyclerView
        RecyclerView rvRequestBookingList = (RecyclerView) fragmentView.findViewById(R.id.rv_request_booking_list);
        rvRequestBookingList.setHasFixedSize(true);

        filterLists = new ArrayList<>();

        requestBookingAdapter = new RequestBookingAdapter(context, sRequestBooking, filterLists);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);

        rvRequestBookingList.setLayoutManager(mLayoutManager);
        rvRequestBookingList.setAdapter(requestBookingAdapter);
    }

    private void getDataFromFireBase() {

        //check Internet Connection
        if (!checkInternetConnection()) {
            return;
        }

        CGetRequestBookingData cGetRequestBookingData = new CGetRequestBookingData(context, filterLists, requestBookingAdapter, sSubHallDocumentId);

        if (sRequestBooking.equals("Booking Requests"))
            cGetRequestBookingData.getRequestsData();
        else                                         //else if (sRequestBooking.equals("Accepted Requests"))
            cGetRequestBookingData.getAcceptedRequestsData();

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

    public static FRequestBookingList newInstance(String sSubHallDocumentId) {
        FRequestBookingList fragment = new FRequestBookingList();
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

        super.onCreateOptionsMenu(menu,inflater);
    }

    private void getData(CharSequence s)
    {
        //FILTER AS YOU TYPE
        try {
            requestBookingAdapter.getFilter().filter(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}