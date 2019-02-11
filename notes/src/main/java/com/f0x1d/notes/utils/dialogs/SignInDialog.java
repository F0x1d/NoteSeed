package com.f0x1d.notes.utils.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AlertDialog;

import com.f0x1d.notes.App;
import com.f0x1d.notes.BuildConfig;
import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class SignInDialog {

    public void show(Activity activity, GoogleSignInClient client){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.do_u_want_signin);
            builder.setMessage(R.string.wihout_sign_in);
            builder.setCancelable(false);

            builder.setPositiveButton("OK", (dialog, which) -> {
                if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(App.getContext()) == ConnectionResult.SUCCESS){
                    Intent signInIntent = client.getSignInIntent();
                    if (!BuildConfig.DEBUG){
                        activity.startActivityForResult(signInIntent, 1);
                    }
                }
            });
            builder.setNeutralButton(R.string.no, (dialog, which) -> {
                dialog.cancel();
                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("want_sign_in", false).apply();
            });
        AlertDialog dialog1337 = builder.create();
        dialog1337.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                try {
                    if (PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("night", false)){
                        dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                        dialog1337.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
                        dialog1337.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                    }
                    if (UselessUtils.ifCustomTheme()){
                        dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ThemesEngine.textColor);
                        dialog1337.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(ThemesEngine.textColor);
                        dialog1337.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ThemesEngine.textColor);

                        dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                        dialog1337.getButton(DialogInterface.BUTTON_NEUTRAL).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                        dialog1337.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ThemesEngine.textColor);
                    }
                } catch (Exception e){}
            }
        });
        dialog1337.show();
    }
}
