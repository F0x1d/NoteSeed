package com.f0x1d.notes.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.f0x1d.notes.App;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

public class SyncUtils {

    public static void pushToGDrive(Activity activity) throws IOException {
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName("database.json");
        fileMetadata.setParents(Collections.singletonList("appDataFolder"));

        File db = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes//db");
        File database = new File(db, "database.noteseed");

        FileContent mediaContent = new FileContent("application/json", database);

        Drive driveService = new Drive(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), null);

        com.google.api.services.drive.model.File file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
        Log.e("notes_err", "File ID: " + file.getId());
    }

    public static void export(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File db = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes//db");
                if (!db.exists()){
                    db.mkdirs();
                }

                JSONArray main = new JSONArray();
                for (NoteOrFolder noteOrFolder : App.getInstance().getDatabase().noteOrFolderDao().getAll()) {
                    JSONObject note = new JSONObject();
                    JSONArray elements = new JSONArray();

                    for (NoteItem noteItem : App.getInstance().getDatabase().noteItemsDao().getAll()) {
                        if (noteItem.to_id == noteOrFolder.id){
                            JSONObject element = new JSONObject();
                            try {
                                element.put("id", noteItem.id);
                                element.put("to_id", noteItem.to_id);
                                if (noteItem.pic_res == null){
                                    element.put("pic_res", "null");
                                } else {
                                    element.put("pic_res", noteItem.pic_res);
                                }
                                if (noteItem.text == null){
                                    element.put("text", "null");
                                } else {
                                    element.put("text", noteItem.text);
                                }
                                element.put("position", noteItem.position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            elements.put(element);
                        }
                    }

                    try {
                        if (noteOrFolder.title == null){
                            note.put("title", "null");
                        } else {
                            note.put("title", noteOrFolder.title);
                        }
                        note.put("locked", noteOrFolder.locked);
                        note.put("id", noteOrFolder.id);
                        note.put("is_folder", noteOrFolder.is_folder);
                        note.put("pinned", noteOrFolder.pinned);
                        note.put("edit_time", noteOrFolder.edit_time);
                        note.put("in_folder_id", noteOrFolder.in_folder_id);
                        if (noteOrFolder.text == null){
                            note.put("text", "null");
                        } else {
                            note.put("text", noteOrFolder.text);
                        }
                        note.put("color", noteOrFolder.color);
                        if (noteOrFolder.folder_name == null){
                            note.put("folder_name", "null");
                        } else {
                            note.put("folder_name", noteOrFolder.folder_name);
                        }

                        note.put("elems", elements);

                        main.put(note);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                File database = new File(db, "database.noteseed");

                try {
                    FileWriter writer = new FileWriter(database);
                    writer.append(main.toString());
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void importFile(){
        File db = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes//db");
        File database = new File(db, "database.noteseed");

        String all = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(database));
            String strLine;
            while ((strLine = br.readLine()) != null){
                all = all + strLine;
            }
        } catch (IOException e) {
            Log.e("notes_err", e.getLocalizedMessage());
        }

        App.getInstance().getDatabase().noteOrFolderDao().nukeTable();
        App.getInstance().getDatabase().noteOrFolderDao().nukeTable2();
        App.getInstance().getDatabase().noteOrFolderDao().nukeTable3();

        try {
            JSONArray main = new JSONArray(all);

            for (int i = 0; i < main.length(); i++){
                JSONObject note = main.getJSONObject(i);
                Log.e("notes_err", note.toString());

                App.getInstance().getDatabase().noteOrFolderDao().insert(new NoteOrFolder(note.getString("title"),
                        note.getString("text"), note.getLong("id"), note.getInt("locked"), note.getString("in_folder_id"), note.getInt("is_folder"),
                        note.getString("folder_name"), note.getInt("pinned"), note.getString("color"), note.getLong("edit_time")));

                JSONArray elements = note.getJSONArray("elems");

                for (int j = 0; j < elements.length(); j++){
                    JSONObject element = elements.getJSONObject(j);

                    if (element.getString("pic_res").equals("null")){
                        App.getInstance().getDatabase().noteItemsDao().insert(new NoteItem(element.getLong("id"),
                                element.getLong("to_id"), element.getString("text"), null, element.getInt("position")));
                    } else if (element.getString("text").equals("null")){
                        App.getInstance().getDatabase().noteItemsDao().insert(new NoteItem(element.getLong("id"),
                                element.getLong("to_id"), null, element.getString("pic_res"), element.getInt("position")));
                    } else {
                        App.getInstance().getDatabase().noteItemsDao().insert(new NoteItem(element.getLong("id"),
                                element.getLong("to_id"), element.getString("text"), element.getString("pic_res"), element.getInt("position")));
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("notes_err", e.getLocalizedMessage());
        }
    }
}
