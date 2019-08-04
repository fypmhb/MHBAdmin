package com.example.mhbadmin.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.mhbadmin.Classes.CDeleteEntireHall;
import com.example.mhbadmin.R;

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
        private Preference
                //pSwitchHallInfo = null,
                pUpdateHallInfo = null,
                pUpdateSubHallInfo = null,
                pUpdatePassword = null,
                pDeleteSubHall = null,
                pDeleteHall = null,
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
                        Toast.makeText(getActivity(), "gg", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getActivity(), "nn", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });*/

            pUpdateHallInfo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), UpdateHallInfoActivity.class));
                    return false;
                }
            });

            pUpdateSubHallInfo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), UpdateSubHallInfoActivity.class));
                    return false;
                }
            });

            pUpdatePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), UpdatePasswordActivity.class));
                    return false;
                }
            });

            pDeleteSubHall.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), DeleteSubHallActivity.class));
                    return false;
                }
            });

            pDeleteHall.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are you sure to delete entire hall?\nThis will delete all sub halls too.");
                    builder.setTitle("Please Confirm!");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new CDeleteEntireHall(getActivity());
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog1 = builder.create();
                    dialog1.show();
                    return false;
                }
            });

            pAddSubHAll.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), AddSubHallActivity.class));
                    return false;
                }
            });
        }

        private void connectivity() {

            //pSwitchHallInfo = (Preference) findPreference("switch_hall_info");
            pUpdateHallInfo = (Preference) findPreference("p_hall_info");
            pUpdateSubHallInfo = (Preference) findPreference("p_sub_hall_info");
            pUpdatePassword = (Preference) findPreference("p_update_password");
            pDeleteSubHall = (Preference) findPreference("p_delete_sub_hall");
            pDeleteHall = (Preference) findPreference("p_delete_entire_hall");
            pAddSubHAll = (Preference) findPreference("p_add_hall");
        }
    }
}