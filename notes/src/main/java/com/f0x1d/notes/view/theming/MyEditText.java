package com.f0x1d.notes.view.theming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import androidx.core.content.res.ResourcesCompat;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;

import java.util.ArrayList;

@SuppressLint("AppCompatCustomView")
public class MyEditText extends EditText {

    private ArrayList<TextWatcher> mListeners = null;

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

    private void setText() {
        if (UselessUtils.ifCustomTheme()) {
            this.setTextColor(ThemesEngine.textColor);
            this.setHintTextColor(ThemesEngine.textHintColor);
            this.setBackground(null);

            //UselessUtils.setCursorColor(this, ThemesEngine.accentColor);
        }

        setTypeface(ResourcesCompat.getFont(getContext(), R.font.medium));
    }

    @Override
    public void setTextColor(int color) {
        if (UselessUtils.ifCustomTheme()) {
            super.setTextColor(ThemesEngine.textColor);
        } else {
            super.setTextColor(color);
        }
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        if (mListeners == null) {
            mListeners = new ArrayList<TextWatcher>();
        }
        mListeners.add(watcher);

        super.addTextChangedListener(watcher);
    }

    @Override
    public void removeTextChangedListener(TextWatcher watcher) {
        if (mListeners != null) {
            int i = mListeners.indexOf(watcher);
            if (i >= 0) {
                mListeners.remove(i);
            }
        }

        super.removeTextChangedListener(watcher);
    }

    public void clearTextChangedListeners() {
        if (mListeners != null) {
            for (TextWatcher watcher : mListeners) {
                super.removeTextChangedListener(watcher);
            }

            mListeners.clear();
            mListeners = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return true;
    }
}
