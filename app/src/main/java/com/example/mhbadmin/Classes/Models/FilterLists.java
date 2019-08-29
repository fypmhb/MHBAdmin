package com.example.mhbadmin.Classes.Models;

public class FilterLists {

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

    private String sRequestTime = null,
            sSubHallName = null,
            sFunctionDate = null,
            sNoOfGuests = null,
            sFunctionTiming = null,
            sDish = null,
            sPerHead = null,
            sEstimatedBudget = null,
            sOtherDetail = null,
            sAcceptDeniedTiming=null;

    public FilterLists() {
    }

    public FilterLists(String sUserFirstName, String sUserLastName, String sUserProfileImageUri, String sEmail, String sPhoneNo, String sCity, String sCountry, String sLocation, String sSubHallId, String sUserID, String sRequestTime, String sSubHallName, String sFunctionDate, String sNoOfGuests, String sFunctionTiming, String sDish, String sPerHead, String sEstimatedBudget, String sOtherDetail, String sAcceptDeniedTiming) {
        this.sUserFirstName = sUserFirstName;
        this.sUserLastName = sUserLastName;
        this.sUserProfileImageUri = sUserProfileImageUri;
        this.sEmail = sEmail;
        this.sPhoneNo = sPhoneNo;
        this.sCity = sCity;
        this.sCountry = sCountry;
        this.sLocation = sLocation;
        this.sSubHallId = sSubHallId;
        this.sUserID = sUserID;
        this.sRequestTime = sRequestTime;
        this.sSubHallName = sSubHallName;
        this.sFunctionDate = sFunctionDate;
        this.sNoOfGuests = sNoOfGuests;
        this.sFunctionTiming = sFunctionTiming;
        this.sDish = sDish;
        this.sPerHead = sPerHead;
        this.sEstimatedBudget = sEstimatedBudget;
        this.sOtherDetail = sOtherDetail;
        this.sAcceptDeniedTiming = sAcceptDeniedTiming;
    }

    public String getsUserFirstName() {
        return sUserFirstName;
    }

    public void setsUserFirstName(String sUserFirstName) {
        this.sUserFirstName = sUserFirstName;
    }

    public String getsUserLastName() {
        return sUserLastName;
    }

    public void setsUserLastName(String sUserLastName) {
        this.sUserLastName = sUserLastName;
    }

    public String getsUserProfileImageUri() {
        return sUserProfileImageUri;
    }

    public void setsUserProfileImageUri(String sUserProfileImageUri) {
        this.sUserProfileImageUri = sUserProfileImageUri;
    }

    public String getsEmail() {
        return sEmail;
    }

    public void setsEmail(String sEmail) {
        this.sEmail = sEmail;
    }

    public String getsPhoneNo() {
        return sPhoneNo;
    }

    public void setsPhoneNo(String sPhoneNo) {
        this.sPhoneNo = sPhoneNo;
    }

    public String getsCity() {
        return sCity;
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

    public String getsLocation() {
        return sLocation;
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

    public String getsUserID() {
        return sUserID;
    }

    public void setsUserID(String sUserID) {
        this.sUserID = sUserID;
    }

    public String getsRequestTime() {
        return sRequestTime;
    }

    public void setsRequestTime(String sRequestTime) {
        this.sRequestTime = sRequestTime;
    }

    public String getsSubHallName() {
        return sSubHallName;
    }

    public void setsSubHallName(String sSubHallName) {
        this.sSubHallName = sSubHallName;
    }

    public String getsFunctionDate() {
        return sFunctionDate;
    }

    public void setsFunctionDate(String sFunctionDate) {
        this.sFunctionDate = sFunctionDate;
    }

    public String getsNoOfGuests() {
        return sNoOfGuests;
    }

    public void setsNoOfGuests(String sNoOfGuests) {
        this.sNoOfGuests = sNoOfGuests;
    }

    public String getsFunctionTiming() {
        return sFunctionTiming;
    }

    public void setsFunctionTiming(String sFunctionTiming) {
        this.sFunctionTiming = sFunctionTiming;
    }

    public String getsDish() {
        return sDish;
    }

    public void setsDish(String sDish) {
        this.sDish = sDish;
    }

    public String getsPerHead() {
        return sPerHead;
    }

    public void setsPerHead(String sPerHead) {
        this.sPerHead = sPerHead;
    }

    public String getsEstimatedBudget() {
        return sEstimatedBudget;
    }

    public void setsEstimatedBudget(String sEstimatedBudget) {
        this.sEstimatedBudget = sEstimatedBudget;
    }

    public String getsOtherDetail() {
        return sOtherDetail;
    }

    public void setsOtherDetail(String sOtherDetail) {
        this.sOtherDetail = sOtherDetail;
    }

    public String getsAcceptDeniedTiming() {
        return sAcceptDeniedTiming;
    }

    public void setsAcceptDeniedTiming(String sAcceptDeniedTiming) {
        this.sAcceptDeniedTiming = sAcceptDeniedTiming;
    }
}