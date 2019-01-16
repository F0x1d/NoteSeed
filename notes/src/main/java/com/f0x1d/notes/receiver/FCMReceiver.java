package com.f0x1d.notes.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.f0x1d.notes.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Collection;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FCMReceiver extends FirebaseMessagingService {

    private String TAG = "notes_fcm";

    @SuppressLint("NewApi")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String name1 = "Напоминания";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("com.f0x1d.notes", name1, importance);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                channel.enableVibration(true);
                channel.enableLights(true);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("body"))
                    .setAutoCancel(true)
                    .setVibrate(new long[]{1000L, 1000L, 1000L})
                    .setChannelId("com.f0x1d.notes");

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(99999, builder.build());
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(99999);
    }
}
