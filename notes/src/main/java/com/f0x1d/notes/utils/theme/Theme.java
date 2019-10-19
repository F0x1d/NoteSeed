package com.f0x1d.notes.utils.theme;

import java.io.File;

public class Theme {

    public String name;
    public String author;
    public int cardColor;
    public int cardTextColor;

    public File themeFile;

    public Theme(File themeFile, String name, String author, int cardColor, int cardTextColor) {
        this.name = name;
        this.cardColor = cardColor;
        this.author = author;
        this.themeFile = themeFile;
        this.cardTextColor = cardTextColor;
    }
}
