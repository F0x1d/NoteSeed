package com.f0x1d.notes.utils.bottomSheet;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetCreator {

    public CustomBottomSheet customBottomSheet;
    private FragmentActivity context;
    private List<Element> elements;

    public BottomSheetCreator(FragmentActivity someContext) {
        this.context = someContext;
        elements = new ArrayList<>();
    }

    public BottomSheetCreator addElement(Element elem) {
        elements.add(elem);
        return this;
    }

    public void show(String TAG, boolean cancelable) {
        customBottomSheet = CustomBottomSheet.newInstance(elements);
        customBottomSheet.setCancelable(cancelable);
        customBottomSheet.show(context.getSupportFragmentManager(), TAG);
    }
}
