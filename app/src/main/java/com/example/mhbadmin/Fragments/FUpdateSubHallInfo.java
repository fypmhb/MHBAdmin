package com.example.mhbadmin.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;
import static com.example.mhbadmin.Activities.DashBoardActivity.S_SUB_HALL_DOCUMENT_ID;
import static com.example.mhbadmin.Fragments.FSignUp.IMAGE_REQUEST_CODE;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class FUpdateSubHallInfo extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    static final String S_SUB_HALL_OBJECT_ID = "S_SUB_HALL_OBJECT_ID";

    private RelativeLayout rlCloseKeyboard = null;

    private Context context = null;

    private View view = null;

    private CCustomToast cCustomToast = null;

    private CSubHallData cSubHallData = null;

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

    private Button btnUpdateSubHallInfo = null;

    //FireBase work

    private SharedPreferences sp = null;

    private ProgressDialog progressDialog = null;

    private String userId = null,
            sHallMarquee = null,
            sSubHallDocumentId = null,
            sSubHallObjectId = null;

    private int noOfTabs = 0;

    public FUpdateSubHallInfo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        noOfTabs = getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : 1;
        if (getArguments() != null) {
            sSubHallDocumentId = getArguments().getString(S_SUB_HALL_DOCUMENT_ID);
            sSubHallObjectId = getArguments().getString(S_SUB_HALL_OBJECT_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_update_sub_hall_info, container, false);

        connectivity();

        setClickListeners();

        settingUpRecyclerViewAdapter();

        settingAdapterClickListener();

        checkFireBaseState();

        return view;
    }

    private void connectivity() {
        context = getActivity();

        cCustomToast = new CCustomToast();

        //FireBase Work

        sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(context);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rlCloseKeyboard = (RelativeLayout) view.findViewById(R.id.rl_hide_soft_keyboard);

        rvAddHall = (RecyclerView) view.findViewById(R.id.rv_hall_marquee_images);
        rvAddHall.setHasFixedSize(true);

        sLAddHallImagesUri = new ArrayList<String>();
        sLAddHallImageNames = new ArrayList<String>();
        sLGetAddHallImagesDownloadUri = new ArrayList<String>();

        ivUploadImage = (ImageView) view.findViewById(R.id.iv_upload_image);

        selectImagesAdapter = new SelectImagesAdapter(getActivity(), sLAddHallImagesUri, sLAddHallImageNames, rvAddHall, ivUploadImage);

        etSubHallName = (EditText) view.findViewById(R.id.et_sub_hall_name);
        etSubHallFloorNo = (EditText) view.findViewById(R.id.et_sub_hall_floor_no);
        etSubHallCapacity = (EditText) view.findViewById(R.id.et_sub_hall_capacity);
        etChickenRate = (EditText) view.findViewById(R.id.et_chicken_rate);
        etMuttonRate = (EditText) view.findViewById(R.id.et_mutton_rate);
        etBeefRate = (EditText) view.findViewById(R.id.et_beef_rate);

        rgSweetDish = (RadioGroup) view.findViewById(R.id.rg_sweet_dish);
        rgSalad = (RadioGroup) view.findViewById(R.id.rg_salad);
        rgDrink = (RadioGroup) view.findViewById(R.id.rg_drink);
        rgNan = (RadioGroup) view.findViewById(R.id.rg_nan);
        rgRise = (RadioGroup) view.findViewById(R.id.rg_rise);

        btnUpdateSubHallInfo = (Button) view.findViewById(R.id.btn_update_sub_hall_info);

    }

    private void setClickListeners() {
        //setting click listeners
        rlCloseKeyboard.setOnClickListener(this);
        ivUploadImage.setOnClickListener(this);
        btnUpdateSubHallInfo.setOnClickListener(this);
    }

    private void settingUpRecyclerViewAdapter() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
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

    private void checkFireBaseState() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if (sp.getString(META_DATA, null) != null)
            this.sHallMarquee = sp.getString(META_DATA, null);

        if (sHallMarquee.equals("Marquee"))
            etSubHallFloorNo.setVisibility(View.GONE);

        getDataFromSharedPreferences();
    }

    private void getDataFromSharedPreferences() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        String cSubHallObject = sp.getString(sSubHallObjectId, null);
        cSubHallData = new Gson().fromJson(cSubHallObject, CSubHallData.class);

        showDataOnView();
    }


    private void showDataOnView() {

        List<String> sample = cSubHallData.getsLGetAddHallImagesDownloadUri();

        for (int i = 0; i < sample.size(); i++) {
            sLAddHallImagesUri.add(sample.get(i));
            sLGetAddHallImagesDownloadUri.add(sample.get(i));
            sLAddHallImageNames.add(i + "d");
        }
        sLAddHallImagesUri.add(Uri.parse("android.resource://com.example.mhbadmin/drawable/ic_upload_image").toString());
        sLAddHallImageNames.add("ic_upload_image");

        selectImagesAdapter.notifyDataSetChanged();

        ivUploadImage.setVisibility(View.GONE);
        rvAddHall.setVisibility(View.VISIBLE);

        etSubHallName.setText(cSubHallData.getsSubHallName());

        if (sHallMarquee.equals("Hall"))
            etSubHallFloorNo.setText(cSubHallData.getsSubHallFloorNo());

        etSubHallCapacity.setText(cSubHallData.getsHallCapacity());
        etChickenRate.setText(cSubHallData.getsChickenPerHead());
        etMuttonRate.setText(cSubHallData.getsMuttonPerHead());
        etBeefRate.setText(cSubHallData.getsBeefPerHead());


        if (cSubHallData.getsSweetDish().equals("No")) {
            RadioButton b = (RadioButton) view.findViewById(R.id.rb_sweet_dish_no);
            b.setChecked(true);
        }

        if (cSubHallData.getsSalad().equals("No")) {
            RadioButton b = (RadioButton) view.findViewById(R.id.rb_salad_no);
            b.setChecked(true);
        }

        if (cSubHallData.getsDrink().equals("No")) {
            RadioButton b = (RadioButton) view.findViewById(R.id.rb_drink_no);
            b.setChecked(true);
        }

        if (cSubHallData.getsNan().equals("No")) {
            RadioButton b = (RadioButton) view.findViewById(R.id.rb_nan_no);
            b.setChecked(true);
        }

        if (cSubHallData.getsRise().equals("No")) {
            RadioButton b = (RadioButton) view.findViewById(R.id.rb_rise_no);
            b.setChecked(true);
        }

        progressDialog.dismiss();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.rl_hide_soft_keyboard) {
            hideSoftKeyboard(v);
        } else if (v.getId() == R.id.iv_upload_image) {
            intentForOpenGallery();
        } else if (v.getId() == R.id.btn_update_sub_hall_info) {
            uploadDataToFireBaseAndIntent();
        }
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void intentForOpenGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Minimum 5 Sub Hall Picture"), IMAGE_REQUEST_CODE);
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

        //check and delete previous images if needed
        deletePreviousSubHallImages();

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
        //is instantiated from addSubHallActivity (=0) or FUpdateSubHAllInfo (=noOfTabs).
        //{but =0 is not "Sub Hall Counter}
        new CUploadSubHallData(context, cSubHallData, true, noOfTabs, sSubHallDocumentId,
                userId, sLAddHallImagesUri, sLAddHallImageNames, sHallMarquee);
    }

    private void deletePreviousSubHallImages() {

        progressDialog.setMessage("Deleting Previous Hall Entrance Images...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        int uriSize = sLAddHallImagesUri.size();
        int downloadSize = sLGetAddHallImagesDownloadUri.size();

        int i = 0;

        try {
            while (i < downloadSize) {
                int j = 0;
                while (j < uriSize) {
                    if (sLGetAddHallImagesDownloadUri.get(i).equals(sLAddHallImagesUri.get(j))) {
                        i++;
                        j = 0;
                    } else {
                        j++;
                    }
                }
                getInstance().getReferenceFromUrl(sLGetAddHallImagesDownloadUri.get(i))
                        .delete();
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        progressDialog.dismiss();
    }

    private void getDataFromView() {

        sSubHallName = etSubHallName.getText().toString().trim();

        if (sHallMarquee.equals("Hall"))
            sSubHallFloorNo = etSubHallFloorNo.getText().toString().trim();
        else
            sSubHallFloorNo = "0";

        sSubHallCapacity = etSubHallCapacity.getText().toString().trim();
        sChickenRate = etChickenRate.getText().toString().trim();
        sMuttonRate = etMuttonRate.getText().toString().trim();
        sBeefRate = etBeefRate.getText().toString().trim();

        //get Checked radio button text(Yes/No)
        int rbCheckedId = 0;
        RadioButton rb;

        //sweet Dish
        rbCheckedId = rgSweetDish.getCheckedRadioButtonId();
        rb = (RadioButton) view.findViewById(rbCheckedId);
        srbSweetDish = rb.getText().toString();

        //Salad
        rbCheckedId = rgSalad.getCheckedRadioButtonId();
        rb = (RadioButton) view.findViewById(rbCheckedId);
        srbSalad = rb.getText().toString();

        //Drink
        rbCheckedId = rgDrink.getCheckedRadioButtonId();
        rb = (RadioButton) view.findViewById(rbCheckedId);
        srbDrink = rb.getText().toString();

        //Nan
        rbCheckedId = rgNan.getCheckedRadioButtonId();
        rb = (RadioButton) view.findViewById(rbCheckedId);
        srbNan = rb.getText().toString();

        //Rise
        rbCheckedId = rgRise.getCheckedRadioButtonId();
        rb = (RadioButton) view.findViewById(rbCheckedId);
        srbRise = rb.getText().toString();
    }

    private boolean allValidation() {

        CValidations addHallCValidations = new CValidations();

        if (addHallCValidations.validateHallMarqueeName(etSubHallName, sSubHallName)) {
            etSubHallName.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etSubHallName.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (sHallMarquee.equals("Hall")) {
            if (addHallCValidations.validateSubHallFloorNo(etSubHallFloorNo, sSubHallFloorNo)) {
                etSubHallFloorNo.setBackground(context.getResources().getDrawable(R.drawable.round_red));
                return false;
            } else
                etSubHallFloorNo.setBackground(context.getResources().getDrawable(R.drawable.round_white));
        }

        if (addHallCValidations.validateSubHallCapacity(etSubHallCapacity, sSubHallCapacity)) {
            etSubHallCapacity.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etSubHallCapacity.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (addHallCValidations.validatePerHead(etChickenRate, sChickenRate)) {
            etChickenRate.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etChickenRate.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (addHallCValidations.validatePerHead(etMuttonRate, sMuttonRate)) {
            etMuttonRate.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etMuttonRate.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (addHallCValidations.validatePerHead(etBeefRate, sBeefRate)) {
            etBeefRate.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etBeefRate.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (sLAddHallImagesUri.size() < 6) {
            cCustomToast.makeText(context, "kindly select minimum 5 Sub Hall pictures");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri mImageUri = null;
        try {
            // When an Image is picked
            if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK
                    && data != null) {

                if (data.getData() != null) {

                    ivUploadImage.setVisibility(View.GONE);
                    rvAddHall.setVisibility(View.VISIBLE);

                    mImageUri = data.getData();

                    //get Image Name
                    CGetImageName CGetImageNAme = new CGetImageName(context);
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
                            CGetImageName CGetImageNAme = new CGetImageName(context);
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
                    cCustomToast.makeText(context, "kindly select minimum 5 Sub Hall pictures");
                }
            } else {
                if (sLAddHallImagesUri.size() > 0) {
                    sLAddHallImagesUri.add(Uri.parse("android.resource://com.example.mhbadmin/drawable/ic_upload_image").toString());
                    sLAddHallImageNames.add("ic_upload_image");
                    selectImagesAdapter.notifyDataSetChanged();
                }
                cCustomToast.makeText(context, "You haven't picked Image");
            }
        } catch (Exception e) {
            cCustomToast.makeText(context, "Something went wrong");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static FUpdateSubHallInfo newInstance(int sectionNumber, String sSubHallDocumentId, String sSubHallObjectId) {
        FUpdateSubHallInfo fragment = new FUpdateSubHallInfo();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(S_SUB_HALL_DOCUMENT_ID, sSubHallDocumentId);
        args.putString(S_SUB_HALL_OBJECT_ID, sSubHallObjectId);

        fragment.setArguments(args);
        return fragment;
    }
}