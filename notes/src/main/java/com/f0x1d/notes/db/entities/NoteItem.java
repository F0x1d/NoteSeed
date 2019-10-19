package com.f0x1d.notes.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NoteItem {

    public String text;

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "to_id")
    public long toId;

    @ColumnInfo(name = "pic_res")
    public String picRes;

    public int position;

    public int checked;
    public int type;

    public NoteItem(long id, long toId, String text, String picRes, int position, int checked, int type) {
        this.picRes = picRes;
        this.text = text;
        this.id = id;
        this.toId = toId;
        this.position = position;
        this.checked = checked;
        this.type = type;
    }
}
