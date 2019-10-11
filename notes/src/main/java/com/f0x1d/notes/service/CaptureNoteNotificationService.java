package com.f0x1d.notes.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.receiver.CaptureNoteReceiver;

public class CaptureNoteNotificationService extends Service {

    public static CaptureNoteNotificationService instance;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = this;

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManagerCompat.createNotificationChannel(
                    new NotificationChannel(getPackageName() + ".capturenotes", getString(R.string.capture_note_notification_action), NotificationManager.IMPORTANCE_NONE));
        }

        buildNotification();

        return START_STICKY;
    }

    public void buildNotification() {
        NotificationCompat.Action action = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            RemoteInput remoteInput = new RemoteInput.Builder("text")
                    .setLabel(getString(R.string.capture_note_notification_action))
                    .build();

            action = new NotificationCompat.Action.Builder(0, getString(R.string.capture_note_notification_action),
                    PendingIntent.getBroadcast(this, 1, new Intent(this, CaptureNoteReceiver.class), 0))
                    .addRemoteInput(remoteInput).build();
        } else {
            action = new NotificationCompat.Action.Builder(0, getString(R.string.capture_note_notification_action),
                    PendingIntent.getActivity(this, 1000, new Intent(this, MainActivity.class)
                            .putExtra("open", "add"), 0)).build();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(getString(R.string.capture_note_notification_title));
        notificationBuilder.setContentText(getString(R.string.capture_note_notification_text));
        notificationBuilder.setSmallIcon(R.drawable.ic_create_black_24dp);
        notificationBuilder.setChannelId(getPackageName() + ".capturenotes");
        notificationBuilder.addAction(action);

        startForeground(Integer.MIN_VALUE + 1000, notificationBuilder.build());
    }
}
