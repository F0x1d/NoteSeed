package com.f0x1d.notes;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import androidx.room.Room;

import com.f0x1d.notes.db.Database;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.fragment.editing.NoteEdit;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.translations.IncorrectTranslationError;
import com.f0x1d.notes.utils.translations.Translations;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.text.Regex;

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

        File mainFolder = new File(Environment.getExternalStorageDirectory() + "/Notes");
        if (!mainFolder.exists())
            mainFolder.mkdirs();
        exportStrings();
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

    private void exportStrings(){
        File mainFolder = new File(Environment.getExternalStorageDirectory() + "/Notes/utils");
        if (!mainFolder.exists())
            mainFolder.mkdirs();

        for (File file : mainFolder.listFiles()){
            if (file.getName().contains("strings ") && file.getName().contains(".json")){
                String versionName = file.getName().split(" ")[1].replace(".json", "");
                if (versionName.equals(BuildConfig.VERSION_NAME))
                    return;
                else {
                    file.delete();
                    break;
                }
            }
        }

        File strings = new File(mainFolder, "strings " + BuildConfig.VERSION_NAME + ".json");
        if (!strings.exists()){
            try {
                NoteEdit.copy(getAssets().open("stringKeys.json"), strings);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}