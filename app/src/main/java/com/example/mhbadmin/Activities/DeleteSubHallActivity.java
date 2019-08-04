package com.example.mhbadmin.Activities;

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

import com.example.mhbadmin.Fragments.FDeleteSubHall;
import com.example.mhbadmin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.mhbadmin.Activities.DashBoardActivity.META_DATA;
import static com.example.mhbadmin.Activities.DashBoardActivity.SUB_HALL_COUNTER;

public class DeleteSubHallActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_delete_sub_hall);

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

        tabLayout = findViewById(R.id.tl_delete_sub_hall);
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
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(DeleteSubHallActivity.this, "Please add sub hall", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getSubHallCounter() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if (sp.getInt(SUB_HALL_COUNTER, 0) != 0) {
            int noOfTabs = sp.getInt(SUB_HALL_COUNTER, 0);
            setActivityDeleteSubHall(noOfTabs);
        }
    }

    private void setActivityDeleteSubHall(int noOfTabs) {

        progressDialog.dismiss();

        DeleteSubHallActivity.ViewPagerAdapter viewPagerAdapter = new DeleteSubHallActivity.ViewPagerAdapter(getSupportFragmentManager(), noOfTabs);
        tabsViewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(tabsViewPager, true);
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
            return FDeleteSubHall.newInstance(sp.getString("sSubHallDocumentId" + (position + 1), null));
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