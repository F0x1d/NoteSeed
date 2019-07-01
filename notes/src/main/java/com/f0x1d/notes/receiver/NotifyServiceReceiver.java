package com.f0x1d.notes.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.text.Html;

import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.db.entities.NoteOrFolder;
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

            if (system_time >= time) {
                title = noteOrFolder.title;
                text = noteOrFolder.text;
                id = noteOrFolder.id;
                to_id = noteOrFolder.to_id;
            }
        }

        String inFolderId = "";

        for (NoteOrFolder noteOrFolder : App.getInstance().getDatabase().noteOrFolderDao().getAll()) {
            if (to_id == noteOrFolder.id) {
                if (noteOrFolder.in_folder_id.equals("def"))
                    break;
                inFolderId = noteOrFolder.in_folder_id + ": ";
                break;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = App.getContext().getString(R.string.notification);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("com.f0x1d.notes.notifications", name, importance);
            channel.enableVibration(true);
            channel.enableLights(true);
            NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity)
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setContentTitle(Html.fromHtml(inFolderId + title.replace("\n", "<br />")))
                .setContentText(Html.fromHtml(text.replace("\n", "<br />")))
                .setContentIntent(PendingIntent.getActivity(App.getContext(), 228, new Intent(App.getContext(), MainActivity.class)
                        .putExtra("id", to_id).putExtra("title", title), PendingIntent.FLAG_CANCEL_CURRENT))
                .setAutoCancel(true)
                .setVibrate(new long[]{1000L, 1000L, 1000L})
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(text.replace("\n", "<br />"))));
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder.setChannelId("com.f0x1d.notes.notifications");

        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify((int) to_id + 1, builder.build());

        delete(id);
    }

    public void delete(long id) {
        App.getInstance().getDatabase().notifyDao().delete(id);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "noteseed:receiver");
        wl.acquire();

        notify(context);

        wl.release();
    }
}
