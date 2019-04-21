package com.f0x1d.notes.utils.bottomSheet;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.io.Serializable;

public class Element implements Parcelable {

    public static final Creator<Element> CREATOR = new Creator<Element>() {
        @Override
        public Element createFromParcel(Parcel in) {
            return new Element(in);
        }

        @Override
        public Element[] newArray(int size) {
            return new Element[size];
        }
    };

    public String name;
    public Drawable pic;
    public View.OnClickListener listener;

    public Element(String name, Drawable pic, View.OnClickListener listener) {
        this.listener = listener;
        this.name = name;
        this.pic = pic;
    }

    protected Element(Parcel in) {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}
}
