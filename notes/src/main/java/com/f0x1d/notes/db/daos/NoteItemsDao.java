package com.f0x1d.notes.db.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.f0x1d.notes.db.entities.NoteItem;

import java.util.List;

@Dao
public interface NoteItemsDao {

    @Query("SELECT * FROM NoteItem order by position asc")
    List<NoteItem> getAll();

    @Query("SELECT * FROM NoteItem WHERE id = :id")
    NoteItem getById(long id);

    @Insert
    long insert(NoteItem noteItem);

    @Query("UPDATE NoteItem SET text=:text WHERE id = :id")
    void updateElementText(String text, long id);

    @Query("UPDATE noteitem SET text=:text WHERE to_id = :to_id AND position = :position")
    void updateElementTextByPos(String text, long to_id, int position);

    @Query("UPDATE NoteOrFolder SET edit_time=:edit_time WHERE id = :id")
    void updateNoteTime(long edit_time, long id);

    @Query("UPDATE NoteItem SET position=:pos WHERE id = :id")
    int updateElementPos(int pos, long id);

    @Query("UPDATE NoteItem SET checked=:checked WHERE id = :id")
    int updateIsChecked(int checked, long id);

    @Query("DELETE FROM NOTEITEM WHERE id = :id")
    int deleteItem(long id);

    @Query("DELETE FROM NoteItem WHERE to_id = :to_id")
    void deleteByToId(long to_id);

    @Query("DELETE FROM NoteItem WHERE to_id = :to_id AND position = :pos")
    int deleteByPos(long to_id, int pos);
}
