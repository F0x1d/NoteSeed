package com.f0x1d.notes.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NoteOrFolder {

    public String title;
    public String text;
    @ColumnInfo(name = "folder_name")
    public String folderName;

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "in_folder_id")
    public String inFolderId;
    @ColumnInfo(name = "is_folder")
    public int isFolder;

    public int locked;

    public int pinned;
    public int position;
    public String color;

    @ColumnInfo(name = "edit_time")
    public long editTime;

    public NoteOrFolder(String title, String text, long id, int locked, String inFolderId, int isFolder, String folderName, int pinned, String color, long editTime, int position) {
        this.title = title;
        this.id = id;
        this.locked = locked;
        this.inFolderId = inFolderId;
        this.isFolder = isFolder;
        this.folderName = folderName;
        this.pinned = pinned;
        this.color = color;
        this.editTime = editTime;
        this.text = text;
        this.position = position;
    }
}
