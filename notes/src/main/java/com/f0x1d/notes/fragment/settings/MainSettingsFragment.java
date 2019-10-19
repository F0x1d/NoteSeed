package com.f0x1d.notes.fragment.settings;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.fragment.bottomSheet.TextSizeDialog;
import com.f0x1d.notes.fragment.lock.СhooseLockFragment;
import com.f0x1d.notes.fragment.settings.themes.ThemesFragment;
import com.f0x1d.notes.fragment.settings.translations.TranslationsFragment;
import com.f0x1d.notes.service.CaptureNoteNotificationService;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.dialogs.ShowAlertDialog;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.view.CenteredToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainSettingsFragment extends PreferenceFragmentCompat {

    boolean delete = false;

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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        if (UselessUtils.isCustomTheme()) {
            requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            requireActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            requireActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            CenteredToolbar toolbar = v.findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.settings));

            if (UselessUtils.isCustomTheme())
                toolbar.setBackgroundColor(ThemesEngine.toolbarColor);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        SwitchPreference lock = (SwitchPreference) findPreference("lock");
        lock.setChecked(App.getDefaultSharedPreferences().getBoolean("lock", false));
        if (App.getDefaultSharedPreferences().getString("pass", "").equals(""))
            lock.setChecked(false);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        SwitchPreference showCaptureNotesNotification = (SwitchPreference) findPreference("showCaptureNotification");
        showCaptureNotesNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean value = (boolean) newValue;

                if (value) {
                    Intent intent = new Intent(requireActivity(), CaptureNoteNotificationService.class);

                    requireActivity().startService(intent);
                    requireActivity().bindService(intent, new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {

                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) {

                        }
                    }, 0);
                } else {
                    if (CaptureNoteNotificationService.instance != null) {
                        CaptureNoteNotificationService.instance.stopForeground(true);
                    }
                }

                return true;
            }
        });

        Preference translations = findPreference("translations");
        translations.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UselessUtils.replace((AppCompatActivity) requireActivity(), TranslationsFragment.newInstance(), "translations", true, null);
                return false;
            }
        });

        Preference date = findPreference("date");
        date.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                View v = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_edit_text, null);

                EditText text = v.findViewById(R.id.edit_text);
                text.setBackground(null);
                text.setHint("HH:mm | dd.MM.yyyy");
                text.setText(App.getDefaultSharedPreferences().getString("date", "HH:mm | dd.MM.yyyy"));

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
                builder.setTitle(getString(R.string.choose_date_appearance));
                builder.setView(v);
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        App.getDefaultSharedPreferences().edit()
                                .putString("date", text.getText().toString())
                                .apply();
                    }
                });
                ShowAlertDialog.show(builder);
                return false;
            }
        });

        Preference sync = findPreference("sync");
        sync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UselessUtils.replace((AppCompatActivity) requireActivity(), new SyncSettingsFragment(), "sync", true, null);
                return false;
            }
        });

        Preference about = findPreference("about");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UselessUtils.replace((AppCompatActivity) requireActivity(), new AboutSettingsFragment(), "about", true, null);
                return false;
            }
        });

        Preference accent = findPreference("dayAccent");
        accent.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UselessUtils.replace((AppCompatActivity) requireActivity(), ThemesFragment.newInstance(true), "themes", true, null);
                return false;
            }
        });

        Preference textSize = findPreference("textSize");
        textSize.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                TextSizeDialog dialog = new TextSizeDialog();
                dialog.show(requireActivity().getSupportFragmentManager(), "TAG");
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
                if (App.getDefaultSharedPreferences().getBoolean("lock", false)) {
                    UselessUtils.replace((AppCompatActivity) requireActivity(), new СhooseLockFragment(), "choose_pin", true, null);
                    App.getDefaultSharedPreferences().edit().putString("pass", "").apply();
                }
                return false;
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            FingerprintManager fingerprintManager = (FingerprintManager) requireActivity().getSystemService(Context.FINGERPRINT_SERVICE);
            if (fingerprintManager == null || !fingerprintManager.isHardwareDetected()) {
                finger.setEnabled(false);
                finger.setSummary(getString(R.string.fingerprint_error2));
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                finger.setEnabled(false);
            }
        } else {
            finger.setEnabled(false);
            finger.setSummary(getString(R.string.fingerprint_error3));
        }
    }

    public void removeAll() {
        if (delete) {
            NoteOrFolderDao dao = App.getInstance().getDatabase().noteOrFolderDao();
            dao.nukeTable();
            dao.nukeTable2();
            dao.nukeTable3();

            Toast.makeText(requireActivity(), getString(R.string.success), Toast.LENGTH_SHORT).show();
            return;
        }

        delete = true;
        Toast.makeText(requireActivity(), getString(R.string.one_more_time_to_delete), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                delete = false;
            }
        }, 2000);
    }

    @Override
    protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        return new CustomPreferenceGroupAdapter(preferenceScreen);
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

            holder.setDividerAllowedAbove(false);
            holder.setDividerAllowedBelow(false);

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
}