package com.example.mhbadmin.Notification.Sending;

public class Data {

    private String userId;
    private int icon;
    private String title;
    private String body;
    private String receiver;
    private String sSubHallId;
    private String cUserData;
    private String cBookingData;

    public Data() {
    }

    public Data(String userId, int icon, String title, String body, String receiver, String sSubHallId, String cUserData, String cBookingData) {
        this.userId = userId;
        this.icon = icon;
        this.title = title;
        this.body = body;
        this.receiver = receiver;
        this.sSubHallId = sSubHallId;
        this.cUserData = cUserData;
        this.cBookingData = cBookingData;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getsSubHallId() {
        return sSubHallId;
    }

    public void setsSubHallId(String sSubHallId) {
        this.sSubHallId = sSubHallId;
    }

    public String getcUserData() {
        return cUserData;
    }

    public void setcUserData(String cUserData) {
        this.cUserData = cUserData;
    }

    public String getcBookingData() {
        return cBookingData;
    }

    public void setcBookingData(String cBookingData) {
        this.cBookingData = cBookingData;
    }
}