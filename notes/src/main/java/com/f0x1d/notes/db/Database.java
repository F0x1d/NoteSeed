package com.f0x1d.notes.db;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.f0x1d.notes.App;
import com.f0x1d.notes.db.daos.NoteItemsDao;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.daos.NotifyDao;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.db.entities.Notify;

import java.util.List;

@androidx.room.Database(entities = {NoteOrFolder.class, Notify.class, NoteItem.class}, version = 10, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE NoteItem ADD COLUMN checked INTENGER DEFAULT 0 NOT NULL");
            database.execSQL("ALTER TABLE NoteItem ADD COLUMN type INTENGER DEFAULT 0 NOT NULL");
        }
    };

    public static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE NoteOrFolder ADD COLUMN position INTENGER DEFAULT 0 NOT NULL");
        }
    };

    public static int getLastPosition(String inFolderId) {
        NoteOrFolderDao dao = App.getInstance().getDatabase().noteOrFolderDao();

        List<NoteOrFolder> notes = dao.getAll();

        int lastPos = 0;

        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).in_folder_id.equals(inFolderId))
                lastPos++;
        }

        return lastPos;
    }

    public static int thingsInFolder(String inFolderId) {
        NoteOrFolderDao dao = App.getInstance().getDatabase().noteOrFolderDao();

        List<NoteOrFolder> things = dao.getAll();

        int thingsInFolder = 0;

        for (int i = 0; i < things.size(); i++) {
            if (things.get(i).in_folder_id.equals(inFolderId))
                thingsInFolder++;
        }

        return thingsInFolder;
    }

    public abstract NoteOrFolderDao noteOrFolderDao();

    public abstract NotifyDao notifyDao();

    public abstract NoteItemsDao noteItemsDao();
}
