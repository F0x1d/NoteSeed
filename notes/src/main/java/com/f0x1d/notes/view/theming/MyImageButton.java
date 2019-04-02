package com.f0x1d.notes.view.theming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

@SuppressLint("AppCompatCustomView")
public class MyImageButton extends ImageButton {

    public MyImageButton(Context context) {
        super(context);

        setup();
    }

    public MyImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setup();
    }

    public MyImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setup();
    }

    public MyImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setup();
    }

    private void setup() {
        if (UselessUtils.ifCustomTheme()) {
            setImageDrawable(this.getDrawable());
        }
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (UselessUtils.ifCustomTheme()) {
            super.setImageDrawable(UselessUtils.setTint(drawable, ThemesEngine.iconsColor));
        } else {
            super.setImageDrawable(drawable);
        }
    }
}
