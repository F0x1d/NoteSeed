package com.f0x1d.notes.view.theming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class MyTextView extends TextView {

    public MyTextView(Context context) {
        super(context);

        setColor();
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setColor();
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setColor();
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setColor();
    }

    private void setColor(){
        if (UselessUtils.ifCustomTheme()){
            this.setTextColor(ThemesEngine.textColor);

            setCompoundDrawables(UselessUtils.setTint(getCompoundDrawables()[0], ThemesEngine.iconsColor), null, null, null);
        }
    }

    @Override
    public void setTextColor(int color) {
        if (UselessUtils.ifCustomTheme()){
            color = ThemesEngine.textColor;
            super.setTextColor(color);
        } else {
            super.setTextColor(color);
        }
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        if (UselessUtils.ifCustomTheme())
            super.setCompoundDrawables(UselessUtils.setTint(left, ThemesEngine.iconsColor), top, right, bottom);
        else
            super.setCompoundDrawables(left, top, right, bottom);
    }
}
