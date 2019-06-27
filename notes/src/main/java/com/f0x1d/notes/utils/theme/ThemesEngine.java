package com.f0x1d.notes.utils.theme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

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
    public static int lightColorTextColor;
    public static int darkColorTextColor;
    public static int lightColorIconColor;
    public static int darkColorIconColor;
    public static boolean dark;
    public static int seekBarColor;
    public static int seekBarThumbColor;
    public static float shadows;

    public void importTheme(Uri uri, Activity activity) {
        File theme = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/" + "/theme");

        if (!theme.exists()) {
            theme.mkdirs();
        }

        InputStream fstream;

        String all = "";
        try {
            fstream = App.getInstance().getContentResolver().openInputStream(uri);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                all = all + strLine;
            }
        } catch (IOException e) {
            Logger.log(e);
        }

        try {
            File copied_theme = new File(theme, UselessUtils.getFileName(uri));
            FileWriter writer = new FileWriter(copied_theme);
            writer.append(all);
            writer.flush();
            writer.close();

            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", true).apply();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putString("path_theme", copied_theme.getAbsolutePath()).apply();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("change", true).apply();

            Intent i = activity.getBaseContext().getPackageManager().
                    getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(i);
            activity.finish();

        } catch (Exception e) {
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
            Logger.log(e);
        }
    }

    public List<Theme> getThemes() {
        File theme = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/" + "/theme");

        if (!theme.exists()) {
            theme.mkdirs();
        }

        List<Theme> themes = new ArrayList<>();

        File[] listFiles = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/theme/").listFiles();
        if (listFiles != null) {
            for (File listFile : listFiles) {
                if (listFile != null) {
                    FileInputStream fstream1 = null;

                    String allnew = null;

                    try {
                        fstream1 = new FileInputStream(listFile);

                        BufferedReader br1 = new BufferedReader(new InputStreamReader(fstream1));
                        boolean first = true;
                        String strLine;
                        while ((strLine = br1.readLine()) != null) {
                            if (first) {
                                allnew = strLine;
                                first = false;
                            } else {
                                allnew = allnew + strLine;
                            }
                        }
                    } catch (IOException e) {
                        Logger.log(e);
                    }

                    String name = "error!";
                    String author = "error!";
                    String card_color = "#ffffff";
                    String card_text_color = "#000000";

                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(allnew);
                    } catch (Exception e) {
                        Logger.log(e);
                    }

                    try {
                        name = jsonObject.getString("name");
                    } catch (Exception e) {
                    }

                    try {
                        author = jsonObject.getString("author");
                    } catch (Exception e) {
                    }

                    try {
                        card_color = jsonObject.getString("card_color");
                    } catch (Exception e) {
                    }

                    try {
                        card_text_color = jsonObject.getString("card_text_color");
                    } catch (Exception e) {
                    }

                    themes.add(new Theme(listFile, name, author, Color.parseColor(card_color), Color.parseColor(card_text_color)));
                }
            }
        }

        return themes;
    }

    public void setupAll() {
        try {
            String all = "";
            File path = new File(PreferenceManager.getDefaultSharedPreferences(App.getContext()).getString("path_theme", ""));
            try {
                FileInputStream fstream = new FileInputStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                boolean first = true;
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    if (first) {
                        all = strLine;
                        first = false;
                    } else {
                        all = all + strLine;
                    }
                }
            } catch (IOException e) {
                Logger.log(e);

                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
            }

            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(all);
            } catch (Exception e) {
                Logger.log(e);
                Toast.makeText(App.getContext(), "Error!", Toast.LENGTH_SHORT).show();
                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
            }

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
                lightColorTextColor = Color.parseColor(jsonObject.getString("lightColorTextColor"));
            } catch (Exception e) {
                lightColorTextColor = 0xff000000;
            }

            try {
                darkColorTextColor = Color.parseColor(jsonObject.getString("darkColorTextColor"));
            } catch (Exception e) {
                darkColorTextColor = 0xffffffff;
            }

            try {
                lightColorIconColor = Color.parseColor(jsonObject.getString("lightColorIconColor"));
            } catch (Exception e) {
                lightColorIconColor = 0xff000000;
            }

            try {
                darkColorIconColor = Color.parseColor(jsonObject.getString("darkColorIconColor"));
            } catch (Exception e) {
                darkColorIconColor = 0xffffffff;
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

        } catch (Exception e) {
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
        }
    }

    public void setTheme(File path, Activity activity) {
        String all = "";

        try {
            FileInputStream fstream = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            boolean first = true;
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (first) {
                    all = strLine;
                    first = false;
                } else {
                    all = all + strLine;
                }
            }
        } catch (IOException e) {
            Logger.log(e);
        }

        try {
            JSONObject jsonObject = new JSONObject(all);

            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", true).apply();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putString("path_theme", path.getAbsolutePath()).apply();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("change", true).apply();

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
                lightColorTextColor = Color.parseColor(jsonObject.getString("lightColorTextColor"));
            } catch (Exception e) {
                lightColorTextColor = 0xff000000;
            }

            try {
                darkColorTextColor = Color.parseColor(jsonObject.getString("darkColorTextColor"));
            } catch (Exception e) {
                darkColorTextColor = 0xffffffff;
            }

            try {
                lightColorIconColor = Color.parseColor(jsonObject.getString("lightColorIconColor"));
            } catch (Exception e) {
                lightColorIconColor = 0xff000000;
            }

            try {
                darkColorIconColor = Color.parseColor(jsonObject.getString("darkColorIconColor"));
            } catch (Exception e) {
                darkColorIconColor = 0xffffffff;
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

            if (dark) {
                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("night", true).apply();
            } else {
                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("night", false).apply();
            }

            Intent i = activity.getBaseContext().getPackageManager().
                    getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(i);
            activity.finish();

        } catch (Exception e) {
            Toast.makeText(App.getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Logger.log(e);
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
        }
    }
}
