package com.f0x1d.notes.fragment.bottomSheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TextSizeDialog extends BottomSheetDialogFragment {

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.text_size_layout, null);

        LinearLayout layout = view.findViewById(R.id.background);

        if (UselessUtils.ifCustomTheme()) {
            layout.setBackgroundColor(ThemesEngine.background);
        } else if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", true)) {
            layout.setBackgroundColor(getActivity().getResources().getColor(R.color.statusbar));
        } else {
            layout.setBackgroundColor(Color.WHITE);
        }

        final SeekBar size = view.findViewById(R.id.text_size);

        final TextView text = view.findViewById(R.id.text);

        size.setProgress(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("text_size", "15")));

        text.setTextSize(size.getProgress());
        text.setText(getString(R.string.text) + " " + size.getProgress());

        size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text.setTextSize(size.getProgress());
                text.setText(getString(R.string.text) + " " + size.getProgress());

                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("text_size", String.valueOf(size.getProgress())).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dialog.setContentView(view);
    }

    @SuppressLint("NewApi")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return super.onCreateDialog(savedInstanceState);

        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(UselessUtils.getNavColor());

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }

        return dialog;
    }
}
