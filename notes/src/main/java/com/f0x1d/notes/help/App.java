package com.f0x1d.notes.help;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.f0x1d.notes.help.db.Database;
import com.f0x1d.notes.help.utils.UselessUtils;

import androidx.room.Room;

import io.fabric.sdk.android.Fabric;

import static com.f0x1d.notes.help.utils.UselessUtils.lol;

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

        if (!UselessUtils.check()){
            throw new RuntimeException();
        }

        String x = "";

        for (char c : lol) {
            x = x + c;
        }

        database = Room.databaseBuilder(this, Database.class, "database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        if (!x.equals(App.getInstance().getPackageName())){
            throw new RuntimeException();
        }
    }

    public Database getDatabase() {
        return database;
    }
}