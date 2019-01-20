package com.f0x1d.notes.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.f0x1d.notes.fragment.lock.LockScreen;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.settings.MainSettings;
import com.f0x1d.notes.fragment.settings.themes.ThemesFragment;
import com.f0x1d.notes.App;
import com.f0x1d.notes.utils.PermissionUtils;
import com.f0x1d.notes.utils.SyncUtils;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.io.FileReader;

import static com.f0x1d.notes.utils.UselessUtils.clear_back_stack;

public class MainActivity extends AppCompatActivity {

    public static FragmentManager getSupportFragmentManager;
    public static MainSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        PermissionUtils.requestWriteExternalPermission(this);

        getSupportFragmentManager = getSupportFragmentManager();
        settings = new MainSettings();

        if (UselessUtils.ifCustomTheme()){
            new ThemesEngine().setupAll();
        }

        if (UselessUtils.ifCustomTheme()){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
                if (ThemesEngine.dark){
                    setTheme(R.style.NightTheme_md2);
                } else {
                    setTheme(R.style.AppTheme_md2);
                }
            } else {
                if (ThemesEngine.dark){
                    setTheme(R.style.NightTheme);
                } else {
                    setTheme(R.style.AppTheme);
                }
            }

            try {
                getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
                getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
            } catch (Exception e){
                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
                recreate();
            }
        } else {
            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("night", false)){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
                    setTheme(R.style.NightTheme_md2);
                } else {
                    setTheme(R.style.NightTheme);
                }

                getWindow().setNavigationBarColor(getResources().getColor(R.color.statusbar));
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("orange", false)){
                        setTheme(R.style.AppTheme_Orange_md2);
                    } else {
                        setTheme(R.style.AppTheme_md2);
                    }
                } else {
                    if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("orange", false)){
                        setTheme(R.style.AppTheme_Orange);
                    } else {
                        setTheme(R.style.AppTheme);
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    getWindow().setStatusBarColor(Color.WHITE);
                } else {
                    getWindow().setStatusBarColor(Color.GRAY);
                }
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (GoogleSignIn.getLastSignedInAccount(this) == null){
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestServerAuthCode("676559112451-e6eil8b6us4s8rsanf8bed219l3elf5r.apps.googleusercontent.com")
                    .requestScopes(new Scope(Scopes.DRIVE_FULL))
                    .build();

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 1);
        }

        if (UselessUtils.appInstalledOrNot("com.encrypt.password")){
            Toast.makeText(getApplicationContext(), "Вот и иди к своему желе", Toast.LENGTH_SHORT).show();
            finish();
        }

        try {
            savedInstanceState.getString("what_frag");
        } catch (Exception e){
                Bundle lockargs = new Bundle();
                lockargs.putInt("id", 0);
                lockargs.putInt("locked", 0);
                lockargs.putString("title", "");
                lockargs.putString("text", "");
                lockargs.putBoolean("to_note", false);

                if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("change", false)){
                    UselessUtils.clear_back_stack(this);
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, new Notes(), "notes").commit();
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, MainActivity.settings, "settings").addToBackStack(null).commit();
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, ThemesFragment.newInstance(false), "themes").addToBackStack(null).commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("change", false).apply();
                } else {
                    if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("lock", false)){
                        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, LockScreen.newInstance(lockargs), "lock").commit();
                    } else {
                        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, new Notes(), "notes").commit();
                    }
                }
            }

        if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("restored", false)){
            File db = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes//db");
            File database = new File(db, "database.noteseed");

            if (database.exists()){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.backup_found);
                builder.setMessage(getString(R.string.restore) + "?");
                builder.setCancelable(false);

                builder.setPositiveButton(getString(R.string.restore), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SyncUtils.importFile();
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("restored", true).apply();
                        recreate();
                    }
                });
                builder.setNeutralButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("restored", true).apply();
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("restored", true).apply();
                SyncUtils.export();
            }
        } else {
            SyncUtils.export();
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

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
        } catch (ApiException e) {
            Log.e("notes_err", "signInResult:failed code = " + e.getStatusCode());
        }
    }

    @Override
    public void onBackPressed() {
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("in_folder_back_stack", false)){
            clear_back_stack(MainActivity.this);
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, new Notes(), "notes").commit();
        } else {
            Fragment notes = getFragmentManager().findFragmentByTag("notes");
            Fragment edit = getFragmentManager().findFragmentByTag("edit");
            Fragment add = getFragmentManager().findFragmentByTag("add");

            if ((edit != null && edit.isVisible()) || (add != null && add.isVisible())){
                getFragmentManager().popBackStack();
                if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("restored", false)){
                    SyncUtils.export();
                }
                return;
            }

            if (notes != null && notes.isVisible()){
                clear_back_stack(MainActivity.this);
                super.onBackPressed();
                return;
            }

            if (getFragmentManager().getBackStackEntryCount() == 0){
                clear_back_stack(MainActivity.this);
                super.onBackPressed();
            } else {
                getFragmentManager().popBackStack();
            }
        }
    }
}
