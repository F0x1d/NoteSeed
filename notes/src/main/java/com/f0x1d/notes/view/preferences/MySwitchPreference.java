package com.f0x1d.notes.view.preferences;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;

public class MySwitchPreference extends SwitchPreference {

    private Switch aSwitch;

    private AttributeSet attrs;

    private int title;
    private int summary;

    public MySwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public MySwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public MySwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MySwitchPreference(Context context) {
        super(context);
    }

    private static void colorSwitch(Switch s, int thumbOn) {

        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked},
        };

        int[] thumbColors = new int[]{Color.GRAY, thumbOn};

        DrawableCompat.setTintList(DrawableCompat.wrap(s.getThumbDrawable()), new ColorStateList(states, thumbColors));
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        aSwitch = holder.itemView.findViewById(android.R.id.switch_widget);

        if (UselessUtils.ifCustomTheme()) {
            colorSwitch(aSwitch, ThemesEngine.accentColor);
        }

        TextView text = holder.itemView.findViewById(android.R.id.title);
        TextView text2 = holder.itemView.findViewById(android.R.id.summary);
        if (UselessUtils.ifCustomTheme()) {
            text.setTextColor(ThemesEngine.textColor);
            text2.setTextColor(ThemesEngine.textColor);
        }

        text.setTypeface(ResourcesCompat.getFont(getContext(), R.font.medium));
        text2.setTypeface(ResourcesCompat.getFont(getContext(), R.font.medium));

        try {
            if (title != -1)
                text.setText(getContext().getString(title));
            if (summary != -1)
                text2.setText(getContext().getString(summary));
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
            } else if (attrs.getAttributeName(i).equals("summary")) {
                try {
                    summary = Integer.parseInt(attrs.getAttributeValue(i).replace("@", ""));
                } catch (Exception e) {
                    summary = -1;
                }
            }
        }
    }
}
