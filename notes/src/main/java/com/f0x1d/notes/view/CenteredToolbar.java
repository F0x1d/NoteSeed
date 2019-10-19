package com.f0x1d.notes.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.search.SearchFragment;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;

public class CenteredToolbar extends Toolbar {

    private TextView tvTitle;
    private TextView tvSubtitle;

    private LinearLayout linear;

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
    public CharSequence getTitle() {
        return tvTitle.getText().toString();
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

    @Override
    public CharSequence getSubtitle() {
        return tvSubtitle.getText().toString();
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

    private void setupTextViews() {
        tvSubtitle = new TextView(getContext());
        tvTitle = new TextView(getContext());

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_down);
        animation.setDuration(300);
        tvTitle.startAnimation(animation);

        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
        tvTitle.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);

        if (UselessUtils.isCustomTheme()) {
            tvTitle.setTextColor(ThemesEngine.toolbarTextColor);
            setBackgroundColor(Color.TRANSPARENT);
        }

        tvTitle.setTypeface(ResourcesCompat.getFont(getContext(), R.font.medium));

        linear = new LinearLayout(getContext());
        linear.setGravity(Gravity.CENTER);
        linear.setOrientation(LinearLayout.VERTICAL);
        linear.addView(tvTitle);
        linear.addView(tvSubtitle);

        tvSubtitle.setSingleLine();
        tvSubtitle.setEllipsize(TextUtils.TruncateAt.END);
        tvSubtitle.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Widget_ActionBar_Subtitle);

        tvSubtitle.setVisibility(GONE);

        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        linear.setLayoutParams(lp);

        addView(linear);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (UselessUtils.isCustomTheme()) {
                    setOverflowIcon(UselessUtils.setTint(getResources().getDrawable(androidx.appcompat.R.drawable.abc_ic_menu_overflow_material, getContext().getTheme()), ThemesEngine.iconsColor));
                } else if (UselessUtils.getBool("night", false)) {
                    setOverflowIcon(UselessUtils.setTint(getResources().getDrawable(androidx.appcompat.R.drawable.abc_ic_menu_overflow_material, getContext().getTheme()), Color.WHITE));
                } else {
                    setOverflowIcon(UselessUtils.setTint(getResources().getDrawable(androidx.appcompat.R.drawable.abc_ic_menu_overflow_material, getContext().getTheme()), Color.BLACK));
                }
            } catch (Exception e) {
                Logger.log(e);
            }
        }
    }

    public void goAnim(String inFolderId, AppCompatActivity activity) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UselessUtils.replace(activity, SearchFragment.newInstance(inFolderId), "search", true, null);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTitle.animate()
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvTitle.setVisibility(View.GONE);
                            }
                        });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvTitle.setVisibility(VISIBLE);
                        tvTitle.setText(getContext().getString(R.string.tap_to_search));
                        tvTitle.setTextSize(17);

                        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        linear.setGravity(Gravity.START);
                        lp.gravity = Gravity.START;
                        linear.setLayoutParams(lp);

                        tvTitle.animate()
                                .alpha(0.0f)
                                .alpha(1.0f)
                                .setDuration(300)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                    }
                                });
                    }
                }, 500);
            }
        }, 1500);
    }

}
