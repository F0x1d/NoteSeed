package com.f0x1d.notes.utils.bottomSheet;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BottomSheetCreator {

    private FragmentActivity context;
    private List<Element> elements;

    public CustomBottomSheet customBottomSheet;

    public BottomSheetCreator(FragmentActivity someContext){
        this.context = someContext;
        elements = new ArrayList<>();
    }

    public BottomSheetCreator addElement(Element elem){
        elements.add(elem);
        return this;
    }

    public void show(String TAG, boolean cancelable){
        customBottomSheet = CustomBottomSheet.newInstance(elements);
        customBottomSheet.setCancelable(cancelable);
        customBottomSheet.show(context.getSupportFragmentManager(), TAG);
    }
}
