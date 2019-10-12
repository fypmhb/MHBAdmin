package com.example.mhbadmin.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.mhbadmin.AdapterClasses.ImageSwipeAdapter;
import com.example.mhbadmin.Classes.CNetworkConnection;
import com.example.mhbadmin.Classes.Delete.CDeleteSubHall;
import com.example.mhbadmin.Classes.Models.CSubHallData;
import com.example.mhbadmin.R;
import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;
import static com.example.mhbadmin.Activities.DashBoardActivity.S_SUB_HALL_DOCUMENT_ID;
import static com.example.mhbadmin.Activities.DashBoardActivity.S_SUB_HALL_OBJECT;
import static com.example.mhbadmin.Fragments.FUpdateSubHallInfo.S_SUB_HALL_OBJECT_ID;


public class FShowDeleteSubHall extends Fragment implements View.OnClickListener {

    private View view = null;

    private Context context = null;

    private TextView tvAveragePerHeadRate = null,
            tvSubHallName = null,
            tvFloorNo = null,
            tvHallCapacity = null,
            tvSweetDish = null,
            tvSalad = null,
            tvDrink = null,
            tvNan = null,
            tvRise = null,
            tvChickenPerHead = null,
            tvMuttonPerHeadRate = null,
            tvBeefPerHeadRate = null;

    private Button btnDeleteSubHall = null;

    private ViewPager imageViewPager = null;

    //FireBase work

    private SharedPreferences sp = null;

    private ProgressDialog progressDialog = null;

    private String sHallMarquee = null,
            sActivityName = null;

    private String sSubHallDocumentId = null,
            getsSubHallObjectId = null,
            getsSubHallObject = null;

    private CSubHallData cSubHallData = null;

    public FShowDeleteSubHall() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sSubHallDocumentId = getArguments().getString(S_SUB_HALL_DOCUMENT_ID);
            getsSubHallObjectId = getArguments().getString(S_SUB_HALL_OBJECT_ID);
            getsSubHallObject = getArguments().getString(S_SUB_HALL_OBJECT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_show_delete_sub_hall, container, false);

        connectivity();

        checkFireBaseState();

        //set click listener on button
        btnDeleteSubHall.setOnClickListener(this);

        return view;
    }

    private void connectivity() {

        context = getActivity();

        sp = getActivity().getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(getActivity());

        tvAveragePerHeadRate = (TextView) view.findViewById(R.id.tv_sub_hall_average_per_head_rate);
        tvSubHallName = (TextView) view.findViewById(R.id.tv_sub_hall_name);
        tvFloorNo = (TextView) view.findViewById(R.id.tv_floor_no);
        tvHallCapacity = (TextView) view.findViewById(R.id.tv_hall_capacity);
        tvSweetDish = (TextView) view.findViewById(R.id.tv_sweet_dish);
        tvSalad = (TextView) view.findViewById(R.id.tv_salad);
        tvDrink = (TextView) view.findViewById(R.id.tv_drink);
        tvNan = (TextView) view.findViewById(R.id.tv_nan);
        tvRise = (TextView) view.findViewById(R.id.tv_rise);
        tvChickenPerHead = (TextView) view.findViewById(R.id.tv_chicken_per_head_rate);
        tvMuttonPerHeadRate = (TextView) view.findViewById(R.id.tv_mutton_per_head_rate);
        tvBeefPerHeadRate = (TextView) view.findViewById(R.id.tv_beef_per_head_rate);

        btnDeleteSubHall = (Button) view.findViewById(R.id.btn_delete_sub_hall);

        imageViewPager = (ViewPager) view.findViewById(R.id.images_view_pager);
    }

    private void checkFireBaseState() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if (sp.getString("sDeleteShowSubHall", null) != null)
            this.sActivityName = sp.getString("sDeleteShowSubHall", null);

        if (sp.getString(META_DATA, null) != null)
            this.sHallMarquee = sp.getString(META_DATA, null);

        getDataFromSharedPreferences();
    }

    private void getDataFromSharedPreferences() {

        cSubHallData = new Gson().fromJson(getsSubHallObject, CSubHallData.class);

        showDataOnView();
    }

    private void showDataOnView() {

        ImageSwipeAdapter imageSwipeAdapter = new ImageSwipeAdapter(getActivity(),
                cSubHallData.getsLGetAddHallImagesDownloadUri());
        imageViewPager.setAdapter(imageSwipeAdapter);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new FShowDeleteSubHall.MyTimerTask(), 3000, 4000);

        int chickenPerHead = Integer.parseInt(cSubHallData.getsChickenPerHead()),
                muttonPerHead = Integer.parseInt(cSubHallData.getsMuttonPerHead()),
                beefPerHead = Integer.parseInt(cSubHallData.getsBeefPerHead());
        double averagePerHeadRate = (chickenPerHead + muttonPerHead + beefPerHead) / 3;

        tvAveragePerHeadRate.setText("Average Per Head = " + averagePerHeadRate);
        tvSubHallName.setText(cSubHallData.getsSubHallName());

        assert sHallMarquee != null;
        if (sHallMarquee.equals("Marquee")) {
            tvFloorNo.setVisibility(View.GONE);
            TextView tvFloorNo_ = (TextView) view.findViewById(R.id.tv_floor_no_);
            tvFloorNo_.setVisibility(View.GONE);
        } else
            tvFloorNo.setText(cSubHallData.getsSubHallFloorNo());

        tvHallCapacity.setText(cSubHallData.getsHallCapacity());
        tvSweetDish.setText(cSubHallData.getsSweetDish());
        tvSalad.setText(cSubHallData.getsSalad());
        tvDrink.setText(cSubHallData.getsDrink());
        tvNan.setText(cSubHallData.getsNan());
        tvRise.setText(cSubHallData.getsRise());
        tvChickenPerHead.setText(cSubHallData.getsChickenPerHead());
        tvMuttonPerHeadRate.setText(cSubHallData.getsMuttonPerHead());
        tvBeefPerHeadRate.setText(cSubHallData.getsBeefPerHead());

        if (sActivityName.equals("show")) {
            btnDeleteSubHall.setVisibility(View.GONE);
        }

        progressDialog.dismiss();
    }

    public static FShowDeleteSubHall newInstance(String sSubHallDocumentId, String sSubHallObjectId, String sSubHallObject) {
        FShowDeleteSubHall fragment = new FShowDeleteSubHall();
        Bundle args = new Bundle();
        args.putString(S_SUB_HALL_DOCUMENT_ID, sSubHallDocumentId);
        args.putString(S_SUB_HALL_OBJECT_ID, sSubHallObjectId);
        args.putString(S_SUB_HALL_OBJECT, sSubHallObject);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_delete_sub_hall) {

            //check Internet Connection
            if (!checkInternetConnection()) {
                return;
            }

            new CDeleteSubHall(context, true, sSubHallDocumentId, getsSubHallObjectId,
                    cSubHallData.getsLGetAddHallImagesDownloadUri());
        }
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

    // timer class for image swipe
    public class MyTimerTask extends TimerTask {

        int numberOfSubHallImages = cSubHallData.getsLGetAddHallImagesDownloadUri().size() - 1;
        int i = 0;

        @Override
        public void run() {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (imageViewPager.getCurrentItem() == i) {
                            i = i + 1;
                            imageViewPager.setCurrentItem(i);
                        } else if (i == numberOfSubHallImages + 1) {
                            i = 0;
                            imageViewPager.setCurrentItem(i);
                        }
                    }
                });
            }
        }
    }
}