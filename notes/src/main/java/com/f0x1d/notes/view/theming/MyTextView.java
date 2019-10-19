package com.f0x1d.notes.view.theming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;

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

    private void setColor() {
        if (UselessUtils.isCustomTheme()) {
            this.setTextColor(ThemesEngine.textColor);
            try {
                setCompoundDrawables(UselessUtils.setTint(getCompoundDrawables()[0], ThemesEngine.iconsColor), null, null, null);
            } catch (Exception e) {
            }
        }
        setTypeface(ResourcesCompat.getFont(getContext(), R.font.medium));
    }

    public void setupCompoundDrawables() {
        if (UselessUtils.isCustomTheme()) {
            try {
                setCompoundDrawables(UselessUtils.setTint(getCompoundDrawables()[0], ThemesEngine.iconsColor), null, null, null);
            } catch (Exception e) {
            }
        } else if (UselessUtils.getBool("night", false)) {
            try {
                setCompoundDrawables(UselessUtils.setTint(getCompoundDrawables()[0], Color.WHITE), null, null, null);
            } catch (Exception e) {
            }
        } else {
            try {
                setCompoundDrawables(UselessUtils.setTint(getCompoundDrawables()[0], Color.BLACK), null, null, null);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void setTextColor(int color) {
        if (UselessUtils.isCustomTheme()) {
            color = ThemesEngine.textColor;
            super.setTextColor(color);
        } else {
            super.setTextColor(color);
        }
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        if (UselessUtils.isCustomTheme()) {
            try {
                super.setCompoundDrawables(UselessUtils.setTint(left, ThemesEngine.iconsColor), top, right, bottom);
            } catch (Exception e) {
            }

        } else
            super.setCompoundDrawables(left, top, right, bottom);
    }
}
