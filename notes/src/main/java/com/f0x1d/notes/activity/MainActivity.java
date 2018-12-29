package com.f0x1d.notes.activity;

import android.app.Fragment;
import android.app.NotificationChannel;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.f0x1d.notes.adapter.ItemsAdapter;
import com.f0x1d.notes.fragment.lock.LockScreen;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.main.NotesInFolder;
import com.f0x1d.notes.fragment.settings.AboutSettings;
import com.f0x1d.notes.fragment.settings.DebugSettings;
import com.f0x1d.notes.fragment.settings.EditorSettings;
import com.f0x1d.notes.fragment.settings.MainSettings;
import com.f0x1d.notes.fragment.themes.ThemesFragment;
import com.f0x1d.notes.App;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import java.util.function.LongFunction;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.f0x1d.notes.utils.UselessUtils.clear_back_stack;

public class MainActivity extends AppCompatActivity implements ColorPickerDialogListener {

    public static FragmentManager getSupportFragmentManager;

    public static MainSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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

        if (UselessUtils.appInstalledOrNot("com.encrypt.password")){
            Toast.makeText(getApplicationContext(), "Вот и иди к своему желе", Toast.LENGTH_SHORT).show();
            finish();
        }

        try {
            savedInstanceState.getString("what_frag");

        } catch (Exception e){
            //getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, MainActivity.settings, "settings").commit();

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

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
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
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, new Notes(), "notes").commit();
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

    @Override
    public void onColorSelected(int dialogId, int color) {
        Log.e("notes_err", "onColorSelected: " + "#" + Integer.toHexString(color));

        if (ItemsAdapter.isFolder){
            App.getInstance().getDatabase().noteOrFolderDao().updateFolderColor("#" + Integer.toHexString(color), ItemsAdapter.folder_id);
        } else {
            App.getInstance().getDatabase().noteOrFolderDao().updateNoteColor("#" + Integer.toHexString(color), ItemsAdapter.id);
        }

        try {
            Notes.recyclerView.getAdapter().notifyItemChanged(ItemsAdapter.position);
        } catch (Exception e){
            Log.e("notes_err", "onColorSelected, Notes crash: " + e.getLocalizedMessage());
        }

        try {
            NotesInFolder.recyclerView.getAdapter().notifyItemChanged(ItemsAdapter.position);
        } catch (Exception e){
            Log.e("notes_err", "onColorSelected, NotesInFolder crash: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
