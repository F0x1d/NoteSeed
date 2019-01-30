package com.f0x1d.notes.utils.resources;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

public class CustomResources extends Resources {

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
    public CustomResources(AssetManager assets, DisplayMetrics metrics, Configuration config) {
        super(assets, metrics, config);
    }

    @Override
    public int getColor(int id) throws NotFoundException {
        if (UselessUtils.ifCustomTheme()){
            if (id == R.color.white_for_dialogs || id == R.color.statusbar_for_dialogs)
                return ThemesEngine.background;
            else if (id == R.color.white_text_color_for_dialogs || id == R.color.black_text_color_for_dialogs)
                return ThemesEngine.textColor;
            else
                return super.getColor(id);
        } else
            return super.getColor(id);
    }

    @Override
    public int getColor(int id, @Nullable Theme theme) throws NotFoundException {
        if (UselessUtils.ifCustomTheme()){
            if (id == R.color.white_for_dialogs || id == R.color.statusbar_for_dialogs)
                return ThemesEngine.background;
            else if (id == R.color.white_text_color_for_dialogs || id == R.color.black_text_color_for_dialogs)
                return ThemesEngine.textColor;
            else
                return super.getColor(id, theme);
        } else
            return super.getColor(id, theme);
    }
}
