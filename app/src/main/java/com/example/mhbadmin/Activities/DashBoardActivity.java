package com.example.mhbadmin.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
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
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.mhbadmin.Activities.FragmentRelated.RequestBookingHistoryListActivity;
import com.example.mhbadmin.Activities.FragmentRelated.ShowDeleteSubHallActivity;
import com.example.mhbadmin.AdapterClasses.ImageSwipeAdapter;
import com.example.mhbadmin.BroadcastReceiver.BRNotification;
import com.example.mhbadmin.Classes.CNetworkConnection;
import com.example.mhbadmin.Classes.Models.CRequestBookingData;
import com.example.mhbadmin.Classes.Models.CSignUpData;
import com.example.mhbadmin.Classes.Models.CSubHallData;
import com.example.mhbadmin.Classes.Models.CUserData;
import com.example.mhbadmin.Classes.Models.ObjectBox.DBBookings;
import com.example.mhbadmin.Classes.Models.ObjectBox.DBObjectBox;
import com.example.mhbadmin.Notification.Token;
import com.example.mhbadmin.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.rupins.drawercardbehaviour.CardDrawerLayout;

import java.util.Timer;
import java.util.TimerTask;

import io.objectbox.Box;

public class DashBoardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String S_SUB_HALL_DOCUMENT_ID = "S_SUB_HALL_DOCUMENT_ID";
    public static final String S_SUB_HALL_OBJECT = "S_SUB_HALL_OBJECT";
    public static final String REQUEST_BADGES = "REQUEST_BADGES";
    public static final String BOOKING_BADGE = "BOOKING_BADGE";

    private Toolbar toolbar = null;
    private CardDrawerLayout drawer = null;
    private ActionBarDrawerToggle toggle = null;
    private NavigationView navigationView = null;

    private boolean navigationFlag = false;

    private ViewPager mViewPager = null;
    private ImageView ivManagerProfile = null;
    private TextView tvHallMarqueeName = null,
            tvManagerName = null,
            tvHallMarqueeEmail = null,
            tvChangeHallMarqueeEmail = null,
            tvHallMarqueePhoneNo = null,
            tvHallMarqueeCity = null,
            tvChangeHallMarqueeCity = null,
            tvHallMarqueeLocation = null,
            tvChangeHallMarqueeLocation = null,
            tvSpotLights = null,
            tvMusic = null,
            tvACHeater = null,
            tvParking = null,
            tvNavigationDashBoardHallMarqueeName = null;

    //FireBase Work

    private SharedPreferences sp = null;

    private SharedPreferences.Editor editor = null;

    public static final String META_DATA = "meta data";
    public static final String SUB_HALL_COUNTER = "Sub Hall Counter";

    private ProgressDialog progressDialog = null;
    private FirebaseDatabase firebaseDatabase = null;

    private String sHallMarquee = null,
            userId = null;

    private CSignUpData cSignUpData = null;

    //offLine dataBase
    Box<DBBookings> bookingsBox = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        navigationFlag = true;

        connectivity();

        startService(new Intent(getApplicationContext(), BRNotification.class));

        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this, drawer, R.string.Open, R.string.Close);
        drawer.useCustomBehavior(Gravity.START); //assign custom behavior for "Left" drawer
        drawer.setViewScale(Gravity.START, 0.98f); //set height scale for main view (0f to 1f)
        drawer.setViewElevation(Gravity.START, 20);//set main view elevation when drawer open (dimension)
        drawer.setViewScrimColor(Gravity.START, Color.TRANSPARENT);//set drawer overlay coloe (color)
        drawer.setDrawerElevation(Gravity.START, 20);//set drawer elevation (dimension)
        drawer.setRadius(Gravity.START, 25);//set end container's corner radius (dimension)

        navigationView.setNavigationItemSelectedListener(this);

        fireBaseWork();
    }

    private void connectivity() {

        sp = getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);
        editor = sp.edit();

//      offLine dataBase
        bookingsBox = DBObjectBox.getBoxStore().boxFor(DBBookings.class);
        //not useAble for first time but useFull for next time visit of dashBoard
        bookingsBox.removeAll();
        editor.clear();
        editor.commit();

        //FireBase work

        progressDialog = new ProgressDialog(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        ivManagerProfile = (ImageView) findViewById(R.id.iv_manager_profile);
        tvHallMarqueeName = (TextView) findViewById(R.id.tv_hall_marquee_name);
        tvManagerName = (TextView) findViewById(R.id.tv_manager_name);
        tvHallMarqueeEmail = (TextView) findViewById(R.id.tv_hall_marquee_email);
        tvChangeHallMarqueeEmail = (TextView) findViewById(R.id.tv_change_hall_marquee_email);
        tvHallMarqueePhoneNo = (TextView) findViewById(R.id.tv_hall_marquee_phone_no);
        tvHallMarqueeCity = (TextView) findViewById(R.id.tv_hall_marquee_city);
        tvChangeHallMarqueeCity = (TextView) findViewById(R.id.tv_change_hall_marquee_city);
        tvHallMarqueeLocation = (TextView) findViewById(R.id.tv_hall_marquee_location);
        tvChangeHallMarqueeLocation = (TextView) findViewById(R.id.tv_change_hall_marquee_location);
        tvSpotLights = (TextView) findViewById(R.id.tv_spotlights);
        tvMusic = (TextView) findViewById(R.id.tv_music);
        tvACHeater = (TextView) findViewById(R.id.tv_ac_heater);
        tvParking = (TextView) findViewById(R.id.tv_parking);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (CardDrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        tvNavigationDashBoardHallMarqueeName = (TextView) headerView.findViewById(R.id.tv_navigation_dash_board_hall_marquee_name);

    }

    private void fireBaseWork() {

        //check Internet Connection
        if (!checkInternetConnection()) {
            return;
        }

        //Notification
        updateToken(FirebaseInstanceId.getInstance().getToken());

        checkFireBaseState();
    }

    private boolean checkInternetConnection() {
        // if there is no internet connection
        CNetworkConnection CNetworkConnection = new CNetworkConnection();
        if (CNetworkConnection.isConnected(DashBoardActivity.this)) {
            CNetworkConnection.buildDialog(DashBoardActivity.this).show();
            return false;
        }
        return true;
    }

    private void updateToken(String token) {
        Token token1 = new Token(token);
        firebaseDatabase.getReference("Tokens").
                child(userId).
                setValue(token1);
    }

    private void checkFireBaseState() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseDatabase.getReference("Meta Data")
                .child(userId)
                .child("meta data")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            sHallMarquee = dataSnapshot.getValue(String.class);

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(META_DATA, sHallMarquee);
                            editor.commit();

                            setView();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void setView() {
        tvChangeHallMarqueeEmail.setText(sHallMarquee+" Email");
        tvChangeHallMarqueeCity.setText(sHallMarquee+" City");
        tvChangeHallMarqueeLocation.setText(sHallMarquee+" Location");
        getBookingsDataFromFireBase();
    }

    private void getBookingsDataFromFireBase() {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Accepted Requests")
                .child("User Ids");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        final String sClient = dataSnapshot1.getKey();

                        assert sClient != null;
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(sClient)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {

                                            final CUserData cUserData = dataSnapshot.getValue(CUserData.class);

                                            final DatabaseReference databaseReference1 = databaseReference
                                                    .child(sClient)
                                                    .child(sHallMarquee + " Ids")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child("Sub Hall Ids");

                                            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.exists()) {

                                                        for (final DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {

                                                            final String sSubHallId = dataSnapshot2.getKey();

                                                            assert sSubHallId != null;
                                                            final DatabaseReference databaseReference2 = databaseReference1
                                                                    .child(sSubHallId)
                                                                    .child("Timing");

                                                            databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.exists()) {

                                                                        for (DataSnapshot dataSnapshot3 : dataSnapshot.getChildren()) {

                                                                            final String sTiming = dataSnapshot3.getKey();

                                                                            assert sTiming != null;
                                                                            databaseReference2
                                                                                    .child(sTiming)
                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                                            if (dataSnapshot.exists()) {

                                                                                                CRequestBookingData cRequestBookingData = dataSnapshot.getValue(CRequestBookingData.class);

                                                                                                assert cUserData != null;
                                                                                                cUserData.setsUserID(sClient);

                                                                                                assert cRequestBookingData != null;
                                                                                                cRequestBookingData.setsAcceptDeniedTiming(sTiming);

                                                                                                String sFunctionDate = cRequestBookingData.getsFunctionDate();

                                                                                                //save data to SQLite ObjectBox
                                                                                                Gson gson = new Gson();

                                                                                                String sUserData = gson.toJson(cUserData);
                                                                                                String sRequestBookingData = gson.toJson(cRequestBookingData);

                                                                                                DBBookings dbBookings = new DBBookings(sUserData, sRequestBookingData, sFunctionDate);

                                                                                                bookingsBox.put(dbBookings);

                                                                                                progressDialog.dismiss();

                                                                                                getDataFromFireBase();
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }
                                                    } else {
                                                        //FireBase Work
                                                        progressDialog.dismiss();
                                                        getDataFromFireBase();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                } else {//FireBase Work
                    progressDialog.dismiss();
                    getDataFromFireBase();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDataFromFireBase() {

        firebaseDatabase
                .getReference(sHallMarquee)
                .child(userId)
                .child(sHallMarquee + " info")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            cSignUpData = dataSnapshot.getValue(CSignUpData.class);


                            Gson gson = new Gson();

                            String sSignUpData = gson.toJson(cSignUpData);

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("sSignUpData", sSignUpData);
                            editor.commit();
                            getSubHallNumbers();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getSubHallNumbers() {

        DatabaseReference databaseReference = firebaseDatabase
                .getReference(sHallMarquee)
                .child(userId)
                .child("Sub Hall Counter");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int addHallCount = 0;
//                    if (dataSnapshot.getValue() != null) {
                    addHallCount = Integer.parseInt(dataSnapshot.getValue(String.class));
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt(SUB_HALL_COUNTER, addHallCount);
                    editor.commit();
                    getSubHallDocumentIdAndSubHallObject();
//                    }
                } else {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt(SUB_HALL_COUNTER, 0);
                    editor.commit();
                    showDataOnView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSubHallDocumentIdAndSubHallObject() {

        final DatabaseReference databaseReference = firebaseDatabase
                .getReference(sHallMarquee)
                .child(userId)
                .child("Sub Hall info");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int i = 1;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            String sSubHallDocumentId = dataSnapshot1.getKey();
                            editor.putString("sSubHallDocumentId" + i, sSubHallDocumentId);
                            editor.commit();

                            final int finalI = i;
                            assert sSubHallDocumentId != null;
                            databaseReference.child(sSubHallDocumentId)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                CSubHallData cSubHallData = dataSnapshot.getValue(CSubHallData.class);
                                                editor.putString("sSubHallObject" + finalI, new Gson().toJson(cSubHallData));
                                                //for update and delete purpose
                                                editor.putString("sSubHallObjectId" + finalI, "sSubHallObject" + finalI);
                                                editor.commit();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                        i += 1;
                    }
                    showDataOnView();
                }
                progressDialog.dismiss();
                showDataOnView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        tvHallMarqueeEmail.setText(cSignUpData.getsEmail());
        tvHallMarqueePhoneNo.setText(cSignUpData.getsPhoneNo());
        tvHallMarqueeCity.setText(cSignUpData.getsCity());
        tvHallMarqueeLocation.setText(cSignUpData.getsLocation());
        tvSpotLights.setText(cSignUpData.getsSpotLights());
        tvMusic.setText(cSignUpData.getsMusic());
        tvACHeater.setText(cSignUpData.getsAc_Heater());
        tvParking.setText(cSignUpData.getsParking());

        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            editor.clear().commit();
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
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            fireBaseWork();
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
            if (!navigationFlag) {
                startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                finish();
            }
        } else if (id == R.id.nav_hall_detail) {
            navigationFlag = false;
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("sDeleteShowSubHall", "show");
            editor.commit();
            startActivity(new Intent(getApplicationContext(), ShowDeleteSubHallActivity.class));
        } else if (id == R.id.nav_requests) {
            navigationFlag = false;
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("sRequestBookingHistory", "Booking Requests");
            editor.commit();
            startActivity(new Intent(getApplicationContext(), RequestBookingHistoryListActivity.class));
        } else if (id == R.id.nav_bookings) {
            navigationFlag = false;
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("sRequestBookingHistory", "Accepted Requests");
            editor.commit();
            startActivity(new Intent(getApplicationContext(), RequestBookingHistoryListActivity.class));
        } else if (id == R.id.nav_history) {
            navigationFlag = false;
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("sRequestBookingHistory", "Canceled Requests");
            editor.putString("sHistoryAcceptedRequests", "Succeed Requests");
            editor.commit();
            startActivity(new Intent(getApplicationContext(), RequestBookingHistoryListActivity.class));
        } else if (id == R.id.nav_log_out) {
            logOut();
        } else if (id == R.id.nav_settings) {
            navigationFlag = false;
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        } else if (id == R.id.nav_about_us) {
            navigationFlag = false;
            startActivity(new Intent(this, AboutUsActivity.class));
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_exit) {
            exit();
        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
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

                //removing data from objectBox
                bookingsBox.removeAll();
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
    }

    private void exit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to exit?");
        builder.setTitle("Please Confirm!");
        builder.setCancelable(false);
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();

                //removing data from objectBox
                bookingsBox.removeAll();
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