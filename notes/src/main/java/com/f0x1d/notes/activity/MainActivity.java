package com.f0x1d.notes.activity;

import android.accounts.Account;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.editing.NoteEdit;
import com.f0x1d.notes.fragment.lock.LockScreen;
import com.f0x1d.notes.fragment.lock.LockTickerScreen;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.fragment.settings.MainSettings;
import com.f0x1d.notes.fragment.settings.themes.ThemesFragment;
import com.f0x1d.notes.utils.PermissionUtils;
import com.f0x1d.notes.utils.SyncUtils;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.dialogs.BackupDialog;
import com.f0x1d.notes.utils.dialogs.SignInDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;

import static android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static com.f0x1d.notes.utils.UselessUtils.clear_back_stack;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("notes", "onCreate");
        instance = this;
        PermissionUtils.requestWriteExternalPermission(this);

        if (UselessUtils.ifCustomTheme()) {
            new ThemesEngine().setupAll();
        }

        if (UselessUtils.ifCustomTheme()) {
            if (ThemesEngine.dark) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setTheme(R.style.NightTheme_md2);
                } else {
                    setTheme(R.style.NightTheme);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setTheme(R.style.AppTheme_md2);
                } else {
                    setTheme(R.style.AppTheme);
                }
            }

            try {
                getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
                getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
            } catch (Exception e) {
                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
                recreate();
            }
        } else {
            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("night", true)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setTheme(R.style.NightTheme_md2);
                } else {
                    setTheme(R.style.NightTheme);
                }

                getWindow().setNavigationBarColor(getResources().getColor(R.color.statusbar));
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("orange", false)) {
                        setTheme(R.style.AppTheme_Orange_md2);
                    } else {
                        setTheme(R.style.AppTheme_md2);
                    }
                } else {
                    if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("orange", false)) {
                        setTheme(R.style.AppTheme_Orange);
                    } else {
                        setTheme(R.style.AppTheme);
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getWindow().setStatusBarColor(Color.WHITE);
                } else {
                    getWindow().setStatusBarColor(Color.GRAY);
                }
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (GoogleSignIn.getLastSignedInAccount(this) == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                    .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            signIn();
        }

        try {
            savedInstanceState.getString("what_frag");
        } catch (Exception e) {
            if (getIntent().getExtras() != null){
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                        R.id.container, NoteEdit.newInstance(getIntent().getExtras()), "edit").commit();
                return;
            }

            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("change", false)) {
                clear_back_stack();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                        R.id.container, new Notes(), "notes").commit();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                        R.id.container, new MainSettings(), "settings").addToBackStack(null).commit();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                        R.id.container, ThemesFragment.newInstance(false), "themes").addToBackStack(null).commit();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("change", false).apply();
            } else {

                if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("lock", false)) {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                            R.id.container, LockScreen.newInstance(), "lock").commit();
                } else {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                            R.id.container, new Notes(), "notes").commit();
                }
            }
        }
    }

    private void signIn() {
        if (UselessUtils.getBool("want_sign_in", true)) {
            Log.e("notes_err", "want_sign_in: " + UselessUtils.getBool("want_sign_in", true));

            new SignInDialog().show(this, mGoogleSignInClient);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("what_frag", "other");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            signIn();
        }
    }

    private void handleSignInResult(@Nullable Task<GoogleSignInAccount> completedTask) {
        Log.e("notes_err", "handleSignInResult:" + completedTask.isSuccessful());

        try {
            Account account = completedTask.getResult(ApiException.class).getAccount();

            Log.e("notes_err", account.name);

            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("want_sign_in", false).apply();
            BackupDialog.show(this, account);
        } catch (ApiException e) {
            Log.e("notes_err", "handleSignInResult:error \n\n", e);
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment notes = getSupportFragmentManager().findFragmentByTag("notes");
        Fragment edit = getSupportFragmentManager().findFragmentByTag("edit");
        Fragment add = getSupportFragmentManager().findFragmentByTag("add");
        Fragment lockTicked = getSupportFragmentManager().findFragmentByTag("lockTicked");

        if (lockTicked != null && lockTicked.isVisible()){
            UselessUtils.edit().putLong("lockTicker", 0).apply();
            super.onBackPressed();
            return;
        }

        if ((edit != null && edit.isVisible()) || (add != null && add.isVisible())) {
            getSupportFragmentManager().popBackStackImmediate("editor", POP_BACK_STACK_INCLUSIVE);

            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("restored", false)) {
                SyncUtils.export();

                if (GoogleSignIn.getLastSignedInAccount(this) != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        String name = getString(R.string.sync);
                        int importance = NotificationManager.IMPORTANCE_LOW;
                        NotificationChannel channel = new NotificationChannel("com.f0x1d.notes.sync", name, importance);
                        channel.enableVibration(false);
                        channel.enableLights(false);
                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);
                    }

                    Notification.Builder builder = new Notification.Builder(getApplicationContext());
                    builder.setContentTitle(getString(R.string.sync));
                    builder.setContentText(getString(R.string.syncing));
                    builder.setSmallIcon(R.drawable.ic_sync_black_24dp);
                    builder.setCategory(NotificationCompat.CATEGORY_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        builder.setChannelId("com.f0x1d.notes.sync");
                    builder.setOngoing(true);
                    builder.setDefaults(0).setVibrate(new long[]{0L});

                    ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(2, builder.build());

                    SyncUtils.exportToGDrive().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(2);
                            Log.e("notes_err", "synced");
                        }
                    });
                }
            }
            return;
        }

        if (notes != null && notes.isVisible()) {
            clear_back_stack();
            super.onBackPressed();
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            clear_back_stack();
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        UselessUtils.edit().putLong("lockTicker", System.currentTimeMillis()).apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("lock", false))
            return;

        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("lockTicker", 0) == 0)
            return;

        if (System.currentTimeMillis() - PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("lockTicker", 0) > 60000){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                    R.id.container, LockTickerScreen.newInstance(new LockTickerScreen.Callback() {
                        @Override
                        public int describeContents() { return 0; }
                        @Override
                        public void writeToParcel(Parcel dest, int flags) {}
                        @Override
                        public void onSuccess(LockTickerScreen screen) {
                            UselessUtils.edit().putLong("lockTicker", 0).apply();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, new Notes(), "notes").commit();
                        }
                    }), "lockTicked").commit();
        }


    }

    public Fragment getCurrentFragment(){
        return getSupportFragmentManager().findFragmentById(R.id.container);
    }
}
