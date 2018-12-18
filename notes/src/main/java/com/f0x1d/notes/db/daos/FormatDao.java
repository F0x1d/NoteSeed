package com.f0x1d.notes.db.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.f0x1d.notes.db.entities.Format;

import java.util.List;

@Dao
public interface FormatDao {

    @Insert
    void insert(Format format);

    @Query("SELECT * FROM Format")
    List<Format> getAll();

    @Query("DELETE FROM Format WHERE start_position = :start")
    void deleteByStartPosition(int start);

    @Query("DELETE FROM Format WHERE end_position = :end")
    void deleteByEndPosition(int end);

    @Query("DELETE FROM Format WHERE id = :id")
    void deleteById(long id);
}
