package com.f0x1d.notes.fragment.settings;

import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.dialogs.SignInDialog;
import com.f0x1d.notes.utils.sync.SyncUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.view.CenteredToolbar;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;

public class SyncSettingsFragment extends PreferenceFragmentCompat {

    public static SyncSettingsFragment instance;
    private Preference login;
    private Preference logout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        instance = this;

        View v = super.onCreateView(inflater, container, savedInstanceState);

        if (UselessUtils.isCustomTheme()) {
            requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            requireActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            requireActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            CenteredToolbar toolbar = v.findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.sync));

            if (UselessUtils.isCustomTheme())
                toolbar.setBackgroundColor(ThemesEngine.toolbarColor);
        }
        return v;
    }

    public void updateSignedState() {
        if (GoogleSignIn.getLastSignedInAccount(getContext()) == null) {
            login.setVisible(true);
            logout.setVisible(false);
        } else {
            login.setVisible(false);
            logout.setVisible(true);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.sync);

        login = findPreference("sign_in");
        logout = findPreference("sign_out");

        updateSignedState();

        login.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .build();

                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

                new SignInDialog().show(requireActivity(), mGoogleSignInClient);
                return false;
            }
        });

        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (GoogleSignIn.getLastSignedInAccount(getContext()) == null) {
                    Toast.makeText(requireActivity(), "error, sign in please", Toast.LENGTH_SHORT).show();
                    Logger.log("error, sign in please");
                }

                ProgressDialog dialog = new ProgressDialog(requireActivity());
                dialog.setCancelable(false);
                dialog.setMessage("Loading...");
                dialog.show();

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .build();

                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.cancel();
                        if (task.isSuccessful())
                            Logger.log("logout success!");
                        else
                            Logger.log("logout not success!");

                        updateSignedState();
                    }
                });
                return false;
            }
        });

        Preference importGDrive = findPreference("import_g");
        importGDrive.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (GoogleSignIn.getLastSignedInAccount(requireActivity()) != null) {
                    importFromGDrive();
                } else {
                    Toast.makeText(requireActivity(), "error, sign in please", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        Preference exportGDrive = findPreference("export_g");
        exportGDrive.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (GoogleSignIn.getLastSignedInAccount(requireActivity()) != null) {
                    SyncUtils.export();

                    ProgressDialog dialog = new ProgressDialog(requireActivity());
                    dialog.setMessage("Loading...");
                    dialog.setCancelable(false);
                    dialog.show();

                    if (UselessUtils.isCustomTheme())
                        dialog.getWindow().getDecorView().getBackground().setColorFilter(ThemesEngine.background, PorterDuff.Mode.SRC);
                    else if (UselessUtils.getBool("night", false))
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
                    Toast.makeText(requireActivity(), "error, sign in please", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        Preference importDb = findPreference("import");
        importDb.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SyncUtils.importFile();
                Toast.makeText(requireActivity(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        Preference export = findPreference("export");
        export.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SyncUtils.export();
                Toast.makeText(requireActivity(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        return new MainSettingsFragment.CustomPreferenceGroupAdapter(preferenceScreen);
    }

    public void importFromGDrive() {
        ProgressDialog progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        if (UselessUtils.isCustomTheme())
            progressDialog.getWindow().getDecorView().getBackground().setColorFilter(ThemesEngine.background, PorterDuff.Mode.SRC);
        else if (UselessUtils.getBool("night", false))
            progressDialog.getWindow().getDecorView().getBackground().setColorFilter(getResources().getColor(R.color.statusbar_for_dialogs), PorterDuff.Mode.SRC);
        else
            progressDialog.getWindow().getDecorView().getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC);

        SyncUtils.ifBackupExistsOnGDrive(GoogleSignIn.getLastSignedInAccount(requireActivity()).getAccount()).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                try {
                    SyncUtils.importFromGDrive(task.getResult(), GoogleSignIn.getLastSignedInAccount(requireActivity()).getAccount()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SyncUtils.importFile();
                            App.getDefaultSharedPreferences().edit().putBoolean("restored", true).apply();
                            progressDialog.cancel();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireActivity(), "error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    });

                } catch (Exception e) {
                    Logger.log(e);
                    Toast.makeText(requireActivity(), "error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                }
            }
        });
    }
}