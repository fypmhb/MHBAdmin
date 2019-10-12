package com.example.mhbadmin.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mhbadmin.Activities.DashBoardActivity;
import com.example.mhbadmin.Classes.CCustomToast;
import com.example.mhbadmin.Classes.CNetworkConnection;
import com.example.mhbadmin.Classes.CValidations;
import com.example.mhbadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FLogIn extends Fragment implements View.OnClickListener {

    private CCustomToast cCustomToast = null;

    private View fragmentView = null;
    private Context context = null;

    private RelativeLayout rlCloseKeyboard = null;

    private Button btnLogin = null;
    private TextView tvForgetPassword = null,
            tvCreateOne = null;
    private EditText etEmail = null,
            etPassword = null;
    private ToggleButton tbShowHidePassword = null;
    private String sEmail = null,
            sPassword = null;

    private ProgressDialog progressDialog = null;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_log_in, container, false);

        connectivity();

        setClickListeners();

        getDataFromForgetPassword();

        //underline the create one text view
        tvCreateOne.setPaintFlags(tvCreateOne.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        return fragmentView;
    }

    private void connectivity() {

        context = getActivity();

        cCustomToast = new CCustomToast();

        progressDialog = new ProgressDialog(context);

        rlCloseKeyboard = (RelativeLayout) fragmentView.findViewById(R.id.rl_hide_soft_keyboard);

        etEmail = (EditText) fragmentView.findViewById(R.id.et_hall_marquee_email);
        etPassword = (EditText) fragmentView.findViewById(R.id.et_hall_password);
        tbShowHidePassword = (ToggleButton) fragmentView.findViewById(R.id.tb_show_hide_password);

        btnLogin = (Button) fragmentView.findViewById(R.id.btn_login);
        tvForgetPassword = (TextView) fragmentView.findViewById(R.id.tv_forget_password);
        tvCreateOne = (TextView) fragmentView.findViewById(R.id.tv_create_one);
    }

    private void setClickListeners() {
        //setting click listeners
        rlCloseKeyboard.setOnClickListener(this);
        tbShowHidePassword.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvCreateOne.setOnClickListener(this);
    }

    private void getDataFromForgetPassword() {
        Bundle email = getArguments();
        if (email != null)
            etEmail.setText(email.getString("email"));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_hide_soft_keyboard) {
            hideSoftKeyboard(v);
        } else if (v.getId() == R.id.tb_show_hide_password) {
            showHidePassword();
            //login setup
        } else if (v.getId() == R.id.btn_login) {
            logIn();
        } else if (v.getId() == R.id.tv_forget_password) {
            forgetPassword();
        } else if (v.getId() == R.id.tv_create_one) {
            assert getFragmentManager() != null;
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.LogInSignUp, new FSignUp());
            ft.commit();
        }
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showHidePassword() {

        //hide or show password
        if (tbShowHidePassword.isChecked()) {
            tbShowHidePassword.setChecked(true);
            tbShowHidePassword.setBackgroundResource(R.drawable.ic_hide_password);
            etPassword.setTransformationMethod(null);
        } else {
            tbShowHidePassword.setChecked(false);
            tbShowHidePassword.setBackgroundResource(R.drawable.ic_show_password);
            etPassword.setTransformationMethod(new PasswordTransformationMethod());
        }
        //move pointer at end of password
        etPassword.setSelection(etPassword.length());
    }

    private void logIn() {

        sEmail = etEmail.getText().toString().trim().toLowerCase();
        sPassword = etPassword.getText().toString().trim();

        if (!allValidation())
            return;

        //check Internet Connection
        if (!checkInternetConnection()) {
            return;
        }

        logInHallMarqueeToFireBase();
    }

    private boolean allValidation() {

        CValidations loginCValidations = new CValidations();
        if (loginCValidations.validateEmail(etEmail, sEmail)) {
            etEmail.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etEmail.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        if (loginCValidations.ValidatePassword(etPassword, sPassword)) {
            etPassword.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etPassword.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        return true;
    }

    private boolean checkInternetConnection() {
        // if there is no internet connection
        CNetworkConnection CNetworkConnection = new CNetworkConnection();
        if (CNetworkConnection.isConnected(context)) {
            CNetworkConnection.buildDialog(context).show();
            return false;
        }
        return true;
    }

    private void logInHallMarqueeToFireBase() {

        progressDialog.setMessage("Login...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        //check email belongs to Hall or Marquee not to User

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(sEmail, sPassword)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(context, DashBoardActivity.class));
                        progressDialog.dismiss();
                        getActivity().finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        cCustomToast.makeText(context, e.getMessage());
                    }
                });
    }

    private void forgetPassword() {
        String sEmail = etEmail.getText().toString().trim();
        Bundle email = new Bundle();
        FForgetPassword passEmail = new FForgetPassword();

        if (!TextUtils.isEmpty(sEmail) && Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.putString("email", sEmail);
            passEmail.setArguments(email);
        }

        assert getFragmentManager() != null;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.LogInSignUp, passEmail);
        ft.commit();
    }
}