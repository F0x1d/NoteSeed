package com.f0x1d.notes.fragment.settings;

import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.dialogs.SignInDialog;
import com.f0x1d.notes.utils.sync.SyncUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.utils.translations.Translation;
import com.f0x1d.notes.utils.translations.Translations;
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

    public static SyncSettings instance;
    private Preference login;
    private Preference logout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        instance = this;

        View v = super.onCreateView(inflater, container, savedInstanceState);

        if (UselessUtils.ifCustomTheme()) {
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            getActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            getActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            CenteredToolbar toolbar = v.findViewById(R.id.toolbar);
            toolbar.setTitle(Translations.getString("sync"));
            getActivity().setActionBar(toolbar);

            if (UselessUtils.ifCustomTheme())
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

        PreferenceCategory accountCategory = (PreferenceCategory) findPreference("accountC");
        accountCategory.setTitle(Translations.getString("account"));

        PreferenceCategory gdriveCategory = (PreferenceCategory) findPreference("gdriveC");
        gdriveCategory.setTitle(Translations.getString("gdrive"));

        PreferenceCategory fileCategory = (PreferenceCategory) findPreference("fileC");
        fileCategory.setTitle(Translations.getString("file"));

        SwitchPreference autoS = (SwitchPreference) findPreference("auto_s");
        autoS.setTitle(Translations.getString("automatic_sync"));

        login = findPreference("sign_in");
        login.setTitle(Translations.getString("sign_in"));
        logout = findPreference("sign_out");
        logout.setTitle(Translations.getString("sign_out"));

        updateSignedState();

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

        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (GoogleSignIn.getLastSignedInAccount(getContext()) == null) {
                    Toast.makeText(getActivity(), "error, sign in please", Toast.LENGTH_SHORT).show();
                    Logger.log("error, sign in please");
                }

                ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setCancelable(false);
                dialog.setMessage("Loading...");
                dialog.show();

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .build();

                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
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

        Preference import_gdrive = findPreference("import_g");
        import_gdrive.setTitle(Translations.getString("import_gdrive"));
        import_gdrive.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (GoogleSignIn.getLastSignedInAccount(getActivity()) != null) {
                    importFromGDrive();
                } else {
                    Toast.makeText(getActivity(), "error, sign in please", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        Preference export_gdrive = findPreference("export_g");
        export_gdrive.setTitle(Translations.getString("export_gdrive"));
        export_gdrive.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (GoogleSignIn.getLastSignedInAccount(getActivity()) != null) {
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
        import_db.setTitle(Translations.getString("import_db"));
        import_db.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SyncUtils.importFile();
                Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        Preference export = findPreference("export");
        export.setTitle(Translations.getString("export"));
        export.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SyncUtils.export();
                Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        return new MainSettings.CustomPreferenceGroupAdapter(preferenceScreen);
    }

    public void importFromGDrive() {
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
                            Toast.makeText(getActivity(), "error: " + e, Toast.LENGTH_SHORT).show();
                            dialog1.cancel();
                        }
                    });

                } catch (Exception e) {
                    Logger.log(e);
                    Toast.makeText(getActivity(), "error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    dialog1.cancel();
                }
            }
        });
    }
}