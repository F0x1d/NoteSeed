package com.f0x1d.notes.fragment.bottomSheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DeleteDialog extends BottomSheetDialogFragment {

    private View.OnClickListener deleteListener;
    private View.OnClickListener cancelListener;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete, null);

        TextView delete = v.findViewById(R.id.delete);
        TextView cancel = v.findViewById(R.id.cancel);

        if (UselessUtils.getBool("night", true)){
            delete.setBackgroundTintList(ColorStateList.valueOf(App.getContext().getResources().getColor(R.color.statusbar)));
            cancel.setBackgroundTintList(ColorStateList.valueOf(App.getContext().getResources().getColor(R.color.statusbar)));

            delete.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_white_24dp, 0, 0, 0);
            cancel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clear_white_24dp, 0, 0, 0);
        } else {
            delete.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_black_24dp, 0, 0, 0);
            cancel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clear_black_24dp, 0, 0, 0);
        }

        delete.setOnClickListener(deleteListener);
        cancel.setOnClickListener(cancelListener);

        LinearLayout layout = v.findViewById(R.id.background);

        if (UselessUtils.ifCustomTheme()){
            layout.setBackgroundColor(ThemesEngine.background);
        } else if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", true)){
            layout.setBackgroundColor(getActivity().getResources().getColor(R.color.statusbar));
        } else {
            layout.setBackgroundColor(Color.TRANSPARENT);
        }

        dialog.setContentView(v);
    }

    public void setDeleteListener(View.OnClickListener listener){
        this.deleteListener = listener;
    }

    public void setCancelListener(View.OnClickListener listener){
        this.cancelListener = listener;
    }
}
