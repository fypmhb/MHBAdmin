package com.example.mhbadmin.Notification.Sending;

public class Data {

    private String user;
    private int icon;
    private String body;
    private String title;
    private String receiver;

    public Data() {
    }

    public Data(String user, int icon, String body, String title, String receiver) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.receiver = receiver;
    }

    public String getUser() {
        return user;
    }

    public int getIcon() {
        return icon;
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSent() {
        return receiver;
    }

    public void setSent(String receiver) {
        this.receiver = receiver;
    }
}