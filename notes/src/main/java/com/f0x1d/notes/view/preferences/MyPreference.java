package com.f0x1d.notes.view.preferences;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;

public class MyPreference extends Preference {

    private AttributeSet attrs;

    private int title;
    private int summary;

    public MyPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public MyPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public MyPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MyPreference(Context context) {
        super(context);
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
            } else if (attrs.getAttributeName(i).equals("summary")) {
                try {
                    summary = Integer.parseInt(attrs.getAttributeValue(i).replace("@", ""));
                } catch (Exception e) {
                    summary = -1;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        try {
            TextView text = holder.itemView.findViewById(android.R.id.title);
            TextView text2 = holder.itemView.findViewById(android.R.id.summary);
            if (UselessUtils.isCustomTheme()) {
                text.setTextColor(ThemesEngine.textColor);
                text2.setTextColor(ThemesEngine.textColor);
            }

            text.setTypeface(ResourcesCompat.getFont(getContext(), R.font.medium));
            text2.setTypeface(ResourcesCompat.getFont(getContext(), R.font.medium));

            if (title != -1)
                text.setText(getContext().getString(title));
            if (summary != -1)
                text2.setText(getContext().getString(summary));
        } catch (Exception e) {
        }
    }
}
