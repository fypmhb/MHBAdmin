package com.example.mhbadmin.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mhbadmin.AdapterClasses.RequestBookingAdapter;
import com.example.mhbadmin.Classes.CGetRequestBookingDataFromFireBase;
import com.example.mhbadmin.Classes.CRequestBookingData;
import com.example.mhbadmin.Classes.CUserData;
import com.example.mhbadmin.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.mhbadmin.Fragments.FSubHallMarqueeDetail.S_SUB_HALL_DOCUMENT_ID;

public class FRequestBookingDetail extends Fragment {

    private View fragmentView = null;
    private Context context = null;

    private RequestBookingAdapter requestBookingAdapter = null;
    private List<CRequestBookingData> lRequestBooking = null;
    private List<CUserData> lUserData = null;

    private String sSubHallDocumentId = null;

    public FRequestBookingDetail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sSubHallDocumentId = getArguments().getString(S_SUB_HALL_DOCUMENT_ID);
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_request_booking_detail, container, false);

        connectivity();

        getDataFromFireBase();

        return fragmentView;
    }

    private void connectivity() {

        context = getActivity();

        //RecyclerView
        RecyclerView rvRequestBookingList = (RecyclerView) fragmentView.findViewById(R.id.rv_request_booking_list);
        rvRequestBookingList.setHasFixedSize(true);

        lRequestBooking = new ArrayList<>();
        lUserData = new ArrayList<>();

        requestBookingAdapter = new RequestBookingAdapter(context, lRequestBooking,lUserData);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);

        rvRequestBookingList.setLayoutManager(mLayoutManager);
        rvRequestBookingList.setAdapter(requestBookingAdapter);
    }

    private void getDataFromFireBase() {

        new CGetRequestBookingDataFromFireBase(context, lRequestBooking,lUserData, requestBookingAdapter, sSubHallDocumentId);
    }

    public static FRequestBookingDetail newInstance(String sSubHallDocumentId) {
        FRequestBookingDetail fragment = new FRequestBookingDetail();
        Bundle args = new Bundle();
        args.putString(S_SUB_HALL_DOCUMENT_ID, sSubHallDocumentId);
        fragment.setArguments(args);
        return fragment;
    }

}