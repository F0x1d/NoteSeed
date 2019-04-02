package com.f0x1d.notes.utils.bottomSheet;

import android.graphics.drawable.Drawable;
import android.view.View;

public class Element {

    public String name;
    public Drawable pic;
    public View.OnClickListener listener;

    public Element(String name, Drawable pic, View.OnClickListener listener){
        this.listener = listener;
        this.name = name;
        this.pic = pic;
    }
}
