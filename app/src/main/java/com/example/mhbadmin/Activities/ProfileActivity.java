package com.example.mhbadmin.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.mhbadmin.Activities.FragmentRelated.ShowDeleteSubHallActivity;
import com.example.mhbadmin.Activities.FragmentRelated.UpdateSubHallInfoActivity;
import com.example.mhbadmin.Classes.CNetworkConnection;
import com.example.mhbadmin.Classes.Delete.CDeleteEntireHall;
import com.example.mhbadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_settings, new SettingsFragment())
                .commit();

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private Context context = null;

        private Preference //pSwitchHallInfo = null,
                pUpdateHallInfo = null,
                pUpdateSubHallInfo = null,
                pUpdatePassword = null,
                pDeleteSubHall = null,
                pDeleteEntireHall = null,
                pAddSubHAll = null;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.profile_preferences, rootKey);

            connectivity();
/*
            pSwitchHallInfo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean switchHallInfo=(boolean) newValue;

                    if(switchHallInfo)
                    {
                        Toast.makeText(context, "gg", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(context, "nn", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });*/

            pUpdateHallInfo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(context, UpdateHallInfoActivity.class));
                    return false;
                }
            });

            pUpdateSubHallInfo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(context, UpdateSubHallInfoActivity.class));
                    return false;
                }
            });

            pUpdatePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(context, UpdatePasswordActivity.class));
                    return false;
                }
            });

            pDeleteSubHall.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences sp = context.getSharedPreferences("MHBAdmin", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("sDeleteShowSubHall", "delete");
                    editor.commit();
                    startActivity(new Intent(context, ShowDeleteSubHallActivity.class));
                    return false;
                }
            });

            pDeleteEntireHall.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    showConfirmationDialog();

                    return false;
                }
            });

            pAddSubHAll.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(context, AddSubHallActivity.class));
                    return false;
                }
            });
        }

        private void connectivity() {

            context = getActivity();

            //pSwitchHallInfo = (Preference) findPreference("switch_hall_info");
            pUpdateHallInfo = (Preference) findPreference("p_hall_info");
            pUpdateSubHallInfo = (Preference) findPreference("p_sub_hall_info");
            pUpdatePassword = (Preference) findPreference("p_update_password");
            pDeleteSubHall = (Preference) findPreference("p_delete_sub_hall");
            pDeleteEntireHall = (Preference) findPreference("p_delete_entire_hall");
            pAddSubHAll = (Preference) findPreference("p_add_hall");
        }

        private void showConfirmationDialog() {

            //check Internet Connection
            if (!checkInternetConnection()) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);

            View alertView = getActivity()
                    .getLayoutInflater().inflate(R.layout.layout_get_password_to_delete_entire_hall, null);

            builder.setView(alertView);

            final EditText etGetPassword = (EditText) alertView.findViewById(R.id.et_get_password);
            Button btnCancel = (Button) alertView.findViewById(R.id.btn_cancel);
            Button btnDelete = (Button) alertView.findViewById(R.id.btn_delete);

            final AlertDialog dialog = builder.create();

            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final ProgressDialog progressDialog = new ProgressDialog(context);

                    String sPassword = etGetPassword.getText().toString().trim();

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    AuthCredential authCredential = null;

                    if (!sPassword.isEmpty()) {

                        progressDialog.setMessage("Authenticating...");
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);

                        authCredential = EmailAuthProvider
                                .getCredential(firebaseUser.getEmail(), sPassword);

                        firebaseUser.reauthenticate(authCredential)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        dialog.dismiss();
                                        new CDeleteEntireHall(context);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        etGetPassword.setBackground(getActivity().getResources().getDrawable(R.drawable.round_red));
                                        progressDialog.dismiss();
                                    }
                                });
                    } else {
                        etGetPassword.setBackground(getActivity().getResources().getDrawable(R.drawable.round_red));
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
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

    }
}