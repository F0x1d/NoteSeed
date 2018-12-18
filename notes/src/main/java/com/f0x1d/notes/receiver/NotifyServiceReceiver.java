package com.f0x1d.notes.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.App;
import com.f0x1d.notes.db.entities.Notify;

import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotifyServiceReceiver extends WakefulBroadcastReceiver {

    public void notify(Context activity) {

        String title = null;
        String text = null;
        long to_id = 0;
        long id = 0;

        for (Notify noteOrFolder : App.getInstance().getDatabase().notifyDao().getAll()) {
            long time = noteOrFolder.time / (1000 * 30);
            long system_time = System.currentTimeMillis() / (1000 * 30);

            if (system_time >= time){
                title = noteOrFolder.title;
                text = noteOrFolder.text;
                id = noteOrFolder.id;
                to_id = noteOrFolder.to_id;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name1 = "Напоминания";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("com.f0x1d.notes", name1, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            channel.enableVibration(true);
            channel.enableLights(true);
            NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

// Create Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity)
                .setSmallIcon(R.drawable.ic_notifications_none_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(PendingIntent.getActivity(App.getContext(), 228, new Intent(App.getContext(), MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                .setAutoCancel(true)
                .setVibrate(new long[]{1000L, 1000L, 1000L})
                .setChannelId("com.f0x1d.notes");

        Notification notification = builder.build();

// Show Notification
        NotificationManager notificationManager =
                (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify((int) to_id, notification);

        delete(id);
    }

    public void delete(long id){
        App.getInstance().getDatabase().notifyDao().delete(id);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        notify(context);
    }
}
