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
import com.example.mhbadmin.Classes.CCustomToast;
import com.example.mhbadmin.Classes.CDeleteSubHall;
import com.example.mhbadmin.Classes.CSubHallData;
import com.example.mhbadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;
import static com.example.mhbadmin.Fragments.FSubHallMarqueeDetail.S_SUB_HALL_DOCUMENT_ID;

public class FDeleteSubHall extends Fragment implements View.OnClickListener {

    private View view = null;

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

    private String userId = null,
            sHallMarquee = null;

    private FirebaseFirestore firebaseFirestore = null;

    private String sSubHallDocumentId = null;

    private CSubHallData cSubHallData = null;

    private CCustomToast cCustomToast = null;

    public FDeleteSubHall() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sSubHallDocumentId = getArguments().getString(S_SUB_HALL_DOCUMENT_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_delete_sub_hall, container, false);

        connectivity();

        checkFireBaseState();

        //set click listener on button
        btnDeleteSubHall.setOnClickListener(this);

        return view;
    }

    private void connectivity() {

        sp = getActivity().getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(getActivity());

        cCustomToast = new CCustomToast();

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

        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

    }

    private void checkFireBaseState() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if (sp.getString(META_DATA, null) != null)
            this.sHallMarquee = sp.getString(META_DATA, null);

        getDataFromFireBase();

    }

    private void getDataFromFireBase() {

        final DocumentReference documentReference1 = firebaseFirestore
                .collection(sHallMarquee)
                .document(userId)
                .collection("Sub Hall info")
                .document(sSubHallDocumentId);

        documentReference1.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            cSubHallData = documentSnapshot.toObject(CSubHallData.class);
                            showDataToView();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                cCustomToast.makeText(getActivity(), e.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void showDataToView() {

        ImageSwipeAdapter imageSwipeAdapter = new ImageSwipeAdapter(getActivity(), cSubHallData.getsLGetAddHallImagesDownloadUri());
        imageViewPager.setAdapter(imageSwipeAdapter);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new FDeleteSubHall.MyTimerTask(), 3000, 4000);

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

        progressDialog.dismiss();
    }

    public static FDeleteSubHall newInstance(String sSubHallDocumentId) {
        FDeleteSubHall fragment = new FDeleteSubHall();
        Bundle args = new Bundle();
        args.putString(S_SUB_HALL_DOCUMENT_ID, sSubHallDocumentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_delete_sub_hall) {
            new CDeleteSubHall(getActivity(), true, sSubHallDocumentId, cSubHallData.getsLGetAddHallImagesDownloadUri());
        }

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