package com.f0x1d.notes.fragment.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.themes.ThemesFragment;
import com.f0x1d.notes.view.CenteredToolbar;

import androidx.annotation.Nullable;

public class Settings extends PreferenceFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings, container, false);

        CenteredToolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.settings));
        getActivity().setActionBar(toolbar);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("in_folder_back_stack", false).apply();

        addPreferencesFromResource(R.xml.settings);

        Preference editor_settings = findPreference("editor_settings");
            editor_settings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getActivity().getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new EditorSettings()).addToBackStack(null).commit();
                    return false;
                }
            });

        Preference debug = findPreference("debug");
            debug.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getActivity().getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new DebugSettings()).addToBackStack(null).commit();
                    return false;
                }
            });

        /*Preference ota = findPreference("ota");
            ota.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getActivity().getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new OTASettings()).addToBackStack(null).commit();
                    return false;
                }
            });*/

        Preference accent = findPreference("dayAccent");
            accent.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getActivity().getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, ThemesFragment.newInstance(true)).addToBackStack(null).commit();
                    return false;
                }
            });

        Preference locking = findPreference("locking");
            locking.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getActivity().getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new SecuritySettings()).addToBackStack(null).commit();
                    return false;
                }
            });

        Preference about = findPreference("about");
            about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getActivity().getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new AboutSettings()).addToBackStack(null).commit();
                    return false;
                }
            });
    }
}
