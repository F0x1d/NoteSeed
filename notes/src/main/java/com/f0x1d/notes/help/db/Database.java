package com.f0x1d.notes.help.db;

import com.f0x1d.notes.help.db.daos.FormatDao;
import com.f0x1d.notes.help.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.help.db.daos.NotifyDao;
import com.f0x1d.notes.help.db.entities.Format;
import com.f0x1d.notes.help.db.entities.NoteOrFolder;
import com.f0x1d.notes.help.db.entities.Notify;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {NoteOrFolder.class, Notify.class, Format.class}, version = 3, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract NoteOrFolderDao noteOrFolderDao();
    public abstract NotifyDao notifyDao();
    public abstract FormatDao formatDao();
}
