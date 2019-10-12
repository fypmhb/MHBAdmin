package com.example.mhbadmin.Classes.Models;

public class FilterLists {

   private CUserData cUserDataList;
   private CRequestBookingData cRequestBookingDataList;

   //for succeed and canceled history
   private String sActivityName=null;

    public FilterLists() {
    }

    public FilterLists(CUserData cUserDataList, CRequestBookingData cRequestBookingDataList) {
        this.cUserDataList = cUserDataList;
        this.cRequestBookingDataList = cRequestBookingDataList;
    }

    public CUserData getcUserDataList() {
        return cUserDataList;
    }

    public void setcUserDataList(CUserData cUserDataList) {
        this.cUserDataList = cUserDataList;
    }

    public CRequestBookingData getcRequestBookingDataList() {
        return cRequestBookingDataList;
    }

    public void setcRequestBookingDataList(CRequestBookingData cRequestBookingDataList) {
        this.cRequestBookingDataList = cRequestBookingDataList;
    }

    public String getsActivityName() {
        return sActivityName;
    }

    public void setsActivityName(String sActivityName) {
        this.sActivityName = sActivityName;
    }
}