package com.f0x1d.notes.view.preferences;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;

public class MyPreferenceCategory extends PreferenceCategory {

    private AttributeSet attrs;

    private int title;

    public MyPreferenceCategory(Context context) {
        super(context);
    }

    public MyPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MyPreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public MyPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        TextView titleView = holder.itemView.findViewById(android.R.id.title);

        if (UselessUtils.isCustomTheme()) {
            titleView.setTextColor(ThemesEngine.accentColor);
        }

        titleView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.medium));
        try {
            if (title != -1)
                titleView.setText(getContext().getString(title));
        } catch (Exception e) {
        }
    }

    private void init(AttributeSet attrs) {
        this.attrs = attrs;
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            if (attrs.getAttributeName(i).equals("title")) {
                try {
                    title = Integer.parseInt(attrs.getAttributeValue(i).replace("@", ""));
                } catch (Exception e) {
                    title = -1;
                }
            }
        }
    }
}