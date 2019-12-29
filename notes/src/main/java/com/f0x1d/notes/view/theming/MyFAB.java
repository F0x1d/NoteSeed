package com.f0x1d.notes.view.theming;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.bottomSheet.Element;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MyFAB extends FloatingActionButton {

    private boolean opened = false;
    private boolean clicked = false;
    private List<MyFAB> miniFabs = new ArrayList<>();
    private List<MyTextView> textViews = new ArrayList<>();
    private ViewGroup rootView;

    public MyFAB(Context context) {
        super(context);
        start();
    }

    public MyFAB(Context context, AttributeSet attrs) {
        super(context, attrs);
        start();
    }

    public MyFAB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        start();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        if (UselessUtils.isCustomTheme())
            setImageTintList(ColorStateList.valueOf(ThemesEngine.fabIconColor));
        else if (UselessUtils.getBool("night", false))
            setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(android.R.color.black)));
        else
            setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(android.R.color.white)));
    }

    public void setElements(List<Element> elements, ViewGroup rootView) {
        if (elements.isEmpty())
            return;

        this.rootView = rootView;

        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);

            MyFAB fab = new MyFAB(getContext());
            fab.setVisibility(View.INVISIBLE);
            fab.setAlpha(0.0f);
            fab.setOnClickListener(element.listener);
            fab.setImageDrawable(element.pic);
            fab.setSize(SIZE_MINI);

            rootView.addView(fab);

            MyTextView textView = new MyTextView(getContext());
            textView.setVisibility(View.INVISIBLE);
            textView.setAlpha(0.0f);
            textView.setText(element.name);
            rootView.addView(textView);

            miniFabs.add(fab);
            textViews.add(textView);
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked)
                    return;

                clicked = true;

                if (opened) {
                    closeList();
                } else {
                    openList();
                }
            }
        });
    }

    public void closeList() {
        opened = false;
        for (int i = 0; i < miniFabs.size(); i++) {
            MyFAB fab = miniFabs.get(i);
            fab.setClickable(false);

            MyTextView textView = textViews.get(i);

            for (int j = 0; j < rootView.getChildCount(); j++) {
                View child = rootView.getChildAt(j);
                if (!(child instanceof MyFAB) && !(child instanceof MyTextView)) {
                    child.animate()
                            .alpha(0.5f)
                            .alpha(1.0f)
                            .setDuration(200)
                            .start();
                }
            }

            animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .rotation(-90f)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();

            ViewCompat.animate(fab)
                    .alpha(1.0f)
                    .alpha(0.0f)
                    .translationYBy(getHeight() * (i + 1) + 100)
                    .setDuration(300)
                    .setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(View view) {
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            fab.setVisibility(View.INVISIBLE);
                            clicked = false;
                        }

                        @Override
                        public void onAnimationCancel(View view) {
                        }
                    }).start();

            ViewCompat.animate(textView)
                    .alpha(1.0f)
                    .alpha(0.0f)
                    .translationYBy(getHeight() * (i + 1) + 100)
                    .setDuration(300)
                    .setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(View view) {
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            textView.setVisibility(View.INVISIBLE);
                            clicked = false;
                        }

                        @Override
                        public void onAnimationCancel(View view) {
                        }
                    }).start();
        }
    }

    public void openList() {
        opened = true;
        for (int i = 0; i < miniFabs.size(); i++) {
            MyFAB fab = miniFabs.get(i);
            fab.setClickable(true);
            fab.setX(getX() + (getWidth() - fab.getWidth()) / 2);
            fab.setY(getY());
            fab.setZ(getZ());

            MyTextView textView = textViews.get(i);
            textView.setX(fab.getX() - textView.getWidth() - 20);
            textView.setY(fab.getY() + (fab.getHeight() - textView.getHeight()) / 2);
            textView.setZ(fab.getZ());

            for (int j = 0; j < rootView.getChildCount(); j++) {
                View child = rootView.getChildAt(j);
                if (!(child instanceof MyFAB)) {
                    child.animate()
                            .alpha(1.0f)
                            .alpha(0.5f)
                            .setDuration(200)
                            .start();
                }
            }

            animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .rotation(45f)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();

            ViewCompat.animate(fab)
                    .alpha(0.0f)
                    .alpha(1.0f)
                    .translationYBy(-(getHeight() * (i + 1) + 100))
                    .setDuration(300)
                    .setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(View view) {
                            fab.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            clicked = false;
                        }

                        @Override
                        public void onAnimationCancel(View view) {
                        }
                    }).start();

            ViewCompat.animate(textView)
                    .alpha(0.0f)
                    .alpha(1.0f)
                    .translationYBy(-(getHeight() * (i + 1) + 100))
                    .setDuration(300)
                    .setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(View view) {
                            textView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            clicked = false;
                        }

                        @Override
                        public void onAnimationCancel(View view) {
                        }
                    }).start();
        }
    }

    private void start() {
        if (UselessUtils.isCustomTheme()) {
            setBackgroundTintList(ColorStateList.valueOf(ThemesEngine.fabColor));
            setImageTintList(ColorStateList.valueOf(ThemesEngine.fabIconColor));
            setCompatElevation(ThemesEngine.shadows);
        } else if (UselessUtils.getBool("night", false)) {
            setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        } else if (UselessUtils.getBool("orange", false)) {
            setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.noname)));
        } else {
            setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
        }
    }
}
