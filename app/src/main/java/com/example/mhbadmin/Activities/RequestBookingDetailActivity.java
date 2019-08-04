package com.example.mhbadmin.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mhbadmin.Classes.CRequestBookingData;
import com.example.mhbadmin.Classes.CUserData;
import com.example.mhbadmin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

public class RequestBookingDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private CRequestBookingData cRequestBookingData = null;

    private CUserData cUserData = null;

    private TextView tvRequestBookingTimeDate = null,
            tvUserName = null,
            tvPhoneNo = null,
            tvUserEmail = null,
            tvCity = null,
            tvLocation = null,
            tvFunctionTiming = null,
            tvFunctionDate = null,
            tvNoOfGuests = null,
            tvDish = null,
            tvEstimatedBudget = null,
            tvOtherDetail = null,
            tvAccept = null,
            tvCancel = null;

    private FloatingActionButton fabMessage = null,
            fabCall = null;

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

        tvRequestBookingTimeDate = (TextView) findViewById(R.id.tv_request_booking_time_date);

        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvPhoneNo = (TextView) findViewById(R.id.tv_user_phone_no);
        tvUserEmail = (TextView) findViewById(R.id.tv_user_email);
        tvCity = (TextView) findViewById(R.id.tv_user_city);
        tvLocation = (TextView) findViewById(R.id.tv_user_location);

        tvFunctionTiming = (TextView) findViewById(R.id.tv_function_timing);
        tvFunctionDate = (TextView) findViewById(R.id.tv_function_date);
        tvNoOfGuests = (TextView) findViewById(R.id.tv_no_of_guests);
        tvDish = (TextView) findViewById(R.id.tv_dish);
        tvEstimatedBudget = (TextView) findViewById(R.id.tv_estimated_budget);
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
            cRequestBookingData = gson.fromJson(bundle.getString("requestBookingData"), CRequestBookingData.class);
            cUserData = gson.fromJson(bundle.getString("userData"), CUserData.class);
        }
    }

    private void showDataToView() {

        Glide.with(getApplicationContext())
                .load(cUserData.getsUserProfileImageUri())
                .into(ivUserProfile);

        tvRequestBookingTimeDate.setText(cRequestBookingData.getsRequestTime());

        tvUserName.setText(cUserData.getsUserFirstName() + " " + cUserData.getsUserLastName());
        tvPhoneNo.setText(cUserData.getsPhoneNo());
        tvUserEmail.setText(cUserData.getsEmail());
        tvCity.setText(cUserData.getsCity());
        tvLocation.setText(cUserData.getsLocation());

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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_message) {
            Intent messageIntent = new Intent();
            messageIntent.setType("text/plain");
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("address", cUserData.getsPhoneNo());
            messageIntent.putExtra(Intent.EXTRA_TEXT, "Hello From App");

            // Verify that the intent will resolve to an activity
            if (messageIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(messageIntent);
            }

        } else if (v.getId() == R.id.fab_call) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + cUserData.getsPhoneNo()));
            if (callIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(callIntent);
            }

        } else if (v.getId() == R.id.tv_cancel) {

        } else if (v.getId() == R.id.iv_accept) {

        }
    }
}