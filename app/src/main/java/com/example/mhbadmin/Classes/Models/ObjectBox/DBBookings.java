package com.example.mhbadmin.Classes.Models.ObjectBox;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class DBBookings {

    @Id
    public long id;

    public String UserData;

    public String RequestBooking;

    public String sFunctionDate;

    public DBBookings() {
    }

    public DBBookings(String userData, String requestBooking, String sFunctionDate) {
        UserData = userData;
        RequestBooking = requestBooking;
        this.sFunctionDate = sFunctionDate;
    }
}