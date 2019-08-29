package com.example.mhbadmin.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mhbadmin.Classes.CNetworkConnection;
import com.example.mhbadmin.Classes.Models.CRequestBookingData;
import com.example.mhbadmin.Classes.Models.CUserData;
import com.example.mhbadmin.Classes.RequestBooking.CAcceptRequest;
import com.example.mhbadmin.Classes.RequestBooking.CCancelRequest;
import com.example.mhbadmin.R;
import com.example.mhbadmin.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

public class RequestBookingDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private CRequestBookingData cRequestBookingData = null;

    private CUserData cUserData = null;

    private TextView tvBookingRequestCancelAcceptTiming = null,
            tvBookingRequestCancelAcceptTimeDate = null,
            tvUserName = null,
            tvPhoneNo = null,
            tvUserEmail = null,
            tvCity = null,
            tvLocation = null,
            tvSubHallName = null,
            tvFunctionTiming = null,
            tvFunctionDate = null,
            tvNoOfGuests = null,
            tvDish = null,
            tvEstimatedBudget = null,
            tvBookingRequestTiming = null,
            tvBookingRequestTimeDate = null,
            tvOtherDetail = null,
            tvAccept = null,
            tvCancel = null;

    private FloatingActionButton fabMessage = null,
            fabCall = null;

    private String sActivityName = null;

    private ImageView ivUserProfile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_booking_detail);

        connectivity();

        getDataFromRequestBookingListActivity();

        showDataToView();

        //setting click listeners on buttons
        fabMessage.setOnClickListener(this);
        fabCall.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvAccept.setOnClickListener(this);
    }

    private void connectivity() {

        tvBookingRequestCancelAcceptTiming = (TextView) findViewById(R.id.tv_booking_request_cancel_accept_timing);
        tvBookingRequestCancelAcceptTimeDate = (TextView) findViewById(R.id.tv_booking_request_cancel_accept_time_date);

        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvPhoneNo = (TextView) findViewById(R.id.tv_user_phone_no);
        tvUserEmail = (TextView) findViewById(R.id.tv_user_email);
        tvCity = (TextView) findViewById(R.id.tv_user_city);
        tvLocation = (TextView) findViewById(R.id.tv_user_location);

        tvSubHallName = (TextView) findViewById(R.id.tv_sub_hall_name);
        tvFunctionTiming = (TextView) findViewById(R.id.tv_function_timing);
        tvFunctionDate = (TextView) findViewById(R.id.tv_function_date);
        tvNoOfGuests = (TextView) findViewById(R.id.tv_no_of_guests);
        tvDish = (TextView) findViewById(R.id.tv_dish);
        tvEstimatedBudget = (TextView) findViewById(R.id.tv_estimated_budget);
        //below both are hidden for request detail and shown for accept, denied and success details.
        tvBookingRequestTiming = (TextView) findViewById(R.id.tv_booking_request_timing);
        tvBookingRequestTimeDate = (TextView) findViewById(R.id.tv_booking_request_time_date);
        tvOtherDetail = (TextView) findViewById(R.id.tv_other_detail);

        tvAccept = (TextView) findViewById(R.id.tv_accept);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);

        fabMessage = (FloatingActionButton) findViewById(R.id.fab_message);
        fabCall = (FloatingActionButton) findViewById(R.id.fab_call);

        ivUserProfile = (ImageView) findViewById(R.id.iv_user_profile);
    }

    private void getDataFromRequestBookingListActivity() {

        Gson gson = new Gson();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sActivityName = bundle.getString("sActivityName");
            cRequestBookingData = gson.fromJson(bundle.getString("requestBookingData"), CRequestBookingData.class);
            cUserData = gson.fromJson(bundle.getString("userData"), CUserData.class);
        }
    }

    private void showDataToView() {

        Glide.with(getApplicationContext())
                .load(cUserData.getsUserProfileImageUri())
                .into(ivUserProfile);

        switch (sActivityName) {
            case "Booking Requests":
                tvBookingRequestCancelAcceptTimeDate.setText(cRequestBookingData.getsRequestTime());
                break;
            case "Accepted Requests":
                tvBookingRequestCancelAcceptTiming.setText("Acceptance Timing");

                setVisibilityOfTextViews();
                break;
            case "Denied Bookings":
                tvBookingRequestCancelAcceptTiming.setText("Denied Timing");

                setVisibilityOfTextViews();
                break;
            case "Succeed Bookings":
                tvBookingRequestCancelAcceptTiming.setText("Success Timing");

                setVisibilityOfTextViews();
                break;
        }

        tvUserName.setText(cUserData.getsUserFirstName() + " " + cUserData.getsUserLastName());
        tvPhoneNo.setText(cUserData.getsPhoneNo());
        tvUserEmail.setText(cUserData.getsEmail());
        tvCity.setText(cUserData.getsCity());
        tvLocation.setText(cUserData.getsLocation());

        tvSubHallName.setText(cRequestBookingData.getsSubHallName());
        tvFunctionTiming.setText(cRequestBookingData.getsFunctionTiming());
        tvFunctionDate.setText(cRequestBookingData.getsFunctionDate());
        tvNoOfGuests.setText(cRequestBookingData.getsNoOfGuests());
        tvDish.setText(cRequestBookingData.getsDish());
        tvEstimatedBudget.setText(cRequestBookingData.getsEstimatedBudget());

        String sOtherDetail = cRequestBookingData.getsOtherDetail();

        if (!sOtherDetail.isEmpty())
            tvOtherDetail.setText(sOtherDetail);
        else
            tvOtherDetail.setText("No extra detail is provided.");
    }

    private void setVisibilityOfTextViews() {

        tvBookingRequestCancelAcceptTimeDate.setText(cRequestBookingData.getsAcceptDeniedTiming());

        tvBookingRequestTiming.setVisibility(View.VISIBLE);
        tvBookingRequestTimeDate.setVisibility(View.VISIBLE);

        tvBookingRequestTimeDate.setText(cRequestBookingData.getsRequestTime());

        tvAccept.setVisibility(View.GONE);
        tvCancel.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_message) {
            String sMessageBody = "Hello From App";
            Utils.sms(RequestBookingDetailActivity.this, cUserData.getsPhoneNo(), sMessageBody);
        } else if (v.getId() == R.id.fab_call) {
            Utils.dial(RequestBookingDetailActivity.this, cUserData.getsPhoneNo());
        } else if (v.getId() == R.id.tv_cancel) {
            //check Internet Connection
            if (!checkInternetConnection()) {
                return;
            }
            new CCancelRequest(this, false, cUserData.getsUserID(), cUserData.getsSubHallId(), cRequestBookingData);
        } else if (v.getId() == R.id.tv_accept) {
            //check Internet Connection
            if (!checkInternetConnection()) {
                return;
            }
            new CAcceptRequest(this, false, cUserData.getsUserID(), cUserData.getsSubHallId(), cRequestBookingData);
        }
    }

    private boolean checkInternetConnection() {
        // if there is no internet connection
        CNetworkConnection CNetworkConnection = new CNetworkConnection();
        if (CNetworkConnection.isConnected(RequestBookingDetailActivity.this)) {
            CNetworkConnection.buildDialog(RequestBookingDetailActivity.this).show();
            return false;
        }
        return true;
    }
}