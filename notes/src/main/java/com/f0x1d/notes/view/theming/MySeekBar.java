package com.f0x1d.notes.view.theming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

@SuppressLint("AppCompatCustomView")
public class MySeekBar extends SeekBar {

    public MySeekBar(Context context) {
        super(context);

        start();
    }

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        start();
    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        start();
    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        start();
    }

    private void start() {
        if (UselessUtils.ifCustomTheme()) {
            getProgressDrawable().setColorFilter(ThemesEngine.seekBarColor, PorterDuff.Mode.SRC_ATOP);
            getThumb().setColorFilter(ThemesEngine.seekBarThumbColor, PorterDuff.Mode.SRC_ATOP);
        }
    }
}
