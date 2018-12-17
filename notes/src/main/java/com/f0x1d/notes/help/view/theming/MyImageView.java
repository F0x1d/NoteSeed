package com.f0x1d.notes.help.view.theming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.f0x1d.notes.fragment.themes.ThemesFragment;
import com.f0x1d.notes.help.utils.ThemesEngine;
import com.f0x1d.notes.help.utils.UselessUtils;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

@SuppressLint("AppCompatCustomView")
public class MyImageView extends ImageView {

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (UselessUtils.ifCustomTheme()){
            super.setImageDrawable(UselessUtils.setTint(drawable, ThemesEngine.iconsColor));
        } else {
            super.setImageDrawable(drawable);
        }
    }
}
