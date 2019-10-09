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
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.databinding.ActivityMainBinding;
import com.f0x1d.notes.fragment.editing.NoteAdd;
import com.f0x1d.notes.fragment.editing.NoteEdit;
import com.f0x1d.notes.fragment.lock.LockScreen;
import com.f0x1d.notes.fragment.lock.LockTickerScreen;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.fragment.settings.MainSettings;
import com.f0x1d.notes.fragment.settings.SyncSettings;
import com.f0x1d.notes.fragment.settings.themes.ThemesFragment;
import com.f0x1d.notes.fragment.settings.translations.TranslationsFragment;
import com.f0x1d.notes.receiver.CopyTextReceiver;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.PermissionUtils;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.dialogs.BackupDialog;
import com.f0x1d.notes.utils.dialogs.SignInDialog;
import com.f0x1d.notes.utils.sync.SyncService;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.utils.theme.ThemingViewModel;
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
import static com.f0x1d.notes.utils.UselessUtils.clear_back_stack;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;
    private GoogleSignInClient mGoogleSignInClient;
    private CustomResources resources;

    public ThemingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        PermissionUtils.requestPermissions(this);

        viewModel = ViewModelProviders.of(this).get(ThemingViewModel.class);

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

        if (UselessUtils.ifCustomTheme()) {
            new ThemesEngine().setupAll(this);
        }

        setTheme(R.style.AppTheme);

        getWindow().setStatusBarColor(viewModel.statusBarColor.getValue());
        getWindow().setNavigationBarColor(viewModel.navBarColor.getValue());

        if (ThemesEngine.dark)
            getWindow().getDecorView().setSystemUiVisibility(0);

        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewmodel(ViewModelProviders.of(this).get(ThemingViewModel.class));
        binding.setLifecycleOwner(this);

        if (GoogleSignIn.getLastSignedInAccount(this) == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            signIn();
        }

        if (getIntent().getExtras() != null && getIntent().getStringExtra("open") != null && getIntent().getStringExtra("open").equals("add")) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                    R.id.container, new Notes(), "notes").commit();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                    R.id.container, NoteAdd.newInstance("def"), "add").addToBackStack("editor").commit();
            return;
        }

        if (getIntent().getExtras() != null) {
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

            } else if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("change_l", false)) {
                clear_back_stack();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                        R.id.container, new Notes(), "notes").commit();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                        R.id.container, new MainSettings(), "settings").addToBackStack(null).commit();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                        R.id.container, TranslationsFragment.newInstance(), "translations").addToBackStack(null).commit();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("change_l", false).apply();

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

        if (lockTicked != null && lockTicked.isVisible()) {
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

        if (System.currentTimeMillis() - PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("lockTicker", 0) > 60000) {
            UselessUtils.clear_back_stack();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                    R.id.container, LockTickerScreen.newInstance(new LockTickerScreen.Callback() {
                        @Override
                        public int describeContents() {
                            return 0;
                        }

                        @Override
                        public void writeToParcel(Parcel dest, int flags) {
                        }

                        @Override
                        public void onSuccess(LockTickerScreen screen) {
                            UselessUtils.edit().putLong("lockTicker", 0).apply();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, new Notes(), "notes").commit();
                        }
                    }), "lockTicked").commit();
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
