package com.f0x1d.notes;

import android.app.Application;
import android.content.Context;
import com.f0x1d.notes.db.Database;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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

        database = Room.databaseBuilder(this, Database.class, "database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public Database getDatabase() {
        return database;
    }
}