package com.f0x1d.notes.help.model;

import java.io.File;

public class Theme {

    public String name;
    public String author;
    public int card_color;
    public int card_text_color;

    public File theme_file;

    public Theme(File theme_file, String name, String author, int card_color, int card_text_color){
        this.name = name;
        this.card_color = card_color;
        this.author = author;
        this.theme_file = theme_file;
        this.card_text_color = card_text_color;
    }
}
