package com.f0x1d.notes.activity;

import android.accounts.Account;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.choose.ChooseFolder;
import com.f0x1d.notes.fragment.lock.LockScreen;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.fragment.main.NotesInFolder;
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

import static com.f0x1d.notes.utils.UselessUtils.clear_back_stack;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        PermissionUtils.requestWriteExternalPermission(this);

        if (UselessUtils.ifCustomTheme()){
            new ThemesEngine().setupAll();
        }

        if (UselessUtils.ifCustomTheme()){
            if (ThemesEngine.dark){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
                    setTheme(R.style.NightTheme_md2);
                } else {
                    setTheme(R.style.NightTheme);
                }
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
                    setTheme(R.style.AppTheme_md2);
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
                    .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                    .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            signIn();
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
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                            android.R.id.content, new Notes(), "notes").commit();
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                            android.R.id.content, new MainSettings(), "settings").addToBackStack(null).commit();
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                            android.R.id.content, ThemesFragment.newInstance(false), "themes").addToBackStack(null).commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("change", false).apply();
                } else {
                    if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("lock", false)){
                        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                                android.R.id.content, LockScreen.newInstance(lockargs), "lock").commit();
                    } else {
                        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                                android.R.id.content, new Notes(), "notes").commit();
                    }
                }
            }
    }

    private void signIn(){
        if (UselessUtils.getBool("want_sign_in", true)){
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

            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("not_want_sign_in", false).apply();
            BackupDialog.show(this, account);
        } catch (ApiException e) {
            Log.e("notes_err", "handleSignInResult:error \n\n", e);
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
            Fragment notes = getFragmentManager().findFragmentByTag("notes");
            Fragment edit = getFragmentManager().findFragmentByTag("edit");
            Fragment add = getFragmentManager().findFragmentByTag("add");
            Fragment chooseFolder = getFragmentManager().findFragmentByTag("choose_folder");
            Fragment notesInFolder = getFragmentManager().findFragmentByTag("in_folder");

            if ((edit != null && edit.isVisible()) || (add != null && add.isVisible())){
                getFragmentManager().popBackStack();

                if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("restored", false)){
                    SyncUtils.export();

                    if (GoogleSignIn.getLastSignedInAccount(this) != null){
                        SyncUtils.exportToGDrive().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.e("notes_err", "synced");
                            }
                        });
                    }
                }
                return;
            }

            if (notes != null && notes.isVisible()){
                if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("restored", false)){
                    SyncUtils.export().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (GoogleSignIn.getLastSignedInAccount(MainActivity.this) != null){
                                SyncUtils.exportToGDrive().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.e("notes_err", "synced");
                                    }
                                });
                            }
                        }
                    });
                }

                clear_back_stack(MainActivity.this);
                super.onBackPressed();
                return;
            }

            if (chooseFolder != null && chooseFolder.isVisible()){
                ChooseFolder.in_ids.remove(ChooseFolder.in_ids.size() - 1);
                getFragmentManager().popBackStack();
                return;
            }

            if (notesInFolder != null && notesInFolder.isVisible()){
                Log.e("notes_err", "removed: " + NotesInFolder.in_ids.get(NotesInFolder.in_ids.size() - 1));
                NotesInFolder.in_ids.remove(NotesInFolder.in_ids.size() - 1);

                try {
                    Log.e("notes_err", "last: " + NotesInFolder.in_ids.get(NotesInFolder.in_ids.size() - 1));
                } catch (Exception e){}

                getFragmentManager().popBackStack();
                return;
            }

            if (getFragmentManager().getBackStackEntryCount() == 0){
                clear_back_stack(MainActivity.this);
                super.onBackPressed();
            } else {
                getFragmentManager().popBackStack();
            }
    }

    @Override
    public Resources getResources() {
        //return new CustomResources(super.getAssets(), super.getResources().getDisplayMetrics(), super.getResources().getConfiguration());
        return super.getResources();
    }
}
