package com.f0x1d.notes.db.daos;

import com.f0x1d.notes.db.entities.NoteOrFolder;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface NoteOrFolderDao {

    @Query("SELECT * FROM NoteOrFolder")
    List<NoteOrFolder> getAll();

    @Query("SELECT * FROM NoteOrFolder WHERE id = :id")
    NoteOrFolder getById(long id);

    @Insert
    long insert(NoteOrFolder NoteOrFolder);

    @Query("UPDATE NoteOrFolder SET title=:title WHERE id = :id")
    void updateNoteTitle(String title, long id);

    @Query("UPDATE NoteOrFolder SET text=:text WHERE id = :id")
    void updateNoteText(String text, long id);

    @Query("UPDATE NoteOrFolder SET folder_name=:text WHERE folder_name = :id")
    void updateFolderTitle(String text, String id);

    @Query("UPDATE NoteOrFolder SET in_folder_id=:text WHERE in_folder_id = :id")
    void updateInFolderId(String text, String id);

    @Query("UPDATE NoteOrFolder SET pinned=:pinned WHERE id = :id")
    void updateNotePinned(int pinned, long id);

    @Query("UPDATE NoteOrFolder SET pinned=:pinned WHERE folder_name = :id")
    void updateFolderPinned(int pinned, String id);

    @Query("UPDATE NoteOrFolder SET color=:color WHERE folder_name = :id")
    void updateFolderColor(String color, String id);

    @Query("UPDATE NoteOrFolder SET color=:color WHERE id = :id")
    void updateNoteColor(String color, long id);

    @Query("UPDATE NoteOrFolder SET locked=:locked WHERE id = :id")
    void updateNoteLocked(int locked, long id);

    @Query("UPDATE NoteOrFolder SET edit_time=:edit_time WHERE id = :id")
    void updateNoteTime(long edit_time, long id);

    @Query("DELETE FROM NoteOrFolder WHERE id = :id")
    void deleteNote(long id);

    @Query("DELETE FROM NoteOrFolder WHERE folder_name = :folder_name")
    void deleteFolder(String folder_name);

    @Query("DELETE FROM NoteOrFolder WHERE in_folder_id = :folder_name")
    void deleteFolder2(String folder_name);

    @Query("DELETE FROM NoteOrFolder")
    void nukeTable();

    @Query("DELETE FROM Notify")
    void nukeTable2();

    @Query("DELETE FROM NoteItem")
    void nukeTable3();
}
