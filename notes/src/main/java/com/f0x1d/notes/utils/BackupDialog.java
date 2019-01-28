package com.f0x1d.notes.utils;

import android.accounts.Account;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.io.File;

public class BackupDialog {

    public static void show(Activity activity, Account account){
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        if (!PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("restored", false)){
            dialog.show();

            File db = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes//db");
            File database = new File(db, "database.noteseed");

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.backup_found);
            builder.setMessage(activity.getString(R.string.restore) + "?");
            builder.setCancelable(false);

            if (database.exists()){
                builder.setPositiveButton(activity.getString(R.string.restore), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SyncUtils.importFile();
                        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("restored", true).apply();
                        activity.recreate();
                    }
                });
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
                        Log.e("notes_err", "gdrive error");
                        dialog.cancel();
                        builder.show();
                        return;
                    }

                    builder.setNegativeButton("GDrive", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                ProgressDialog dialog1 = new ProgressDialog(activity);
                                dialog1.setCancelable(false);
                                dialog1.setMessage("Loading...");
                                dialog1.show();

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
                                        Log.e("notes_err", e.getLocalizedMessage());
                                        dialog1.cancel();
                                        dialog.cancel();
                                    }
                                });

                            } catch (Exception e){
                                Log.e("notes_err", e.getLocalizedMessage());
                            }
                        }
                    });

                    dialog.cancel();
                    builder.show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("notes_err", e.getLocalizedMessage());
                    dialog.cancel();
                    builder.show();
                }
            });
        }
    }
}
