package com.f0x1d.notes.view.theming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

@SuppressLint("AppCompatCustomView")
public class MyEditText extends EditText {

    public MyEditText(Context context) {
        super(context);

        setText();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        setText();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setText();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setText();
    }

    private void setText(){
        if (UselessUtils.ifCustomTheme()){
            this.setTextColor(ThemesEngine.textColor);
            this.setHintTextColor(ThemesEngine.textHintColor);
            this.setBackground(null);

            UselessUtils.setCursorColor(this, ThemesEngine.accentColor);
        }
    }

    @Override
    public void setTextColor(int color) {
        if (UselessUtils.ifCustomTheme()){
            super.setTextColor(ThemesEngine.textColor);
        } else {
            super.setTextColor(color);
        }
    }
}
