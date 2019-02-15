package com.f0x1d.notes.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;

public class AnimUtils {

    public static void animRotate(View view){
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .rotation(360f)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public static void animPushUp(View view){
        Animation animation = AnimationUtils.loadAnimation(App.getContext(), R.anim.push_up);
            animation.setDuration(300);
        view.startAnimation(animation);
    }
}
