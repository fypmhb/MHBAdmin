package com.example.mhbadmin.Classes.AbstractClasses;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mhbadmin.Classes.Models.DBBookings;
import com.example.mhbadmin.Interfaces.DBBookingsDao;

@Database(entities = DBBookings.class, exportSchema = false, version = 1)
public abstract class BookingsDataBase extends RoomDatabase {

    private static final String DB_NAME = "DBBookings";

    private static BookingsDataBase instance = null;

    public static synchronized BookingsDataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), BookingsDataBase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract DBBookingsDao dbBookingsDao();
}
