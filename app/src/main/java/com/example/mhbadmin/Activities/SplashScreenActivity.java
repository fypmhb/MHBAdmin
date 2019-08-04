package com.example.mhbadmin.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mhbadmin.Classes.CCustomToast;
import com.example.mhbadmin.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        connectivity();

        checkFireBaseState();

    }

    private void connectivity() {

        mAuth = FirebaseAuth.getInstance();
    }

    private void checkFireBaseState() {

        CCustomToast cCustomToast = new CCustomToast();
        cCustomToast.makeText(getApplicationContext(), "Splash Screen");

//        Toast.makeText(this, "Splash Screen", Toast.LENGTH_SHORT).show();
        //if user already login
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
            finish();
        } else {
            //if user not login
            startActivity(new Intent(getApplicationContext(), LogInSignUpActivity.class));
            finish();
        }
    }

}
