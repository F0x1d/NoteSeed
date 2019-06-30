package com.f0x1d.notes.fragment.settings;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.BuildConfig;
import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.utils.translations.Translations;
import com.f0x1d.notes.view.CenteredToolbar;

public class AboutSettings extends PreferenceFragmentCompat {

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
            toolbar.setTitle(Translations.getString("about"));
            getActivity().setActionBar(toolbar);

            if (UselessUtils.ifCustomTheme())
                toolbar.setBackgroundColor(ThemesEngine.toolbarColor);
        }

        return v;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.about);

        Preference ikso = findPreference("ikso");
        ikso.setSummary(Translations.getString("murleek"));

        Preference murleek = findPreference("murleek");
        murleek.setSummary(Translations.getString("murleek"));

        Preference gg = findPreference("gg");
        gg.setSummary(Translations.getString("icon_by_emulond"));

        Preference emulond = findPreference("emulond");
        emulond.setSummary(Translations.getString("icon_by_emulond"));

        Preference donate = findPreference("donate");
        donate.setTitle(Translations.getString("donate"));

        Preference tgDev = findPreference("tgdev");
        tgDev.setTitle(Translations.getString("telegram"));

        PreferenceCategory thanks = (PreferenceCategory) findPreference("thanksC");
        thanks.setTitle(Translations.getString("thanks"));

        PreferenceCategory links = (PreferenceCategory) findPreference("linksC");
        links.setTitle(Translations.getString("links"));

        Preference preference = findPreference("about_v");
        preference.setSummary(Html.fromHtml("Version: <b>" + BuildConfig.VERSION_NAME + "</b>"));
    }

    @Override
    protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        return new MainSettings.CustomPreferenceGroupAdapter(preferenceScreen);
    }
}