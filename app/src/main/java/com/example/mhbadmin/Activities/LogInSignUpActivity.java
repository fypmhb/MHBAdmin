package com.example.mhbadmin.Activities;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.mhbadmin.Fragments.FLogIn;
import com.example.mhbadmin.R;

public class LogInSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_sign_up);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.LogInSignUp, new FLogIn());
        ft.commit();
    }
}