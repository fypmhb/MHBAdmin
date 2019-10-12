package com.example.mhbadmin.Classes.Models.ObjectBox;

import android.content.Context;

import io.objectbox.BoxStore;

public class DBObjectBox {

    private static BoxStore boxStore = null;

    public static void init(Context context) {
        boxStore = MyObjectBox.builder()
                .androidContext(context.getApplicationContext())
                .build();
    }

    public static BoxStore getBoxStore() {
        return boxStore;
    }
}