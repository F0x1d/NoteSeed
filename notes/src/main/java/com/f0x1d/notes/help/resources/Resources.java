package com.f0x1d.notes.help.resources;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.f0x1d.notes.help.App;
import com.f0x1d.notes.help.utils.ThemesEngine;
import com.f0x1d.notes.help.utils.UselessUtils;

import androidx.annotation.Nullable;

public class Resources extends android.content.res.Resources {

    /**
     * Create a new Resources object on top of an existing set of assets in an
     * AssetManager.
     *
     * @param assets  Previously created AssetManager.
     * @param metrics Current display metrics to consider when
     *                selecting/computing resource values.
     * @param config  Desired device configuration to consider when
     * @deprecated Resources should not be constructed by apps.
     * See {@link Context#createConfigurationContext(Configuration)}.
     */
    public Resources(AssetManager assets, DisplayMetrics metrics, Configuration config) {
        super(assets, metrics, config);
    }

    @Override
    public Drawable getDrawable(int id) throws NotFoundException {
        if (UselessUtils.ifCustomTheme()){
            if (id == androidx.appcompat.R.drawable.abc_ic_menu_overflow_material){
                return UselessUtils.setTint(super.getDrawable(id), ThemesEngine.iconsColor);
            } else {
                return super.getDrawable(id);
            }
        } else if (!PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("night", false) && id == androidx.appcompat.R.drawable.abc_ic_menu_overflow_material){
            return UselessUtils.setTint(super.getDrawable(id), Color.BLACK);
        } else {
            return super.getDrawable(id);
        }
    }

    @Override
    public Drawable getDrawable(int id, @Nullable Theme theme) throws NotFoundException {
        if (UselessUtils.ifCustomTheme()){
            if (id == androidx.appcompat.R.drawable.abc_ic_menu_overflow_material){
                return UselessUtils.setTint(super.getDrawable(id, theme), ThemesEngine.iconsColor);
            } else {
                return super.getDrawable(id, theme);
            }
        }  else if (!PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("night", false) && id == androidx.appcompat.R.drawable.abc_ic_menu_overflow_material){
            return UselessUtils.setTint(super.getDrawable(id, theme), Color.BLACK);
        } else {
            return super.getDrawable(id, theme);
        }
    }

}
