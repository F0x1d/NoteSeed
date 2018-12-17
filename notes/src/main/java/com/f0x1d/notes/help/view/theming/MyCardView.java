package com.f0x1d.notes.help.view.theming;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.f0x1d.notes.help.utils.ThemesEngine;
import com.f0x1d.notes.help.utils.UselessUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class MyCardView extends CardView {

    public MyCardView(@NonNull Context context) {
        super(context);
    }

    public MyCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setCardBackgroundColor(int color) {
        if (UselessUtils.ifCustomTheme()){
            super.setCardBackgroundColor(ThemesEngine.defaultNoteColor);
        } else {
            super.setCardBackgroundColor(color);
        }
    }
}
