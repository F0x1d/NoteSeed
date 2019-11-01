package com.f0x1d.notes;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.crashlytics.android.Crashlytics;
import com.f0x1d.notes.db.Database;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.fragment.editing.NoteEditFragment;
import com.f0x1d.notes.service.CaptureNoteNotificationService;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.analytics.DeviceInfoCollector;
import com.f0x1d.notes.utils.translations.IncorrectTranslationError;
import com.f0x1d.notes.utils.translations.Translations;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public final class App extends Application {

    private static App instance;
    private static SharedPreferences defaultSharedPrefs;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Database database;

    public static App getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static SharedPreferences getDefaultSharedPreferences() {
        return defaultSharedPrefs == null ? defaultSharedPrefs = PreferenceManager.getDefaultSharedPreferences(instance) : defaultSharedPrefs;
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

        if (App.getDefaultSharedPreferences().getBoolean("showCaptureNotification", false)) {
            Intent intent = new Intent(this, CaptureNoteNotificationService.class);
            startService(intent);
            bindService(intent, UselessUtils.EMPTY_SERVICE_CONNECTION, 0);
        }

        if (App.getDefaultSharedPreferences().getBoolean("shouldSend", true))
            new SendDevice().execute();
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

            if (positions.containsKey(note.inFolderId))
                position = positions.get(note.inFolderId);
            else
                position = 0;
            positions.put(note.inFolderId, position + 1);

            dao.updatePosition(position, note.id);
        }

        UselessUtils.edit().putBoolean("inited_pos", true).apply();
    }

    public void exportStrings() {
        try {
            File mainFolder = new File(Environment.getExternalStorageDirectory() + "/Notes/utils");
            if (!mainFolder.exists())
                mainFolder.mkdirs();

            for (File file : mainFolder.listFiles()) {
                if (file.getName().contains("strings ") && file.getName().contains(".json")) {
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
            if (!strings.exists()) {
                try {
                    NoteEditFragment.copy(getAssets().open("stringKeys.json"), strings);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Logger.log(e);
        }
    }

    public class SendDevice extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Logger.log(new Gson().toJson(new DeviceInfoCollector().collect(App.this)));
                URL obj = new URL("https://f0x1d.gq/api/noteseed/newUser?text=" + URLEncoder.encode(new Gson().toJson(new DeviceInfoCollector().collect(App.this)), "utf8"));
                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                if (response.toString().equals("vzlom"))
                    App.getDefaultSharedPreferences().edit().putBoolean("shouldSend", false).apply();
            } catch (Exception e) {
                Logger.log(e);
            }
            return null;
        }
    }
}
