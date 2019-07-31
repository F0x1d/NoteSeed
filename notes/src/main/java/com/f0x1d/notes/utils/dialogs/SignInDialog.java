package com.f0x1d.notes.utils.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.Logger;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SignInDialog {

    public void show(Activity activity, GoogleSignInClient client) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        builder.setTitle(activity.getString(R.string.do_u_want_signin));
        builder.setMessage(activity.getString(R.string.wihout_sign_in));
        builder.setCancelable(false);

        builder.setPositiveButton(activity.getString(R.string.ok), (dialog, which) -> {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(App.getContext()) == ConnectionResult.SUCCESS) {
                try {
                    Intent signInIntent = client.getSignInIntent();
                    activity.startActivityForResult(signInIntent, 1);
                } catch (Exception e) {
                    Logger.log(e);
                }
            }
        });
        builder.setNeutralButton(activity.getString(R.string.no), (dialog, which) -> {
            dialog.cancel();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("want_sign_in", false).apply();
        });
        ShowAlertDialog.show(builder);
    }
}
