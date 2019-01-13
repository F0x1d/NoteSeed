package com.f0x1d.notes.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NoteItem {

    public String text;

    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    public long to_id;

    public String pic_res;

    public int position;

    public NoteItem(long to_id, String text, String pic_res, int position){
        this.pic_res = pic_res;
        this.text = text;
        this.to_id = to_id;
        this.position = position;
    }
}
