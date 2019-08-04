package com.example.mhbadmin.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mhbadmin.Classes.CCustomToast;
import com.example.mhbadmin.Fragments.FRequestBookingDetail;
import com.example.mhbadmin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;
import static com.example.mhbadmin.Activities.DashBoardActivity.SUB_HALL_COUNTER;

public class RequestBookingListActivity extends AppCompatActivity {

    private TabLayout tabLayout = null;
    private ViewPager tabsViewPager = null;

    //FireBase work

    private SharedPreferences sp = null;
    SharedPreferences.Editor editor = null;

    private ProgressDialog progressDialog = null;

    private String userId = null,
            sHallMarquee = null;

    private FirebaseFirestore firebaseFirestore = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_booking_list);

        connectivity();

        checkFireBaseState();
    }

    private void connectivity() {

        sp = getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        editor = sp.edit();

        progressDialog = new ProgressDialog(this);

        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        tabsViewPager = findViewById(R.id.tabs_view_pager);

        tabLayout = findViewById(R.id.tl_hall_marquee_detail);

    }

    private void checkFireBaseState() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if (sp.getString(META_DATA, null) != null)
            sHallMarquee = sp.getString(META_DATA, null);

        assert sHallMarquee != null;
        firebaseFirestore.collection(sHallMarquee)
                .document(userId)
                .collection("Sub Hall info")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    int i = 1;
                    for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                        editor.putString("sSubHallDocumentId" + i, snapshots.getId());
                        editor.commit();
                        i += 1;
                    }
                    getSubHallCounter();
                } else {
                    progressDialog.dismiss();
                    CCustomToast cCustomToast=new CCustomToast();
                    cCustomToast.makeText(getApplicationContext(),"Please add sub hall");
                }
            }
        });

    }

    private void getSubHallCounter() {
        if (sp.getInt(SUB_HALL_COUNTER, 0) != 0) {
            int noOfTabs = sp.getInt(SUB_HALL_COUNTER, 0);
            setActivityHallMarqueeDetail(noOfTabs);
        }
    }

    private void setActivityHallMarqueeDetail(int noOfTabs) {

        progressDialog.dismiss();

        RequestBookingListActivity.ViewPagerAdapter viewPagerAdapter = new RequestBookingListActivity.ViewPagerAdapter(getSupportFragmentManager(), noOfTabs);
        tabsViewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(tabsViewPager);
    }

    //Fragment Replacement Class
    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private int noOfItems = 0;


        ViewPagerAdapter(FragmentManager fm, int noOfItems) {
            super(fm);
            this.noOfItems = noOfItems;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return FRequestBookingDetail.newInskktance(sp.getString("sSubHallDocumentId" + (position + 1), null));
        }

        @Override
        public int getCount() {
            return noOfItems;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Sub Hall " + (position + 1);
        }
    }
}