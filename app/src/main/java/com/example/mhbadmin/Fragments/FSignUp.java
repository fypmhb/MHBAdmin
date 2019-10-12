package com.example.mhbadmin.Fragments;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mhbadmin.AdapterClasses.SelectImagesAdapter;
import com.example.mhbadmin.Classes.CCustomToast;
import com.example.mhbadmin.Classes.CGetImageName;
import com.example.mhbadmin.Classes.CNetworkConnection;
import com.example.mhbadmin.Classes.CValidations;
import com.example.mhbadmin.Classes.Models.CSignUpData;
import com.example.mhbadmin.Classes.Upload.CUploadSignUpData;
import com.example.mhbadmin.R;
import com.santalu.maskedittext.MaskEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FSignUp extends Fragment implements View.OnClickListener {

    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int STORAGE_REQUEST_CODE = 200;
    public static final int IMAGE_PICK_GALLERY_CODE = 300;
    public static final int IMAGE_PICK_CAMERA_CODE = 400;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 120;

    private Context context = null;
    private View fragmentView = null;

    private CCustomToast cCustomToast = null;

    private RelativeLayout rlCloseKeyboard = null;

    private RecyclerView rvSignUp = null;
    private SelectImagesAdapter selectImagesAdapter = null;
    private ImageView ivUploadImage = null;
    private RadioGroup rgHallMarquee = null;
    private RadioButton rbHallMarquee = null;
    private String srbHallMarquee = null;
    private ImageView ivManagerProfile = null,
            ivAddManagerProfile = null;
    private List<String> sLHallEntranceImagesUri = null,
            sLHallEntranceImageNames = null,
            sLGetHallEntranceImagesDownloadUri = null;

    private String sManagerProfileName = null,
            sManagerProfileDownloadUri = null;
    private Uri uriManagerProfile = null;
    private boolean bImageFlag = false;
    private EditText etHallMarqueeName = null,
            etManagerFirstName = null,
            etManagerLastName = null,
            etHallMarqueeEmail = null,
            etHallMarqueeCity = null,
            etHallMarqueeLocation = null,
            etHallPassword = null,
            etHallConformPassword = null;
    private TextView tvHallImages = null;
    private MaskEditText metHallMarqueePhoneNo = null;
    private CheckBox cbSoptLights = null,
            cbMusic = null,
            cbAcHeater = null,
            cbParking = null;
    private Button btnHallMarqueeSignUp = null;
    private TextView tvAlreadyHaveOne = null;
    private String sHallMarqueeName = null,
            sManagerFirstName = null,
            sManagerLastName = null,
            sHallMarqueeEmail = null,
            sHallMarqueePhoneNo = null,
            sHallMarqueeCity = null,
            sHallMarqueeLocation = null,
            sPassword = null,
            sConformPassword = null,
            sSpotLights = "No",
            sMusic = "No",
            sACHeater = "No",
            sParking = "No";

    //arrays of permissions to be requested
    private String[] cameraPermissions = null;
    private String[] storagePermissions = null;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        connectivity();

        setClickListeners();

        settingUpRecyclerViewAdapter();

        //underline already have one text view
        tvAlreadyHaveOne.setPaintFlags(tvAlreadyHaveOne.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        settingAdapterClickListener();

        return fragmentView;
    }

    private void connectivity() {

        context = getActivity();

        //init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        cCustomToast = new CCustomToast();

        rlCloseKeyboard = (RelativeLayout) fragmentView.findViewById(R.id.rl_hide_soft_keyboard);

        rvSignUp = (RecyclerView) fragmentView.findViewById(R.id.rv_hall_marquee_images);
        rvSignUp.setHasFixedSize(true);

        sLHallEntranceImagesUri = new ArrayList<String>();
        sLGetHallEntranceImagesDownloadUri = new ArrayList<String>();
        sLHallEntranceImageNames = new ArrayList<String>();

        ivUploadImage = (ImageView) fragmentView.findViewById(R.id.iv_upload_image);
        selectImagesAdapter = new SelectImagesAdapter(context, sLHallEntranceImagesUri, sLHallEntranceImageNames, rvSignUp, ivUploadImage);

        rgHallMarquee = (RadioGroup) fragmentView.findViewById(R.id.rg_hall_marquee);

        ivManagerProfile = (ImageView) fragmentView.findViewById(R.id.iv_manager_profile);
        ivAddManagerProfile = (ImageView) fragmentView.findViewById(R.id.iv_add_manager_profile);

        etHallMarqueeName = (EditText) fragmentView.findViewById(R.id.et_hall_marquee_name);
        etManagerFirstName = (EditText) fragmentView.findViewById(R.id.et_first_name);
        etManagerLastName = (EditText) fragmentView.findViewById(R.id.et_last_name);
        etHallMarqueeEmail = (EditText) fragmentView.findViewById(R.id.et_hall_marquee_email);
        metHallMarqueePhoneNo = (MaskEditText) fragmentView.findViewById(R.id.met_hall_marquee_phone_no);
        etHallMarqueeCity = (EditText) fragmentView.findViewById(R.id.et_hall_marquee_city);
        etHallMarqueeLocation = (EditText) fragmentView.findViewById(R.id.et_hall_marquee_location);
        etHallPassword = (EditText) fragmentView.findViewById(R.id.et_hall_password);
        etHallConformPassword = (EditText) fragmentView.findViewById(R.id.et_hall_confirm_password);

        tvHallImages = (TextView) fragmentView.findViewById(R.id.tv_hall_images);

        cbSoptLights = (CheckBox) fragmentView.findViewById(R.id.cb_chicken);
        cbMusic = (CheckBox) fragmentView.findViewById(R.id.cb_music);
        cbAcHeater = (CheckBox) fragmentView.findViewById(R.id.cb_ac_heater);
        cbParking = (CheckBox) fragmentView.findViewById(R.id.cb_parking);

        btnHallMarqueeSignUp = (Button) fragmentView.findViewById(R.id.btn_sign_up);
        tvAlreadyHaveOne = (TextView) fragmentView.findViewById(R.id.tv_already_have_one);
    }

    private void setClickListeners() {
        //setting click listeners on radioGroup
        rgHallMarquee.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbHallMarquee = rgHallMarquee.findViewById(checkedId);
                srbHallMarquee = rbHallMarquee.getText().toString();

                etHallMarqueeName.setHint(srbHallMarquee + " Name");
                etHallMarqueeEmail.setHint(srbHallMarquee + " Email");
                metHallMarqueePhoneNo.setHint(srbHallMarquee + " Phone No");
                etHallMarqueeCity.setHint(srbHallMarquee + " City");
                etHallMarqueeLocation.setHint(srbHallMarquee + " Location");
                tvHallImages.setText("Select minimum 5 " + srbHallMarquee.toLowerCase() + " entrance images");
            }
        });

        //set Click Listeners on Buttons
        rlCloseKeyboard.setOnClickListener(this);
        ivUploadImage.setOnClickListener(this);
        ivAddManagerProfile.setOnClickListener(this);
        btnHallMarqueeSignUp.setOnClickListener(this);
        tvAlreadyHaveOne.setOnClickListener(this);
    }

    private void settingUpRecyclerViewAdapter() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvSignUp.setLayoutManager(layoutManager);
        rvSignUp.setAdapter(selectImagesAdapter);
    }

    private void settingAdapterClickListener() {

        selectImagesAdapter.setOnClickListener(new SelectImagesAdapter.ClickListener() {
            @Override
            public void onItemClick(boolean onRecyclerViewClickFlag, int position) {
                if (onRecyclerViewClickFlag) {
                    int listSize = sLHallEntranceImagesUri.size();
                    if (position + 1 == listSize) {


                        sLHallEntranceImagesUri.remove(listSize - 1);
                        sLHallEntranceImageNames.remove(listSize - 1);

                        selectImagesAdapter.notifyItemRemoved(position);
                        selectImagesAdapter.notifyItemRangeChanged(position, listSize);

                        intentForOpenGallery(true);
                    }
                } else {
                    sLHallEntranceImagesUri.remove(position);
                    sLHallEntranceImageNames.remove(position);

                    selectImagesAdapter.notifyItemRemoved(position);
                    selectImagesAdapter.notifyItemRangeChanged(position, sLHallEntranceImagesUri.size());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_hide_soft_keyboard) {
            hideSoftKeyboard(v);
        } else if (v.getId() == R.id.iv_upload_image) {
            //pass false for open gallery to select multiple pictures.
            //Gallery clicked
            if (!checkStoragePermission())
                requestStoragePermission();
            else
                intentForOpenGallery(true);
        } else if (v.getId() == R.id.iv_add_manager_profile) {
            showImagePicDialog();
        } else if (v.getId() == R.id.btn_sign_up) {
            signUp();
        } else if (v.getId() == R.id.tv_already_have_one) {
            assert getFragmentManager() != null;
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.LogInSignUp, new FLogIn());
            ft.commit();
        }
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //single or Multiple Image Selection Flag
    //flag is true for multiple images selection
    private void intentForOpenGallery(boolean flag) {
        if (flag) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Hall Entrance Picture"), CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } else {
            bImageFlag = true;
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent,
                    "Select Profile Image"), IMAGE_PICK_GALLERY_CODE);
        }
    }

    private void showImagePicDialog() {
        //show dialog containing options Camera and Gallery to pick the image

        String[] options = {"Camera", "Gallery"};
        //alert dialog
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //set title
        builder.setTitle("Pick Image From");
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if (which == 0) {
                    //Camera clicked
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    //Gallery clicked
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        intentForOpenGallery(false);
                    }
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    private boolean checkCameraPermission() {
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        //request runtime storage permission
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void pickFromCamera() {
        bImageFlag = true;

        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        uriManagerProfile = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriManagerProfile);

        if (cameraIntent.resolveActivity(context.getPackageManager()) != null)
            startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission() {
        //request runtime storage permission
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*This method called when user press Allow or Deny from permission request dialog
         * here we will handle permission cases (allowed & denied)*/

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                //picking from camera, first check if camera and storage permissions allowed or not
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        //permissions enabled
                        pickFromCamera();
                    } else {
                        //permissions denied
                        Toast.makeText(context, "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {

                //picking from gallery, first check if storage permissions allowed or not
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        //permissions enabled
                        intentForOpenGallery(false);
                    } else {
                        //permissions denied
                        Toast.makeText(context, "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    private void signUp() {

        getDataFromView();

        if (!allValidation())
            return;

        //check Internet Connection
        if (!checkInternetConnection()) {
            return;
        }


        CSignUpData cSignUpData = new CSignUpData(sHallMarqueeName, sManagerFirstName, sManagerLastName,
                sManagerProfileDownloadUri, sHallMarqueeEmail, sHallMarqueePhoneNo, sHallMarqueeCity,
                sHallMarqueeLocation, sSpotLights, sMusic, sACHeater, sParking,
                sLGetHallEntranceImagesDownloadUri);

        //FireBase work
        new CUploadSignUpData(context, cSignUpData, uriManagerProfile, sPassword,
                srbHallMarquee, sManagerProfileName, sLHallEntranceImagesUri, sLHallEntranceImageNames);
    }

    private void getDataFromView() {

        sHallMarqueeName = etHallMarqueeName.getText().toString().trim();
        sManagerFirstName = etManagerFirstName.getText().toString().trim();
        sManagerLastName = etManagerLastName.getText().toString().trim();
        sHallMarqueeEmail = etHallMarqueeEmail.getText().toString().trim().toLowerCase();
        sHallMarqueePhoneNo = metHallMarqueePhoneNo.getText().toString().trim();
        sHallMarqueeCity = etHallMarqueeCity.getText().toString().trim();
        sHallMarqueeLocation = etHallMarqueeLocation.getText().toString().trim();
        sPassword = etHallPassword.getText().toString().trim();
        sConformPassword = etHallConformPassword.getText().toString().trim();

        //get Checked radio button text(Hall/Marquee)
        int rbCheckedId = rgHallMarquee.getCheckedRadioButtonId();
        rbHallMarquee = (RadioButton) fragmentView.findViewById(rbCheckedId);
        srbHallMarquee = rbHallMarquee.getText().toString();

        if (cbSoptLights.isChecked())
            sSpotLights = "Yes";
        if (cbMusic.isChecked())
            sMusic = "Yes";
        if (cbAcHeater.isChecked())
            sACHeater = "Yes";
        if (cbParking.isChecked())
            sParking = "Yes";
    }

    private boolean allValidation() {
        CValidations signUpCValidations = new CValidations();

        if (signUpCValidations.validateHallMarqueeName(etHallMarqueeName, sHallMarqueeName)) {
            etHallMarqueeName.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etHallMarqueeName.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (signUpCValidations.validateName(etManagerFirstName, sManagerFirstName)) {
            etManagerFirstName.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etManagerFirstName.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (signUpCValidations.validateName(etManagerLastName, sManagerLastName)) {
            etManagerLastName.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etManagerLastName.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (uriManagerProfile == null) {

            return false;
        }

        if (signUpCValidations.validateEmail(etHallMarqueeEmail, sHallMarqueeEmail)) {
            etHallMarqueeEmail.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etHallMarqueeEmail.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (signUpCValidations.validatePhoneNo(metHallMarqueePhoneNo, sHallMarqueePhoneNo)) {
            metHallMarqueePhoneNo.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            metHallMarqueePhoneNo.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (signUpCValidations.validateCity(etHallMarqueeCity, sHallMarqueeCity)) {
            etHallMarqueeCity.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etHallMarqueeCity.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (signUpCValidations.validateLocation(etHallMarqueeLocation, sHallMarqueeLocation)) {
            etHallMarqueeLocation.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etHallMarqueeLocation.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (signUpCValidations.ValidatePassword(etHallPassword, sPassword)) {
            etHallPassword.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etHallPassword.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (signUpCValidations.validateConformPassword(etHallConformPassword, sConformPassword, sPassword, "Password doesn't match")) {
            etHallConformPassword.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etHallConformPassword.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (sLHallEntranceImagesUri.size() < 6) {
            cCustomToast.makeText(context, "kindly select minimum 5 Hall Entrance pictures");
            return false;
        }

        return true;
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

    //Select images and set to imageView
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (bImageFlag) {
                if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                    if (data != null) {

                        //got image from camera now crop it
                        CropImage.activity(uriManagerProfile)
                                .setGuidelines(CropImageView.Guidelines.ON) //enable image guidlines
                                .start(getContext(), this);

                    } else { // Result was a failure
                        cCustomToast.makeText(context, "Picture wasn't taken!");
                    }
                } else if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                    if (data.getData() != null) {

                        //got image from gallery now crop it
                        CropImage.activity(data.getData())
                                .setGuidelines(CropImageView.Guidelines.ON) //enable image guidlines
                                .start(getContext(), this);
                    } else { // Result was a failure
                        cCustomToast.makeText(context, "Picture wasn't chosen!");
                    }
                }
            } else if (data != null) {

                Uri mImageUri = null;
                if (data.getData() != null) {

                    ivUploadImage.setVisibility(View.GONE);
                    rvSignUp.setVisibility(View.VISIBLE);

                    mImageUri = data.getData();
/*
                    //got image from gallery now crop it
                    CropImage.activity(data.getData())
                            .setGuidelines(CropImageView.Guidelines.ON) //enable image guidlines
                            .start(getContext(), this);*/

                    //get Image Name
                    CGetImageName CGetImageNAme = new CGetImageName(context);
                    final String sImageName = CGetImageNAme.getImageName(mImageUri);
                    sLHallEntranceImageNames.add(sImageName);

                    sLHallEntranceImagesUri.add(mImageUri.toString());
                    sLHallEntranceImagesUri.add(Uri.parse("android.resource://com.example.mhbadmin/drawable/ic_upload_image").toString());
                    sLHallEntranceImageNames.add("ic_upload_image");
                    selectImagesAdapter.notifyDataSetChanged();
                } else {
                    if (data.getClipData() != null) {

                        ivUploadImage.setVisibility(View.GONE);
                        rvSignUp.setVisibility(View.VISIBLE);

                        ClipData mClipData = data.getClipData();

                        int imageCount = mClipData.getItemCount();

                        for (int i = 0; i < imageCount; i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            mImageUri = item.getUri();

                            //get Image Name
                            CGetImageName CGetImageNAme = new CGetImageName(context);
                            final String sImageName = CGetImageNAme.getImageName(mImageUri);
                            sLHallEntranceImageNames.add(sImageName);
                            sLHallEntranceImagesUri.add(mImageUri.toString());
                        }
                        sLHallEntranceImagesUri.add(Uri.parse("android.resource://com.example.mhbadmin/drawable/ic_upload_image").toString());
                        sLHallEntranceImageNames.add("ic_upload_image");
                        selectImagesAdapter.notifyDataSetChanged();
                    }
                }
                if (sLHallEntranceImagesUri.size() < 6) {
                    cCustomToast.makeText(context, "kindly select minimum 5 Sub Hall pictures");
                }
            }
        } else {
            if (sLHallEntranceImagesUri.size() > 0) {
                sLHallEntranceImagesUri.add(Uri.parse("android.resource://com.example.mhbadmin/drawable/ic_upload_image").toString());
                sLHallEntranceImageNames.add("ic_upload_image");
                selectImagesAdapter.notifyDataSetChanged();
            }
            cCustomToast.makeText(context, "You haven't picked Image");
        }

        //get cropped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                assert result != null;
                uriManagerProfile = result.getUri(); //get image uri
                //set image to image view

                //get Image Name
                CGetImageName objCGetImageNAme = new CGetImageName(context);
                sManagerProfileName = objCGetImageNAme.getImageName(uriManagerProfile);
                try {
                    Glide.with(context).load(uriManagerProfile).into(ivManagerProfile);
                    bImageFlag = false;
                } catch (Exception e) {
                    cCustomToast.makeText(context, e.getMessage());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //if there is any error show it
                Exception error = result.getError();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}