package com.example.mhbadmin.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mhbadmin.AdapterClasses.RequestBookingHistoryAdapter;
import com.example.mhbadmin.Classes.CNetworkConnection;
import com.example.mhbadmin.Classes.GetData.CGetRequestBookingHistoryData;
import com.example.mhbadmin.Classes.Models.FilterLists;
import com.example.mhbadmin.R;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FHistory extends Fragment implements View.OnClickListener, RecyclerView.OnScrollChangeListener {

    private View fragmentView = null;
    private Context context = null;

    private String sFragmentName = null;

    private SwipeRefreshLayout mySwipeRefreshLayout = null;

    private RecyclerView rvHistory = null;
    private List<FilterLists> filterLists = null;

    private Button btnReturnToTop = null;

    private RequestBookingHistoryAdapter requestBookingHistoryAdapter = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_history, container, false);

        connectivity();


        SharedPreferences sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        if (sp.getString("sRequestBookingHistory", null) != null)
            sFragmentName = sp.getString("sRequestBookingHistory", null);

        getDataFromFireBase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.

                        filterLists.clear();
                        requestBookingHistoryAdapter.notifyDataSetChanged();
                        getDataFromFireBase();
                    }
                });

        mySwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //set click and scroll listener
        btnReturnToTop.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rvHistory.setOnScrollChangeListener(this);
        }


        return fragmentView;
    }

    private void connectivity() {

        context = getActivity();

        btnReturnToTop = (Button) fragmentView.findViewById(R.id.btn_return_to_top);

        mySwipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipe_refresh);

        rvHistory = (RecyclerView) fragmentView.findViewById(R.id.rv_hall_marquee_success);

        filterLists = new ArrayList<>();

        requestBookingHistoryAdapter = new RequestBookingHistoryAdapter(context, "History", filterLists);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);

        rvHistory.setLayoutManager(mLayoutManager);
        rvHistory.setAdapter(requestBookingHistoryAdapter);
    }

    private void getDataFromFireBase() {

        //check Internet Connection
        if (!checkInternetConnection()) {
            return;
        }

        CGetRequestBookingHistoryData cGetRequestBookingHistoryData =
                new CGetRequestBookingHistoryData(context, filterLists, requestBookingHistoryAdapter,
                        "-LlaNhwyi4fiQHEgW_zb");

        if (sFragmentName != null)
            cGetRequestBookingHistoryData.getRequestBookingHistoryData(sFragmentName);

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
            if (rvHistory != null)
                rvHistory.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

        if (scrollY > oldScrollY)
            btnReturnToTop.setVisibility(View.VISIBLE);
        else
            btnReturnToTop.setVisibility(View.GONE);
    }
}