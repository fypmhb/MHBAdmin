package com.example.mhbadmin.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mhbadmin.Classes.CCustomToast;
import com.example.mhbadmin.Classes.CNetworkConnection;
import com.example.mhbadmin.Classes.CValidations;
import com.example.mhbadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class FForgetPassword extends Fragment implements View.OnClickListener {

    private CCustomToast cCustomToast = null;

    private View fragmentView = null;
    private Context context = null;
    private EditText etResetEmail = null;
    private Button btnResetPassword = null,
            btnBack = null;

    private ProgressDialog progressDialog = null;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_forget_password, container, false);

        connectivity();

        setClickListeners();

        getDataFromLogIn();

        return fragmentView;
    }

    private void connectivity() {
        context = getActivity();

        cCustomToast = new CCustomToast();

        progressDialog = new ProgressDialog(context);

        etResetEmail = (EditText) fragmentView.findViewById(R.id.et_reset_email);
        btnResetPassword = (Button) fragmentView.findViewById(R.id.btn_reset_password);
        btnBack = (Button) fragmentView.findViewById(R.id.btn_back);
    }

    private void setClickListeners() {

        //setting click listeners on buttons
        btnResetPassword.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void getDataFromLogIn() {
        Bundle email = getArguments();
        if (email != null)
            etResetEmail.setText(email.getString("email"));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_reset_password) {
            resetPassword();
        } else if (v.getId() == R.id.btn_back) {
            replaceFragment();
        }
    }

    private void resetPassword() {

        //check Internet Connection
        if (!checkInternetConnection()) {
            return;
        }

        String sEmail = etResetEmail.getText().toString().trim();

        CValidations forgetPasswordCValidations = new CValidations();
        if (forgetPasswordCValidations.validateEmail(etResetEmail, sEmail)) {
            etResetEmail.setBackground(context.getResources().getDrawable(R.drawable.round_red));
            return;
        } else
            etResetEmail.setBackground(context.getResources().getDrawable(R.drawable.round_white));

        progressDialog.setMessage("Sending Mail...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.sendPasswordResetEmail(sEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                replaceFragment();
                cCustomToast.makeText(context, "Password is reset\nCheck your mail");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                cCustomToast.makeText(context, e.getMessage());
            }
        });
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

    private void replaceFragment() {
        String sEmail = etResetEmail.getText().toString().trim();
        Bundle email = new Bundle();
        FLogIn passEmail = new FLogIn();

        if (!TextUtils.isEmpty(sEmail) && Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.putString("email", sEmail);
            passEmail.setArguments(email);
        }

        assert getFragmentManager() != null;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.LogInSignUp, passEmail);
        ft.commit();
        progressDialog.dismiss();
    }
}