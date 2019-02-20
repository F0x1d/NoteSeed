package com.f0x1d.notes.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.db.entities.Notify;

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
            channel.enableVibration(true);
            channel.enableLights(true);
            NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity)
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(PendingIntent.getActivity(App.getContext(), 228, new Intent(App.getContext(), MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                .setAutoCancel(true)
                .setVibrate(new long[]{1000L, 1000L, 1000L});

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    builder.setChannelId("com.f0x1d.notes");

        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify((int) to_id + 1, builder.build());

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
