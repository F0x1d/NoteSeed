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
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.LayoutInflater;
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
import com.f0x1d.notes.adapter.NoteItemsAdapter;
import com.f0x1d.notes.db.Database;
import com.f0x1d.notes.db.daos.NoteItemsDao;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.db.entities.Notify;
import com.f0x1d.notes.fragment.bottomSheet.SetNotifyDialog;
import com.f0x1d.notes.fragment.main.NotesFragment;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.bottomSheet.BottomSheetCreator;
import com.f0x1d.notes.utils.bottomSheet.Element;
import com.f0x1d.notes.utils.dialogs.ShowAlertDialog;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.view.CenteredToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class NoteEditFragment extends Fragment {

    public NoteItemsAdapter adapter;

    private EditText title;
    private RecyclerView recyclerView;
    private CenteredToolbar toolbar;

    private long id;
    public boolean newNote;
    private int locked;
    private String titleStr;
    private String inFolderId;

    private NoteOrFolderDao dao;
    private NoteItemsDao noteItemsDao;
    private List<NoteItem> noteItems;

    public boolean editMode = false;
    private boolean pinned = false;
    private String currentPhotoPath;

    public static NoteEditFragment newInstance(boolean newNote, long id, String inFolderId) {
        Bundle args = new Bundle();
        args.putLong("id", id);
        if (id != 0) {
            args.putString("title", App.getInstance().getDatabase().noteOrFolderDao().getById(id).title);
            args.putInt("locked", App.getInstance().getDatabase().noteOrFolderDao().getById(id).locked);
        }
        args.putBoolean("new", newNote);
        args.putString("in_id", inFolderId);

        NoteEditFragment myFragment = new NoteEditFragment();
        myFragment.setArguments(args);
        return myFragment;
    }

    public static void copy(InputStream in, File dst) throws IOException {
        try (OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notes_editing_layout, container, false);

        if (getArguments() != null) {
            newNote = getArguments().getBoolean("new");
            if (!newNote) {
                titleStr = getArguments().getString("title");
                id = getArguments().getLong("id");
                locked = getArguments().getInt("locked");
            } else {
                editMode = true;
                titleStr = "";
                locked = 0;
                id = 0;
                inFolderId = getArguments().getString("in_id");
            }
        }

        pinned = requireActivity().getSharedPreferences("notifications", Context.MODE_PRIVATE).getBoolean("note " + id, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.edit_menu);

        toolbar.getMenu().findItem(R.id.lock).setTitle(getString(R.string.lock));
        toolbar.getMenu().findItem(R.id.export).setTitle(getString(R.string.export));
        toolbar.getMenu().findItem(R.id.pin_status).setTitle(getString(R.string.pin_in_status_bar));
        for (int i = 0; i < toolbar.getMenu().size(); i++) {
            toolbar.getMenu().getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    onOptionsItemSelected(item);
                    return false;
                }
            });
        }

        if (UselessUtils.isCustomTheme()) {
            toolbar.setNavigationIcon(UselessUtils.setTint(requireActivity().getDrawable(R.drawable.ic_timer_black_24dp), ThemesEngine.iconsColor));
        } else if (UselessUtils.getBool("night", false)) {
            toolbar.setNavigationIcon(requireActivity().getDrawable(R.drawable.ic_timer_white_24dp));
        } else {
            toolbar.setNavigationIcon(requireActivity().getDrawable(R.drawable.ic_timer_black_24dp));
        }

        if (locked == 1) {
            toolbar.getMenu().findItem(R.id.lock).setChecked(true);
        } else {
            toolbar.getMenu().findItem(R.id.lock).setChecked(false);
        }

        if (pinned)
            toolbar.getMenu().findItem(R.id.pin_status).setTitle(getString(R.string.unpin_from_status_bar));

        MenuItem pic = toolbar.getMenu().findItem(R.id.attach);
        pic.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        if (UselessUtils.isCustomTheme()) {
            pic.setIcon(UselessUtils.setTint(getResources().getDrawable(R.drawable.ic_add_black_24dp), ThemesEngine.iconsColor));
        } else if (UselessUtils.getBool("night", false)) {
            pic.setIcon(R.drawable.ic_add_white_24dp);
        } else {
            pic.setIcon(R.drawable.ic_add_black_24dp);
        }

        toolbar.setTitle(getString(R.string.checking));

        if (UselessUtils.isCustomTheme()) {
            requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            requireActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            requireActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);

            toolbar.setBackgroundColor(ThemesEngine.toolbarColor);
        }

        toolbar.setNavigationOnClickListener(v1 -> {
            NoteItem item = noteItemsDao.getAllByToId(id).get(0);

            SetNotifyDialog notify = new SetNotifyDialog(new Notify(title.getText().toString(), item.text, 0, item.toId));
            notify.show(requireActivity().getSupportFragmentManager(), "TAG");
        });

        title = view.findViewById(R.id.edit_title);
        title.setTextSize(Integer.parseInt(App.getDefaultSharedPreferences().getString("text_size", "15")));

        if (UselessUtils.getBool("mono", false)) {
            title.setTypeface(Typeface.MONOSPACE);
            BetterLinkMovementMethod.linkify(Linkify.ALL, title);
        }

        dao = App.getInstance().getDatabase().noteOrFolderDao();
        noteItemsDao = App.getInstance().getDatabase().noteItemsDao();

        if (newNote) {
            id = dao.insert(new NoteOrFolder(generateName(), null, NotesFragment.genId(), 0, inFolderId,
                    0, null, 0, "", System.currentTimeMillis(), Database.getLastPosition(inFolderId)));

            noteItemsDao.insert(new NoteItem(NoteItemsAdapter.getId(), id, "", null, 0, 0, 0));
        }

        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        noteItems = new ArrayList<>();
        noteItems.addAll(noteItemsDao.getAllByToId(id));

        adapter = new NoteItemsAdapter(noteItems, requireActivity(), this, false);
        recyclerView.setAdapter(adapter);

        title.setText(Html.fromHtml(titleStr.replace("\n", "<br />")));
        title.setHint(getString(R.string.title));

        title.setFocusableInTouchMode(false);
        title.setFocusable(false);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterEditMode();
            }
        });

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dao.updateNoteTitle(title.getText().toString(), id);
                dao.updateNoteTime(System.currentTimeMillis(), id);
            }
        });

        if (App.getDefaultSharedPreferences().getBoolean("auto_editmode", false) || newNote)
            editModeSetup();

        return view;
    }

    public String generateName() {
        int first_number = 1;

        String name = getString(R.string.new_note);

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.isFolder == 0 && noteOrFolder.title.equals(name)) {
                name = getString(R.string.new_note) + first_number;
                first_number++;
            }
        }

        return name;
    }

    public void enterEditMode() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(getString(R.string.warning));
        builder.setMessage(getString(R.string.enter_edit_mode));
        builder.setPositiveButton(getString(R.string.enter), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editModeSetup();
            }
        });
        ShowAlertDialog.show(builder);
    }

    private void editModeSetup() {
        editMode = true;
        recyclerView.setAdapter(adapter = new NoteItemsAdapter(noteItems, requireActivity(), this, true));

        toolbar.setTitle(getString(R.string.editing));

        attachHelper();

        title.setText(titleStr);
        title.setFocusableInTouchMode(true);
        title.setFocusable(true);
        title.setOnClickListener(null);
    }

    private void attachHelper() {
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
                    ((NoteItemsAdapter) recyclerView.getAdapter()).onItemMoved(fromPosition, toPosition);
                else
                    Toast.makeText(requireActivity(), "Nope.", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
                if (viewHolder.getPosition() == 0) {
                    recyclerView.getAdapter().notifyItemChanged(viewHolder.getPosition());
                    Toast.makeText(requireActivity(), "Nope.", Toast.LENGTH_SHORT).show();
                } else
                    ((NoteItemsAdapter) recyclerView.getAdapter()).delete(viewHolder.getPosition(), getView());
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
    }

    private void addThisTODOs() {
        View v = LayoutInflater.from(requireActivity()).inflate(R.layout.text_size_layout, null);

        SeekBar seekBar = v.findViewById(R.id.text_size);
        seekBar.setMax(20);
        TextView textView = v.findViewById(R.id.text);
        textView.setTextSize(30);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
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
                    NoteItem noteItem = new NoteItem(NoteItemsAdapter.getId(), id, "", null, adapter.getItemCount(), 0, 1);
                    noteItemsDao.insert(noteItem);
                    noteItems.add(noteItem);
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
                View v = LayoutInflater.from(requireActivity()).inflate(R.layout.export_file_type_dialog, null);
                EditText text = v.findViewById(R.id.extension);
                ((TextView) v.findViewById(R.id.text)).setText(getString(R.string.export_file_type));

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
                builder.setView(v);
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
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

                            for (NoteItem noteItem : noteItemsDao.getAllByToId(id)) {
                                if (noteItem.picRes == null) {
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
                            Toast.makeText(requireActivity(), getString(R.string.saved) + " " + note.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Logger.log(e);
                        }
                    }
                });
                ShowAlertDialog.show(builder);

                break;
            case R.id.attach:
                if (!editMode) {
                    enterEditMode();
                    break;
                }

                BottomSheetCreator creator = new BottomSheetCreator(requireActivity());
                creator.addElement(new Element(getString(R.string.text), requireActivity().getDrawable(R.drawable.ic_text_fields_white_24dp), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NoteItem noteItem = new NoteItem(NoteItemsAdapter.getId(), id, "", null, adapter.getItemCount(), 0, 0);
                        noteItemsDao.insert(noteItem);
                        noteItems.add(noteItem);

                        recyclerView.getAdapter().notifyDataSetChanged();

                        creator.customBottomSheet.dismiss();
                    }
                }));
                creator.addElement(new Element(getString(R.string.picture), requireActivity().getDrawable(R.drawable.ic_image_white_24dp), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openChoosePicture();

                        creator.customBottomSheet.dismiss();
                    }
                }));
                creator.addElement(new Element(getString(R.string.item_checkbox), requireActivity().getDrawable(R.drawable.ic_work_white_24dp), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addThisTODOs();

                        creator.customBottomSheet.dismiss();
                    }
                }));
                creator.addElement(new Element(getString(R.string.file), requireActivity().getDrawable(R.drawable.ic_insert_drive_file_white_24dp), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFile("*/*", 229, requireActivity());

                        creator.customBottomSheet.dismiss();
                    }
                }));
                creator.show("", true);
                break;
            case R.id.lock:
                if (!editMode) {
                    enterEditMode();
                    break;
                }

                if (App.getDefaultSharedPreferences().getBoolean("lock", false)) {
                    if (item.isChecked()) {
                        item.setChecked(false);
                    } else {
                        item.setChecked(true);
                    }

                    if (item.isChecked()) {
                        locked = 1;
                        dao.updateNoteLocked(1, id);
                    } else {
                        locked = 0;
                        dao.updateNoteLocked(0, id);
                    }
                } else {
                    Toast.makeText(requireActivity(), getString(R.string.enable_pin), Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.pin_status:
                NotificationManager manager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                if (pinned) {
                    manager.cancel((int) id + 1);
                    toolbar.getMenu().findItem(R.id.pin_status).setTitle(getString(R.string.pin_in_status_bar));
                    pinned = false;
                    requireActivity().getSharedPreferences("notifications", Context.MODE_PRIVATE).edit().putBoolean("note " + id, false).apply();
                    break;
                }

                NoteItem flexNoteItem = noteItemsDao.getAllByToId(id).get(0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String name = getString(R.string.notification);
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("com.f0x1d.notes.notifications", name, importance);
                    channel.enableVibration(true);
                    channel.enableLights(true);
                    NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

                Notification.Builder notifBuilder = new Notification.Builder(requireActivity());
                notifBuilder.setSmallIcon(R.drawable.ic_notifications_active_black_24dp);
                notifBuilder.setContentTitle(Html.fromHtml(title.getText().toString().replace("\n", "<br />")));
                notifBuilder.setContentText(Html.fromHtml(flexNoteItem.text.replace("\n", "<br />")));
                notifBuilder.setOngoing(true);
                notifBuilder.setStyle(new Notification.BigTextStyle().bigText(Html.fromHtml(flexNoteItem.text.replace("\n", "<br />"))));
                notifBuilder.setContentIntent(PendingIntent.getActivity(App.getContext(), 228, new Intent(App.getContext(), MainActivity.class)
                        .putExtra("id", id).putExtra("title", title.getText().toString()), 0));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    notifBuilder.setChannelId("com.f0x1d.notes.notifications");

                manager.notify((int) id + 1, notifBuilder.build());

                toolbar.getMenu().findItem(R.id.pin_status).setTitle(getString(R.string.unpin_from_status_bar));
                pinned = true;

                requireActivity().getSharedPreferences("notifications", Context.MODE_PRIVATE).edit().putBoolean("note " + id, true).apply();
                break;
            case R.id.bold:
                if (!adapter.applyFormat("bold", null))
                    Toast.makeText(requireContext(), R.string.pls_select_text, Toast.LENGTH_SHORT).show();
                break;
            case R.id.italic:
                if (!adapter.applyFormat("italic", null))
                    Toast.makeText(requireContext(), R.string.pls_select_text, Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openChoosePicture() {
        BottomSheetCreator creator = new BottomSheetCreator(requireActivity());
        creator.addElement(new Element(getString(R.string.camera), getResources().getDrawable(R.drawable.ic_camera_alt_white_24dp), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Logger.log(ex);
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                        if (Build.VERSION.SDK_INT >= 24) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        }
                        startActivityForResult(takePictureIntent, 1337);
                        Logger.log("started intent!");
                    }
                }
            }
        }));
        creator.addElement(new Element(getString(R.string.gallery), getResources().getDrawable(R.drawable.ic_image_white_24dp), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage("image/*", 228, requireActivity());
            }
        }));
        creator.show("TAG", true);
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
            Toast.makeText(requireActivity(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
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
            Toast.makeText(requireActivity(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.log("got onActivityResult");

        if (resultCode == Activity.RESULT_OK && requestCode == 229) {
            try {
                String path = data.getData().toString();
                requireActivity().getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    NoteItem noteItem = new NoteItem(NoteItemsAdapter.getId(), id, null, path, adapter.getItemCount(), 0, 2);
                    noteItemsDao.insert(noteItem);
                    noteItems.add(noteItem);
                } catch (IndexOutOfBoundsException e) {
                    Logger.log(e);
                }

                recyclerView.getAdapter().notifyDataSetChanged();

                dao.updateNoteTime(System.currentTimeMillis(), id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == 1337) {
            if (new File(currentPhotoPath).length() < 10)
                return;

            try {
                NoteItem noteItem = new NoteItem(NoteItemsAdapter.getId(), id, null, currentPhotoPath, adapter.getItemCount(), 0, 0);
                noteItemsDao.insert(noteItem);
                noteItems.add(noteItem);
            } catch (IndexOutOfBoundsException e) {
                Logger.log(e);
            }

            recyclerView.getAdapter().notifyDataSetChanged();

            dao.updateNoteTime(System.currentTimeMillis(), id);
        }

        if (data != null && requestCode == 228) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File picture = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/" + "/pics");
                    if (!picture.exists()) {
                        picture.mkdirs();
                    }

                    File nomedia = new File(picture, ".nomedia");
                    try {
                        nomedia.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    File fleks = null;
                    try {
                        fleks = new File(picture, id + UselessUtils.getFileName(data.getData()));
                        copy(Objects.requireNonNull(requireActivity().getContentResolver().openInputStream(data.getData())), fleks);
                    } catch (IOException e) {
                        Logger.log(e);
                    }

                    Logger.log("saved: " + fleks.getPath());

                    File finalFleks = fleks;
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                NoteItem noteItem = new NoteItem(NoteItemsAdapter.getId(), id, null, finalFleks.getPath(), adapter.getItemCount(), 0, 0);
                                noteItemsDao.insert(noteItem);
                                noteItems.add(noteItem);
                            } catch (IndexOutOfBoundsException e) {
                                Logger.log(e);
                            }

                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });

                    dao.updateNoteTime(System.currentTimeMillis(), id);
                }
            }).start();
        }
    }
}
