package com.f0x1d.notes.utils.sync;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.translations.Translations;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SyncService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SyncUtils.export();

        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String name = Translations.getString("sync");
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel channel = new NotificationChannel("com.f0x1d.notes.sync", name, importance);
                channel.enableVibration(false);
                channel.enableLights(false);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setContentTitle(Translations.getString("sync"));
            builder.setContentText(Translations.getString("syncing"));
            builder.setSmallIcon(R.drawable.ic_sync_black_24dp);
            builder.setCategory(NotificationCompat.CATEGORY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                builder.setChannelId("com.f0x1d.notes.sync");
            builder.setOngoing(true);
            builder.setDefaults(0);

            startForeground(13377331, builder.build());

            SyncUtils.exportToGDrive().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    stopForeground(true);
                    if (task.isSuccessful())
                        Logger.log("synced successfully");
                    else
                        Logger.log("synced not successfully");
                    stopSelf();
                }
            });
        }
    }
}
