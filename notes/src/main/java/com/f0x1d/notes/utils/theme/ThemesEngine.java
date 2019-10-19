package com.f0x1d.notes.utils.theme;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.f0x1d.notes.App;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ThemesEngine {

    public static int background;
    public static int statusBarColor;
    public static int navBarColor;
    public static int textColor;
    public static int accentColor;
    public static int iconsColor;
    public static int textHintColor;
    public static int toolbarColor;
    public static int toolbarTextColor;
    public static int fabColor;
    public static int fabIconColor;
    public static int defaultNoteColor;
    public static boolean dark;
    public static int seekBarColor;
    public static int seekBarThumbColor;
    public static float shadows;

    public static void importTheme(Uri uri) {
        File theme = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/" + "/theme");

        if (!theme.exists()) {
            theme.mkdirs();
        }

        String text = UselessUtils.readFileURI(uri);
        try {
            File copiedTheme = new File(theme, UselessUtils.getFileName(uri));
            FileWriter writer = new FileWriter(copiedTheme);
            writer.append(text);
            writer.flush();
            writer.close();

            App.getDefaultSharedPreferences().edit().putBoolean("custom_theme", true).apply();
            App.getDefaultSharedPreferences().edit().putString("path_theme", copiedTheme.getAbsolutePath()).apply();

            JSONObject jsonObject = new JSONObject(text);

            setupAllFromJSON(jsonObject);
            App.getDefaultSharedPreferences().edit().putBoolean("night", dark).apply();
        } catch (Exception e) {
            App.getDefaultSharedPreferences().edit().putBoolean("custom_theme", false).apply();
            Logger.log(e);
        }
    }

    public static List<Theme> getThemes() {
        File theme = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/" + "/theme");

        if (!theme.exists()) {
            theme.mkdirs();
        }

        List<Theme> themes = new ArrayList<>();

        File[] listFiles = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/theme/").listFiles();
        if (listFiles != null) {
            for (File listFile : listFiles) {
                if (listFile != null) {
                    String text = UselessUtils.readFile(listFile);

                    String name = "error!";
                    String author = "error!";
                    String cardColor = "#ffffff";
                    String cardTextColor = "#000000";

                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(text);

                        if (jsonObject.has("name"))
                            name = jsonObject.getString("name");
                        if (jsonObject.has("author"))
                            author = jsonObject.getString("author");
                        if (jsonObject.has("card_color"))
                            cardColor = jsonObject.getString("card_color");
                        if (jsonObject.has("card_text_color"))
                            cardTextColor = jsonObject.getString("card_text_color");
                    } catch (Exception e) {
                        Logger.log(e);
                    }

                    themes.add(new Theme(listFile, name, author, Color.parseColor(cardColor), Color.parseColor(cardTextColor)));
                }
            }
        }

        return themes;
    }

    public static void setupAll() {
        try {
            File path = new File(App.getDefaultSharedPreferences().getString("path_theme", ""));

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(UselessUtils.readFile(path));
            } catch (Exception e) {
                Logger.log(e);
                Toast.makeText(App.getContext(), "Error!", Toast.LENGTH_SHORT).show();
                App.getDefaultSharedPreferences().edit().putBoolean("custom_theme", false).apply();
            }

            setupAllFromJSON(jsonObject);
        } catch (Exception e) {
            App.getDefaultSharedPreferences().edit().putBoolean("custom_theme", false).apply();
        }
    }

    public static void setTheme(File path) {
        String text = UselessUtils.readFile(path);

        try {
            JSONObject jsonObject = new JSONObject(text);

            App.getDefaultSharedPreferences().edit().putBoolean("custom_theme", true).apply();
            App.getDefaultSharedPreferences().edit().putString("path_theme", path.getAbsolutePath()).apply();

            setupAllFromJSON(jsonObject);
            App.getDefaultSharedPreferences().edit().putBoolean("night", dark).apply();
        } catch (Exception e) {
            Toast.makeText(App.getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Logger.log(e);
            App.getDefaultSharedPreferences().edit().putBoolean("custom_theme", false).apply();
        }
    }

    public static void setupAllFromJSON(JSONObject jsonObject) {
        try {
            background = Color.parseColor(jsonObject.getString("background"));
        } catch (Exception e) {
            background = 0xffffffff;
        }

        try {
            statusBarColor = Color.parseColor(jsonObject.getString("status_bar_color"));
        } catch (Exception e) {
            statusBarColor = 0xffffffff;
        }

        try {
            textColor = Color.parseColor(jsonObject.getString("text_color"));
        } catch (Exception e) {
            textColor = 0xff000000;
        }

        try {
            accentColor = Color.parseColor(jsonObject.getString("accent"));
        } catch (Exception e) {
            accentColor = 0xff00ff00;
        }

        try {
            navBarColor = Color.parseColor(jsonObject.getString("nav_color"));
        } catch (Exception e) {
            navBarColor = 0xff000000;
        }

        try {
            iconsColor = Color.parseColor(jsonObject.getString("icons_color"));
        } catch (Exception e) {
            iconsColor = 0xff000000;
        }

        try {
            textHintColor = Color.parseColor(jsonObject.getString("hint_color"));
        } catch (Exception e) {
            textHintColor = Color.GRAY;
        }

        try {
            dark = jsonObject.getBoolean("dark");
        } catch (Exception e) {
            dark = false;
        }

        try {
            toolbarColor = Color.parseColor(jsonObject.getString("toolbar_color"));
        } catch (Exception e) {
            toolbarColor = Color.TRANSPARENT;
        }

        try {
            toolbarTextColor = Color.parseColor(jsonObject.getString("toolbar_text_color"));
        } catch (Exception e) {
            toolbarTextColor = 0xff000000;
        }

        try {
            fabColor = Color.parseColor(jsonObject.getString("fab_color"));
        } catch (Exception e) {
            fabColor = 0xffffffff;
        }

        try {
            fabIconColor = Color.parseColor(jsonObject.getString("fab_icon_color"));
        } catch (Exception e) {
            fabIconColor = 0xff000000;
        }

        try {
            defaultNoteColor = Color.parseColor(jsonObject.getString("default_note_color"));
        } catch (Exception e) {
            defaultNoteColor = 0xffffffff;
        }

        try {
            seekBarColor = Color.parseColor(jsonObject.getString("seekbar_color"));
        } catch (Exception e) {
            seekBarColor = 0xffffffff;
        }

        try {
            seekBarThumbColor = Color.parseColor(jsonObject.getString("seekbar_thumb_color"));
        } catch (Exception e) {
            seekBarThumbColor = 0xff888888;
        }

        try {
            shadows = Float.parseFloat(jsonObject.getString("shadows"));
        } catch (Exception e) {
            shadows = 1.0f;
        }
    }
}
