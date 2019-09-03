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

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.example.mhbadmin.Activities.DashBoardActivity.BOOKING_BADGE;
import static com.example.mhbadmin.Activities.DashBoardActivity.REQUEST_BADGES;
import static com.example.mhbadmin.Activities.DashBoardActivity.S_SUB_HALL_DOCUMENT_ID;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FRequestBookingList extends Fragment implements View.OnClickListener, RecyclerView.OnScrollChangeListener {

    private View fragmentView = null;
    private Context context = null;

    private Button btnReturnToTop = null;

    private SharedPreferences sp = null;

    private SwipeRefreshLayout mySwipeRefreshLayout = null;

    private RequestBookingHistoryAdapter requestBookingHistoryAdapter = null;
    private List<FilterLists> filterLists = null;

    private RecyclerView rvRequestBookingList = null;

    private String sSubHallDocumentId = null,
            sRequestBookingHistory = null;

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

        removeBadges();

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
            rvRequestBookingList.setOnScrollChangeListener(this);
        }

        return fragmentView;
    }

    private void connectivity() {

        context = getActivity();

        sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        btnReturnToTop = (Button) fragmentView.findViewById(R.id.btn_return_to_top);

        if (sp.getString("sRequestBookingHistory", null) != null)
            sRequestBookingHistory = sp.getString("sRequestBookingHistory", null);

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

        CGetRequestBookingHistoryData cGetRequestBookingHistoryData = new CGetRequestBookingHistoryData(context,
                filterLists, requestBookingHistoryAdapter, sSubHallDocumentId);

        if (sRequestBookingHistory != null)
            cGetRequestBookingHistoryData.getRequestBookingHistoryData(sRequestBookingHistory);

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

    @Override
    public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

        if (scrollY > oldScrollY)
            btnReturnToTop.setVisibility(View.VISIBLE);
        else
            btnReturnToTop.setVisibility(View.GONE);
    }
}