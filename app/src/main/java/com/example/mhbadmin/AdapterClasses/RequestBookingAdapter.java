package com.example.mhbadmin.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mhbadmin.Activities.RequestBookingDetailActivity;
import com.example.mhbadmin.Classes.CRequestBookingData;
import com.example.mhbadmin.Classes.CUserData;
import com.example.mhbadmin.R;
import com.google.gson.Gson;

import java.util.List;

public class RequestBookingAdapter extends RecyclerView.Adapter<RequestBookingAdapter.MyViewHolder> {

    private Context context = null;
    private List<CRequestBookingData> lRequestBooking = null;
    private List<CUserData> lUserData = null;

    private CUserData cUserData = null;
    private CRequestBookingData cRequestBookingData = null;

    public RequestBookingAdapter(Context context, List<CRequestBookingData> lRequestBooking, List<CUserData> lUserData) {
        this.context = context;
        this.lRequestBooking = lRequestBooking;
        this.lUserData = lUserData;
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_request_booking, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestBookingAdapter.MyViewHolder holder, int position) {

        cRequestBookingData = lRequestBooking.get(position);

        cUserData = lUserData.get(position);

        Glide.with(context).load(cUserData.getsUserProfileImageUri()).into(holder.ivUserProfile);
        holder.tvUserName.setText(cUserData.getsUserFirstName() + " " + cUserData.getsUserLastName());
        holder.tvPhoneNo.setText(cUserData.getsPhoneNo());
        holder.tvRequestTimeDate.setText(cRequestBookingData.getsFunctionTiming()
                + " " + cRequestBookingData.getsFunctionDate());
    }

    @Override
    public int getItemCount() {
        return lRequestBooking.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivUserProfile = null;
        private TextView tvUserName = null,
                tvPhoneNo = null,
                tvRequestTimeDate = null,
                tvCancel = null,
                tvAccept = null;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivUserProfile = (ImageView) itemView.findViewById(R.id.iv_user_profile);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvPhoneNo = (TextView) itemView.findViewById(R.id.tv_user_phone_no);
            tvRequestTimeDate = (TextView) itemView.findViewById(R.id.tv_request_booking_time_date);
            tvCancel = (TextView) itemView.findViewById(R.id.tv_cancel);
            tvAccept = (TextView) itemView.findViewById(R.id.tv_accept);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gson gson = new Gson();

                    String requestBookingData = gson.toJson(cRequestBookingData);
                    String userData = gson.toJson(cUserData);

                    Bundle bundle = new Bundle();
                    bundle.putString("userData", userData);
                    bundle.putString("requestBookingData", requestBookingData);

                    Intent intent = new Intent(context, RequestBookingDetailActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }
    }
}