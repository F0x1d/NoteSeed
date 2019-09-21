package com.f0x1d.notes.utils.theme;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.f0x1d.notes.utils.UselessUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ColorBindingAdapter {

    @BindingAdapter("android:background")
    public static void setBackground(final View view, int color) {
        int colorFrom = 0;

        Drawable backgroundDrawable = view.getBackground();
        if (backgroundDrawable instanceof ColorDrawable)
            colorFrom = ((ColorDrawable) backgroundDrawable).getColor();
        else if (UselessUtils.ifCustomTheme())
            colorFrom = ThemesEngine.background;
        else
            colorFrom = Color.WHITE;

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, color);
        colorAnimation.setDuration(250);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

    @BindingAdapter("android:backgroundTint")
    public static void setBackgroundTint(final View view, int color) {
        int colorFrom;
        try {
            colorFrom = view.getBackgroundTintList().getDefaultColor();
        } catch (Exception e) {
            colorFrom = Color.WHITE;
        }

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, color);
        colorAnimation.setDuration(250);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundTintList(ColorStateList.valueOf((int) animator.getAnimatedValue()));
            }
        });
        colorAnimation.start();
    }

    @BindingAdapter("android:textColor")
    public static void setButtonTextColor(final MaterialButton view, int color) {
        int colorFrom = view.getCurrentTextColor();

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, color);
        colorAnimation.setDuration(250);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setTextColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

    @BindingAdapter("android:textColor")
    public static void setSwitchTextColor(final SwitchMaterial view, int color) {
        int colorFrom = view.getCurrentTextColor();

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, color);
        colorAnimation.setDuration(250);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setTextColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

    @BindingAdapter("android:textColor")
    public static void setTextColor(final TextView view, int color) {
        int colorFrom = view.getCurrentTextColor();

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, color);
        colorAnimation.setDuration(250);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setTextColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }
}
