package com.f0x1d.notes.view.preferences;

import android.content.Context;
import android.content.res.ColorStateList;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

public class MyCheckBoxPreference extends CheckBoxPreference {

    public MyCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyCheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCheckBoxPreference(Context context) {
        super(context);
    }



    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        CheckBox checkBox = view.findViewById(android.R.id.checkbox);
        TextView title = view.findViewById(android.R.id.title);

        if (UselessUtils.ifCustomTheme()){
            checkBox.setButtonTintList(ColorStateList.valueOf(ThemesEngine.accentColor));
            title.setTextColor(ThemesEngine.textColor);
        }
    }
}
