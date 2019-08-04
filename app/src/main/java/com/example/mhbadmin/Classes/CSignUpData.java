package com.example.mhbadmin.Classes;

import java.util.List;

public class CSignUpData {
    private String sHallMarqueeName;
    private String sManagerFirstName;
    private String sManagerLastName;
    private String sManagerProfileImageUri;
    private String sEmail;
    private String sPhoneNo;
    private String sCity;
    private String sLocation;
    private String sSpotLights;
    private String sMusic;
    private String sAc_Heater;
    private String sParking;
    private List<String> sLHallEntranceDownloadImagesUri;

    public CSignUpData() {
    }

    public CSignUpData(String sHallMarqueeName, String sManagerFirstName, String sManagerLastName, String sManagerProfileImageUri, String sEmail, String sPhoneNo, String sCity, String sLocation, String sSpotLights, String sMusic, String sAc_Heater, String sParking, List<String> sLHallEntranceDownloadImagesUri) {
        this.sHallMarqueeName = sHallMarqueeName;
        this.sManagerFirstName = sManagerFirstName;
        this.sManagerLastName = sManagerLastName;
        this.sManagerProfileImageUri = sManagerProfileImageUri;
        this.sEmail = sEmail;
        this.sPhoneNo = sPhoneNo;
        this.sCity = sCity;
        this.sLocation = sLocation;
        this.sSpotLights = sSpotLights;
        this.sMusic = sMusic;
        this.sAc_Heater = sAc_Heater;
        this.sParking = sParking;
        this.sLHallEntranceDownloadImagesUri = sLHallEntranceDownloadImagesUri;
    }

    public String getsHallMarqueeName() {
        return sHallMarqueeName;
    }

    public String getsManagerFirstName() {
        return sManagerFirstName;
    }

    public String getsManagerLastName() {
        return sManagerLastName;
    }

    public String getsManagerProfileImageUri() {
        return sManagerProfileImageUri;
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

    public String getsSpotLights() {
        return sSpotLights;
    }

    public String getsMusic() {
        return sMusic;
    }

    public String getsAc_Heater() {
        return sAc_Heater;
    }

    public String getsParking() {
        return sParking;
    }

    public List<String> getsLHallEntranceDownloadImagesUri() {
        return sLHallEntranceDownloadImagesUri;
    }
}