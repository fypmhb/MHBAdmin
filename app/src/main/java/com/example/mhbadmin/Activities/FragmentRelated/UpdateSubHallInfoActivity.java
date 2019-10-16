package com.example.mhbadmin.Activities.FragmentRelated;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mhbadmin.Classes.Models.CSubHallData;
import com.example.mhbadmin.Fragments.FUpdateSubHallInfo;
import com.example.mhbadmin.R;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import static com.example.mhbadmin.Activities.DashBoardActivity.SUB_HALL_COUNTER;

public class UpdateSubHallInfoActivity extends AppCompatActivity {

    private TabLayout tabLayout = null;
    private ViewPager tabsViewPager = null;

    //FireBase work

    private SharedPreferences sp = null;

    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sub_hall_info);

        connectivity();

        checkFireBaseState();
    }

    private void connectivity() {

        sp = getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);

        tabsViewPager = findViewById(R.id.tabs_view_pager);

        tabLayout = findViewById(R.id.tl_hall_marquee_update);

    }

    private void checkFireBaseState() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        getSubHallCounter();
    }

    private void getSubHallCounter() {

        if (sp.getInt(SUB_HALL_COUNTER, 0) != 0) {
            int noOfTabs = sp.getInt(SUB_HALL_COUNTER, 0);
            setActivityHallMarqueeDetail(noOfTabs);
        } else {
            Toast.makeText(this, "Please add sub hall", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private void setActivityHallMarqueeDetail(int noOfTabs) {

        progressDialog.dismiss();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), noOfTabs);
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
            return FUpdateSubHallInfo.newInstance(position + 1,
                    sp.getString("sSubHallDocumentId" + (position + 1), null),
                    sp.getString("sSubHallObjectId" + (position + 1), null));
        }

        @Override
        public int getCount() {
            return noOfItems;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String sSubHallObject = sp.getString("sSubHallObject" + (position + 1), null);
            CSubHallData subHallObject = new Gson().fromJson(sSubHallObject, CSubHallData.class);
            return subHallObject.getsSubHallName();
        }
    }
}