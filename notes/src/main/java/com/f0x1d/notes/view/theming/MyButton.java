package com.f0x1d.notes.view.theming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;

@SuppressLint("AppCompatCustomView")
public class MyButton extends Button {

    public MyButton(Context context) {
        super(context);

        setText();
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setText();
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setText();
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setText();
    }

    private void setText() {
        if (UselessUtils.ifCustomTheme()) {
            this.setTextColor(ThemesEngine.textColor);
            this.setBackgroundTintList(ColorStateList.valueOf(ThemesEngine.accentColor));
        }

        setTypeface(ResourcesCompat.getFont(getContext(), R.font.medium));
    }

    @Override
    public void setBackgroundTintList(@Nullable ColorStateList tint) {
        if (UselessUtils.ifCustomTheme()) {
            super.setBackgroundTintList(ColorStateList.valueOf(ThemesEngine.accentColor));
        } else {
            super.setBackgroundTintList(tint);
        }
    }
}
