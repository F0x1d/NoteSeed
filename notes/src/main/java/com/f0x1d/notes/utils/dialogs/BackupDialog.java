package com.f0x1d.notes.utils.dialogs;

import android.accounts.Account;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.sync.SyncUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;

public class BackupDialog {

    private static boolean haveAnyBackup = false;

    public static void show(Activity activity, Account account) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        if (!PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("restored", false)) {
            dialog.show();

            if (UselessUtils.ifCustomTheme())
                dialog.getWindow().getDecorView().getBackground().setColorFilter(ThemesEngine.background, PorterDuff.Mode.SRC);
            else if (UselessUtils.getBool("night", false))
                dialog.getWindow().getDecorView().getBackground().setColorFilter(activity.getResources().getColor(R.color.statusbar_for_dialogs), PorterDuff.Mode.SRC);
            else
                dialog.getWindow().getDecorView().getBackground().setColorFilter(activity.getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC);

            File db = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes//db");
            File database = new File(db, "database.noteseed");

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
            builder.setTitle(activity.getString(R.string.backup_found));
            builder.setMessage(activity.getString(R.string.restore) + "?");
            builder.setCancelable(false);

            if (database.exists()) {
                builder.setPositiveButton(activity.getString(R.string.restore), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SyncUtils.importFile();
                        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("restored", true).apply();
                        activity.recreate();
                    }
                });

                haveAnyBackup = true;
            }

            builder.setNeutralButton(activity.getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("restored", true).apply();
                    dialog.cancel();
                }
            });

            SyncUtils.ifBackupExistsOnGDrive(account).addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (task.getResult() == null) {
                        Logger.log("gdrive error");
                        dialog.cancel();
                        if (haveAnyBackup)
                            ShowAlertDialog.show(builder);
                        return;
                    }

                    haveAnyBackup = true;

                    builder.setNegativeButton("GDrive", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                ProgressDialog dialog1 = new ProgressDialog(activity);
                                dialog1.setCancelable(false);
                                dialog1.setMessage("Loading...");
                                dialog1.show();

                                if (UselessUtils.ifCustomTheme())
                                    dialog1.getWindow().getDecorView().getBackground().setColorFilter(ThemesEngine.background, PorterDuff.Mode.SRC);
                                else if (UselessUtils.getBool("night", false))
                                    dialog1.getWindow().getDecorView().getBackground().setColorFilter(activity.getResources().getColor(R.color.statusbar_for_dialogs), PorterDuff.Mode.SRC);
                                else
                                    dialog1.getWindow().getDecorView().getBackground().setColorFilter(activity.getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC);

                                SyncUtils.importFromGDrive(task.getResult(), account).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        SyncUtils.importFile();
                                        dialog1.cancel();
                                        dialog.cancel();
                                        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("restored", true).apply();
                                        activity.recreate();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Logger.log(e);
                                        dialog1.cancel();
                                        dialog.cancel();
                                    }
                                });

                            } catch (Exception e) {
                                Logger.log(e);
                            }
                        }
                    });

                    dialog.cancel();
                    if (haveAnyBackup)
                        ShowAlertDialog.show(builder);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Logger.log(e);
                    dialog.cancel();
                    if (haveAnyBackup)
                        ShowAlertDialog.show(builder);
                }
            });
        }
    }
}
