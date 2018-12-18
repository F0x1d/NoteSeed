package com.f0x1d.notes.view.theming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class MyImageButton extends ImageButton {

    public MyImageButton(Context context) {
        super(context);
    }

    public MyImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
