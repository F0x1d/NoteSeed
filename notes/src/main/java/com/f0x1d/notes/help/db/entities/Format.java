package com.f0x1d.notes.help.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Format {

    @PrimaryKey (autoGenerate = true)
    public long id;

    public int start_position;
    public int end_position;

    public String type;

    public boolean ifTitle;

    public long to_id;

    public Format(long id, int start_position, int end_position, String type, boolean ifTitle, long to_id){
        this.end_position = end_position;
        this.id = id;
        this.start_position = start_position;
        this.type = type;
        this.ifTitle = ifTitle;
        this.to_id = to_id;
    }
}
