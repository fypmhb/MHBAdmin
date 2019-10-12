package com.example.mhbadmin.Classes.Models;

public class CRequestBookingData {

    private String sRequestTime = null,
            sSubHallName = null,
            sSubHallId = null,
            sFunctionDate = null,
            sNoOfGuests = null,
            sFunctionTiming = null,
            sDish = null,
            sPerHead = null,
            sEstimatedBudget = null,
            sOtherDetail = null,
            sAcceptDeniedTiming = null;

    public CRequestBookingData() {
    }

    public CRequestBookingData(String sRequestTime, String sSubHallName, String sSubHallId, String sFunctionDate, String sNoOfGuests, String sFunctionTiming, String sDish, String sPerHead, String sEstimatedBudget, String sOtherDetail, String sAcceptDeniedTiming) {
        this.sRequestTime = sRequestTime;
        this.sSubHallName = sSubHallName;
        this.sSubHallId = sSubHallId;
        this.sFunctionDate = sFunctionDate;
        this.sNoOfGuests = sNoOfGuests;
        this.sFunctionTiming = sFunctionTiming;
        this.sDish = sDish;
        this.sPerHead = sPerHead;
        this.sEstimatedBudget = sEstimatedBudget;
        this.sOtherDetail = sOtherDetail;
        this.sAcceptDeniedTiming = sAcceptDeniedTiming;
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

    public String getsSubHallId() {
        return sSubHallId;
    }

    public void setsSubHallId(String sSubHallId) {
        this.sSubHallId = sSubHallId;
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