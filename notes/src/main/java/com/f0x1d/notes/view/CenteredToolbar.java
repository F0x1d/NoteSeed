package com.f0x1d.notes.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public class CenteredToolbar extends Toolbar {

    private TextView tvTitle;
    private TextView tvSubtitle;

    public CenteredToolbar(Context context) {
        super(context);
        setupTextViews();
    }

    public CenteredToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupTextViews();
    }

    public CenteredToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupTextViews();
    }

    public CenteredToolbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupTextViews();
    }

    @Override
    public void setTitle(@StringRes int resId) {
        String s = getResources().getString(resId);
        setTitle(s);
    }

    @Override
    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    public void setTitleColor(int color){
        if (UselessUtils.ifCustomTheme()){
            tvTitle.setTextColor(ThemesEngine.toolbarTextColor);
        } else {
            tvTitle.setTextColor(color);
        }
    }

    @Override
    public void setSubtitle(int resId) {
        String s = getResources().getString(resId);
        setSubtitle(s);
    }

    @Override
    public void setSubtitle(CharSequence subtitle) {
        tvSubtitle.setVisibility(VISIBLE);
        tvSubtitle.setText(subtitle);
    }

    public void setTypeFace(Typeface face){
        tvTitle.setTypeface(face);
    }

    @Override
    public CharSequence getTitle() {
        return tvTitle.getText().toString();
    }

    @Override
    public CharSequence getSubtitle() {
        return tvSubtitle.getText().toString();
    }

    private void setupTextViews() {
        tvSubtitle = new TextView(getContext());
        tvTitle = new TextView(getContext());

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_down);
        animation.setDuration(300);
        tvTitle.startAnimation(animation);

        tvTitle.setSingleLine();
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
        tvTitle.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);

        if (UselessUtils.ifCustomTheme()){
            tvTitle.setTextColor(ThemesEngine.toolbarTextColor);

            if (!ThemesEngine.toolbarTransparent){
                this.setBackgroundColor(ThemesEngine.toolbarColor);
            }
        }

        tvSubtitle.setSingleLine();
        tvSubtitle.setEllipsize(TextUtils.TruncateAt.END);
        tvSubtitle.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Widget_ActionBar_Subtitle);

        LinearLayout linear = new LinearLayout(getContext());
        linear.setGravity(Gravity.CENTER);
        linear.setOrientation(LinearLayout.VERTICAL);
        linear.addView(tvTitle);
        linear.addView(tvSubtitle);

        tvSubtitle.setVisibility(GONE);

        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        linear.setLayoutParams(lp);

        addView(linear);

        if (UselessUtils.ifCustomTheme()){
            this.setOverflowIcon(UselessUtils.setTint(getResources().getDrawable(androidx.appcompat.R.drawable.abc_ic_menu_overflow_material), ThemesEngine.iconsColor));
        } else if (UselessUtils.getBool("night", true)){
            this.setOverflowIcon(UselessUtils.setTint(getResources().getDrawable(androidx.appcompat.R.drawable.abc_ic_menu_overflow_material), Color.WHITE));
        } else {
            this.setOverflowIcon(UselessUtils.setTint(getResources().getDrawable(androidx.appcompat.R.drawable.abc_ic_menu_overflow_material), Color.BLACK));
        }
    }

}
