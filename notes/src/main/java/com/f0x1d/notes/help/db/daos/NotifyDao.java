package com.f0x1d.notes.help.db.daos;

import com.f0x1d.notes.help.db.entities.NoteOrFolder;
import com.f0x1d.notes.help.db.entities.Notify;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface NotifyDao {

    @Query("SELECT * FROM Notify")
    List<Notify> getAll();

    @Query("SELECT * FROM Notify WHERE id = :id")
    Notify getById(long id);

    @Insert
    long insert(Notify notify);

    @Query("DELETE FROM Notify WHERE id = :id")
    void delete(long id);
}
