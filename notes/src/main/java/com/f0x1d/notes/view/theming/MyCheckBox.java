package com.f0x1d.notes.view.theming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;

@SuppressLint("AppCompatCustomView")
public class MyCheckBox extends CheckBox {

    public MyCheckBox(Context context) {
        super(context);
        init();
    }

    public MyCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        if (UselessUtils.isCustomTheme())
            setCheckBoxColor(this, Color.GRAY, ThemesEngine.accentColor);
    }

    public static void setCheckBoxColor(CheckBox checkBox, int uncheckedColor, int checkedColor) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        uncheckedColor,
                        checkedColor
                }
        );
        checkBox.setButtonTintList(colorStateList);
    }
}
