package com.f0x1d.notes.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.f0x1d.notes.R;

public class CopyTextReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("NoteSeed", intent.getExtras().getString("text"));
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, context.getString(R.string.copied), Toast.LENGTH_SHORT).show();

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(Integer.MIN_VALUE);
    }
}
