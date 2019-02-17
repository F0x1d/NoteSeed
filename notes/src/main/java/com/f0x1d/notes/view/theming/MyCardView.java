package com.f0x1d.notes.view.theming;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

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
