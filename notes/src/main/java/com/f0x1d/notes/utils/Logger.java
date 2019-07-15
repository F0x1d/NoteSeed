package com.f0x1d.notes.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {

    public static void log(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/");
        File logFile = new File(dir, "log.txt");
        try {
            Date currentDate = new Date(System.currentTimeMillis());
            DateFormat df = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.US);
            String time = df.format(currentDate);

            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write("[TIME:" + time + "] " + stackTrace + "\n");
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Log.e("noteseed", e.getLocalizedMessage());
        e.printStackTrace();
    }

    public static void log(String s) {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/");
        File logFile = new File(dir, "log.txt");
        try {
            Date currentDate = new Date(System.currentTimeMillis());
            DateFormat df = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.US);
            String time = df.format(currentDate);

            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write("[TIME:" + time + "] " + s + "\n");
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Log.e("noteseed", s);
    }

    public static String getStackTrace(Throwable e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
