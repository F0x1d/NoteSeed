package com.f0x1d.notes.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Notify {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    public long time;

    public String title;
    public String text;

    @ColumnInfo(name = "to_id")
    public long toId;

    public Notify(String title, String text, long time, long toId) {
        this.toId = toId;
        this.time = time;
        this.text = text;
        this.title = title;
    }
}
