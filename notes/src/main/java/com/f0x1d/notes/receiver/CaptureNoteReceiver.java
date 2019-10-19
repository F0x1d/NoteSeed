package com.f0x1d.notes.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.RemoteInput;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.adapter.NoteItemsAdapter;
import com.f0x1d.notes.db.Database;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.fragment.main.NotesFragment;
import com.f0x1d.notes.service.CaptureNoteNotificationService;

public class CaptureNoteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long rowID = App.getInstance().getDatabase().noteOrFolderDao().insert(new NoteOrFolder(generateName(context), null, NotesFragment.genId(), 0, "def",
                0, null, 0, "", System.currentTimeMillis(), Database.getLastPosition("def")));

        App.getInstance().getDatabase().noteItemsDao().insert(
                new NoteItem(NoteItemsAdapter.getId(), rowID, RemoteInput.getResultsFromIntent(intent).getCharSequence("text").toString(), null, 0, 0, 0));

        if (CaptureNoteNotificationService.instance != null)
            CaptureNoteNotificationService.instance.buildNotification();
    }

    public String generateName(Context c) {
        int first_number = 1;

        String name = c.getString(R.string.new_note);

        for (NoteOrFolder noteOrFolder : App.getInstance().getDatabase().noteOrFolderDao().getAll()) {
            if (noteOrFolder.isFolder == 0 && noteOrFolder.title.equals(name)) {
                name = c.getString(R.string.new_note) + first_number;
                first_number++;
            }
        }

        return name;
    }
}
