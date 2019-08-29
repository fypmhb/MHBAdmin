package com.example.mhbadmin.Interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mhbadmin.Classes.Models.DBBookings;

import java.util.List;

@Dao
public interface DBBookingsDao {

    @Query("Select * from Bookings where sFunctionDate = (:sCurrentDate)")
    List<DBBookings> loadAllByFunctionDates(String sCurrentDate);

    @Insert
    void insertAll(DBBookings dbBookings);

    @Update
    void updateAll(DBBookings dbBookings);

    @Delete
    void deleteAll(DBBookings dbBookings);

}