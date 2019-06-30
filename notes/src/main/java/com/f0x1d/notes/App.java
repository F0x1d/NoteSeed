package com.f0x1d.notes;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.f0x1d.notes.db.Database;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.translations.IncorrectTranslationError;
import com.f0x1d.notes.utils.translations.Translations;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.List;

public final class App extends Application {

    private static App instance;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Database database;

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

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        database = Room.databaseBuilder(this, Database.class, "noteseed_db")
                .allowMainThreadQueries()
                .addMigrations(Database.MIGRATION_8_9, Database.MIGRATION_9_10)
                .build();

        try {
            Translations.init(getApplicationContext());
        } catch (IncorrectTranslationError incorrectTranslationError) {
            Logger.log(incorrectTranslationError);
        }

        initPositions();
    }

    public Database getDatabase() {
        return database;
    }

    private void initPositions() {
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