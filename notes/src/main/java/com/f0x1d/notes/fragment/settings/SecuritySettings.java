package com.f0x1d.notes.fragment.settings;

import android.app.Activity;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.fragment.lock.СhoosePin;
import com.f0x1d.notes.help.view.CenteredToolbar;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class SecuritySettings extends PreferenceFragment {

    public static SwitchPreference lock;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings, container, false);

        CenteredToolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.security));
        getActivity().setActionBar(toolbar);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.security);

        final SwitchPreference finger = (SwitchPreference) findPreference("finger");

        lock = (SwitchPreference) findPreference("lock");
            lock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("lock", false)){
                        getActivity().getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new СhoosePin()).addToBackStack(null).commit();
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

    @Override
    public void onResume() {
        super.onResume();
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("pass", "").isEmpty()){
            lock.setChecked(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("pass", "").isEmpty()){
            lock.setChecked(false);
        }
    }
}
