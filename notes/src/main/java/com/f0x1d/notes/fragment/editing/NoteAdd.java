package com.f0x1d.notes.fragment.editing;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.App;
import com.f0x1d.notes.BuildConfig;
import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.adapter.ItemsAdapter;
import com.f0x1d.notes.adapter.NoteItemsAdapter;
import com.f0x1d.notes.db.Database;
import com.f0x1d.notes.db.daos.NoteItemsDao;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.db.entities.Notify;
import com.f0x1d.notes.fragment.bottomSheet.SetNotify;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.bottomSheet.BottomSheetCreator;
import com.f0x1d.notes.utils.bottomSheet.Element;
import com.f0x1d.notes.utils.dialogs.ShowAlertDialog;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.view.CenteredToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.f0x1d.notes.fragment.editing.NoteEdit.copy;
import static com.f0x1d.notes.fragment.editing.NoteEdit.last_pos;

public class NoteAdd extends Fragment {

    String id;
    boolean pinned = false;
    EditText title;
    RecyclerView recyclerView;
    long rowID;
    NoteOrFolderDao dao;
    NoteItemsDao noteItemsDao;
    CenteredToolbar toolbar;
    List<NoteItem> noteItems;
    private String currentPhotoPath;

    public static NoteAdd newInstance(String in_folder_id) {

        Bundle args = new Bundle();
        args.putString("id", in_folder_id);

        NoteAdd fragment = new NoteAdd();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notes_editing_layout, container, false);

        id = getArguments().getString("id");

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.new_note));

        getActivity().setActionBar(toolbar);

        toolbar.inflateMenu(R.menu.edit_menu);

        toolbar.getMenu().findItem(R.id.lock).setTitle(getString(R.string.lock));
        toolbar.getMenu().findItem(R.id.export).setTitle(getString(R.string.export));
        toolbar.getMenu().findItem(R.id.pin_status).setTitle(getString(R.string.pin_in_status_bar));
        toolbar.getMenu().findItem(R.id.settings).setTitle(getString(R.string.settings));

        if (UselessUtils.ifCustomTheme()) {
            toolbar.setNavigationIcon(UselessUtils.setTint(getActivity().getDrawable(R.drawable.ic_timer_black_24dp), ThemesEngine.iconsColor));
        } else if (UselessUtils.getBool("night", false)) {
            toolbar.setNavigationIcon(getActivity().getDrawable(R.drawable.ic_timer_white_24dp));
        } else {
            toolbar.setNavigationIcon(getActivity().getDrawable(R.drawable.ic_timer_black_24dp));
        }

        toolbar.setNavigationOnClickListener(v1 -> {
            NoteItem item = null;
            for (NoteItem noteItem : noteItemsDao.getAll()) {
                if (rowID == noteItem.to_id && noteItem.position == 0) {
                    item = noteItem;
                }
            }

            /*PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("notify_title", title.getText().toString()).putString("notify_text", item.text)
                    .putInt("notify_id", PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("id", 0)).apply();*/
            SetNotify notify = new SetNotify(new Notify(title.getText().toString(), item.text, 0, item.to_id));
            notify.show(getActivity().getSupportFragmentManager(), "TAG");
        });

        MenuItem pic = toolbar.getMenu().findItem(R.id.attach);
        pic.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        if (UselessUtils.ifCustomTheme()) {
            pic.setIcon(UselessUtils.setTint(getResources().getDrawable(R.drawable.ic_add_black_24dp), ThemesEngine.iconsColor));
        } else if (UselessUtils.getBool("night", false)) {
            pic.setIcon(R.drawable.ic_add_white_24dp);
        } else {
            pic.setIcon(R.drawable.ic_add_black_24dp);
        }

        if (UselessUtils.ifCustomTheme()) {
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            getActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            getActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);

            toolbar.setBackgroundColor(ThemesEngine.toolbarColor);
        }

        dao = App.getInstance().getDatabase().noteOrFolderDao();
        rowID = dao.insert(new NoteOrFolder(generateName(), null, Notes.genId(), 0, id,
                0, null, 0, "", System.currentTimeMillis(), Database.getLastPosition(id)));

        noteItemsDao = App.getInstance().getDatabase().noteItemsDao();

        noteItemsDao.insert(new NoteItem(NoteItemsAdapter.getId(), rowID, "", null, 0, 0, 0));
        last_pos = 0;

        setHasOptionsMenu(true);

        getActivity().invalidateOptionsMenu();

        title = view.findViewById(R.id.edit_title);
        title.setHint(getString(R.string.title));
        title.setTextSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("text_size", "15")));

        UselessUtils.showKeyboard(title, getActivity());

        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        noteItems = new ArrayList<>();

        for (NoteItem item : noteItemsDao.getAll()) {
            if (item.to_id == rowID) {
                add(item.position, item);
            }
        }

        NoteItemsAdapter adapter = new NoteItemsAdapter(noteItems, getActivity(), this);

        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder h1, RecyclerView.ViewHolder h2) {
                int fromPosition = h1.getAdapterPosition();
                int toPosition = h2.getAdapterPosition();

                if (fromPosition != 0 && toPosition != 0)
                    adapter.onItemMoved(fromPosition, toPosition);
                else
                    Toast.makeText(getActivity(), "Nope.", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
                if (viewHolder.getPosition() == 0) {
                    recyclerView.getAdapter().notifyItemChanged(viewHolder.getPosition());
                    Toast.makeText(getActivity(), "Nope.", Toast.LENGTH_SHORT).show();
                } else
                    adapter.delete(viewHolder.getPosition(), view);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        Typeface face;

        if (UselessUtils.getBool("mono", false)) {
            face = Typeface.MONOSPACE;

            title.setTypeface(face);
        }

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dao.updateNoteTitle(title.getText().toString(), rowID);
                dao.updateNoteTime(System.currentTimeMillis(), rowID);
            }
        });
        return view;
    }

    private void add(int pos, NoteItem item) {
        try {
            noteItems.add(pos, item);
        } catch (IndexOutOfBoundsException e) {
            add(pos - 1, item);
        }
    }

    private void remove(int pos) {
        try {
            noteItems.remove(pos);
        } catch (IndexOutOfBoundsException e) {
            remove(pos - 1);
        }
    }

    private void addThisTODOs() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.text_size_layout, null);

        SeekBar seekBar = v.findViewById(R.id.text_size);
        seekBar.setMax(20);
        TextView textView = v.findViewById(R.id.text);
        textView.setTextSize(30);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setView(v);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBar.setProgress(3);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < seekBar.getProgress(); i++) {
                    last_pos = last_pos + 1;
                    NoteItem noteItem2 = new NoteItem(NoteItemsAdapter.getId(), rowID, "", null, last_pos, 0, 1);
                    noteItemsDao.insert(noteItem2);
                    add(last_pos, noteItem2);
                }

                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        ShowAlertDialog.show(builder);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export:
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.export_file_type_dialog, null);
                EditText text = v.findViewById(R.id.extension);
                ((TextView) v.findViewById(R.id.text)).setText(getString(R.string.export_file_type));

                MaterialAlertDialogBuilder builder2 = new MaterialAlertDialogBuilder(getActivity());
                builder2.setView(v);
                builder2.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File noteDir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/" + "/Exported notes");
                        if (!noteDir.exists()) {
                            noteDir.mkdirs();
                        }
                        File note = new File(noteDir, title.getText().toString() + text.getText().toString());
                        try {
                            String text = "";

                            boolean first = true;

                            for (NoteItem noteItem : noteItemsDao.getAll()) {
                                if (noteItem.to_id == rowID && noteItem.pic_res == null) {
                                    if (first) {
                                        text = text + noteItem.text;
                                        first = false;
                                    } else {
                                        text = text + "\n" + noteItem.text;
                                    }
                                }
                            }

                            FileWriter writer = new FileWriter(note);
                            writer.append(text);
                            writer.flush();
                            writer.close();
                            Toast.makeText(getActivity(), getString(R.string.saved) + " " + note.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Logger.log(e);
                        }
                    }
                });
                ShowAlertDialog.show(builder2);

                break;
            case R.id.attach:
                BottomSheetCreator creator = new BottomSheetCreator(getActivity());
                creator.addElement(new Element(getString(R.string.text), getActivity().getDrawable(R.drawable.ic_text_fields_white_24dp), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        last_pos = last_pos + 1;
                        NoteItem noteItem = new NoteItem(NoteItemsAdapter.getId(), rowID, "", null, last_pos, 0, 0);
                        noteItemsDao.insert(noteItem);
                        add(last_pos, noteItem);

                        recyclerView.getAdapter().notifyDataSetChanged();

                        try {
                            creator.customBottomSheet.dismiss();
                        } catch (Exception e) {
                        }
                    }
                }));
                creator.addElement(new Element(getString(R.string.picture), getActivity().getDrawable(R.drawable.ic_image_white_24dp), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openChoosePicture();
                    }
                }));
                creator.addElement(new Element(getString(R.string.item_checkbox), getActivity().getDrawable(R.drawable.ic_work_white_24dp), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addThisTODOs();

                        try {
                            creator.customBottomSheet.dismiss();
                        } catch (Exception e) {
                        }
                    }
                }));
                creator.addElement(new Element(getString(R.string.file), getActivity().getDrawable(R.drawable.ic_insert_drive_file_white_24dp), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFile("*/*", 229, getActivity());

                        try {
                            creator.customBottomSheet.dismiss();
                        } catch (Exception e) {
                        }
                    }
                }));
                creator.show("", true);
                break;
            case R.id.lock:
                if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("lock", false)) {
                    if (item.isChecked()) {
                        item.setChecked(false);
                    } else {
                        item.setChecked(true);
                    }

                    if (item.isChecked()) {
                        dao.updateNoteLocked(1, rowID);
                    } else {
                        dao.updateNoteLocked(0, rowID);
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.enable_pin), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.settings:
                new ItemsAdapter(null, getActivity(), true).getNotesDialog(rowID);
                break;
            case R.id.pin_status:
                NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                if (pinned) {
                    manager.cancel((int) rowID + 1);
                    toolbar.getMenu().findItem(R.id.pin_status).setTitle(getString(R.string.pin_in_status_bar));
                    pinned = false;
                    getActivity().getSharedPreferences("notifications", Context.MODE_PRIVATE).edit().putBoolean("note " + rowID, false).apply();
                    break;
                }

                NoteItem flexNoteItem = null;
                for (NoteItem noteItem : noteItemsDao.getAll()) {
                    if (rowID == noteItem.to_id && noteItem.position == 0) {
                        flexNoteItem = noteItem;
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String name = getString(R.string.notification);
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("com.f0x1d.notes.notifications", name, importance);
                    channel.enableVibration(true);
                    channel.enableLights(true);
                    NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

                Notification.Builder builder = new Notification.Builder(getActivity());
                builder.setSmallIcon(R.drawable.ic_notifications_active_black_24dp);
                builder.setContentTitle(Html.fromHtml(title.getText().toString().replace("\n", "<br />")));
                builder.setContentText(Html.fromHtml(flexNoteItem.text.replace("\n", "<br />")));
                builder.setOngoing(true);
                builder.setStyle(new Notification.BigTextStyle().bigText(Html.fromHtml(flexNoteItem.text.replace("\n", "<br />"))));
                builder.setContentIntent(PendingIntent.getActivity(App.getContext(), 228, new Intent(App.getContext(), MainActivity.class)
                        .putExtra("id", rowID).putExtra("title", title.getText().toString()), 0));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    builder.setChannelId("com.f0x1d.notes.notifications");

                manager.notify((int) rowID + 1, builder.build());

                toolbar.getMenu().findItem(R.id.pin_status).setTitle(getString(R.string.unpin_from_status_bar));
                pinned = true;

                getActivity().getSharedPreferences("notifications", Context.MODE_PRIVATE).edit().putBoolean("note " + rowID, true).apply();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openChoosePicture() {
        BottomSheetCreator creator = new BottomSheetCreator(getActivity());
        creator.addElement(new Element(getString(R.string.camera), getResources().getDrawable(R.drawable.ic_camera_alt_white_24dp), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    creator.customBottomSheet.dismiss();
                } catch (Exception e) {
                }

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Logger.log(ex);
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                        if (Build.VERSION.SDK_INT >= 24) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        }
                        startActivityForResult(takePictureIntent, 1337);
                    }
                }
            }
        }));
        creator.addElement(new Element(getString(R.string.gallery), getResources().getDrawable(R.drawable.ic_image_white_24dp), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    creator.customBottomSheet.dismiss();
                } catch (Exception e) {
                }

                openImage("image/*", 228, getActivity());
            }
        }));
        creator.show("TAG", true);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/" + "/pics");
        if (!storageDir.exists())
            storageDir.mkdirs();

        File nomedia = new File(storageDir, ".nomedia");
        if (!nomedia.exists())
            nomedia.createNewFile();

        File image = new File(storageDir, imageFileName + ".jpg");
        image.createNewFile();

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void openImage(String minmeType, int requestCode, Context c) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(minmeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.f0x1d.notes.main.PICK_DATA");
        // if you want any file type, you can skip next line
        sIntent.putExtra("CONTENT_TYPE", minmeType);
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;
        if (c.getPackageManager().resolveActivity(sIntent, 0) != null) {
            // it is device with samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "Open file");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
        } else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        }

        try {
            startActivityForResult(chooserIntent, requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }

    public void openFile(String minmeType, int requestCode, Context c) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType(minmeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.f0x1d.notes.main.PICK_DATA");
        // if you want any file type, you can skip next line
        sIntent.putExtra("CONTENT_TYPE", minmeType);
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;
        if (c.getPackageManager().resolveActivity(sIntent, 0) != null) {
            // it is device with samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "Open file");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
        } else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        }

        try {
            startActivityForResult(chooserIntent, requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 229) {
            try {
                String path = data.getData().toString();
                getActivity().getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    last_pos = last_pos + 1;
                    NoteItem noteItem = new NoteItem(NoteItemsAdapter.getId(), rowID, null, path, last_pos, 0, 2);
                    noteItemsDao.insert(noteItem);
                    noteItems.add(last_pos, noteItem);
                } catch (IndexOutOfBoundsException e) {
                    Logger.log(e);
                }

                recyclerView.getAdapter().notifyDataSetChanged();

                dao.updateNoteTime(System.currentTimeMillis(), rowID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == 1337) {
            if (new File(currentPhotoPath).length() < 1)
                return;

            try {
                last_pos = last_pos + 1;
                NoteItem noteItem = new NoteItem(NoteItemsAdapter.getId(), rowID, null, currentPhotoPath, last_pos, 0, 0);
                noteItemsDao.insert(noteItem);
                noteItems.add(last_pos, noteItem);
            } catch (IndexOutOfBoundsException e) {
                Logger.log(e);
            }

            recyclerView.getAdapter().notifyDataSetChanged();

            dao.updateNoteTime(System.currentTimeMillis(), rowID);

        } else if (data != null && requestCode == 228) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File picture = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/" + "/pics");
                    if (!picture.exists()) {
                        picture.mkdirs();
                    }

                    File nomedia = new File(picture, ".nomedia");
                    try {
                        nomedia.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    InputStream inputStream;

                    File fleks = null;

                    try {
                        inputStream = getActivity().getContentResolver().openInputStream(data.getData());

                        fleks = new File(picture, rowID + UselessUtils.getFileName(data.getData()));

                        copy(inputStream, fleks);
                    } catch (FileNotFoundException e) {
                        Logger.log(e);
                    } catch (IOException e) {
                        Logger.log(e);
                    }

                    Logger.log("saved: " + fleks.getPath());

                    File finalFleks = fleks;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                last_pos = last_pos + 1;
                                NoteItem noteItem = new NoteItem(NoteItemsAdapter.getId(), rowID, null, finalFleks.getPath(), last_pos, 0, 0);
                                noteItemsDao.insert(noteItem);
                                add(last_pos, noteItem);
                            } catch (IndexOutOfBoundsException e) {
                                Logger.log(e);
                            }

                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });

                    dao.updateNoteTime(System.currentTimeMillis(), rowID);
                }
            }).start();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_menu, menu);

        MenuItem pic = menu.findItem(R.id.attach);
        pic.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.findItem(R.id.lock).setTitle(getString(R.string.lock));
        menu.findItem(R.id.export).setTitle(getString(R.string.export));
        menu.findItem(R.id.pin_status).setTitle(getString(R.string.pin_in_status_bar));
        menu.findItem(R.id.settings).setTitle(getString(R.string.settings));

        if (UselessUtils.ifCustomTheme()) {
            pic.setIcon(UselessUtils.setTint(getResources().getDrawable(R.drawable.ic_add_black_24dp), ThemesEngine.iconsColor));
        } else if (UselessUtils.getBool("night", false)) {
            pic.setIcon(R.drawable.ic_add_white_24dp);
        } else {
            pic.setIcon(R.drawable.ic_add_black_24dp);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public String generateName() {
        int first_number = 1;

        String name = getString(R.string.new_note);

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.is_folder == 0 && noteOrFolder.title.equals(name)) {
                name = getString(R.string.new_note) + first_number;
                first_number++;
            }
        }

        return name;
    }
}
