package com.f0x1d.notes.fragment.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.bottom_sheet.TextSizeDialog;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.view.CenteredToolbar;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class EditorSettings extends PreferenceFragment {

    FragmentActivity myContext;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings, container, false);

        CenteredToolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.editing));
        getActivity().setActionBar(toolbar);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.editor);

        Preference fon = findPreference("fon");
        fon.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String[] themes = {getString(R.string.fon_standart), getString(R.string.fon_wallpaper)};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setSingleChoiceItems(themes, PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("fon", 0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt("fon", 0).apply();
                                break;
                            case 1:
                                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt("fon", 1).apply();
                                break;
                        }
                    }
                });
                AlertDialog dialog =  builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("fon", 0) == 1){
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                            builder1.setTitle(getString(R.string.wallpapers_ask));
                            builder1.setCancelable(false);
                            builder1.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("dark_fon", true).apply();
                                }
                            });
                            AlertDialog dialog1 =  builder1.setNeutralButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("dark_fon", false).apply();
                                }
                            }).create();

                            dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialog) {
                                    if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", false)){
                                        dialog1.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                        dialog1.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
                                    }

                                    if (UselessUtils.ifCustomTheme()){
                                        dialog1.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ThemesEngine.textColor);
                                        dialog1.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(ThemesEngine.textColor);

                                        dialog1.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                                        dialog1.getButton(DialogInterface.BUTTON_NEUTRAL).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                                    }
                                }
                            });
                            dialog1.show();
                        }
                    }
                }).create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog0) {
                        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", false)){
                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                        }
                        if (UselessUtils.ifCustomTheme()){
                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ThemesEngine.textColor);

                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                        }
                    }
                });
                dialog.show();
                return false;
            }
        });

        Preference textSize = findPreference("textSize");
        textSize.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                TextSizeDialog dialog1 = new TextSizeDialog();
                dialog1.show(myContext.getSupportFragmentManager(), "TAG");

                return false;
            }
        });
    }
}
