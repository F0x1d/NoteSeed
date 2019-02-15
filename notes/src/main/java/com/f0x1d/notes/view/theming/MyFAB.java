package com.f0x1d.notes.view.theming;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.model.Theme;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

public class MyFAB extends FloatingActionButton {

    public MyFAB(Context context) {
        super(context);
        start();
    }

    public MyFAB(Context context, AttributeSet attrs) {
        super(context, attrs);
        start();
    }

    public MyFAB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        start();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        if (UselessUtils.ifCustomTheme())
            setImageTintList(ColorStateList.valueOf(ThemesEngine.fabIconColor));
    }

    private void start(){
        if (UselessUtils.ifCustomTheme()){
            setBackgroundTintList(ColorStateList.valueOf(ThemesEngine.fabColor));
            setImageTintList(ColorStateList.valueOf(ThemesEngine.fabIconColor));
        } else if (UselessUtils.getBool("night", false)){
            setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        } else if (UselessUtils.getBool("orange", false)){
            setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.noname)));
        } else {
            setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
        }
    }
}
