package com.example.mhbadmin.Classes.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Bookings")
public class DBBookings {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "cUserData")
    private String UserData;

    @ColumnInfo(name = "cRequestBooking")
    private String RequestBooking;

    @ColumnInfo(name = "sFunctionDate")
    public String sFunctionDate;

    public DBBookings(int id, String UserData, String RequestBooking, String sFunctionDate) {
        this.id = id;
        this.UserData = UserData;
        this.RequestBooking = RequestBooking;
        this.sFunctionDate = sFunctionDate;
    }

    @Ignore
    public DBBookings(String UserData, String RequestBooking, String sFunctionDate) {
        this.UserData = UserData;
        this.RequestBooking = RequestBooking;
        this.sFunctionDate = sFunctionDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserData() {
        return UserData;
    }

    public void setUserData(String cUserData) {
        this.UserData = cUserData;
    }

    public String getRequestBooking() {
        return RequestBooking;
    }

    public void setRequestBooking(String cRequestBooking) {
        this.RequestBooking = cRequestBooking;
    }

    public String getsFunctionDate() {
        return sFunctionDate;
    }

    public void setsFunctionDate(String sFunctionDate) {
        this.sFunctionDate = sFunctionDate;
    }
}
