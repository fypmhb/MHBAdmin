package com.example.mhbadmin.Classes.Models;

public class CUserData {
    private String sUserFirstName;
    private String sUserLastName;
    private String sUserProfileImageUri;
    private String sEmail;
    private String sPhoneNo;
    private String sCity;
    private String sCountry;
    private String sLocation;
    private String sSubHallId = null;

    private String sUserID;

    public CUserData() {
    }

    public CUserData(String sUserFirstName, String sUserLastName, String sUserProfileImageUri, String sEmail,
                     String sPhoneNo, String sCity,String sCountry, String sLocation) {
        this.sUserFirstName = sUserFirstName;
        this.sUserLastName = sUserLastName;
        this.sUserProfileImageUri = sUserProfileImageUri;
        this.sEmail = sEmail;
        this.sPhoneNo = sPhoneNo;
        this.sCity = sCity;
        this.sCountry=sCountry;
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

    public void setsUserFirstName(String sUserFirstName) {
        this.sUserFirstName = sUserFirstName;
    }

    public void setsUserLastName(String sUserLastName) {
        this.sUserLastName = sUserLastName;
    }

    public void setsUserProfileImageUri(String sUserProfileImageUri) {
        this.sUserProfileImageUri = sUserProfileImageUri;
    }

    public void setsEmail(String sEmail) {
        this.sEmail = sEmail;
    }

    public void setsPhoneNo(String sPhoneNo) {
        this.sPhoneNo = sPhoneNo;
    }

    public void setsCity(String sCity) {
        this.sCity = sCity;
    }

    public String getsCountry() {
        return sCountry;
    }

    public void setsCountry(String sCountry) {
        this.sCountry = sCountry;
    }

    public void setsLocation(String sLocation) {
        this.sLocation = sLocation;
    }


    public String getsSubHallId() {
        return sSubHallId;
    }

    public void setsSubHallId(String sSubHallId) {
        this.sSubHallId = sSubHallId;
    }
}