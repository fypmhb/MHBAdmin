package com.example.mhbadmin.Classes.Models;

import java.util.List;

public class CSubHallData {
    private String sSubHallName;
    private String sSubHallFloorNo;
    private String sHallCapacity;
    private String sChickenPerHead;
    private String sMuttonPerHead;
    private String sBeefPerHead;
    private String sSweetDish;
    private String sSalad;
    private String sDrink;
    private String sNan;
    private String sRise;
    private List<String> sLGetAddHallImagesDownloadUri;

    public CSubHallData() {
    }

   public CSubHallData(String sSubHallName, String sSubHallFloorNo, String sHallCapacity, String sChickenPerHead, String sMuttonPerHead, String sBeefPerHead, String sSweetDish, String sSalad, String sDrink, String sNan, String sRise, List<String> sLGetAddHallImagesDownloadUri) {
        this.sSubHallName = sSubHallName;
        this.sSubHallFloorNo = sSubHallFloorNo;
        this.sHallCapacity = sHallCapacity;
        this.sChickenPerHead = sChickenPerHead;
        this.sMuttonPerHead = sMuttonPerHead;
        this.sBeefPerHead = sBeefPerHead;
        this.sSweetDish = sSweetDish;
        this.sSalad = sSalad;
        this.sDrink = sDrink;
        this.sNan = sNan;
        this.sRise = sRise;
        this.sLGetAddHallImagesDownloadUri = sLGetAddHallImagesDownloadUri;
    }

    public CSubHallData(String sSubHallName, String sHallCapacity, String sChickenPerHead, String sMuttonPerHead, String sBeefPerHead, String sSweetDish, String sSalad, String sDrink, String sNan, String sRise, List<String> sLGetAddHallImagesDownloadUri) {
        this.sSubHallName = sSubHallName;
        this.sHallCapacity = sHallCapacity;
        this.sChickenPerHead = sChickenPerHead;
        this.sMuttonPerHead = sMuttonPerHead;
        this.sBeefPerHead = sBeefPerHead;
        this.sSweetDish = sSweetDish;
        this.sSalad = sSalad;
        this.sDrink = sDrink;
        this.sNan = sNan;
        this.sRise = sRise;
        this.sLGetAddHallImagesDownloadUri = sLGetAddHallImagesDownloadUri;
    }

    public String getsSubHallName() {
        return sSubHallName;
    }

    public String getsSubHallFloorNo() {
        return sSubHallFloorNo;
    }

    public String getsHallCapacity() {
        return sHallCapacity;
    }

    public String getsChickenPerHead() {
        return sChickenPerHead;
    }

    public String getsMuttonPerHead() {
        return sMuttonPerHead;
    }

    public String getsBeefPerHead() {
        return sBeefPerHead;
    }

    public String getsSweetDish() {
        return sSweetDish;
    }

    public String getsSalad() {
        return sSalad;
    }

    public String getsDrink() {
        return sDrink;
    }

    public String getsNan() {
        return sNan;
    }

    public String getsRise() {
        return sRise;
    }

    public List<String> getsLGetAddHallImagesDownloadUri() {
        return sLGetAddHallImagesDownloadUri;
    }
}