package com.example.mhbadmin.Activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mhbadmin.Classes.Models.CUserData;
import com.example.mhbadmin.R;
import com.example.mhbadmin.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

public class UserDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivUserProfile = null;

    private TextView tvUserFirstName = null,
            tvUserLastName = null,
            tvUserEmail = null,
            tvUserPhoneNo = null,
            tvUserCity = null,
            tvUserCountry = null,
            tvUserLocation = null;

    private FloatingActionButton fabMessage = null,
            fabCall = null;

    private CUserData cUserData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        getSupportActionBar().hide();

        connectivity();

        getDataFromUserDetailDialogInfoActivity();

        fabCall.setOnClickListener(this);
        fabMessage.setOnClickListener(this);

        showDataToView();
    }

    private void connectivity() {

        ivUserProfile = (ImageView) findViewById(R.id.iv_user_profile);

        tvUserFirstName = (TextView) findViewById(R.id.tv_user_first_name);
        tvUserLastName = (TextView) findViewById(R.id.tv_user_last_name);
        tvUserEmail = (TextView) findViewById(R.id.tv_user_email);
        tvUserPhoneNo = (TextView) findViewById(R.id.tv_user_phone_no);
        tvUserCity = (TextView) findViewById(R.id.tv_user_city);
        tvUserCountry = (TextView) findViewById(R.id.tv_user_country);
        tvUserLocation = (TextView) findViewById(R.id.tv_user_location);

        fabMessage = (FloatingActionButton) findViewById(R.id.fab_message);
        fabCall = (FloatingActionButton) findViewById(R.id.fab_call);
    }

    private void getDataFromUserDetailDialogInfoActivity() {
        if (!getIntent().getExtras().isEmpty()) {
            String sUserData = getIntent().getExtras().getString("userDetail");

            cUserData = new Gson().fromJson(sUserData, CUserData.class);
        }
    }

    private void showDataToView() {

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(cUserData.getsUserProfileImageUri())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ivUserProfile.setImageBitmap(resource);
                        setToolbarColor(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

        tvUserFirstName.setText(cUserData.getsUserFirstName());
        tvUserLastName.setText(cUserData.getsUserLastName());
        tvUserEmail.setText(cUserData.getsEmail());
        tvUserPhoneNo.setText(cUserData.getsPhoneNo());
        tvUserCity.setText(cUserData.getsCity());
        tvUserCountry.setText(cUserData.getsCountry());
        tvUserLocation.setText(cUserData.getsLocation());
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.fab_message) {
            String sMessageBody = "Hello From App";
            Utils.sms(UserDetailActivity.this, cUserData.getsPhoneNo(), sMessageBody);
        } else if (v.getId() == R.id.fab_call) {
            Utils.dial(UserDetailActivity.this, cUserData.getsPhoneNo());
        }
    }

    // Generate palette synchronously and return it
    public Palette createPaletteSync(Bitmap bitmap) {
        return Palette.from(bitmap).generate();
    }

    // Set the background and text colors of a toolbar given a
    // bitmap image to match
    public void setToolbarColor(Bitmap bitmap) {
        // Generate the palette and get the vibrant swatch
        // See the createPaletteSync() method
        // from the code snippet above
        Palette p = createPaletteSync(bitmap);
        Palette.Swatch vibrantSwatch = p.getVibrantSwatch();

        // Load default colors
        int backgroundColor = ContextCompat.getColor(getApplicationContext(),
                R.color.colorPrimaryDark);
        /*int textColor = ContextCompat.getColor(getApplicationContext(),
                R.color.blackColor);*/

        // Check that the Vibrant swatch is available
        if (vibrantSwatch != null) {
            backgroundColor = vibrantSwatch.getRgb();
//            textColor = vibrantSwatch.getTitleTextColor();
        }
/*
        // Set the toolbar background and text colors
        toolbar.setBackgroundColor(backgroundColor);
        toolbar.setTitleTextColor(textColor);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Set the statusBar background
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(backgroundColor);
        }
    }
}