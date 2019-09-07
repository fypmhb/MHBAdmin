package com.example.mhbadmin.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santalu.maskedittext.MaskEditText;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;
import static com.example.mhbadmin.Fragments.FSignUp.IMAGE_PICK_GALLERY_CODE;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class UpdateHallInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 120;
    private RelativeLayout rlCloseKeyboard = null;

    private RecyclerView rvHallInfo = null;
    private CCustomToast cCustomToast = null;

    private SelectImagesAdapter selectImagesAdapter = null;

    private ImageView ivUploadImage = null,
            ivManagerProfile = null,
            ivAddManagerProfile = null;

    private List<String> sLHallEntranceImagesUri = null,
            sLHallEntranceImageNames = null,
            sLGetHallEntranceImagesDownloadUri = null;

    private String sManagerProfileName = null,
            sManagerProfileDownloadUri = null;

    private Uri uriManagerProfile = null;

    private String uriOldManagerProfile = null;

    private boolean bImageFlag = false;

    private EditText etHallMarqueeName = null,
            etManagerFirstName = null,
            etManagerLastName = null,
            etHallMarqueeCity = null,
            etHallMarqueeLocation = null;

    private TextView tvHallImages = null;

    private MaskEditText metHallMarqueePhoneNo = null;

    private CheckBox cbSoptLights = null,
            cbMusic = null,
            cbAcHeater = null,
            cbParking = null;

    private Button btnHallMarqueeUpdateInfo = null;

    private String sHallMarqueeName = null,
            sManagerFirstName = null,
            sManagerLastName = null,
            sHallMarqueeEmail = null,
            sHallMarqueePhoneNo = null,
            sHallMarqueeCity = null,
            sHallMarqueeLocation = null,
            sSpotLights = "No",
            sMusic = "No",
            sACHeater = "No",
            sParking = "No";

    //FireBase Work

    private SharedPreferences sp = null;

    private ProgressDialog progressDialog = null;

    private FirebaseDatabase firebaseDatabase = null;

    private String userId = null,
            sHallMarquee = null;

    private CSignUpData cSignUpData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_hall_info);

        connectivity();

        setClickListeners();

        settingUpRecyclerViewAdapter();

        settingAdapterClickListener();

        checkFireBaseState();
    }

    private void connectivity() {

        cCustomToast = new CCustomToast();

        //FireBase work

        sp = getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rlCloseKeyboard = (RelativeLayout) findViewById(R.id.rl_hide_soft_keyboard);

        rvHallInfo = (RecyclerView) findViewById(R.id.rv_hall_marquee_images);
        rvHallInfo.setHasFixedSize(true);

        sLHallEntranceImagesUri = new ArrayList<String>();
        sLGetHallEntranceImagesDownloadUri = new ArrayList<String>();
        sLHallEntranceImageNames = new ArrayList<String>();

        ivUploadImage = (ImageView) findViewById(R.id.iv_upload_image);
        selectImagesAdapter = new SelectImagesAdapter(getApplicationContext(), sLHallEntranceImagesUri, sLHallEntranceImageNames, rvHallInfo, ivUploadImage);

        ivManagerProfile = (ImageView) findViewById(R.id.iv_manager_profile);
        ivAddManagerProfile = (ImageView) findViewById(R.id.iv_add_manager_profile);

        etHallMarqueeName = (EditText) findViewById(R.id.et_hall_marquee_name);
        etManagerFirstName = (EditText) findViewById(R.id.et_first_name);
        etManagerLastName = (EditText) findViewById(R.id.et_last_name);
        metHallMarqueePhoneNo = (MaskEditText) findViewById(R.id.met_hall_marquee_phone_no);
        etHallMarqueeCity = (EditText) findViewById(R.id.et_hall_marquee_city);
        etHallMarqueeLocation = (EditText) findViewById(R.id.et_hall_marquee_location);

        tvHallImages = (TextView) findViewById(R.id.tv_hall_images);

        cbSoptLights = (CheckBox) findViewById(R.id.cb_chicken);
        cbMusic = (CheckBox) findViewById(R.id.cb_music);
        cbAcHeater = (CheckBox) findViewById(R.id.cb_ac_heater);
        cbParking = (CheckBox) findViewById(R.id.cb_parking);

        btnHallMarqueeUpdateInfo = (Button) findViewById(R.id.btn_update_hall_info);

    }

    private void setClickListeners() {
        //set Click Listeners
        rlCloseKeyboard.setOnClickListener(this);
        ivUploadImage.setOnClickListener(this);
        ivAddManagerProfile.setOnClickListener(this);
        btnHallMarqueeUpdateInfo.setOnClickListener(this);
    }

    private void settingUpRecyclerViewAdapter() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rvHallInfo.setLayoutManager(layoutManager);
        rvHallInfo.setAdapter(selectImagesAdapter);
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

    private void checkFireBaseState() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if (sp.getString(META_DATA, null) != null)
            sHallMarquee = sp.getString(META_DATA, null);

        //check Internet Connection
        if (!checkInternetConnection()) {
            return;
        }

        getDataFromFireBase();
    }

    private void getDataFromFireBase() {

        final DatabaseReference databaseReference = firebaseDatabase
                .getReference(sHallMarquee)
                .child(userId)
                .child(sHallMarquee + " info");

        databaseReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            cSignUpData = dataSnapshot.getValue(CSignUpData.class);
                            showDataOnView();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showDataOnView() {

        Glide.with(getApplicationContext())
                .load(cSignUpData.getsManagerProfileImageUri())
                .into(ivManagerProfile);

        uriManagerProfile = Uri.parse(cSignUpData.getsManagerProfileImageUri());
        uriOldManagerProfile = cSignUpData.getsManagerProfileImageUri();
        sManagerProfileName = "mProfile";

        List<String> sample = cSignUpData.getsLHallEntranceDownloadImagesUri();

        for (int i = 0; i < sample.size(); i++) {
            sLHallEntranceImagesUri.add(sample.get(i));
            sLGetHallEntranceImagesDownloadUri.add(sample.get(i));
            sLHallEntranceImageNames.add(i + "d");
        }
        sLHallEntranceImagesUri.add(Uri.parse("android.resource://com.example.mhbadmin/drawable/ic_upload_image").toString());
        sLHallEntranceImageNames.add("ic_upload_image");

        selectImagesAdapter.notifyDataSetChanged();

        ivUploadImage.setVisibility(View.GONE);
        rvHallInfo.setVisibility(View.VISIBLE);

        etHallMarqueeName.setText(cSignUpData.getsHallMarqueeName());
        etManagerFirstName.setText(cSignUpData.getsManagerFirstName());
        etManagerLastName.setText(cSignUpData.getsManagerLastName());
        metHallMarqueePhoneNo.setText(cSignUpData.getsPhoneNo());
        etHallMarqueeCity.setText(cSignUpData.getsCity());
        etHallMarqueeLocation.setText(cSignUpData.getsLocation());

        if (cSignUpData.getsSpotLights().equals("Yes"))
            cbSoptLights.setChecked(true);

        if (cSignUpData.getsMusic().equals("Yes"))
            cbMusic.setChecked(true);

        if (cSignUpData.getsAc_Heater().equals("Yes"))
            cbAcHeater.setChecked(true);

        if (cSignUpData.getsParking().equals("Yes"))
            cbParking.setChecked(true);

        progressDialog.dismiss();
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
        } else if (v.getId() == R.id.btn_update_hall_info) {
            updateHallInfo();
        }
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
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
            startActivityForResult(Intent.createChooser(intent, "Select Hall Entrance Picture"), IMAGE_PICK_GALLERY_CODE);
        } else {
            bImageFlag = true;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent,
                    "Select Profile Image"), CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void profilePicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String permissions[] = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, IMAGE_PICK_GALLERY_CODE);
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
        startActivityForResult(i, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == IMAGE_PICK_GALLERY_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }

    private Uri saveInGallery(Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }

    private void updateHallInfo() {

        getDataFromView();

        if (!allValidation())
            return;

        //check Internet Connection
        if (!checkInternetConnection()) {
            return;
        }

        //check and delete previous images if needed
        deletePreviousManagerProfile();

        //check and delete previous images if needed
        deletePreviousHallEntranceImages();

        CSignUpData cSignUpData = new CSignUpData(sHallMarqueeName, sManagerFirstName, sManagerLastName,
                sManagerProfileDownloadUri, sHallMarqueeEmail, sHallMarqueePhoneNo, sHallMarqueeCity,
                sHallMarqueeLocation, sSpotLights, sMusic, sACHeater, sParking,
                sLGetHallEntranceImagesDownloadUri);

        //FireBase work
        new CUploadSignUpData(this, cSignUpData, uriManagerProfile, null, sHallMarquee,
                sManagerProfileName, sLHallEntranceImagesUri, sLHallEntranceImageNames);
    }

    private void deletePreviousManagerProfile() {

        if (sManagerProfileName.equals("mProfile")) {

            progressDialog.setMessage("Deleting Previous Manager Profile...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            getInstance().getReferenceFromUrl(uriOldManagerProfile)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void deletePreviousHallEntranceImages() {

        progressDialog.setMessage("Deleting Previous Hall Entrance Images...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        int uriSize = sLHallEntranceImagesUri.size();
        int downloadSize = sLGetHallEntranceImagesDownloadUri.size();

        int i = 0;

        try {
            while (i < downloadSize) {
                int j = 0;
                while (j < uriSize) {
                    if (sLGetHallEntranceImagesDownloadUri.get(i).equals(sLHallEntranceImagesUri.get(j))) {
                        i++;
                        j = 0;
                    } else {
                        j++;
                    }
                }
                getInstance().getReferenceFromUrl(sLGetHallEntranceImagesDownloadUri.get(i))
                        .delete();
                i++;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        progressDialog.dismiss();
    }

    private void getDataFromView() {

        sHallMarqueeName = etHallMarqueeName.getText().toString().trim();
        sManagerFirstName = etManagerFirstName.getText().toString().trim();
        sManagerLastName = etManagerLastName.getText().toString().trim();
        sHallMarqueeEmail = cSignUpData.getsEmail();
        sHallMarqueePhoneNo = metHallMarqueePhoneNo.getText().toString().trim();
        sHallMarqueeCity = etHallMarqueeCity.getText().toString().trim();
        sHallMarqueeLocation = etHallMarqueeLocation.getText().toString().trim();

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
        CValidations hallInfoCValidations = new CValidations();

        if (hallInfoCValidations.validateHallMarqueeName(etHallMarqueeName, sHallMarqueeName)) {
            etHallMarqueeName.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etHallMarqueeName.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_white));

        if (hallInfoCValidations.validateName(etManagerFirstName, sManagerFirstName)) {
            etManagerFirstName.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etManagerFirstName.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_white));

        if (hallInfoCValidations.validateName(etManagerLastName, sManagerLastName)) {
            etManagerLastName.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etManagerLastName.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_white));

        if (uriManagerProfile == null) {
            cCustomToast.makeText(getApplicationContext(), "Please select Manager Profile Picture");
            return false;
        }

        if (hallInfoCValidations.validatePhoneNo(metHallMarqueePhoneNo, sHallMarqueePhoneNo)) {
            metHallMarqueePhoneNo.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            metHallMarqueePhoneNo.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_white));

        if (hallInfoCValidations.validateCity(etHallMarqueeCity, sHallMarqueeCity)) {
            etHallMarqueeCity.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etHallMarqueeCity.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_white));

        if (hallInfoCValidations.validateLocation(etHallMarqueeLocation, sHallMarqueeLocation)) {
            etHallMarqueeLocation.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etHallMarqueeLocation.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_white));

        if (sLHallEntranceImagesUri.size() < 6) {
            cCustomToast.makeText(getApplicationContext(), "kindly select minimum 5 Hall Entrance pictures");
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

    //Select image and set to imageView
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK_GALLERY_CODE || requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            if (bImageFlag) {
                if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                    if (resultCode == RESULT_OK) {

                        //take image from camera
                        Bitmap bitmap;
                        Bundle b = data.getExtras();
                        assert b != null;
                        bitmap = (Bitmap) b.getParcelable("data");
                        assert bitmap != null;
                        uriManagerProfile = saveInGallery(bitmap);

                        //get Image Name
                        CGetImageName objCGetImageNAme = new CGetImageName(getApplicationContext());
                        sManagerProfileName = objCGetImageNAme.getImageName(uriManagerProfile);

                        try {
                            Glide.with(getApplicationContext()).load(uriManagerProfile).into(ivManagerProfile);
                            bImageFlag = false;
                        } catch (Exception e) {
                            cCustomToast.makeText(getApplicationContext(), e.getMessage());
                        }

                    } else {
                        // Result was a failure
                        cCustomToast.makeText(getApplicationContext(), "Picture wasn't taken!");
                    }
                } else {
                    if (data.getData() != null) {
                        uriManagerProfile = data.getData();

                        //get Image Name
                        CGetImageName objCGetImageNAme = new CGetImageName(getApplicationContext());
                        sManagerProfileName = objCGetImageNAme.getImageName(uriManagerProfile);

                        try {
                            Glide.with(getApplicationContext()).load(uriManagerProfile).into(ivManagerProfile);
                            bImageFlag = false;
                        } catch (Exception e) {
                            cCustomToast.makeText(getApplicationContext(), e.getMessage());
                        }
                    }
                }
            } else if (data != null) {
                Uri mImageUri = null;
                if (data.getData() != null) {

                    ivUploadImage.setVisibility(View.GONE);
                    rvHallInfo.setVisibility(View.VISIBLE);

                    mImageUri = data.getData();

                    //get Image Name
                    CGetImageName CGetImageNAme = new CGetImageName(getApplicationContext());
                    final String sImageName = CGetImageNAme.getImageName(mImageUri);
                    sLHallEntranceImageNames.add(sImageName);

                    sLHallEntranceImagesUri.add(mImageUri.toString());
                    sLHallEntranceImagesUri.add(Uri.parse("android.resource://com.example.mhbadmin/drawable/ic_upload_image").toString());
                    sLHallEntranceImageNames.add("ic_upload_image");
                    selectImagesAdapter.notifyDataSetChanged();
                } else {
                    if (data.getClipData() != null) {

                        ivUploadImage.setVisibility(View.GONE);
                        rvHallInfo.setVisibility(View.VISIBLE);

                        ClipData mClipData = data.getClipData();

                        int imageCount = mClipData.getItemCount();

                        for (int i = 0; i < imageCount; i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            mImageUri = item.getUri();

                            //get Image Name
                            CGetImageName CGetImageNAme = new CGetImageName(getApplicationContext());
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
                    cCustomToast.makeText(getApplicationContext(), "kindly select minimum 5 Sub Hall pictures");
                }
            }
        } else {
            if (sLHallEntranceImagesUri.size() > 0) {
                sLHallEntranceImagesUri.add(Uri.parse("android.resource://com.example.mhbadmin/drawable/ic_upload_image").toString());
                sLHallEntranceImageNames.add("ic_upload_image");
                selectImagesAdapter.notifyDataSetChanged();
            }
            cCustomToast.makeText(getApplicationContext(), "You haven't picked Image");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}