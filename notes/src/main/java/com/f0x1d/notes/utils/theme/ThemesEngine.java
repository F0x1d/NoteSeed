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
import androidx.lifecycle.ViewModelProviders;

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

    public static final String LIGHT_BLUE = "blue";
    public static final String LIGHT_ORANGE = "orange";
    public static final String DARK = "dark";

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

    public void setupStockTheme(String theme, AppCompatActivity activity) {
        ThemingViewModel viewModel = ViewModelProviders.of(activity).get(ThemingViewModel.class);

        if (theme.equals(DARK)) {
            background = 0xff303030;
            viewModel.background.setValue(0xff303030);

            statusBarColor = 0xff303030;
            viewModel.statusBarColor.setValue(0xff303030);

            navBarColor = 0xff303030;
            viewModel.navBarColor.setValue(0xff303030);

            textColor = 0xffffffff;
            viewModel.textColor.setValue(0xffffffff);

            accentColor = 0xffffffff;
            viewModel.accentColor.setValue(0xffffffff);

            iconsColor = 0xffffffff;
            viewModel.iconsColor.setValue(0xffffffff);

            textHintColor = 0xff808080;
            viewModel.textHintColor.setValue(0xff808080);

            toolbarColor = 0xff424242;
            viewModel.toolbarColor.setValue(0xff424242);

            toolbarTextColor = 0xffffffff;
            viewModel.toolbarTextColor.setValue(0xffffffff);

            fabColor = 0xffffffff;
            viewModel.fabColor.setValue(0xffffffff);

            fabIconColor = 0xff000000;
            viewModel.fabIconColor.setValue(0xff000000);

            defaultNoteColor = 0xff424242;
            viewModel.defaultNoteColor.setValue(0xff424242);

            lightColorTextColor = 0xff000000;
            viewModel.lightColorTextColor.setValue(0xff000000);

            lightColorIconColor = 0xff000000;
            viewModel.lightColorIconColor.setValue(0xff000000);

            darkColorTextColor = 0xffffffff;
            viewModel.darkColorTextColor.setValue(0xffffffff);

            darkColorIconColor = 0xffffffff;
            viewModel.darkColorIconColor.setValue(0xffffffff);

            dark = true;

            seekBarColor = 0xffffffff;
            viewModel.seekBarColor.setValue(0xffffffff);

            seekBarThumbColor = 0xffffffff;
            viewModel.seekBarThumbColor.setValue(0xffffffff);

            shadows = 0.0f;
        } else if (theme.equals(LIGHT_BLUE)) {
            background = 0xffffffff;
            viewModel.background.setValue(0xffffffff);

            statusBarColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 0xffffffff : Color.GRAY;
            viewModel.statusBarColor.setValue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 0xffffffff : Color.GRAY);

            navBarColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? 0xffffffff : 0xff000000;
            viewModel.navBarColor.setValue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? 0xffffffff : 0xff000000);

            textColor = 0xff000000;
            viewModel.textColor.setValue(0xff000000);

            accentColor = 0xff64B5F6;
            viewModel.accentColor.setValue(0xff64B5F6);

            iconsColor = 0xff000000;
            viewModel.iconsColor.setValue(0xff000000);

            textHintColor = 0xff808080;
            viewModel.textHintColor.setValue(0xff808080);

            toolbarColor = 0xffffffff;
            viewModel.toolbarColor.setValue(0xffffffff);

            toolbarTextColor = 0xff000000;
            viewModel.toolbarTextColor.setValue(0xff000000);

            fabColor = 0xff64B5F6;
            viewModel.fabColor.setValue(0xff64B5F6);

            fabIconColor = 0xffffffff;
            viewModel.fabIconColor.setValue(0xffffffff);

            defaultNoteColor = 0xffffffff;
            viewModel.defaultNoteColor.setValue(0xffffffff);

            lightColorTextColor = 0xff000000;
            viewModel.lightColorTextColor.setValue(0xff000000);

            lightColorIconColor = 0xff000000;
            viewModel.lightColorIconColor.setValue(0xff000000);

            darkColorTextColor = 0xffffffff;
            viewModel.darkColorTextColor.setValue(0xffffffff);

            darkColorIconColor = 0xffffffff;
            viewModel.darkColorIconColor.setValue(0xffffffff);

            dark = false;

            seekBarColor = 0xff64B5F6;
            viewModel.seekBarColor.setValue(0xff64B5F6);

            seekBarThumbColor = 0xff64B5F6;
            viewModel.seekBarThumbColor.setValue(0xff64B5F6);

            shadows = 10.0f;
        } else if (theme.equals(ThemesEngine.LIGHT_ORANGE)) {
            background = 0xffffffff;
            viewModel.background.setValue(0xffffffff);

            statusBarColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 0xffffffff : Color.GRAY;
            viewModel.statusBarColor.setValue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 0xffffffff : Color.GRAY);

            navBarColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? 0xffffffff : 0xff000000;
            viewModel.navBarColor.setValue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? 0xffffffff : 0xff000000);

            textColor = 0xff000000;
            viewModel.textColor.setValue(0xff000000);

            accentColor = 0xffffaa00;
            viewModel.accentColor.setValue(0xffffaa00);

            iconsColor = 0xff000000;
            viewModel.iconsColor.setValue(0xff000000);

            textHintColor = 0xff808080;
            viewModel.textHintColor.setValue(0xff808080);

            toolbarColor = 0xffffffff;
            viewModel.toolbarColor.setValue(0xffffffff);

            toolbarTextColor = 0xff000000;
            viewModel.toolbarTextColor.setValue(0xff000000);

            fabColor = 0xffffaa00;
            viewModel.fabColor.setValue(0xffffaa00);

            fabIconColor = 0xffffffff;
            viewModel.fabIconColor.setValue(0xffffffff);

            defaultNoteColor = 0xffffffff;
            viewModel.defaultNoteColor.setValue(0xffffffff);

            lightColorTextColor = 0xff000000;
            viewModel.lightColorTextColor.setValue(0xff000000);

            lightColorIconColor = 0xff000000;
            viewModel.lightColorIconColor.setValue(0xff000000);

            darkColorTextColor = 0xffffffff;
            viewModel.darkColorTextColor.setValue(0xffffffff);

            darkColorIconColor = 0xffffffff;
            viewModel.darkColorIconColor.setValue(0xffffffff);

            dark = false;

            seekBarColor = 0xffffaa00;
            viewModel.seekBarColor.setValue(0xffffaa00);

            seekBarThumbColor = 0xffffaa00;
            viewModel.seekBarThumbColor.setValue(0xffffaa00);

            shadows = 10.0f;
        }
    }

    public void importTheme(Uri uri, AppCompatActivity activity) {
        ThemingViewModel viewModel = ViewModelProviders.of(activity).get(ThemingViewModel.class);

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

            JSONObject jsonObject = new JSONObject(all);

            try {
                viewModel.background.setValue(Color.parseColor(jsonObject.getString("background")));
                background = Color.parseColor(jsonObject.getString("background"));
                viewModel.background.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        background = integer.intValue();
                    }
                });
            } catch (Exception e) {
                background = 0xffffffff;
            }

            try {
                viewModel.statusBarColor.setValue(Color.parseColor(jsonObject.getString("status_bar_color")));
                statusBarColor = Color.parseColor(jsonObject.getString("status_bar_color"));
                viewModel.statusBarColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        statusBarColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                statusBarColor = 0xffffffff;
            }

            try {
                viewModel.textColor.setValue(Color.parseColor(jsonObject.getString("text_color")));
                textColor = Color.parseColor(jsonObject.getString("text_color"));
                viewModel.textColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        textColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                textColor = 0xff000000;
            }

            try {
                viewModel.accentColor.setValue(Color.parseColor(jsonObject.getString("accent")));
                accentColor = Color.parseColor(jsonObject.getString("accent"));
                viewModel.accentColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        accentColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                accentColor = 0xff00ff00;
            }

            try {
                viewModel.navBarColor.setValue(Color.parseColor(jsonObject.getString("nav_color")));
                navBarColor = Color.parseColor(jsonObject.getString("nav_color"));
                viewModel.navBarColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        navBarColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                navBarColor = 0xff000000;
            }

            try {
                viewModel.iconsColor.setValue(Color.parseColor(jsonObject.getString("icons_color")));
                iconsColor = Color.parseColor(jsonObject.getString("icons_color"));
                viewModel.iconsColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        iconsColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                iconsColor = 0xff000000;
            }

            try {
                viewModel.textHintColor.setValue(Color.parseColor(jsonObject.getString("hint_color")));
                textHintColor = Color.parseColor(jsonObject.getString("hint_color"));
                viewModel.textHintColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        textHintColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                textHintColor = Color.GRAY;
            }

            try {
                dark = jsonObject.getBoolean("dark");
            } catch (Exception e) {
                dark = false;
            }

            try {
                viewModel.toolbarColor.setValue(Color.parseColor(jsonObject.getString("toolbar_color")));
                toolbarColor = Color.parseColor(jsonObject.getString("toolbar_color"));
                viewModel.toolbarColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        toolbarColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                toolbarColor = Color.TRANSPARENT;
            }

            try {
                viewModel.toolbarTextColor.setValue(Color.parseColor(jsonObject.getString("toolbar_text_color")));
                toolbarTextColor = Color.parseColor(jsonObject.getString("toolbar_text_color"));
                viewModel.toolbarTextColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        toolbarTextColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                toolbarTextColor = 0xff000000;
            }

            try {
                viewModel.fabColor.setValue(Color.parseColor(jsonObject.getString("fab_color")));
                fabColor = Color.parseColor(jsonObject.getString("fab_color"));
                viewModel.fabColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        fabColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                fabColor = 0xffffffff;
            }

            try {
                viewModel.fabIconColor.setValue(Color.parseColor(jsonObject.getString("fab_icon_color")));
                fabIconColor = Color.parseColor(jsonObject.getString("fab_icon_color"));
                viewModel.fabIconColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        fabIconColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                fabIconColor = 0xff000000;
            }

            try {
                viewModel.defaultNoteColor.setValue(Color.parseColor(jsonObject.getString("default_note_color")));
                defaultNoteColor = Color.parseColor(jsonObject.getString("default_note_color"));
                viewModel.defaultNoteColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        defaultNoteColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                defaultNoteColor = 0xffffffff;
            }

            try {
                viewModel.lightColorTextColor.setValue(Color.parseColor(jsonObject.getString("lightColorTextColor")));
                lightColorTextColor = Color.parseColor(jsonObject.getString("lightColorTextColor"));
                viewModel.lightColorTextColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        lightColorTextColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                lightColorTextColor = 0xff000000;
            }

            try {
                viewModel.darkColorTextColor.setValue(Color.parseColor(jsonObject.getString("darkColorTextColor")));
                darkColorTextColor = Color.parseColor(jsonObject.getString("darkColorTextColor"));
                viewModel.darkColorTextColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        darkColorTextColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                darkColorTextColor = 0xffffffff;
            }

            try {
                viewModel.lightColorIconColor.setValue(Color.parseColor(jsonObject.getString("lightColorIconColor")));
                lightColorIconColor = Color.parseColor(jsonObject.getString("lightColorIconColor"));
                viewModel.lightColorIconColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        lightColorIconColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                lightColorIconColor = 0xff000000;
            }

            try {
                viewModel.darkColorIconColor.setValue(Color.parseColor(jsonObject.getString("darkColorIconColor")));
                darkColorIconColor = Color.parseColor(jsonObject.getString("darkColorIconColor"));
                viewModel.darkColorIconColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        darkColorIconColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                darkColorIconColor = 0xffffffff;
            }

            try {
                viewModel.seekBarColor.setValue(Color.parseColor(jsonObject.getString("seekbar_color")));
                seekBarColor = Color.parseColor(jsonObject.getString("seekbar_color"));
                viewModel.seekBarColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        seekBarColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                seekBarColor = 0xffffffff;
            }

            try {
                viewModel.seekBarThumbColor.setValue(Color.parseColor(jsonObject.getString("seekbar_thumb_color")));
                seekBarThumbColor = Color.parseColor(jsonObject.getString("seekbar_thumb_color"));
                viewModel.seekBarThumbColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        seekBarThumbColor = integer.intValue();
                    }
                });
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
                activity.getWindow().getDecorView().setSystemUiVisibility(0);
            } else {
                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("night", false).apply();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }

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

    public void setupAll(AppCompatActivity activity) {
        ThemingViewModel viewModel = ViewModelProviders.of(activity).get(ThemingViewModel.class);

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
                setupStockTheme(PreferenceManager.getDefaultSharedPreferences(App.getContext()).getString("path_theme", ThemesEngine.LIGHT_BLUE), activity);
                return;
                //PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
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
                viewModel.background.setValue(Color.parseColor(jsonObject.getString("background")));
                background = Color.parseColor(jsonObject.getString("background"));
                viewModel.background.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        background = integer.intValue();
                    }
                });
            } catch (Exception e) {
                background = 0xffffffff;
            }

            try {
                viewModel.statusBarColor.setValue(Color.parseColor(jsonObject.getString("status_bar_color")));
                statusBarColor = Color.parseColor(jsonObject.getString("status_bar_color"));
                viewModel.statusBarColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        statusBarColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                statusBarColor = 0xffffffff;
            }

            try {
                viewModel.textColor.setValue(Color.parseColor(jsonObject.getString("text_color")));
                textColor = Color.parseColor(jsonObject.getString("text_color"));
                viewModel.textColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        textColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                textColor = 0xff000000;
            }

            try {
                viewModel.accentColor.setValue(Color.parseColor(jsonObject.getString("accent")));
                accentColor = Color.parseColor(jsonObject.getString("accent"));
                viewModel.accentColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        accentColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                accentColor = 0xff00ff00;
            }

            try {
                viewModel.navBarColor.setValue(Color.parseColor(jsonObject.getString("nav_color")));
                navBarColor = Color.parseColor(jsonObject.getString("nav_color"));
                viewModel.navBarColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        navBarColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                navBarColor = 0xff000000;
            }

            try {
                viewModel.iconsColor.setValue(Color.parseColor(jsonObject.getString("icons_color")));
                iconsColor = Color.parseColor(jsonObject.getString("icons_color"));
                viewModel.iconsColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        iconsColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                iconsColor = 0xff000000;
            }

            try {
                viewModel.textHintColor.setValue(Color.parseColor(jsonObject.getString("hint_color")));
                textHintColor = Color.parseColor(jsonObject.getString("hint_color"));
                viewModel.textHintColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        textHintColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                textHintColor = Color.GRAY;
            }

            try {
                dark = jsonObject.getBoolean("dark");
            } catch (Exception e) {
                dark = false;
            }

            try {
                viewModel.toolbarColor.setValue(Color.parseColor(jsonObject.getString("toolbar_color")));
                toolbarColor = Color.parseColor(jsonObject.getString("toolbar_color"));
                viewModel.toolbarColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        toolbarColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                toolbarColor = Color.TRANSPARENT;
            }

            try {
                viewModel.toolbarTextColor.setValue(Color.parseColor(jsonObject.getString("toolbar_text_color")));
                toolbarTextColor = Color.parseColor(jsonObject.getString("toolbar_text_color"));
                viewModel.toolbarTextColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        toolbarTextColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                toolbarTextColor = 0xff000000;
            }

            try {
                viewModel.fabColor.setValue(Color.parseColor(jsonObject.getString("fab_color")));
                fabColor = Color.parseColor(jsonObject.getString("fab_color"));
                viewModel.fabColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        fabColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                fabColor = 0xffffffff;
            }

            try {
                viewModel.fabIconColor.setValue(Color.parseColor(jsonObject.getString("fab_icon_color")));
                fabIconColor = Color.parseColor(jsonObject.getString("fab_icon_color"));
                viewModel.fabIconColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        fabIconColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                fabIconColor = 0xff000000;
            }

            try {
                viewModel.defaultNoteColor.setValue(Color.parseColor(jsonObject.getString("default_note_color")));
                defaultNoteColor = Color.parseColor(jsonObject.getString("default_note_color"));
                viewModel.defaultNoteColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        defaultNoteColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                defaultNoteColor = 0xffffffff;
            }

            try {
                viewModel.lightColorTextColor.setValue(Color.parseColor(jsonObject.getString("lightColorTextColor")));
                lightColorTextColor = Color.parseColor(jsonObject.getString("lightColorTextColor"));
                viewModel.lightColorTextColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        lightColorTextColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                lightColorTextColor = 0xff000000;
            }

            try {
                viewModel.darkColorTextColor.setValue(Color.parseColor(jsonObject.getString("darkColorTextColor")));
                darkColorTextColor = Color.parseColor(jsonObject.getString("darkColorTextColor"));
                viewModel.darkColorTextColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        darkColorTextColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                darkColorTextColor = 0xffffffff;
            }

            try {
                viewModel.lightColorIconColor.setValue(Color.parseColor(jsonObject.getString("lightColorIconColor")));
                lightColorIconColor = Color.parseColor(jsonObject.getString("lightColorIconColor"));
                viewModel.lightColorIconColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        lightColorIconColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                lightColorIconColor = 0xff000000;
            }

            try {
                viewModel.darkColorIconColor.setValue(Color.parseColor(jsonObject.getString("darkColorIconColor")));
                darkColorIconColor = Color.parseColor(jsonObject.getString("darkColorIconColor"));
                viewModel.darkColorIconColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        darkColorIconColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                darkColorIconColor = 0xffffffff;
            }

            try {
                viewModel.seekBarColor.setValue(Color.parseColor(jsonObject.getString("seekbar_color")));
                seekBarColor = Color.parseColor(jsonObject.getString("seekbar_color"));
                viewModel.seekBarColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        seekBarColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                seekBarColor = 0xffffffff;
            }

            try {
                viewModel.seekBarThumbColor.setValue(Color.parseColor(jsonObject.getString("seekbar_thumb_color")));
                seekBarThumbColor = Color.parseColor(jsonObject.getString("seekbar_thumb_color"));
                viewModel.seekBarThumbColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        seekBarThumbColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                seekBarThumbColor = 0xff888888;
            }

        } catch (Exception e) {
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
        }
    }

    public void setTheme(File path, AppCompatActivity activity) {
        String all = "";

        ThemingViewModel viewModel = ViewModelProviders.of(activity).get(ThemingViewModel.class);

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

            try {
                viewModel.background.setValue(Color.parseColor(jsonObject.getString("background")));
                background = Color.parseColor(jsonObject.getString("background"));
                viewModel.background.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        background = integer.intValue();
                    }
                });
            } catch (Exception e) {
                background = 0xffffffff;
            }

            try {
                viewModel.statusBarColor.setValue(Color.parseColor(jsonObject.getString("status_bar_color")));
                statusBarColor = Color.parseColor(jsonObject.getString("status_bar_color"));
                viewModel.statusBarColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        statusBarColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                statusBarColor = 0xffffffff;
            }

            try {
                viewModel.textColor.setValue(Color.parseColor(jsonObject.getString("text_color")));
                textColor = Color.parseColor(jsonObject.getString("text_color"));
                viewModel.textColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        textColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                textColor = 0xff000000;
            }

            try {
                viewModel.accentColor.setValue(Color.parseColor(jsonObject.getString("accent")));
                accentColor = Color.parseColor(jsonObject.getString("accent"));
                viewModel.accentColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        accentColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                accentColor = 0xff00ff00;
            }

            try {
                viewModel.navBarColor.setValue(Color.parseColor(jsonObject.getString("nav_color")));
                navBarColor = Color.parseColor(jsonObject.getString("nav_color"));
                viewModel.navBarColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        navBarColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                navBarColor = 0xff000000;
            }

            try {
                viewModel.iconsColor.setValue(Color.parseColor(jsonObject.getString("icons_color")));
                iconsColor = Color.parseColor(jsonObject.getString("icons_color"));
                viewModel.iconsColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        iconsColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                iconsColor = 0xff000000;
            }

            try {
                viewModel.textHintColor.setValue(Color.parseColor(jsonObject.getString("hint_color")));
                textHintColor = Color.parseColor(jsonObject.getString("hint_color"));
                viewModel.textHintColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        textHintColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                textHintColor = Color.GRAY;
            }

            try {
                dark = jsonObject.getBoolean("dark");
            } catch (Exception e) {
                dark = false;
            }

            try {
                viewModel.toolbarColor.setValue(Color.parseColor(jsonObject.getString("toolbar_color")));
                toolbarColor = Color.parseColor(jsonObject.getString("toolbar_color"));
                viewModel.toolbarColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        toolbarColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                toolbarColor = Color.TRANSPARENT;
            }

            try {
                viewModel.toolbarTextColor.setValue(Color.parseColor(jsonObject.getString("toolbar_text_color")));
                toolbarTextColor = Color.parseColor(jsonObject.getString("toolbar_text_color"));
                viewModel.toolbarTextColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        toolbarTextColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                toolbarTextColor = 0xff000000;
            }

            try {
                viewModel.fabColor.setValue(Color.parseColor(jsonObject.getString("fab_color")));
                fabColor = Color.parseColor(jsonObject.getString("fab_color"));
                viewModel.fabColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        fabColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                fabColor = 0xffffffff;
            }

            try {
                viewModel.fabIconColor.setValue(Color.parseColor(jsonObject.getString("fab_icon_color")));
                fabIconColor = Color.parseColor(jsonObject.getString("fab_icon_color"));
                viewModel.fabIconColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        fabIconColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                fabIconColor = 0xff000000;
            }

            try {
                viewModel.defaultNoteColor.setValue(Color.parseColor(jsonObject.getString("default_note_color")));
                defaultNoteColor = Color.parseColor(jsonObject.getString("default_note_color"));
                viewModel.defaultNoteColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        defaultNoteColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                defaultNoteColor = 0xffffffff;
            }

            try {
                viewModel.lightColorTextColor.setValue(Color.parseColor(jsonObject.getString("lightColorTextColor")));
                lightColorTextColor = Color.parseColor(jsonObject.getString("lightColorTextColor"));
                viewModel.lightColorTextColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        lightColorTextColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                lightColorTextColor = 0xff000000;
            }

            try {
                viewModel.darkColorTextColor.setValue(Color.parseColor(jsonObject.getString("darkColorTextColor")));
                darkColorTextColor = Color.parseColor(jsonObject.getString("darkColorTextColor"));
                viewModel.darkColorTextColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        darkColorTextColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                darkColorTextColor = 0xffffffff;
            }

            try {
                viewModel.lightColorIconColor.setValue(Color.parseColor(jsonObject.getString("lightColorIconColor")));
                lightColorIconColor = Color.parseColor(jsonObject.getString("lightColorIconColor"));
                viewModel.lightColorIconColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        lightColorIconColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                lightColorIconColor = 0xff000000;
            }

            try {
                viewModel.darkColorIconColor.setValue(Color.parseColor(jsonObject.getString("darkColorIconColor")));
                darkColorIconColor = Color.parseColor(jsonObject.getString("darkColorIconColor"));
                viewModel.darkColorIconColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        darkColorIconColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                darkColorIconColor = 0xffffffff;
            }

            try {
                viewModel.seekBarColor.setValue(Color.parseColor(jsonObject.getString("seekbar_color")));
                seekBarColor = Color.parseColor(jsonObject.getString("seekbar_color"));
                viewModel.seekBarColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        seekBarColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                seekBarColor = 0xffffffff;
            }

            try {
                viewModel.seekBarThumbColor.setValue(Color.parseColor(jsonObject.getString("seekbar_thumb_color")));
                seekBarThumbColor = Color.parseColor(jsonObject.getString("seekbar_thumb_color"));
                viewModel.seekBarThumbColor.observe(activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        seekBarThumbColor = integer.intValue();
                    }
                });
            } catch (Exception e) {
                seekBarThumbColor = 0xff888888;
            }

            if (dark) {
                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("night", true).apply();
                activity.getWindow().getDecorView().setSystemUiVisibility(0);
            } else {
                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("night", false).apply();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }

        } catch (Exception e) {
            Toast.makeText(App.getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Logger.log(e);
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
        }
    }
}
