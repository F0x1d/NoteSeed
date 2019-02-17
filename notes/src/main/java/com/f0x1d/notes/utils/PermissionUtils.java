package com.f0x1d.notes.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

    public static final int REQUEST_WRITE_EXTERNAL = 3;

    public static void requestWriteExternalPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(activity, "Write permission is needed to create Excel file ", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        REQUEST_WRITE_EXTERNAL);

                Toast.makeText(activity, "REQUEST LOCATION PERMISSION", Toast.LENGTH_LONG).show();

            } else {
                ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        REQUEST_WRITE_EXTERNAL);
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}