package com.example.mhbadmin.Classes;

public class CUserData {
    private String sUserFirstName;
    private String sUserLastName;
    private String sUserProfileImageUri;
    private String sEmail;
    private String sPhoneNo;
    private String sCity;
    private String sLocation;

    private String sUserID;

    public CUserData() {
    }

    public CUserData(String sUSerFirstName, String sUserLastName, String sUserProfileImageUri, String sEmail, String sPhoneNo, String sCity, String sLocation) {
        this.sUserFirstName = sUserFirstName;
        this.sUserLastName = sUserLastName;
        this.sUserProfileImageUri = sUserProfileImageUri;
        this.sEmail = sEmail;
        this.sPhoneNo = sPhoneNo;
        this.sCity = sCity;
        this.sLocation = sLocation;
    }


    public String getsUserFirstName() {
        return sUserFirstName;
    }

    public String getsUserLastName() {
        return sUserLastName;
    }

    public String getsUserProfileImageUri() {
        return sUserProfileImageUri;
    }

    public String getsEmail() {
        return sEmail;
    }

    public String getsPhoneNo() {
        return sPhoneNo;
    }

    public String getsCity() {
        return sCity;
    }

    public String getsLocation() {
        return sLocation;
    }

    public String getsUserID() {
        return sUserID;
    }

    public void setsUserID(String sUserID) {
        this.sUserID = sUserID;
    }
}
