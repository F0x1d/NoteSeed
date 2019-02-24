package com.f0x1d.notes.view.preferences;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

public class MyPreference extends Preference {

    public MyPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        TextView text = view.findViewById(android.R.id.title);
        TextView text2 = view.findViewById(android.R.id.summary);
            if (UselessUtils.ifCustomTheme()){
                text.setTextColor(ThemesEngine.textColor);
                text2.setTextColor(ThemesEngine.textColor);
            }

        text.setTypeface(ResourcesCompat.getFont(getContext(), R.font.medium));
        text2.setTypeface(ResourcesCompat.getFont(getContext(), R.font.medium));
    }
}
