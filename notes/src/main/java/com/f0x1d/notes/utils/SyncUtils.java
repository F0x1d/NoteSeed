package com.f0x1d.notes.utils;

import android.accounts.Account;
import android.os.Environment;
import android.util.Log;

import com.f0x1d.notes.App;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SyncUtils {

    private static final Executor mExecutor = Executors.newSingleThreadExecutor();

    public static Task<String> ifBackupExistsOnGDrive(Account account) {
        return Tasks.call(mExecutor, () -> {
            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                            App.getContext(), Collections.singleton(DriveScopes.DRIVE_APPDATA));
            credential.setSelectedAccount(account);

            Drive driveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName("NoteSeed").build();

            try {
                FileList files = driveService.files().list()
                        .setSpaces("appDataFolder")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageSize(10)
                        .execute();

                for (com.google.api.services.drive.model.File file : files.getFiles()) {
                    Log.e("notes_err", "file found: " + file.getName());

                    if (file.getName().equals("database.json")) {
                        return file.getId();
                    }
                }
            } catch (Exception e) {
                Log.e("notes_err", e.getLocalizedMessage());
            }

            return null;
        });
    }

    public static Task<Void> importFromGDrive(String id, Account account) {
        return Tasks.call(mExecutor, () -> {
            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                            App.getContext(), Collections.singleton(DriveScopes.DRIVE_APPDATA));

            credential.setSelectedAccount(account);

            Drive driveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName("NoteSeed").build();

            File db = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes//db");
            File database = new File(db, "database.noteseed");

            FileOutputStream stream = new FileOutputStream(database);

            try {
                driveService.files().get(id)
                        .executeMediaAndDownloadTo(stream);

                stream.close();
            } catch (IOException e) {
                Log.e("notes_err", e.getLocalizedMessage());
            }

            return null;
        });
    }

    public static Task<Void> exportToGDrive() {
        return Tasks.call(mExecutor, () -> {
            File db = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes//db");
            File database = new File(db, "database.noteseed");

            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName("database.json");
            fileMetadata.setParents(Collections.singletonList("appDataFolder"));

            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                            App.getContext(), Collections.singleton(DriveScopes.DRIVE_APPDATA));
            if (GoogleSignIn.getLastSignedInAccount(App.getContext()) != null) {
                credential.setSelectedAccount(GoogleSignIn.getLastSignedInAccount(App.getContext()).getAccount());
            }

            Drive driveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName("NoteSeed").build();

            FileContent mediaContent = new FileContent("application/json", database);
            try {
                com.google.api.services.drive.model.File file = driveService.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();
            } catch (IOException e) {
                Log.e("notes_err", e.getLocalizedMessage());
            }

            return null;
        });
    }

    public static Task<Void> export() {
        return Tasks.call(mExecutor, () -> {
            File db = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes//db");
            if (!db.exists()) {
                db.mkdirs();
            }

            JSONArray main = new JSONArray();
            for (NoteOrFolder noteOrFolder : App.getInstance().getDatabase().noteOrFolderDao().getAll()) {
                JSONObject note = new JSONObject();
                JSONArray elements = new JSONArray();

                for (NoteItem noteItem : App.getInstance().getDatabase().noteItemsDao().getAll()) {
                    if (noteItem.to_id == noteOrFolder.id) {

                        JSONObject element = new JSONObject();
                        try {
                            element.put("id", noteItem.id);
                            element.put("to_id", noteItem.to_id);
                            element.put("type", noteItem.type);
                            element.put("checked", noteItem.checked);

                            if (noteItem.pic_res == null) {
                                element.put("pic_res", "null");
                            } else {
                                element.put("pic_res", noteItem.pic_res);
                            }

                            if (noteItem.text == null) {
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
                    if (noteOrFolder.title == null) {
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

                    if (noteOrFolder.text == null) {
                        note.put("text", "null");
                    } else {
                        note.put("text", noteOrFolder.text);
                    }

                    note.put("color", noteOrFolder.color);
                    if (noteOrFolder.folder_name == null) {
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

            return null;
        });
    }

    public static void importFile() {
        File db = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes//db");
        File database = new File(db, "database.noteseed");

        String all = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(database));
            String strLine;
            while ((strLine = br.readLine()) != null) {
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

            for (int i = 0; i < main.length(); i++) {
                JSONObject note = main.getJSONObject(i);
                Log.e("notes_err", note.toString());

                App.getInstance().getDatabase().noteOrFolderDao().insert(new NoteOrFolder(note.getString("title"),
                        note.getString("text"), note.getLong("id"), note.getInt("locked"), note.getString("in_folder_id"), note.getInt("is_folder"),
                        note.getString("folder_name"), note.getInt("pinned"), note.getString("color"), note.getLong("edit_time")));

                JSONArray elements = note.getJSONArray("elems");

                for (int j = 0; j < elements.length(); j++) {
                    JSONObject element = elements.getJSONObject(j);

                    if (element.getString("pic_res").equals("null")) {
                        App.getInstance().getDatabase().noteItemsDao().insert(new NoteItem(element.getLong("id"),
                                element.getLong("to_id"), element.getString("text"), null, element.getInt("position"),
                                element.getInt("checked"), element.getInt("type")));
                    } else if (element.getString("text").equals("null")) {
                        App.getInstance().getDatabase().noteItemsDao().insert(new NoteItem(element.getLong("id"),
                                element.getLong("to_id"), null, element.getString("pic_res"), element.getInt("position"),
                                element.getInt("checked"), element.getInt("type")));
                    } else {
                        App.getInstance().getDatabase().noteItemsDao().insert(new NoteItem(element.getLong("id"),
                                element.getLong("to_id"), element.getString("text"), element.getString("pic_res"), element.getInt("position"),
                                element.getInt("checked"), element.getInt("type")));
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("notes_err", e.getLocalizedMessage());
        }
    }
}
