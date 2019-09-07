package com.example.mhbadmin.Activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mhbadmin.AdapterClasses.SelectImagesAdapter;
import com.example.mhbadmin.Classes.CCustomToast;
import com.example.mhbadmin.Classes.CGetImageName;
import com.example.mhbadmin.Classes.CNetworkConnection;
import com.example.mhbadmin.Classes.Models.CSubHallData;
import com.example.mhbadmin.Classes.Upload.CUploadSubHallData;
import com.example.mhbadmin.Classes.CValidations;
import com.example.mhbadmin.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;
import static com.example.mhbadmin.Fragments.FSignUp.IMAGE_PICK_GALLERY_CODE;

public class AddSubHallActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout rlCloseKeyboard = null;

    private CCustomToast cCustomToast = null;

    private RecyclerView rvAddHall = null;
    private List<String> sLAddHallImagesUri = null,
            sLAddHallImageNames = null,
            sLGetAddHallImagesDownloadUri = null;

    private SelectImagesAdapter selectImagesAdapter = null;
    private ImageView ivUploadImage = null;
    private EditText etSubHallName = null,
            etSubHallFloorNo = null,
            etSubHallCapacity = null,
            etChickenRate = null,
            etMuttonRate = null,
            etBeefRate = null;
    private RadioGroup rgSweetDish = null,
            rgSalad = null,
            rgDrink = null,
            rgNan = null,
            rgRise = null;
    private String sSubHallName = null,
            sSubHallFloorNo = null,
            sSubHallCapacity = null,
            sChickenRate = null,
            sMuttonRate = null,
            sBeefRate = null,
            srbSweetDish = null,
            srbSalad = null,
            srbDrink = null,
            srbNan = null,
            srbRise = null;

    private boolean intentFlag = false;

    private Button btnAddHall = null,
            btnDone = null;

    //FireBase work

    private String userId = null,
            sHallMarquee = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_hall);

        connectivity();

        setClickListeners();

        settingUpRecyclerViewAdapter();

        settingAdapterClickListener();
    }

    private void connectivity() {

        cCustomToast = new CCustomToast();

        //FireBase Work

        SharedPreferences sp = getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);
        sHallMarquee = sp.getString(META_DATA, null);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        rlCloseKeyboard = (RelativeLayout) findViewById(R.id.rl_hide_soft_keyboard);

        rvAddHall = (RecyclerView) findViewById(R.id.rv_hall_marquee_images);
        rvAddHall.setHasFixedSize(true);

        sLAddHallImagesUri = new ArrayList<String>();
        sLAddHallImageNames = new ArrayList<String>();
        sLGetAddHallImagesDownloadUri = new ArrayList<String>();

        ivUploadImage = (ImageView) findViewById(R.id.iv_upload_image);

        selectImagesAdapter = new SelectImagesAdapter(getApplicationContext(), sLAddHallImagesUri, sLAddHallImageNames, rvAddHall, ivUploadImage);

        etSubHallName = (EditText) findViewById(R.id.et_sub_hall_name);

        etSubHallFloorNo = (EditText) findViewById(R.id.et_sub_hall_floor_no);

        if (sHallMarquee != null && sHallMarquee.equals("Marquee")) {
            etSubHallFloorNo.setVisibility(View.GONE);
            sSubHallFloorNo = "0";
        }

        etSubHallCapacity = (EditText) findViewById(R.id.et_sub_hall_capacity);
        etChickenRate = (EditText) findViewById(R.id.et_chicken_rate);
        etMuttonRate = (EditText) findViewById(R.id.et_mutton_rate);
        etBeefRate = (EditText) findViewById(R.id.et_beef_rate);

        rgSweetDish = (RadioGroup) findViewById(R.id.rg_sweet_dish);
        rgSalad = (RadioGroup) findViewById(R.id.rg_salad);
        rgDrink = (RadioGroup) findViewById(R.id.rg_drink);
        rgNan = (RadioGroup) findViewById(R.id.rg_nan);
        rgRise = (RadioGroup) findViewById(R.id.rg_rise);

        btnAddHall = (Button) findViewById(R.id.btn_add_more);
        btnDone = (Button) findViewById(R.id.btn_done);
    }

    private void setClickListeners() {
        //setting click listeners
        rlCloseKeyboard.setOnClickListener(this);
        ivUploadImage.setOnClickListener(this);
        btnAddHall.setOnClickListener(this);
        btnDone.setOnClickListener(this);
    }

    private void settingUpRecyclerViewAdapter() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rvAddHall.setLayoutManager(layoutManager);
        rvAddHall.setAdapter(selectImagesAdapter);
    }

    private void settingAdapterClickListener() {
        selectImagesAdapter.setOnClickListener(new SelectImagesAdapter.ClickListener() {
            @Override
            public void onItemClick(boolean onRecyclerViewClickFlag, int position) {
                if (onRecyclerViewClickFlag) {
                    int listSize = sLAddHallImagesUri.size();
                    if (position + 1 == listSize) {
                        sLAddHallImagesUri.remove(listSize - 1);
                        sLAddHallImageNames.remove(listSize - 1);

                        selectImagesAdapter.notifyItemRemoved(position);
                        selectImagesAdapter.notifyItemRangeChanged(position, listSize);

                        intentForOpenGallery();
                    }
                } else {
                    sLAddHallImagesUri.remove(position);
                    sLAddHallImageNames.remove(position);

                    selectImagesAdapter.notifyItemRemoved(position);
                    selectImagesAdapter.notifyItemRangeChanged(position, sLAddHallImagesUri.size());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.rl_hide_soft_keyboard) {
            hideSoftKeyboard(v);
        } else if (v.getId() == R.id.iv_upload_image) {
            intentForOpenGallery();
        } else if (v.getId() == R.id.btn_add_more) {
//            No need to assign
//            intentFlag = true;
//            because it is already initialised with false.
            uploadDataToFireBaseAndIntent();
        } else if (v.getId() == R.id.btn_done) {
            intentFlag = true;
            uploadDataToFireBaseAndIntent();
        }
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void intentForOpenGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Minimum 5 Sub Hall Picture"), IMAGE_PICK_GALLERY_CODE);
    }

    private void uploadDataToFireBaseAndIntent() {

        getDataFromView();

        if (!allValidation())
            return;

        //check Internet Connection
        if (!checkInternetConnection()) {
            return;
        }

        //FireBase work

        CSubHallData cSubHallData = null;

        if (!sSubHallFloorNo.equals("0")) {
            cSubHallData = new CSubHallData(sSubHallName, sSubHallFloorNo, sSubHallCapacity, sChickenRate,
                    sMuttonRate, sBeefRate, srbSweetDish, srbSalad, srbDrink, srbNan, srbRise,
                    sLGetAddHallImagesDownloadUri);
        } else {
            cSubHallData = new CSubHallData(sSubHallName, sSubHallCapacity, sChickenRate, sMuttonRate, sBeefRate,
                    srbSweetDish, srbSalad, srbDrink, srbNan, srbRise, sLGetAddHallImagesDownloadUri);
        }

        //this int is used to check whether this class
        //is instantiated from addSubHallActivity (=0) or FUpdateSubHAllInfo (=noOfTabs)
        //{but =0 is not "Sub Hall Counter}.
        new CUploadSubHallData(this, cSubHallData, intentFlag, 0,
                null, userId, sLAddHallImagesUri, sLAddHallImageNames, sHallMarquee);
    }

    private void getDataFromView() {

        sSubHallName = etSubHallName.getText().toString().trim();

        if (sHallMarquee != null && sHallMarquee.equals("Hall")) {
            sSubHallFloorNo = etSubHallFloorNo.getText().toString().trim();
        }

        sSubHallCapacity = etSubHallCapacity.getText().toString().trim();
        sChickenRate = etChickenRate.getText().toString().trim();
        sMuttonRate = etMuttonRate.getText().toString().trim();
        sBeefRate = etBeefRate.getText().toString().trim();

        //get Checked radio button text(Yes/No)
        int rbCheckedId = 0;
        RadioButton rb;

        //sweet Dish
        rbCheckedId = rgSweetDish.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(rbCheckedId);
        srbSweetDish = rb.getText().toString();

        //Salad
        rbCheckedId = rgSalad.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(rbCheckedId);
        srbSalad = rb.getText().toString();

        //Drink
        rbCheckedId = rgDrink.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(rbCheckedId);
        srbDrink = rb.getText().toString();

        //Nan
        rbCheckedId = rgNan.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(rbCheckedId);
        srbNan = rb.getText().toString();

        //Rise
        rbCheckedId = rgRise.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(rbCheckedId);
        srbRise = rb.getText().toString();
    }

    private boolean allValidation() {

        CValidations addHallCValidations = new CValidations();

        if (addHallCValidations.validateHallMarqueeName(etSubHallName, sSubHallName)) {
            etSubHallName.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etSubHallName.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_white));

        if (sHallMarquee != null && sHallMarquee.equals("Hall")) {
            if (addHallCValidations.validateSubHallFloorNo(etSubHallFloorNo, sSubHallFloorNo)) {
                etSubHallFloorNo.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_red));
                return false;
            } else
                etSubHallFloorNo.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_white));
        }

        if (addHallCValidations.validateSubHallCapacity(etSubHallCapacity, sSubHallCapacity)) {
            etSubHallCapacity.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etSubHallCapacity.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_white));

        if (addHallCValidations.validatePerHead(etChickenRate, sChickenRate)) {
            etChickenRate.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etChickenRate.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_white));

        if (addHallCValidations.validatePerHead(etMuttonRate, sMuttonRate)) {
            etMuttonRate.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etMuttonRate.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_white));

        if (addHallCValidations.validatePerHead(etBeefRate, sBeefRate)) {
            etBeefRate.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etBeefRate.setBackground(getApplication().getResources().getDrawable(R.drawable.round_white));

        if (sLAddHallImagesUri.size() < 6) {
            cCustomToast.makeText(getApplicationContext(), "kindly select minimum 5 Sub Hall pictures");
            return false;
        }

        return true;
    }

    private boolean checkInternetConnection() {
        // if there is no internet connection
        CNetworkConnection CNetworkConnection = new CNetworkConnection();
        if (CNetworkConnection.isConnected(getApplicationContext())) {
            CNetworkConnection.buildDialog(getApplicationContext()).show();
            return false;
        }
        return true;
    }

    private void checkFireBaseState() {


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri mImageUri = null;
        try {
            // When an Image is picked
            if (requestCode == IMAGE_PICK_GALLERY_CODE && resultCode == Activity.RESULT_OK
                    && data != null) {

                if (data.getData() != null) {

                    ivUploadImage.setVisibility(View.GONE);
                    rvAddHall.setVisibility(View.VISIBLE);

                    mImageUri = data.getData();

                    //get Image Name
                    CGetImageName CGetImageNAme = new CGetImageName(getApplicationContext());
                    final String sImageName = CGetImageNAme.getImageName(mImageUri);
                    sLAddHallImageNames.add(sImageName);

                    sLAddHallImagesUri.add(mImageUri.toString());
                    sLAddHallImagesUri.add(Uri.parse("android.resource://com.example.mhbadmin/drawable/ic_upload_image").toString());
                    sLAddHallImageNames.add("ic_upload_image");
                    selectImagesAdapter.notifyDataSetChanged();

                } else {
                    if (data.getClipData() != null) {

                        ivUploadImage.setVisibility(View.GONE);
                        rvAddHall.setVisibility(View.VISIBLE);

                        ClipData mClipData = data.getClipData();
                        int imageCount = mClipData.getItemCount();

                        for (int i = 0; i < imageCount; i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            mImageUri = item.getUri();

                            //get Image Name
                            CGetImageName CGetImageNAme = new CGetImageName(getApplicationContext());
                            final String sImageName = CGetImageNAme.getImageName(mImageUri);
                            sLAddHallImageNames.add(sImageName);
                            sLAddHallImagesUri.add(mImageUri.toString());
                        }
                        sLAddHallImagesUri.add(Uri.parse("android.resource://com.example.mhbadmin/drawable/ic_upload_image").toString());
                        sLAddHallImageNames.add("ic_upload_image");
                        selectImagesAdapter.notifyDataSetChanged();
                    }
                }
                if (sLAddHallImagesUri.size() < 6) {
                    cCustomToast.makeText(getApplicationContext(), "kindly select minimum 5 Sub Hall pictures");
                }
            } else {
                if (sLAddHallImagesUri.size() > 0) {
                    sLAddHallImagesUri.add(Uri.parse("android.resource://com.example.mhbadmin/drawable/ic_upload_image").toString());
                    sLAddHallImageNames.add("ic_upload_image");
                    selectImagesAdapter.notifyDataSetChanged();
                }
                cCustomToast.makeText(getApplicationContext(), "You haven't picked Image");
            }
        } catch (Exception e) {
            cCustomToast.makeText(getApplicationContext(), "Something went wrong");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}