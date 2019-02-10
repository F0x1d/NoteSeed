package com.f0x1d.notes.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NoteItem {

    public String text;

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long to_id;

    public String pic_res;

    public int position;

    public int checked;
    public int type;

    public NoteItem(long id, long to_id, String text, String pic_res, int position, int checked, int type){
        this.pic_res = pic_res;
        this.text = text;
        this.id = id;
        this.to_id = to_id;
        this.position = position;
        this.checked = checked;
        this.type = type;
    }
}
