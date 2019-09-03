package com.example.mhbadmin.Activities.FragmentRelated;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mhbadmin.Fragments.FHistory;
import com.example.mhbadmin.R;
import com.google.android.material.tabs.TabLayout;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        connectivity();
    }

    private void connectivity() {
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.tabs_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);


        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            SharedPreferences sp = getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            switch (position) {
                case 0: {
                    editor.putString("sRequestBookingHistory", "Accepted Requests");
                    editor.commit();
                    return new FHistory();
                }
                case 1: {
                    editor.putString("sRequestBookingHistory", "Canceled Requests");
                    editor.commit();
                    return new FHistory();
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
}