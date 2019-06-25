package com.f0x1d.notes.utils.dialogs;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.f0x1d.notes.App;
import com.f0x1d.notes.BuildConfig;
import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;

public class ShowAlertDialog {

    public static void show(AlertDialog dialog1337) {
        dialog1337.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog1) {
                if (PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("night", true)) {
                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    dialog1337.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
                    dialog1337.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                }
                if (UselessUtils.ifCustomTheme()) {
                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ThemesEngine.textColor);
                    dialog1337.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(ThemesEngine.textColor);
                    dialog1337.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ThemesEngine.textColor);

                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                    dialog1337.getButton(DialogInterface.BUTTON_NEUTRAL).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                    dialog1337.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));

                    try {
                        ((TextView) dialog1337.findViewById(App.getContext().getResources().getIdentifier("alertTitle", "id", BuildConfig.APPLICATION_ID)))
                                .setTextColor(ThemesEngine.textColor);
                    } catch (Exception e){
                        Logger.log(e);
                    }
                    try {
                        ((TextView) dialog1337.findViewById(android.R.id.message)).setTextColor(ThemesEngine.textColor);
                    } catch (Exception e){
                        Logger.log(e);
                    }

                    try {
                        ViewGroup listView = dialog1337.findViewById(androidx.appcompat.R.id.select_dialog_listview);
                        for (int i = 0; i < listView.getChildCount(); i++){
                            View view = listView.getChildAt(i);
                            if (view instanceof TextView){
                                ((TextView) view).setTextColor(ThemesEngine.textColor);
                            }
                        }
                    } catch (Exception e){
                        Logger.log(e);
                    }
                }
            }
        });

        dialog1337.show();

        if (UselessUtils.ifCustomTheme())
            dialog1337.getWindow().getDecorView().getBackground().setColorFilter(ThemesEngine.background, PorterDuff.Mode.SRC);
        else if (UselessUtils.getBool("night", true))
            dialog1337.getWindow().getDecorView().getBackground().setColorFilter(App.getContext().getResources().getColor(R.color.statusbar_for_dialogs), PorterDuff.Mode.SRC);
        else
            dialog1337.getWindow().getDecorView().getBackground().setColorFilter(App.getContext().getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC);
    }
}
