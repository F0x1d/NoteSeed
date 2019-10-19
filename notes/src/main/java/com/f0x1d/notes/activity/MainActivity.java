package com.f0x1d.notes.activity;

import android.accounts.Account;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.editing.NoteEditFragment;
import com.f0x1d.notes.fragment.lock.LockScreenFragment;
import com.f0x1d.notes.fragment.main.NotesFragment;
import com.f0x1d.notes.fragment.settings.SyncSettingsFragment;
import com.f0x1d.notes.receiver.CopyTextReceiver;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.PermissionUtils;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.dialogs.BackupDialog;
import com.f0x1d.notes.utils.dialogs.SignInDialog;
import com.f0x1d.notes.utils.sync.SyncService;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.utils.translations.CustomResources;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;

import static android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static com.f0x1d.notes.utils.UselessUtils.clearBackStack;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private CustomResources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PermissionUtils.requestPermissions(this);

        Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                String stackTrace = Logger.getStackTrace(e);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String name = App.getContext().getString(R.string.notification);
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("com.f0x1d.notes.notifications", name, importance);
                    channel.enableVibration(true);
                    channel.enableLights(true);
                    NotificationManager notificationManager = MainActivity.this.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

                Notification.Builder builder = new Notification.Builder(getApplicationContext());
                builder.setSmallIcon(R.drawable.ic_bug_report_black_24dp);
                builder.setContentTitle("NoteSeed crashed!");
                builder.setContentText(Logger.getStackTrace(e));
                builder.addAction(new Notification.Action(0, getString(R.string.copy), PendingIntent.getBroadcast(
                        getApplicationContext(), 228, new Intent(MainActivity.this, CopyTextReceiver.class).putExtra("text", stackTrace), 0)));
                builder.setStyle(new Notification.BigTextStyle().bigText(stackTrace));
                builder.setAutoCancel(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    builder.setChannelId("com.f0x1d.notes.notifications");

                NotificationManager notificationManager = (NotificationManager) MainActivity.this.getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(Integer.MIN_VALUE, builder.build());

                Logger.log(e);

                defaultHandler.uncaughtException(t, e);
            }
        });

        if (UselessUtils.isCustomTheme()) {
            ThemesEngine.setupAll();
        }

        if (UselessUtils.isDarkTheme()) {
            setTheme(R.style.NightTheme);
        } else {
            setTheme(R.style.AppTheme);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                getWindow().setNavigationBarColor(Color.BLACK);
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(Color.GRAY);
            }
        }

        if (UselessUtils.isCustomTheme()) {
            getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
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

        if (getIntent().getExtras() != null && getIntent().getStringExtra("open") != null && getIntent().getStringExtra("open").equals("add")) {
            UselessUtils.replace(this, NotesFragment.newInstance("def"), "notes", false, null);
            UselessUtils.replace(this, NoteEditFragment.newInstance(true, 0, "def"), "add", true, "editor");
        } else if (getIntent().getExtras() != null) {
            UselessUtils.replace(this, NotesFragment.newInstance("def"), "notes", false, null);
            UselessUtils.replace(this,
                    NoteEditFragment.newInstance(false, getIntent().getExtras().getLong("id"), null), "edit", true, "editor");
        } else {
            if (savedInstanceState == null) {
                if (App.getDefaultSharedPreferences().getBoolean("lock", false)) {
                    UselessUtils.replace(this, LockScreenFragment.newInstance(false, 0), "lock", false, null);
                } else {
                    UselessUtils.replace(this, NotesFragment.newInstance("def"), "notes", false, null);
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
        //outState.putString("what_frag", "other");
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

            App.getDefaultSharedPreferences().edit().putBoolean("want_sign_in", false).apply();
            if (SyncSettingsFragment.instance != null)
                SyncSettingsFragment.instance.updateSignedState();
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

        if (lockTicked != null && lockTicked.isVisible()) {
            UselessUtils.edit().putLong("lockTicker", 0).apply();
            super.onBackPressed();
            return;
        }

        if ((edit != null && edit.isVisible()) || (add != null && add.isVisible())) {
            getSupportFragmentManager().popBackStackImmediate("editor", POP_BACK_STACK_INCLUSIVE);

            if (App.getDefaultSharedPreferences().getBoolean("restored", false)) {
                if (!UselessUtils.getBool("auto_s", false))
                    return;

                Intent intent = new Intent(this, SyncService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    startForegroundService(intent);
                else
                    startService(intent);
                bindService(intent, UselessUtils.EMPTY_SERVICE_CONNECTION, 0);

            }
            return;
        }

        if (notes != null && notes.isVisible()) {
            clearBackStack(this);
            super.onBackPressed();
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            clearBackStack(this);
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
        if (App.getDefaultSharedPreferences().getBoolean("change", false)) {
            if (UselessUtils.getBool("autolock", true))
                UselessUtils.edit().putLong("lockTicker", 0).apply();
        }
    }

    @Override
    public Resources getResources() {
        if (resources == null)
            resources = new CustomResources(super.getResources().getAssets(), super.getResources().getDisplayMetrics(), super.getResources().getConfiguration());
        return resources;
    }

    public Resources getDefaultResources() {
        return super.getResources();
    }
}
