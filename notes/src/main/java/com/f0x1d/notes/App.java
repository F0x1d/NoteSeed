package com.f0x1d.notes;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.db.Database;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import io.fabric.sdk.android.Fabric;

import static androidx.constraintlayout.widget.Constraints.TAG;

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

        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("notes_err", "subscribed");
                    }
                });

        database = Room.databaseBuilder(this, Database.class, "database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public Database getDatabase() {
        return database;
    }
}