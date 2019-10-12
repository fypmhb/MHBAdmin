package com.example.mhbadmin.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mhbadmin.Classes.CCustomToast;
import com.example.mhbadmin.Classes.CNetworkConnection;
import com.example.mhbadmin.Classes.CValidations;
import com.example.mhbadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout rlCloseKeyboard = null;

    private CCustomToast cCustomToast = null;

    private EditText etOldPassword = null,
            etNewPassword = null,
            etConformPassword = null;


    private String sOldPassword = null,
            sNewPassword = null,
            sConformPassword = null;

    private Button btnUpdatePassword = null;

    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        connectivity();

        setClickListeners();
    }

    private void connectivity() {

        cCustomToast = new CCustomToast();

        progressDialog=new ProgressDialog(this);

        rlCloseKeyboard = (RelativeLayout) findViewById(R.id.rl_hide_soft_keyboard);

        etOldPassword = (EditText) findViewById(R.id.et_old_password);
        etNewPassword = (EditText) findViewById(R.id.et_new_password);
        etConformPassword = (EditText) findViewById(R.id.et_conform_password);

        btnUpdatePassword = (Button) findViewById(R.id.btn_update_password);
    }

    private void setClickListeners()
    {
        //set Click Listeners
        rlCloseKeyboard.setOnClickListener(this);
        btnUpdatePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_hide_soft_keyboard) {
            hideSoftKeyboard(v);
        } else if (v.getId() == R.id.btn_update_password) {
            updatePassword();
        }
    }


    private void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void updatePassword() {
        getDataFromView();

        if (!allValidation())
            return;

        //check Internet Connection
        if (!checkInternetConnection()) {
            return;
        }

        updatePasswordToFireBase();
    }

    private void getDataFromView() {
        sOldPassword = etOldPassword.getText().toString().trim();
        sNewPassword = etNewPassword.getText().toString().trim();
        sConformPassword = etConformPassword.getText().toString().trim();
    }

    private boolean allValidation() {

        CValidations signUpCValidations = new CValidations();

        if (TextUtils.isEmpty(sOldPassword)) {
            etOldPassword.requestFocus();
            etOldPassword.setBackground(getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etNewPassword.setBackground(getResources().getDrawable(R.drawable.round_white));

        //Password must different from old password
        if (!TextUtils.isEmpty(sNewPassword)) {
            if (sNewPassword.equals(sOldPassword)) {
                etNewPassword.setError("Password must different from old password");
                etNewPassword.requestFocus();
                return false;
            }
        } else
            etNewPassword.setBackground(getResources().getDrawable(R.drawable.round_white));

        //validate new Password
        if (signUpCValidations.ValidatePassword(etNewPassword, sNewPassword)) {
            etNewPassword.setBackground(getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etNewPassword.setBackground(getResources().getDrawable(R.drawable.round_white));


        if (signUpCValidations.validateConformPassword(etConformPassword, sConformPassword, sNewPassword, "Password doesn't match")) {
            etConformPassword.setBackground(getResources().getDrawable(R.drawable.round_red));
            return false;
        } else
            etConformPassword.setBackground(getResources().getDrawable(R.drawable.round_white));

        return true;
    }

    private boolean checkInternetConnection() {
        // if there is no internet connection
        CNetworkConnection CNetworkConnection = new CNetworkConnection();
        if (CNetworkConnection.isConnected(getApplicationContext())) {
            CNetworkConnection.buildDialog(getApplicationContext()).show();
            return false;
        }
        return true;
    }

    private void updatePasswordToFireBase() {

        progressDialog.setMessage("Updating...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        final FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();

        assert mAuth != null;
        String sUserEmail = mAuth.getEmail();

        assert sUserEmail != null;
        AuthCredential credential = EmailAuthProvider
                .getCredential(sUserEmail, sOldPassword);

        mAuth.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mAuth.updatePassword(sNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        cCustomToast.makeText(getApplicationContext(), "Password updated");
                                        conformationContinueLogOut();
                                    } else {
                                        //error. get and show error message
                                        progressDialog.dismiss();
                                        cCustomToast.makeText(getApplicationContext(), task.getException().getMessage());
                                    }
                                }
                            });
                        } else {
                            etOldPassword.setText("");
                            etNewPassword.setText("");
                            etConformPassword.setText("");
                            progressDialog.dismiss();
                            cCustomToast.makeText(getApplicationContext(), task.getException().getMessage());
                        }
                    }
                });
    }

    private void conformationContinueLogOut() {
        progressDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Dou you want to continue session or log out?");
        builder.setTitle("Please Confirm!");
        builder.setCancelable(false);
        builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
                finish();
            }
        }).setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
                dialog.dismiss();
            }
        });
        AlertDialog dialog1 = builder.create();
        dialog1.show();
    }
}