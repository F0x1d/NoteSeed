package com.f0x1d.notes.view.theming;

import android.content.Context;
import android.util.AttributeSet;

import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.google.android.material.card.MaterialCardView;

public class ToolbarCardView extends MaterialCardView {

    public ToolbarCardView(Context context) {
        super(context);
        init();
    }

    public ToolbarCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ToolbarCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (UselessUtils.ifCustomTheme()) {
            setCardElevation(ThemesEngine.shadows);
            setCardBackgroundColor(ThemesEngine.toolbarColor);
        } else if (UselessUtils.getBool("night", false)) {
            setCardElevation(0.0f);
            setCardBackgroundColor(0xff424242);
        }
    }
}
