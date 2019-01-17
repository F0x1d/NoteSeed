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

import org.json.JSONException;
import org.json.JSONObject;

public class FCMReceiver extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("notes_fcm", "From: " + remoteMessage.getFrom());

            String title = null;
            String text = null;

            JSONObject jsonObject = new JSONObject(remoteMessage.getData());

            Log.e("notes_err", jsonObject.toString());

            try {
                title = jsonObject.getString("title");
                text = jsonObject.getString("body");

                Notification.Builder builder = new Notification.Builder(getApplicationContext());

                    builder.setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setAutoCancel(true)
                            .setVibrate(new long[]{1000L, 1000L, 1000L});
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            builder.setChannelId("com.f0x1d.notes");
                        }

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(9999999, builder.build());
            } catch (JSONException e) {
                Log.e("notes_err", e.getLocalizedMessage());
            }
    }
}
