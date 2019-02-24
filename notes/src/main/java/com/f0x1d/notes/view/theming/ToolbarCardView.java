package com.f0x1d.notes.view.theming;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

public class ToolbarCardView extends CardView {

    public ToolbarCardView(@NonNull Context context) {
        super(context);

        setup();
    }

    public ToolbarCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setup();
    }

    public ToolbarCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setup();
    }

    @Override
    public void setCardBackgroundColor(int color) {
        if (UselessUtils.ifCustomTheme()){
            super.setCardBackgroundColor(ThemesEngine.toolbarColor);
        } else {
            super.setCardBackgroundColor(color);
        }
    }

    private void setup(){
        if (UselessUtils.ifCustomTheme()){
            setCardBackgroundColor(getCardBackgroundColor());
        }
    }
}
