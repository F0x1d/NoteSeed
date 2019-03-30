
package com.f0x1d.notes.fragment.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.fragment.bottomSheet.TextSizeDialog;
import com.f0x1d.notes.fragment.lock.СhoosePin;
import com.f0x1d.notes.fragment.settings.themes.ThemesFragment;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.dialogs.ShowAlertDialog;
import com.f0x1d.notes.view.CenteredToolbar;

import java.nio.file.Files;

public class MainSettings extends PreferenceFragmentCompat {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        if (UselessUtils.ifCustomTheme()){
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            getActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            getActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            CenteredToolbar toolbar = v.findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.settings);
            getActivity().setActionBar(toolbar);

            if (UselessUtils.ifCustomTheme())
                toolbar.setBackgroundColor(ThemesEngine.toolbarColor);
        }

        return v;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        Preference date = findPreference("date");
        date.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_text, null);

                EditText text = v.findViewById(R.id.edit_text);
                text.setBackground(null);
                text.setHint("HH:mm | dd.MM.yyyy");
                text.setText(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("date", "HH:mm | dd.MM.yyyy"));

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.choose_date_appearance);
                builder.setView(v);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                                .putString("date", text.getText().toString())
                                .apply();
                    }
                });
                ShowAlertDialog.show(builder.create());
                return false;
            }
        });

        Preference sync = findPreference("sync");
        sync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MainActivity.instance.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(R.id.container, new SyncSettings(), "sync").addToBackStack(null).commit();
                return false;
            }
        });

        Preference about = findPreference("about");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MainActivity.instance.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(R.id.container, new AboutSettings(), "themes").addToBackStack(null).commit();
                return false;
            }
        });

        Preference accent = findPreference("dayAccent");
        accent.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MainActivity.instance.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(R.id.container, ThemesFragment.newInstance(true), "themes").addToBackStack(null).commit();
                return false;
            }
        });

        Preference textSize = findPreference("textSize");
        textSize.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                TextSizeDialog dialog1 = new TextSizeDialog();
                dialog1.show(MainActivity.instance.getSupportFragmentManager(), "TAG");

                return false;
            }
        });

        Preference delete_all = findPreference("delete_all");
        delete_all.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                removeAll();
                return false;
            }
        });

        final SwitchPreference finger = (SwitchPreference) findPreference("finger");

        SwitchPreference lock = (SwitchPreference) findPreference("lock");
        lock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("lock", false)){
                    MainActivity.instance.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(R.id.container, new СhoosePin(), "choose_pin").addToBackStack(null).commit();
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("pass", "").apply();
                }
                return false;
            }
        });

        if (Build.VERSION.SDK_INT >= 23){
            FingerprintManager fingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
            if (fingerprintManager == null || !fingerprintManager.isHardwareDetected()){
                finger.setEnabled(false);
                finger.setSummary(getString(R.string.fingerprint_error2));
            } else if (!fingerprintManager.hasEnrolledFingerprints()){
                finger.setEnabled(false);
            }
        } else {
            finger.setEnabled(false);
            finger.setSummary(getString(R.string.fingerprint_error3));
        }
    }

    boolean delete = false;

    public void removeAll() {

        if (delete) {
            NoteOrFolderDao dao = App.getInstance().getDatabase().noteOrFolderDao();
            dao.nukeTable();
            dao.nukeTable2();

            Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
            return;
        }

        delete = true;
        Toast.makeText(getActivity(), R.string.one_more_time_to_delete, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                delete = false;
            }
        }, 2000);


    }

    @Override
    protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        return super.onCreateAdapter(preferenceScreen);
    }

    @SuppressLint("RestrictedApi")
    public static class CustomPreferenceGroupAdapter extends PreferenceGroupAdapter {

        @SuppressLint("RestrictedApi")
        public CustomPreferenceGroupAdapter(PreferenceGroup preferenceGroup) {
            super(preferenceGroup);
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onBindViewHolder(PreferenceViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            Preference currentPreference = getItem(position);

            if (position != 0 && currentPreference instanceof PreferenceCategory) {
                holder.setDividerAllowedAbove(false);
                holder.setDividerAllowedBelow(false);
            } else {
                holder.setDividerAllowedAbove(false);
                holder.setDividerAllowedBelow(false);
            }

            if (currentPreference instanceof PreferenceCategory)
                setZeroPaddingToLayoutChildren(holder.itemView);
            else {
                View iconFrame = holder.itemView.findViewById(R.id.icon_frame);
                if (iconFrame != null) {
                    iconFrame.setVisibility(currentPreference.getIcon() == null ? View.GONE : View.VISIBLE);
                }
            }
        }
    }

    private static void setZeroPaddingToLayoutChildren(View view) {
        if (!(view instanceof ViewGroup))
            return;
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            setZeroPaddingToLayoutChildren(viewGroup.getChildAt(i));
            viewGroup.setPaddingRelative(0, viewGroup.getPaddingTop(), viewGroup.getPaddingEnd(), viewGroup.getPaddingBottom());
        }
    }
}