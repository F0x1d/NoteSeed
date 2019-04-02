package com.f0x1d.notes.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Notify {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    public long time;

    public String title;
    public String text;

    public long to_id;

    public Notify(String title, String text, long time, long to_id) {
        this.to_id = to_id;
        this.time = time;
        this.text = text;
        this.title = title;
    }
}
