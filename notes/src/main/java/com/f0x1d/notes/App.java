package com.f0x1d.notes;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.f0x1d.notes.db.Database;
import com.f0x1d.notes.utils.UselessUtils;

import androidx.room.Room;

import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private Database database;

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        database = Room.databaseBuilder(this, Database.class, "database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public Database getDatabase() {
        return database;
    }
}