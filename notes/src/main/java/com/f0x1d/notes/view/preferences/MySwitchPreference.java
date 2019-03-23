package com.f0x1d.notes.view.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

import static android.content.Context.MODE_PRIVATE;

public class MySwitchPreference extends SwitchPreference {

    Switch aSwitch;
    SharedPreferences sharedPreferences;

    public MySwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MySwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MySwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySwitchPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        aSwitch = holder.itemView.findViewById(android.R.id.switch_widget);

        if (UselessUtils.ifCustomTheme()){
            colorSwitch(aSwitch, ThemesEngine.accentColor);
        }

        TextView text = holder.itemView.findViewById(android.R.id.title);
        TextView text2 = holder.itemView.findViewById(android.R.id.summary);
        if (UselessUtils.ifCustomTheme()){
            text.setTextColor(ThemesEngine.textColor);
            text2.setTextColor(ThemesEngine.textColor);
        }

        text.setTypeface(ResourcesCompat.getFont(getContext(), R.font.medium));
        text2.setTypeface(ResourcesCompat.getFont(getContext(), R.font.medium));
    }

    private static void colorSwitch(Switch s, int thumbOn) {

        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked},
        };

        int[] thumbColors = new int[]{Color.GRAY, thumbOn};

        DrawableCompat.setTintList(DrawableCompat.wrap(s.getThumbDrawable()), new ColorStateList(states, thumbColors));
    }

    private void changeColor(boolean checked){
        try {
            sharedPreferences = getContext().getSharedPreferences("settings_data",MODE_PRIVATE);
            //apply the colors here
            int thumbCheckedColor = sharedPreferences.getInt("theme_color_key", ThemesEngine.accentColor);
            int thumbUncheckedColor = Color.GRAY;

            aSwitch.getThumbDrawable().setColorFilter(checked ? thumbCheckedColor : thumbUncheckedColor, PorterDuff.Mode.MULTIPLY);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
