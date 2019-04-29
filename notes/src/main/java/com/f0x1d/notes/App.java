package com.f0x1d.notes;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.crashlytics.android.Crashlytics;
import com.f0x1d.notes.db.Database;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.utils.UselessUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private FirebaseAnalytics mFirebaseAnalytics;

    private Database database;
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        Fabric.with(this, new Crashlytics());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        database = Room.databaseBuilder(this, Database.class, "noteseed_db")
                .allowMainThreadQueries()
                .addMigrations(Database.MIGRATION_8_9, Database.MIGRATION_9_10)
                .build();

        initPositions();
    }

    public Database getDatabase() {
        return database;
    }

    private void initPositions(){
        if (UselessUtils.getBool("inited_pos", false))
            return;

        NoteOrFolderDao dao = getDatabase().noteOrFolderDao();
        List<NoteOrFolder> notes = dao.getAll();

        HashMap<String, Integer> positions = new HashMap<>();

        for (NoteOrFolder note : notes) {
            int position;

            if (positions.containsKey(note.in_folder_id))
                position = positions.get(note.in_folder_id);
            else
                position = 0;
            positions.put(note.in_folder_id, position + 1);

            dao.updatePosition(position, note.id);
        }

        UselessUtils.edit().putBoolean("inited_pos", true).apply();
    }
}