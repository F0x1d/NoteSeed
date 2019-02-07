package com.f0x1d.notes.utils.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.f0x1d.notes.App;
import com.f0x1d.notes.BuildConfig;
import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.PreferenceUtils;
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
                PreferenceUtils.edit().putBoolean("want_sign_in", false);
            });
            builder.show();
    }
}
