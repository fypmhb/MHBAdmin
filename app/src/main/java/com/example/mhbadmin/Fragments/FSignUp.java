package com.example.mhbadmin.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mhbadmin.AdapterClasses.SelectImagesAdapter;
import com.example.mhbadmin.Classes.CCustomToast;
import com.example.mhbadmin.Classes.CGetImageName;
import com.example.mhbadmin.Classes.CNetworkConnection;
import com.example.mhbadmin.Classes.Models.CSignUpData;
import com.example.mhbadmin.Classes.Upload.CUploadSignUpData;
import com.example.mhbadmin.Classes.CValidations;
import com.example.mhbadmin.R;
import com.santalu.maskedittext.MaskEditText;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FSignUp extends Fragment implements View.OnClickListener {

    public final static int IMAGE_REQUEST_CODE = 121;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 120;

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
            intentForOpenGallery(true);
        } else if (v.getId() == R.id.iv_add_manager_profile) {
            profilePicture();
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

    //single ot Multiple Image Selection Flag
    //flag is true for multiple images selection
    private void intentForOpenGallery(boolean flag) {
        if (flag) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Hall Entrance Picture"), IMAGE_REQUEST_CODE);
        } else {
            bImageFlag = true;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent,
                    "Select Profile Image"), CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void profilePicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("Complete This Action Using");
        builder.setTitle("Please Confirm");
        builder.setCancelable(false);
        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //pass false for open gallery to select one picture.
                intentForOpenGallery(false);
            }
        }).setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intentForOpenCamera();
            }
        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog1 = builder.create();
        dialog1.show();
    }

    private void intentForOpenCamera() {
        ask_CheckCameraStoragePermission_OpenCamera();
    }

    private void ask_CheckCameraStoragePermission_OpenCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String permissions[] = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, IMAGE_REQUEST_CODE);
            } else { // permission granted
                openCamera();
            }
        } else {
//            < M
            openCamera();
        }
    }

    private void openCamera() {
        bImageFlag = true;
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, IMAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == IMAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }

    private Uri saveInGallery(Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
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
        new CUploadSignUpData(context, cSignUpData,uriManagerProfile, sPassword,
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

    //Select image and set to imageView
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST_CODE || requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            if (bImageFlag) {
                if (requestCode == IMAGE_REQUEST_CODE) {
                    if (resultCode == RESULT_OK) {

                        //take image from camera

                        Bitmap bitmap;
                        Bundle b = data.getExtras();
                        assert b != null;
                        bitmap = (Bitmap) b.getParcelable("data");
                        assert bitmap != null;
                        uriManagerProfile = saveInGallery(bitmap);

                        //get Image Name
                        CGetImageName objCGetImageNAme = new CGetImageName(context);
                        sManagerProfileName = objCGetImageNAme.getImageName(uriManagerProfile);

                        try {
                            Glide.with(context).load(uriManagerProfile).into(ivManagerProfile);
                            bImageFlag = false;
                        } catch (Exception e) {
                            cCustomToast.makeText(context, e.getMessage());
                        }

                    } else { // Result was a failure
                        cCustomToast.makeText(context, "Picture wasn't taken!");
                    }
                } else {
                    if (data.getData() != null) {
                        uriManagerProfile = data.getData();
                        //get Image Name
                        CGetImageName objCGetImageNAme = new CGetImageName(context);
                        sManagerProfileName = objCGetImageNAme.getImageName(uriManagerProfile);

                        try {
                            Glide.with(context).load(uriManagerProfile).into(ivManagerProfile);
                            bImageFlag = false;
                        } catch (Exception e) {
                            cCustomToast.makeText(context, e.getMessage());
                        }
                    }
                }
            } else if (data != null) {
                Uri mImageUri = null;
                if (data.getData() != null) {

                    ivUploadImage.setVisibility(View.GONE);
                    rvSignUp.setVisibility(View.VISIBLE);

                    mImageUri = data.getData();

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
        super.onActivityResult(requestCode, resultCode, data);
    }
}