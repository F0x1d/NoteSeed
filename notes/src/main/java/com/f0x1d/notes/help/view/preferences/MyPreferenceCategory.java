package com.f0x1d.notes.help.view.preferences;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.f0x1d.notes.help.utils.ThemesEngine;
import com.f0x1d.notes.help.utils.UselessUtils;

public class MyPreferenceCategory extends PreferenceCategory {

    public MyPreferenceCategory(Context context) {
        super(context);
    }

    public MyPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPreferenceCategory(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = (TextView) view.findViewById(android.R.id.title);

        if (UselessUtils.ifCustomTheme()){
            titleView.setTextColor(ThemesEngine.accentColor);
        }

    }
}