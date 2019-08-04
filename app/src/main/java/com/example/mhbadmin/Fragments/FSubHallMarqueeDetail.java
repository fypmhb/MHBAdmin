package com.example.mhbadmin.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.mhbadmin.AdapterClasses.ImageSwipeAdapter;
import com.example.mhbadmin.Classes.CSubHallData;
import com.example.mhbadmin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;

public class FSubHallMarqueeDetail extends Fragment {

    static final String S_SUB_HALL_DOCUMENT_ID = "S_SUB_HALL_DOCUMENT_ID";

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

    private ViewPager imageViewPager = null;

    //FireBase work

    private SharedPreferences sp = null;

    private ProgressDialog progressDialog = null;

    private String userId = null,
            sHallMarquee = null;

    private FirebaseFirestore firebaseFirestore = null;

    private String sSubHallDocumentId = null;

    private CSubHallData CSubHallData = null;


    public FSubHallMarqueeDetail() {
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
        view = inflater.inflate(R.layout.fragment_sub_hall_marquee_detail, container, false);

        connectivity();

        checkFireBaseState();

        return view;
    }

    private void connectivity() {

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
                            CSubHallData = documentSnapshot.toObject(CSubHallData.class);
                            showDataToView();
                        }
                    }
                });
    }

    private void showDataToView() {

        ImageSwipeAdapter imageSwipeAdapter = new ImageSwipeAdapter(getActivity(), CSubHallData.getsLGetAddHallImagesDownloadUri());
        imageViewPager.setAdapter(imageSwipeAdapter);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 3000, 4000);

        int chickenPerHead = Integer.parseInt(CSubHallData.getsChickenPerHead()),
                muttonPerHead = Integer.parseInt(CSubHallData.getsMuttonPerHead()),
                beefPerHead = Integer.parseInt(CSubHallData.getsBeefPerHead());
        double averagePerHeadRate = (chickenPerHead + muttonPerHead + beefPerHead) / 3;

        tvAveragePerHeadRate.setText("Average Per Head = " + averagePerHeadRate);
        tvSubHallName.setText(CSubHallData.getsSubHallName());

        assert sHallMarquee != null;
        if (sHallMarquee.equals("Marquee")) {
            tvFloorNo.setVisibility(View.GONE);
            TextView tvFloorNo_ = (TextView) view.findViewById(R.id.tv_floor_no_);
            tvFloorNo_.setVisibility(View.GONE);
        } else
            tvFloorNo.setText(CSubHallData.getsSubHallFloorNo());

        tvHallCapacity.setText(CSubHallData.getsHallCapacity());
        tvSweetDish.setText(CSubHallData.getsSweetDish());
        tvSalad.setText(CSubHallData.getsSalad());
        tvDrink.setText(CSubHallData.getsDrink());
        tvNan.setText(CSubHallData.getsNan());
        tvRise.setText(CSubHallData.getsRise());
        tvChickenPerHead.setText(CSubHallData.getsChickenPerHead());
        tvMuttonPerHeadRate.setText(CSubHallData.getsMuttonPerHead());
        tvBeefPerHeadRate.setText(CSubHallData.getsBeefPerHead());

        progressDialog.dismiss();
    }

    public static FSubHallMarqueeDetail newInstance(String sSubHallDocumentId) {
        FSubHallMarqueeDetail fragment = new FSubHallMarqueeDetail();
        Bundle args = new Bundle();
        args.putString(S_SUB_HALL_DOCUMENT_ID, sSubHallDocumentId);
        fragment.setArguments(args);
        return fragment;
    }

    // timer class for image swipe
    public class MyTimerTask extends TimerTask {

        int numberOfHallEntranceImages = CSubHallData.getsLGetAddHallImagesDownloadUri().size() - 1;
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
                        } else if (i == numberOfHallEntranceImages + 1) {
                            i = 0;
                            imageViewPager.setCurrentItem(i);
                        }
                    }
                });
            }
        }
    }
}