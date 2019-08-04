package com.example.mhbadmin.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mhbadmin.R;

public class FSuccess extends Fragment {

    private View fragmentView = null;
    private Context context = null;
    private RecyclerView rvSuccess;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_success, container, false);

        connectivity();

        return fragmentView;
    }

    private void connectivity() {
        context = getActivity();

        rvSuccess = (RecyclerView) fragmentView.findViewById(R.id.rv_hall_marquee_success);
    }
}