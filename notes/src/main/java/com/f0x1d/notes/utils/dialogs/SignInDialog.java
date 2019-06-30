package com.f0x1d.notes.utils.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AlertDialog;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.translations.Translations;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class SignInDialog {

    public void show(Activity activity, GoogleSignInClient client) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(Translations.getString("do_u_want_signin"));
        builder.setMessage(Translations.getString("wihout_sign_in"));
        builder.setCancelable(false);

        builder.setPositiveButton(Translations.getString("ok"), (dialog, which) -> {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(App.getContext()) == ConnectionResult.SUCCESS) {
                try {
                    Intent signInIntent = client.getSignInIntent();
                    activity.startActivityForResult(signInIntent, 1);
                } catch (Exception e) {
                    Logger.log(e);
                }
            }
        });
        builder.setNeutralButton(Translations.getString("no"), (dialog, which) -> {
            dialog.cancel();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("want_sign_in", false).apply();
        });
        ShowAlertDialog.show(builder.create());
    }
}
