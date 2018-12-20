package com.f0x1d.notes.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import com.f0x1d.notes.fragment.lock.LockScreen;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.settings.AboutSettings;
import com.f0x1d.notes.fragment.settings.DebugSettings;
import com.f0x1d.notes.fragment.settings.EditorSettings;
import com.f0x1d.notes.fragment.settings.SecuritySettings;
import com.f0x1d.notes.fragment.settings.Settings;
import com.f0x1d.notes.fragment.themes.ThemesFragment;
import com.f0x1d.notes.App;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.f0x1d.notes.utils.UselessUtils.clear_back_stack;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (UselessUtils.ifCustomTheme()){
            new ThemesEngine().setupAll();
        }

        if (UselessUtils.ifCustomTheme()){

            if (ThemesEngine.dark){
                setTheme(R.style.NightTheme);
            } else {
                setTheme(R.style.AppTheme);
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
                setTheme(R.style.NightTheme);

                getWindow().setNavigationBarColor(getResources().getColor(R.color.statusbar));
            } else {
                if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("orange", false)){
                    setTheme(R.style.AppTheme_Orange);
                } else {
                    setTheme(R.style.AppTheme);
                }

                getWindow().setStatusBarColor(Color.WHITE);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (UselessUtils.appInstalledOrNot("com.encrypt.password")){
            Toast.makeText(getApplicationContext(), "желе лох", Toast.LENGTH_SHORT).show();
        }

        try {
            savedInstanceState.getString("what_frag");

        } catch (Exception e){
            getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new Settings(), "settings").commit();

            Bundle lockargs = new Bundle();
            lockargs.putInt("id", 0);
            lockargs.putInt("locked", 0);
            lockargs.putString("title", "");
            lockargs.putString("text", "");
            lockargs.putBoolean("to_note", false);

            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("change", false)){
                UselessUtils.clear_back_stack(this);
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new Notes(), "notes").commit();
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new Settings(), "settings").addToBackStack(null).commit();
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, ThemesFragment.newInstance(false), "themes").addToBackStack(null).commit();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("change", false).apply();
            } else {
                if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("lock", false)){
                    getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, LockScreen.newInstance(lockargs), "lock").commit();
                } else {
                    getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new Notes(), "notes").commit();
                }
            }


        }

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public Resources getResources() {
        return new com.f0x1d.notes.resources.Resources(super.getResources().getAssets(), super.getResources().getDisplayMetrics(), super.getResources().getConfiguration());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("what_frag", "other");
    }

    @Override
    public void onBackPressed() {
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("in_folder_back_stack", false)){
            clear_back_stack(MainActivity.this);
            getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new Notes(), "notes").commit();
        } else {
            Fragment notes = getFragmentManager().findFragmentByTag("notes");

            if (notes != null && notes.isVisible()){
                clear_back_stack(MainActivity.this);
                super.onBackPressed();
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
