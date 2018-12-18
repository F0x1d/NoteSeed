package com.f0x1d.notes.db.entities;

import com.f0x1d.notes.utils.UselessUtils;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NoteOrFolder {

    public String title;
    public String folder_name;

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String in_folder_id;
    public int is_folder;

    public String text;
    public int locked;

    public int pinned;
    public String color;

    public NoteOrFolder(String title, String text, long id, int locked, String in_folder_id, int is_folder, String folder_name, int pinned, String color){
        this.title = title;
        this.text = text;
        this.id = id;
        this.locked = locked;
        this.in_folder_id = in_folder_id;
        this.is_folder = is_folder;
        this.folder_name = folder_name;
        this.pinned = pinned;
        this.color = color;
    }
}
