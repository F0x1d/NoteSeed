package com.f0x1d.notes.view.theming;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;

public class ItemCardView extends CardView {

    public ItemCardView(@NonNull Context context) {
        super(context);
        setup();
    }

    public ItemCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public ItemCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public void setThemedCardBackgroundColor() {
        if (UselessUtils.isCustomTheme()) {
            super.setCardBackgroundColor(ThemesEngine.defaultNoteColor);
        } else if (UselessUtils.getBool("night", false))
            super.setCardBackgroundColor(Color.parseColor("#424242"));
    }

    @Override
    public void setCardBackgroundColor(int color) {
        super.setCardBackgroundColor(color);
    }

    private void setup() {
        if (UselessUtils.isCustomTheme()) {
            setThemedCardBackgroundColor();
            setCardElevation(ThemesEngine.shadows);
            setElevation(ThemesEngine.shadows);
        } else if (UselessUtils.getBool("night", false)) {
            setThemedCardBackgroundColor();
        }
    }
}
