package com.example.mhbadmin.AdapterClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mhbadmin.Activities.RequestBookingDetailActivity;
import com.example.mhbadmin.Activities.UserDetailActivity;
import com.example.mhbadmin.Classes.CNetworkConnection;
import com.example.mhbadmin.Classes.Models.CRequestBookingData;
import com.example.mhbadmin.Classes.Models.CUserData;
import com.example.mhbadmin.Classes.Models.FilterLists;
import com.example.mhbadmin.Classes.RequestBooking.CCancelAcceptRequest;
import com.example.mhbadmin.Filters.RequestBookingFilter;
import com.example.mhbadmin.R;
import com.example.mhbadmin.Utils;
import com.google.gson.Gson;

import java.util.List;

public class RequestBookingHistoryAdapter extends RecyclerView.Adapter<RequestBookingHistoryAdapter.MyViewHolder> implements Filterable {

    private Context context = null;
    private String sActivityName = null;
    public List<FilterLists> filterLists = null;

    private RequestBookingFilter requestBookingFilter = null;

    public RequestBookingHistoryAdapter(Context context, String sActivityName, List<FilterLists> filterLists) {
        this.context = context;
        this.sActivityName = sActivityName;
        this.filterLists = filterLists;
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_request_booking, parent, false);
        return new MyViewHolder(v);
    }

    //RETURN FILTER OBJ
    @Override
    public Filter getFilter() {
        if (requestBookingFilter == null) {
            requestBookingFilter = new RequestBookingFilter(filterLists, this);
        }
        return requestBookingFilter;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        FilterLists filterLists1 = filterLists.get(position);

        CUserData cUserData = filterLists1.getcUserDataList();
        CRequestBookingData cRequestBookingData = filterLists1.getcRequestBookingDataList();

        Glide.with(context).load(cUserData.getsUserProfileImageUri()).into(holder.ivUserProfile);
        holder.tvUserName.setText(cUserData.getsUserFirstName() + " " + cUserData.getsUserLastName());
        holder.tvPhoneNo.setText(cUserData.getsPhoneNo());
        holder.tvRequestTimeDate.setText(cRequestBookingData.getsFunctionTiming()
                + " " + cRequestBookingData.getsFunctionDate());

        if (!sActivityName.equals("Booking Requests")) {
            holder.tvAccept.setVisibility(View.GONE);
            holder.ivAccept.setVisibility(View.GONE);
            holder.tvCancel.setVisibility(View.GONE);
            holder.ivCancel.setVisibility(View.GONE);

            holder.tvUserLocation.setVisibility(View.VISIBLE);
            holder.tvUserLocation.setText(cUserData.getsLocation());
        }
    }

    @Override
    public int getItemCount() {
        return filterLists.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivUserProfile = null;
        private TextView tvUserName = null,
                tvPhoneNo = null,
                tvRequestTimeDate = null,
                tvCancel = null,
                tvAccept = null,
        //below textView is hidden
        tvUserLocation = null;

        private ImageView ivAccept = null,
                ivCancel = null;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            connectivity(itemView);

            //setting click listener on cardView
            ivUserProfile.setOnClickListener(this);
            tvUserName.setOnClickListener(this);
            tvPhoneNo.setOnClickListener(this);
            tvRequestTimeDate.setOnClickListener(this);
            tvCancel.setOnClickListener(this);
            tvAccept.setOnClickListener(this);

            tvUserLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intentToNextActivity(getAdapterPosition());
                }
            });
        }

        private void connectivity(View itemView) {
            ivUserProfile = (ImageView) itemView.findViewById(R.id.iv_user_profile);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvPhoneNo = (TextView) itemView.findViewById(R.id.tv_user_phone_no);
            tvRequestTimeDate = (TextView) itemView.findViewById(R.id.tv_booking_request_cancel_accept_time_date);
            tvCancel = (TextView) itemView.findViewById(R.id.tv_cancel);
            tvAccept = (TextView) itemView.findViewById(R.id.tv_accept);
            //below textView is hidden
            tvUserLocation = (TextView) itemView.findViewById(R.id.tv_user_location);

            ivAccept = (ImageView) itemView.findViewById(R.id.iv_accept);
            ivCancel = (ImageView) itemView.findViewById(R.id.iv_cancel);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.iv_user_profile)
                showDetailDialog(getAdapterPosition());
            else if (v.getId() == R.id.tv_user_name)
                intentToNextActivity(getAdapterPosition());
            else if (v.getId() == R.id.tv_user_phone_no)
                intentToNextActivity(getAdapterPosition());
            else if (v.getId() == R.id.tv_booking_request_cancel_accept_time_date)
                intentToNextActivity(getAdapterPosition());
            else if (v.getId() == R.id.tv_cancel) {

                //check Internet Connection
                if (!checkInternetConnection()) {
                    return;
                }

                FilterLists filterLists1 = filterLists.get(getAdapterPosition());

                CRequestBookingData cRequestBookingData = filterLists1.getcRequestBookingDataList();

                new CCancelAcceptRequest(context, true, filterLists1.getcUserDataList().getsUserID(), cRequestBookingData, "Canceled Requests");
            } else if (v.getId() == R.id.tv_accept) {

                //check Internet Connection
                if (!checkInternetConnection()) {
                    return;
                }

                FilterLists filterLists1 = filterLists.get(getAdapterPosition());

                CRequestBookingData cRequestBookingData = filterLists1.getcRequestBookingDataList();

                new CCancelAcceptRequest(context, true, filterLists1.getcUserDataList().getsUserID(), cRequestBookingData, "Accepted Requests");
            } else if (v.getId() == R.id.tv_user_location)
                intentToNextActivity(getAdapterPosition());
        }


        private void showDetailDialog(final int position) {

            //check Internet Connection
            if (!checkInternetConnection()) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            View alertView = LayoutInflater.from(context).inflate(R.layout.layout_detail, null);

            builder.setView(alertView);

            ImageView ivUserProfile = (ImageView) alertView.findViewById(R.id.iv_user_profile);

            ImageButton btnMessage = (ImageButton) alertView.findViewById(R.id.ib_message);
            ImageButton btnCall = (ImageButton) alertView.findViewById(R.id.ib_call);
            ImageButton btnMail = (ImageButton) alertView.findViewById(R.id.ib_mail);
            ImageButton btnInfo = (ImageButton) alertView.findViewById(R.id.ib_info);

            final AlertDialog dialog = builder.create();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            Glide.with(context)
                    .load(filterLists.get(position)
                            .getcUserDataList().getsUserProfileImageUri())
                    .into(ivUserProfile);

            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sMessageBody = "Hello From App";
                    Utils.sms(context, filterLists.get(position).getcUserDataList().getsPhoneNo(), sMessageBody);
                }
            });

            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.dial(context, filterLists.get(position).getcUserDataList().getsPhoneNo());
                }
            });

            btnMail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sMailBody = "Hello From App";
                    Utils.reportUser(context, filterLists.get(position).getcUserDataList().getsEmail(), sMailBody);
                }
            });

            btnInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, UserDetailActivity.class);
                    Bundle bundle = new Bundle();

                    FilterLists filterLists1 = filterLists.get(position);

                    CUserData cUserData = filterLists1.getcUserDataList();

                    bundle.putString("userDetail", new Gson().toJson(cUserData));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
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

        private void intentToNextActivity(int position) {

            FilterLists filterLists1 = filterLists.get(position);

            CRequestBookingData cRequestBookingData = filterLists1.getcRequestBookingDataList();
            CUserData cUserData = filterLists1.getcUserDataList();

            Gson gson = new Gson();

            String requestBookingData = gson.toJson(cRequestBookingData);
            String userData = gson.toJson(cUserData);

            Bundle bundle = new Bundle();

            bundle.putString("sActivityName", sActivityName);
            bundle.putString("userData", userData);
            bundle.putString("requestBookingData", requestBookingData);

            Intent intent = new Intent(context, RequestBookingDetailActivity.class);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }
}