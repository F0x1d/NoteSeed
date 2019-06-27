package com.f0x1d.notes.activity;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.editing.NoteEdit;
import com.f0x1d.notes.fragment.lock.LockScreen;
import com.f0x1d.notes.fragment.lock.LockTickerScreen;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.fragment.settings.MainSettings;
import com.f0x1d.notes.fragment.settings.SyncSettings;
import com.f0x1d.notes.fragment.settings.themes.ThemesFragment;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.PermissionUtils;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.dialogs.BackupDialog;
import com.f0x1d.notes.utils.dialogs.SignInDialog;
import com.f0x1d.notes.utils.sync.SyncService;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.view.CenteredToolbar;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;

import static android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static com.f0x1d.notes.utils.UselessUtils.clear_back_stack;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        PermissionUtils.requestPermissions(this);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Logger.log(e);
            }
        });

        if (UselessUtils.ifCustomTheme()) {
            new ThemesEngine().setupAll();
        }

        if (UselessUtils.ifCustomTheme()) {
            if (ThemesEngine.dark) {
                setTheme(R.style.NightTheme);
            } else {
                setTheme(R.style.AppTheme);
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
                setTheme(R.style.NightTheme);
                getWindow().setNavigationBarColor(getResources().getColor(R.color.statusbar));
            } else {
                if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("orange", false)) {
                    setTheme(R.style.AppTheme_Orange);
                } else {
                    setTheme(R.style.AppTheme);
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
                    .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            signIn();
        }

        if (getIntent().getExtras() != null){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                    R.id.container, new Notes(), "notes").commit();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                    R.id.container, NoteEdit.newInstance(getIntent().getExtras()), "edit").addToBackStack("editor").commit();
            return;
        }

        try {
            savedInstanceState.getString("what_frag");
        } catch (Exception e) {
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
        try {
            Account account = completedTask.getResult(ApiException.class).getAccount();

            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("want_sign_in", false).apply();
            if (SyncSettings.instance != null)
                SyncSettings.instance.updateSignedState();
            BackupDialog.show(this, account);
        } catch (ApiException e) {
            Logger.log(e);
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
                if (!UselessUtils.getBool("auto_s", false))
                    return;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    startForegroundService(new Intent(this, SyncService.class));
                else
                    startService(new Intent(this, SyncService.class));
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
    protected void onPause() {
        super.onPause();
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("change", false)) {
            if (UselessUtils.getBool("autolock", true))
                UselessUtils.edit().putLong("lockTicker", 0).apply();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (UselessUtils.getBool("autolock", true))
            UselessUtils.edit().putLong("lockTicker", System.currentTimeMillis()).apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!UselessUtils.getBool("autolock", true))
            return;

        if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("lock", false))
            return;

        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("lockTicker", 0) == 0)
            return;

        if (System.currentTimeMillis() - PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("lockTicker", 0) > 60000){
            UselessUtils.clear_back_stack();
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
}
