package com.f0x1d.notes.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.f0x1d.notes.App;

public class PreferenceUtils {

    public static SharedPreferences.Editor edit(){
        return PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit();
    }
}
