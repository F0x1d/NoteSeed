package com.f0x1d.notes.db;

import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.f0x1d.notes.db.daos.NoteItemsDao;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.daos.NotifyDao;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.db.entities.Notify;

@androidx.room.Database(entities = {NoteOrFolder.class, Notify.class, NoteItem.class}, version = 9, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE NoteItem ADD COLUMN checked INTENGER DEFAULT 0 NOT NULL");
            database.execSQL("ALTER TABLE NoteItem ADD COLUMN type INTENGER DEFAULT 0 NOT NULL");
        }
    };

    public abstract NoteOrFolderDao noteOrFolderDao();

    public abstract NotifyDao notifyDao();

    public abstract NoteItemsDao noteItemsDao();
}
