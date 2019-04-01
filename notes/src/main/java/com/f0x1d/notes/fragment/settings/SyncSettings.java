package com.f0x1d.notes.fragment.settings;

import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.SyncUtils;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.dialogs.SignInDialog;
import com.f0x1d.notes.view.CenteredToolbar;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;

public class SyncSettings extends PreferenceFragmentCompat {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        if (UselessUtils.ifCustomTheme()){
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            getActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            getActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            CenteredToolbar toolbar = v.findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.sync);
            getActivity().setActionBar(toolbar);

            if (UselessUtils.ifCustomTheme())
                toolbar.setBackgroundColor(ThemesEngine.toolbarColor);
        }
        return v;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.sync);

        Preference login = findPreference("sign_in");
        login.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .build();

                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

                new SignInDialog().show(getActivity(), mGoogleSignInClient);
                return false;
            }
        });

        Preference import_gdrive = findPreference("import_g");
        import_gdrive.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (GoogleSignIn.getLastSignedInAccount(getActivity()) != null){
                    importFromGDrive();
                } else {
                    Toast.makeText(getActivity(), "error, sign in please", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        Preference export_gdrive = findPreference("export_g");
        export_gdrive.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (GoogleSignIn.getLastSignedInAccount(getActivity()) != null){
                    ProgressDialog dialog = new ProgressDialog(getActivity());
                    dialog.setMessage("Loading...");
                    dialog.setCancelable(false);
                    dialog.show();

                    if (UselessUtils.ifCustomTheme())
                        dialog.getWindow().getDecorView().getBackground().setColorFilter(ThemesEngine.background, PorterDuff.Mode.SRC);
                    else if (UselessUtils.getBool("night", true))
                        dialog.getWindow().getDecorView().getBackground().setColorFilter(getResources().getColor(R.color.statusbar_for_dialogs), PorterDuff.Mode.SRC);
                    else
                        dialog.getWindow().getDecorView().getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC);

                    SyncUtils.exportToGDrive().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.cancel();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "error, sign in please", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        Preference import_db = findPreference("import");
        import_db.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SyncUtils.importFile();
                Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        Preference export = findPreference("export");
        export.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SyncUtils.export();
                Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public void importFromGDrive(){
        ProgressDialog dialog1 = new ProgressDialog(getActivity());
        dialog1.setCancelable(false);
        dialog1.setMessage("Loading...");
        dialog1.show();

        if (UselessUtils.ifCustomTheme())
            dialog1.getWindow().getDecorView().getBackground().setColorFilter(ThemesEngine.background, PorterDuff.Mode.SRC);
        else if (UselessUtils.getBool("night", true))
            dialog1.getWindow().getDecorView().getBackground().setColorFilter(getResources().getColor(R.color.statusbar_for_dialogs), PorterDuff.Mode.SRC);
        else
            dialog1.getWindow().getDecorView().getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC);

        SyncUtils.ifBackupExistsOnGDrive(GoogleSignIn.getLastSignedInAccount(getActivity()).getAccount()).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                try {
                    SyncUtils.importFromGDrive(task.getResult(), GoogleSignIn.getLastSignedInAccount(getActivity()).getAccount()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SyncUtils.importFile();
                            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("restored", true).apply();
                            dialog1.cancel();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            dialog1.cancel();
                        }
                    });

                } catch (Exception e){
                    Log.e("notes_err", e.getLocalizedMessage());
                    Toast.makeText(getActivity(), "error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    dialog1.cancel();
                }
            }
        });
    }
}