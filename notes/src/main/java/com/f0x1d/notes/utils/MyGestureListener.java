package com.f0x1d.notes.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.mancj.slideup.SlideUp;

public class MyGestureListener implements GestureDetector.OnGestureListener {

    private Context context;
    private SlideUp slideUp;

    public MyGestureListener(Context context, SlideUp slideUp) {
        this.context = context;
        this.slideUp = slideUp;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getY() - e2.getY() > 50){
            if (!slideUp.isVisible())
                slideUp.show();
            return true;
        }

        if(e2.getY() - e1.getY() > 50){
            if (slideUp.isVisible())
                slideUp.hide();
            return true;
        }
        return false;
    }
}
