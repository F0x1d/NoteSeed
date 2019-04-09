package com.f0x1d.notes.view.theming;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

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

    @Override
    public void setCardBackgroundColor(int color) {
        if (UselessUtils.ifCustomTheme()) {
            super.setCardBackgroundColor(ThemesEngine.defaultNoteColor);
        } else if (UselessUtils.getBool("night", true))
            super.setCardBackgroundColor(Color.parseColor("#424242"));
        //super.setCardBackgroundColor(color);
    }

    private void setup() {
        if (UselessUtils.ifCustomTheme()) {
            setCardBackgroundColor(getCardBackgroundColor());
            setCardElevation(ThemesEngine.shadows);
        } else if (UselessUtils.getBool("night", true)) {
            setCardBackgroundColor(Color.parseColor("#424242"));
        }
    }
}
