package com.f0x1d.notes.view.theming;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;

public class MyColorPickerDialog extends ColorPickerDialog {

    private static final String ARG_ID = "id";
    private static final String ARG_TYPE = "dialogType";
    private static final String ARG_COLOR = "color";
    private static final String ARG_ALPHA = "alpha";
    private static final String ARG_PRESETS = "presets";
    private static final String ARG_ALLOW_PRESETS = "allowPresets";
    private static final String ARG_ALLOW_CUSTOM = "allowCustom";
    private static final String ARG_DIALOG_TITLE = "dialogTitle";
    private static final String ARG_SHOW_COLOR_SHADES = "showColorShades";
    private static final String ARG_COLOR_SHAPE = "colorShape";
    private static final String ARG_PRESETS_BUTTON_TEXT = "presetsButtonText";
    private static final String ARG_CUSTOM_BUTTON_TEXT = "customButtonText";
    private static final String ARG_SELECTED_BUTTON_TEXT = "selectedButtonText";

    public static Builder newBuilderNew() {
        return new Builder();
    }

    @Override
    public void onStart() {
        super.onStart();

        Button positive = getDialog().findViewById(android.R.id.button1);
        Button neutral = getDialog().findViewById(android.R.id.button3);

        if (UselessUtils.isCustomTheme()) {
            positive.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            positive.setTextColor(ThemesEngine.textColor);

            neutral.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            neutral.setTextColor(ThemesEngine.textColor);
        } else if (UselessUtils.getBool("night", false)) {
            positive.setTextColor(Color.BLACK);
            neutral.setTextColor(Color.BLACK);
        }

        if (UselessUtils.isCustomTheme())
            getDialog().getWindow().getDecorView().getBackground().setColorFilter(ThemesEngine.background, PorterDuff.Mode.SRC);
        else if (UselessUtils.getBool("night", false))
            getDialog().getWindow().getDecorView().getBackground().setColorFilter(getResources().getColor(R.color.statusbar_for_dialogs), PorterDuff.Mode.SRC);
        else
            getDialog().getWindow().getDecorView().getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC);
    }

    public static final class Builder {

        ColorPickerDialogListener colorPickerDialogListener;
        @StringRes
        int dialogTitle = R.string.cpv_default_title;
        @StringRes
        int presetsButtonText = R.string.cpv_presets;
        @StringRes
        int customButtonText = R.string.cpv_custom;
        @StringRes
        int selectedButtonText = R.string.cpv_select;
        @DialogType
        int dialogType = TYPE_PRESETS;
        int[] presets = MATERIAL_COLORS;
        @ColorInt
        int color = Color.BLACK;
        int dialogId = 0;
        boolean showAlphaSlider = false;
        boolean allowPresets = true;
        boolean allowCustom = true;
        boolean showColorShades = true;
        @ColorShape
        int colorShape = ColorShape.CIRCLE;

        /*package*/ Builder() {

        }

        /**
         * Set the dialog title string resource id
         *
         * @param dialogTitle The string resource used for the dialog title
         * @return This builder object for chaining method calls
         */
        public Builder setDialogTitle(@StringRes int dialogTitle) {
            this.dialogTitle = dialogTitle;
            return this;
        }

        /**
         * Set the selected button text string resource id
         *
         * @param selectedButtonText The string resource used for the selected button text
         * @return This builder object for chaining method calls
         */
        public Builder setSelectedButtonText(@StringRes int selectedButtonText) {
            this.selectedButtonText = selectedButtonText;
            return this;
        }

        /**
         * Set the presets button text string resource id
         *
         * @param presetsButtonText The string resource used for the presets button text
         * @return This builder object for chaining method calls
         */
        public Builder setPresetsButtonText(@StringRes int presetsButtonText) {
            this.presetsButtonText = presetsButtonText;
            return this;
        }

        /**
         * Set the custom button text string resource id
         *
         * @param customButtonText The string resource used for the custom button text
         * @return This builder object for chaining method calls
         */
        public Builder setCustomButtonText(@StringRes int customButtonText) {
            this.customButtonText = customButtonText;
            return this;
        }

        /**
         * Set which dialog view to show.
         *
         * @param dialogType Either {@link ColorPickerDialog#TYPE_CUSTOM} or {@link ColorPickerDialog#TYPE_PRESETS}.
         * @return This builder object for chaining method calls
         */
        public Builder setDialogType(@DialogType int dialogType) {
            this.dialogType = dialogType;
            return this;
        }

        /**
         * Set the colors used for the presets
         *
         * @param presets An array of color ints.
         * @return This builder object for chaining method calls
         */
        public Builder setPresets(@NonNull int[] presets) {
            this.presets = presets;
            return this;
        }

        /**
         * Set the original color
         *
         * @param color The default color for the color picker
         * @return This builder object for chaining method calls
         */
        public Builder setColor(int color) {
            this.color = color;
            return this;
        }


        public Builder setDialogId(int dialogId) {
            this.dialogId = dialogId;
            return this;
        }


        public Builder setShowAlphaSlider(boolean showAlphaSlider) {
            this.showAlphaSlider = showAlphaSlider;
            return this;
        }

        /**
         * Show/Hide a neutral button to select preset colors.
         *
         * @param allowPresets {@code false} to disable showing the presets button.
         * @return This builder object for chaining method calls
         */
        public Builder setAllowPresets(boolean allowPresets) {
            this.allowPresets = allowPresets;
            return this;
        }

        /**
         * Show/Hide the neutral button to select a custom color.
         *
         * @param allowCustom {@code false} to disable showing the custom button.
         * @return This builder object for chaining method calls
         */
        public Builder setAllowCustom(boolean allowCustom) {
            this.allowCustom = allowCustom;
            return this;
        }

        /**
         * Show/Hide the color shades in the presets picker
         *
         * @param showColorShades {@code false} to hide the color shades.
         * @return This builder object for chaining method calls
         */
        public Builder setShowColorShades(boolean showColorShades) {
            this.showColorShades = showColorShades;
            return this;
        }

        public Builder setColorShape(int colorShape) {
            this.colorShape = colorShape;
            return this;
        }

        /**
         * Create the {@link ColorPickerDialog} instance.
         *
         * @return A new {@link ColorPickerDialog}.
         * @see #show(FragmentActivity)
         */
        public MyColorPickerDialog create() {
            MyColorPickerDialog dialog = new MyColorPickerDialog();
            Bundle args = new Bundle();
            args.putInt(ARG_ID, dialogId);
            args.putInt(ARG_TYPE, dialogType);
            args.putInt(ARG_COLOR, color);
            args.putIntArray(ARG_PRESETS, presets);
            args.putBoolean(ARG_ALPHA, showAlphaSlider);
            args.putBoolean(ARG_ALLOW_CUSTOM, allowCustom);
            args.putBoolean(ARG_ALLOW_PRESETS, allowPresets);
            args.putInt(ARG_DIALOG_TITLE, dialogTitle);
            args.putBoolean(ARG_SHOW_COLOR_SHADES, showColorShades);
            args.putInt(ARG_COLOR_SHAPE, colorShape);
            args.putInt(ARG_PRESETS_BUTTON_TEXT, presetsButtonText);
            args.putInt(ARG_CUSTOM_BUTTON_TEXT, customButtonText);
            args.putInt(ARG_SELECTED_BUTTON_TEXT, selectedButtonText);
            dialog.setArguments(args);
            return dialog;
        }

        /**
         * Create and show the {@link ColorPickerDialog} created with this builder.
         *
         * @param activity The current activity.
         */
        public void show(FragmentActivity activity) {
            create().show(activity.getSupportFragmentManager(), "color-picker-dialog");
        }
    }
}
