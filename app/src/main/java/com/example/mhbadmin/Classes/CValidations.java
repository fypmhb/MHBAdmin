package com.example.mhbadmin.Classes;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

import com.santalu.maskedittext.MaskEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CValidations {

    public boolean validateHallMarqueeName(EditText etHallMarqueeName, String sHallMarqueeName) {
        if (TextUtils.isEmpty((sHallMarqueeName))) {
            etHallMarqueeName.requestFocus();
            return true;
        } else if (sHallMarqueeName.length() < 6) {
            etHallMarqueeName.setError("Hall name must contain 6 characters");
            etHallMarqueeName.requestFocus();
            return true;
        } else if (!isValidName(sHallMarqueeName)) {
            etHallMarqueeName.setError("Hall name must contain only letters");
            etHallMarqueeName.requestFocus();
            return true;
        }
        return false;
    }

    public boolean validateName(EditText etName, String sName) {
        if (TextUtils.isEmpty(sName)) {
            etName.requestFocus();
            return true;
        } else if (sName.length() < 3) {
            etName.setError("Name must contain 3 characters");
            etName.requestFocus();
            return true;
        } else if (!isValidName(sName)
        ) {
            etName.setError("Name must contain only letters");
            etName.requestFocus();
            return true;
        }
        return false;
    }

    public boolean validateEmail(EditText etEmail, String sEmail) {
        if (TextUtils.isEmpty(sEmail)) {
            etEmail.requestFocus();
            return true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return true;
        }
        return false;
    }

    public boolean validatePhoneNo(MaskEditText metPhoneNo, String sPhoneNo) {
        if (TextUtils.isEmpty(sPhoneNo)) {
            metPhoneNo.requestFocus();
            return true;
        } else if (sPhoneNo.length() < 13) {
            metPhoneNo.setError("Please enter a valid phone no");
            metPhoneNo.requestFocus();
            return true;
        }
        return false;
    }

    public boolean validateCity(EditText etCity, String sCity) {
        if (TextUtils.isEmpty(sCity)) {
            etCity.requestFocus();
            return true;
        } else if (sCity.length() < 5) {
            etCity.setError("Please enter a valid city name");
            etCity.requestFocus();
            return true;
        } else if (!isValidAddress(sCity)) {
            etCity.setError("City name must contain only letters");
            etCity.requestFocus();
            return true;
        }
        return false;
    }

    public boolean validateLocation(EditText etLocation, String sLocation) {
        if (TextUtils.isEmpty(sLocation)) {
            etLocation.requestFocus();
            return true;
        } else if (sLocation.length() < 5) {
            etLocation.setError("Please enter a valid country name");
            etLocation.requestFocus();
            return true;
        } else if (!isValidAddress(sLocation)) {
            etLocation.setError("Please Enter a valid location");
            etLocation.requestFocus();
            return true;
        }
        return false;
    }

    public boolean ValidatePassword(EditText etPassword, String sPassword) {
        if (TextUtils.isEmpty(sPassword)) {
            etPassword.requestFocus();
            return true;
        } else if (!isValidPassword(sPassword)) {
            etPassword.setError("Password must Contain 8 number, small and capital characters");
            etPassword.requestFocus();
            return true;
        }
        return false;
    }

    private boolean isValidPassword(String password) {
        final String PASSWORD_PATTERN = "^(?=\\D*\\d)(?=[^a-z]*[a-z])(?=[^A-Z]*[A-Z])[\\w~@#$%^&*+=`|{}:;!.?\\\"\\s()\\[\\]-]{8,25}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private boolean isValidName(String name) {
        Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
        Matcher ms = ps.matcher(name);
        return ms.matches();
    }

    private boolean isValidAddress(String address) {
        Pattern ps = Pattern.compile("^[a-zA-Z ,#0-9]+$");
        Matcher ms = ps.matcher(address);
        return ms.matches();
    }

    public boolean validateConformPassword(EditText etConfirmPassword, String sConformPassword, String sPassword, String sError) {
        if (TextUtils.isEmpty(sConformPassword)) {
            etConfirmPassword.requestFocus();
            return true;
        } else if (!sConformPassword.equals(sPassword)) {
            etConfirmPassword.setError(sError);
            etConfirmPassword.requestFocus();
            return true;
        }
        return false;
    }

    public boolean validateSubHallFloorNo(EditText etHallFloorNo, String sHallFloorNo) {
        if (TextUtils.isEmpty(sHallFloorNo)) {
            etHallFloorNo.requestFocus();
            return true;
        } else {
            int floorNo = Integer.parseInt(sHallFloorNo);
            if (floorNo < 1 || floorNo > 10) {
                etHallFloorNo.setError("Hall floor must lye in between 0 & 10");
                etHallFloorNo.requestFocus();
                return true;
            }
        }
        return false;
    }

    public boolean validateSubHallCapacity(EditText etHallCapacity, String sHallCapacity) {
        if (TextUtils.isEmpty(sHallCapacity)) {
            etHallCapacity.requestFocus();
            return true;
        } else {
            int hallCapacity = Integer.parseInt(sHallCapacity);
            if (hallCapacity < 50 || hallCapacity > 2000) {
                etHallCapacity.setError("At least 50 or at last 2000 guests required");
                etHallCapacity.requestFocus();
                return true;
            } else if (hallCapacity % 10 != 0) {
                etHallCapacity.setError("No of guests must be divisible of 10");
                etHallCapacity.requestFocus();
                return true;
            }
        }
        return false;
    }

    public boolean validatePerHead(EditText etPerHead, String sPerHead) {
        if (TextUtils.isEmpty(sPerHead)) {
            etPerHead.requestFocus();
            return true;
        } else {
            int perHead = Integer.parseInt(sPerHead);
            if (perHead < 100 || perHead > 5000) {
                etPerHead.setError("Per head at least R.S 100 or at last 5000 required");
                etPerHead.requestFocus();
                return true;
            } else if (perHead % 10 != 0) {
                etPerHead.setError("Per Head must be divisible of 10");
                etPerHead.requestFocus();
                return true;
            }
        }
        return false;
    }
}