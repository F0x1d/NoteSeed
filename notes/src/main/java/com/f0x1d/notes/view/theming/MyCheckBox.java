package com.f0x1d.notes.view.theming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

@SuppressLint("AppCompatCustomView")
public class MyCheckBox extends CheckBox {

    public MyCheckBox(Context context) {
        super(context);

        if (UselessUtils.ifCustomTheme())
            setCheckBoxColor(this, Color.GRAY, ThemesEngine.accentColor);
    }

    public MyCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (UselessUtils.ifCustomTheme())
            setCheckBoxColor(this, Color.GRAY, ThemesEngine.accentColor);
    }

    public MyCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (UselessUtils.ifCustomTheme())
            setCheckBoxColor(this, Color.GRAY, ThemesEngine.accentColor);
    }

    public MyCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        if (UselessUtils.ifCustomTheme())
            setCheckBoxColor(this, Color.GRAY, ThemesEngine.accentColor);
    }


    public static void setCheckBoxColor(CheckBox checkBox, int uncheckedColor, int checkedColor) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked}  // checked
                },
                new int[]{
                        uncheckedColor,
                        checkedColor
                }
        );
        checkBox.setButtonTintList(colorStateList);
    }
}
