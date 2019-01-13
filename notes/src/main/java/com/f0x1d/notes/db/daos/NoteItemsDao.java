package com.f0x1d.notes.db.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.f0x1d.notes.db.entities.NoteItem;

import java.util.List;

@Dao
public interface NoteItemsDao {

    @Query("SELECT * FROM NoteItem")
    List<NoteItem> getAll();

    @Insert
    long insert(NoteItem noteItem);

    @Query("UPDATE NoteItem SET text=:text WHERE id = :id")
    void updateElementText(String text, long id);

    @Query("UPDATE NoteOrFolder SET edit_time=:edit_time WHERE id = :id")
    void updateNoteTime(long edit_time, long id);

    @Query("UPDATE NoteItem SET position=:pos WHERE id = :id")
    void updateElementPos(int pos, long id);

    @Query("DELETE FROM NOTEITEM WHERE id = :id")
    void deleteItem(long id);
}
