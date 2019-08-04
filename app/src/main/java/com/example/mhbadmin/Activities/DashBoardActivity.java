package com.example.mhbadmin.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.mhbadmin.AdapterClasses.ImageSwipeAdapter;
import com.example.mhbadmin.Classes.CSignUpData;
import com.example.mhbadmin.Notification.Receiving.Token;
import com.example.mhbadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Timer;
import java.util.TimerTask;

public class DashBoardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar = null;
    private DrawerLayout drawer = null;
    private NavigationView navigationView = null;

    private ViewPager mViewPager = null;
    private ImageView ivManagerProfile = null;
    private TextView tvHallMarqueeName = null,
            tvManagerName = null,
            tvHallsMarquees = null,
            tvAveragePerHeadRate = null,
            tvLocation = null,
            tvNavigationDashBoardHallMarqueeName = null;

    //FireBase Work

    private SharedPreferences sp = null;

    public static final String META_DATA = "meta data";
    public static final String SUB_HALL_COUNTER = "Sub Hall Counter";

    private ProgressDialog progressDialog = null;
    private FirebaseFirestore firebaseFirestore = null;

    private String sHallMarquee = null,
            userId = null;

    private CSignUpData cSignUpData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        connectivity();

        //Notification
        updateToken(FirebaseInstanceId.getInstance().getToken());

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //FireBase Work
        checkFireBaseState();
    }

    private void connectivity() {

        //FireBase work

        sp = getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        ivManagerProfile = (ImageView) findViewById(R.id.iv_manager_profile);
        tvHallMarqueeName = (TextView) findViewById(R.id.tv_hall_marquee_name);
        tvManagerName = (TextView) findViewById(R.id.tv_manager_name);
        tvHallsMarquees = (TextView) findViewById(R.id.tv_halls_marquees);
        tvAveragePerHeadRate = (TextView) findViewById(R.id.tv_average_per_head_rate);
        tvLocation = (TextView) findViewById(R.id.tv_location);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        tvNavigationDashBoardHallMarqueeName = (TextView) headerView.findViewById(R.id.tv_navigation_dash_board_hall_marquee_name);
    }

    private void updateToken(String token) {
        Token token1 = new Token(token);

        FirebaseFirestore.getInstance()
                .collection("Tokens")
                .document(userId)
                .set(token1);
    }

    private void checkFireBaseState() {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        DocumentReference documentReference = firebaseFirestore
                .collection("Meta Data")
                .document(userId);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {

                        sHallMarquee = document.getString("meta data");

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(META_DATA, sHallMarquee);
                        editor.commit();

                        getDataFromFireBase();
                    }
                }
            }
        });
    }

    private void getDataFromFireBase() {
        final DocumentReference documentReference = firebaseFirestore
                .collection(sHallMarquee)
                .document(userId)
                .collection(sHallMarquee + " info")
                .document(sHallMarquee + " Document");

        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            cSignUpData = documentSnapshot.toObject(CSignUpData.class);
                            getSubHallNumbers();
                        }
                    }
                });
    }

    private void getSubHallNumbers() {
        DocumentReference documentReference = firebaseFirestore
                .collection(sHallMarquee)
                .document(userId);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        int addHallCount = 0;
                        if (document.getString(SUB_HALL_COUNTER) != null) {
                            addHallCount = Integer.parseInt(document.getString(SUB_HALL_COUNTER));
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt(SUB_HALL_COUNTER, addHallCount);
                            editor.commit();
                            showDataOnView();
                        }
                    } else {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt(SUB_HALL_COUNTER, 0);
                        editor.commit();
                        showDataOnView();
                    }
                }
            }
        });
    }

    private void showDataOnView() {

        ImageSwipeAdapter imageSwipeAdapter = new ImageSwipeAdapter(getApplicationContext(),
                cSignUpData.getsLHallEntranceDownloadImagesUri());
        mViewPager.setAdapter(imageSwipeAdapter);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 3000, 4000);

        Glide.with(getApplicationContext())
                .load(cSignUpData.getsManagerProfileImageUri())
                .into(ivManagerProfile);
        String sHallMarqueeName = cSignUpData.getsHallMarqueeName();
        tvHallMarqueeName.setText(sHallMarqueeName);
        tvNavigationDashBoardHallMarqueeName.setText(sHallMarqueeName);
        String sManagerName = cSignUpData.getsManagerFirstName() + " " + cSignUpData.getsManagerLastName();
        tvManagerName.setText(sManagerName);
        //find average Per head
        tvLocation.setText(cSignUpData.getsLocation());

        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            recreate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
            finish();
        } else if (id == R.id.nav_hall_detail) {
            startActivity(new Intent(getApplicationContext(), SubHallMarqueeDetailActivity.class));
        } else if (id == R.id.nav_requests) {
            startActivity(new Intent(getApplicationContext(), RequestBookingListActivity.class));
        } else if (id == R.id.nav_bookings) {
            startActivity(new Intent(getApplicationContext(), RequestBookingListActivity.class));
        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_log_out) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure to log out?");
            builder.setTitle("Please Confirm!");
            builder.setCancelable(false);
            builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.clear();
                    editor.commit();
                    startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
                    finish();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog1 = builder.create();
            dialog1.show();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        } else if (id == R.id.nav_about_us) {
            startActivity(new Intent(this, AboutUsActivity.class));
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure to exit?");
            builder.setTitle("Please Confirm!");
            builder.setCancelable(false);
            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog1 = builder.create();
            dialog1.show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // timer class for image swipe
    public class MyTimerTask extends TimerTask {

        int numberOfHallEntranceImages = cSignUpData.getsLHallEntranceDownloadImagesUri().size() - 1;
        int i = 0;

        @Override
        public void run() {
            DashBoardActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mViewPager.getCurrentItem() == i) {
                        i = i + 1;
                        mViewPager.setCurrentItem(i);
                    } else if (i == numberOfHallEntranceImages + 1) {
                        i = 0;
                        mViewPager.setCurrentItem(i);
                    }
                }
            });
        }
    }
}