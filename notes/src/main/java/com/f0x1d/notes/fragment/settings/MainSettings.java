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
import com.f0x1d.notes.fragment.settings.translations.TranslationsFragment;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.dialogs.ShowAlertDialog;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.utils.translations.Translation;
import com.f0x1d.notes.utils.translations.Translations;
import com.f0x1d.notes.view.CenteredToolbar;

public class MainSettings extends PreferenceFragmentCompat {

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

        if (UselessUtils.ifCustomTheme()) {
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            getActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            getActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            CenteredToolbar toolbar = v.findViewById(R.id.toolbar);
            toolbar.setTitle(Translations.getString("settings"));
            getActivity().setActionBar(toolbar);

            if (UselessUtils.ifCustomTheme())
                toolbar.setBackgroundColor(ThemesEngine.toolbarColor);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        SwitchPreference lock = (SwitchPreference) findPreference("lock");
        lock.setChecked(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("lock", false));
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("pass", "").equals(""))
            lock.setChecked(false);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        PreferenceCategory debugCategory = (PreferenceCategory) findPreference("debugC");
        debugCategory.setTitle(Translations.getString("debug"));

        PreferenceCategory securityCategory = (PreferenceCategory) findPreference("securityC");
        securityCategory.setTitle(Translations.getString("security"));

        PreferenceCategory notesCategory = (PreferenceCategory) findPreference("notesC");
        notesCategory.setTitle(Translations.getString("notes"));

        PreferenceCategory onScreen = (PreferenceCategory) findPreference("onscreenC");
        onScreen.setTitle(Translations.getString("onscreen"));

        PreferenceCategory syncCategory = (PreferenceCategory) findPreference("syncC");
        syncCategory.setTitle(Translations.getString("sync"));

        SwitchPreference mono = (SwitchPreference) findPreference("mono");
        mono.setTitle(Translations.getString("use_mono"));
        mono.setSummary(Translations.getString("in_editor"));

        SwitchPreference twoRows = (SwitchPreference) findPreference("two_rows");
        twoRows.setTitle(Translations.getString("two_rows"));

        SwitchPreference showThings = (SwitchPreference) findPreference("show_things");
        showThings.setTitle(Translations.getString("show_skolko_thing_on_folder"));

        SwitchPreference shakal = (SwitchPreference) findPreference("shakal");
        shakal.setTitle(Translations.getString("shakal"));
        shakal.setSummary(Translations.getString("shakal_summary"));

        SwitchPreference autoEditMode = (SwitchPreference) findPreference("auto_editmode");
        autoEditMode.setTitle(Translations.getString("auto_editmode"));

        SwitchPreference autoLock = (SwitchPreference) findPreference("autolock");
        autoLock.setTitle(Translations.getString("autolock"));

        Preference translations = findPreference("translations");
        translations.setTitle(Translations.getString("translations"));
        translations.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UselessUtils.replace(TranslationsFragment.newInstance(), "translations");
                return false;
            }
        });

        Preference date = findPreference("date");
        date.setTitle(Translations.getString("date_appearance"));
        date.setSummary(Translations.getString("choose_date_appearance"));
        date.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_text, null);

                EditText text = v.findViewById(R.id.edit_text);
                text.setBackground(null);
                text.setHint("HH:mm | dd.MM.yyyy");
                text.setText(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("date", "HH:mm | dd.MM.yyyy"));

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(Translations.getString("choose_date_appearance"));
                builder.setView(v);
                builder.setPositiveButton(Translations.getString("ok"), new DialogInterface.OnClickListener() {
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
        sync.setTitle(Translations.getString("sync"));
        sync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MainActivity.instance.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(R.id.container, new SyncSettings(), "sync").addToBackStack(null).commit();
                return false;
            }
        });

        Preference about = findPreference("about");
        about.setTitle(Translations.getString("about"));
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MainActivity.instance.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(R.id.container, new AboutSettings(), "themes").addToBackStack(null).commit();
                return false;
            }
        });

        Preference accent = findPreference("dayAccent");
        accent.setTitle(Translations.getString("theme"));
        accent.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MainActivity.instance.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(R.id.container, ThemesFragment.newInstance(true), "themes").addToBackStack(null).commit();
                return false;
            }
        });

        Preference textSize = findPreference("textSize");
        textSize.setTitle(Translations.getString("text_size"));
        textSize.setSummary(Translations.getString("in_editor"));
        textSize.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                TextSizeDialog dialog1 = new TextSizeDialog();
                dialog1.show(MainActivity.instance.getSupportFragmentManager(), "TAG");

                return false;
            }
        });

        Preference debugWarning = findPreference("warning_d");
        debugWarning.setTitle(Translations.getString("debug_title"));

        Preference delete_all = findPreference("delete_all");
        delete_all.setTitle(Translations.getString("clear_all"));
        delete_all.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                removeAll();
                return false;
            }
        });

        final SwitchPreference finger = (SwitchPreference) findPreference("finger");
        finger.setTitle(Translations.getString("use_finger"));

        SwitchPreference lock = (SwitchPreference) findPreference("lock");
        lock.setTitle(Translations.getString("use_pin"));
        lock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("lock", false)) {
                    MainActivity.instance.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(R.id.container, new СhoosePin(), "choose_pin").addToBackStack(null).commit();
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("pass", "").apply();
                }
                return false;
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            FingerprintManager fingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
            if (fingerprintManager == null || !fingerprintManager.isHardwareDetected()) {
                finger.setEnabled(false);
                finger.setSummary(Translations.getString("fingerprint_error2"));
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                finger.setEnabled(false);
            }
        } else {
            finger.setEnabled(false);
            finger.setSummary(Translations.getString("fingerprint_error3"));
        }
    }

    public void removeAll() {

        if (delete) {
            NoteOrFolderDao dao = App.getInstance().getDatabase().noteOrFolderDao();
            dao.nukeTable();
            dao.nukeTable2();
            dao.nukeTable3();

            Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
            return;
        }

        delete = true;
        Toast.makeText(getActivity(), Translations.getString("one_more_time_to_delete"), Toast.LENGTH_SHORT).show();

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