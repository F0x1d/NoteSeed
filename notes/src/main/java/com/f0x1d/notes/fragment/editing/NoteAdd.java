package com.f0x1d.notes.fragment.editing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
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
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.bottomSheet.BottomSheetCreator;
import com.f0x1d.notes.utils.bottomSheet.Element;
import com.f0x1d.notes.utils.dialogs.ShowAlertDialog;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.view.CenteredToolbar;

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

    public static NoteAdd newInstance(String in_folder_id) {

        Bundle args = new Bundle();
        args.putString("id", in_folder_id);

        NoteAdd fragment = new NoteAdd();
        fragment.setArguments(args);
        return fragment;
    }

    EditText title;
    RecyclerView recyclerView;

    long rowID;

    NoteOrFolderDao dao;
    NoteItemsDao noteItemsDao;

    CenteredToolbar toolbar;

    List<NoteItem> noteItems;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notes_editing_layout, container, false);

        id = getArguments().getString("id");

        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.new_note));

        getActivity().setActionBar(toolbar);

        toolbar.inflateMenu(R.menu.edit_menu);

        if (UselessUtils.ifCustomTheme()) {
            toolbar.setNavigationIcon(UselessUtils.setTint(getActivity().getDrawable(R.drawable.ic_timer_black_24dp), ThemesEngine.iconsColor));
        } else if (UselessUtils.getBool("night", true)) {
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
        } else if (UselessUtils.getBool("night", true)) {
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
        return v;
    }

    @SuppressLint({"RestrictedApi", "WrongConstant"})
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dao = App.getInstance().getDatabase().noteOrFolderDao();
        rowID = dao.insert(new NoteOrFolder(generateName(), null, Notes.genId(), 0, id,
                0, null, 0, "", System.currentTimeMillis(), Database.getLastPosition(id)));

        noteItemsDao = App.getInstance().getDatabase().noteItemsDao();

        noteItemsDao.insert(new NoteItem(NoteItemsAdapter.getId(), rowID, "", null, 0, 0, 0));
        last_pos = 0;

        setHasOptionsMenu(true);

        getActivity().invalidateOptionsMenu();

        title = view.findViewById(R.id.edit_title);
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

        NoteItemsAdapter adapter = new NoteItemsAdapter(noteItems, getActivity());

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
                    adapter.delete(viewHolder.getPosition());
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < seekBar.getProgress(); i++) {
                    last_pos = last_pos + 1;
                    NoteItem noteItem2 = new NoteItem(NoteItemsAdapter.getId(), rowID, "", null, last_pos, 0, 1);
                    noteItemsDao.insert(noteItem2);
                    add(last_pos, noteItem2);

                    Log.e("notes_err", "last pos: " + last_pos);
                }

                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        ShowAlertDialog.show(builder.create());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export:
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.export_file_type_dialog, null);
                EditText text = v.findViewById(R.id.extension);

                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                builder2.setView(v);
                builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                ShowAlertDialog.show(builder2.create());

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
        }

        return super.onOptionsItemSelected(item);
    }

    private void openChoosePicture(){
        BottomSheetCreator creator = new BottomSheetCreator(getActivity());
        creator.addElement(new Element(getString(R.string.camera), getResources().getDrawable(R.drawable.ic_camera_alt_white_24dp), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    creator.customBottomSheet.dismiss();
                } catch (Exception e) {}

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Toast.makeText(getActivity(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.f0x1d.notes.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
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
                } catch (Exception e) {}

                openFile("image/*", 228, getActivity());
            }
        }));
        creator.show("TAG", true);
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/" + "/pics");
        if (!storageDir.exists())
            storageDir.mkdirs();
        File image = new File(storageDir, imageFileName + ".jpg");
        image.createNewFile();

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void openFile(String minmeType, int requestCode, Context c) {

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == 1337){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (new File(currentPhotoPath).length() < 10)
                        return;

                    try {
                        last_pos = last_pos + 1;
                        NoteItem noteItem = new NoteItem(NoteItemsAdapter.getId(), rowID, null, currentPhotoPath, last_pos, 0, 0);
                        noteItemsDao.insert(noteItem);
                        noteItems.add(last_pos, noteItem);
                    } catch (IndexOutOfBoundsException e) {
                        Log.e("notes_err", e.getLocalizedMessage());
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });

                    dao.updateNoteTime(System.currentTimeMillis(), rowID);
                }
            }).start();
        }

        if (data != null && requestCode == 228) {
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
                        Log.e("notes_err", e.getLocalizedMessage());
                    } catch (IOException e) {
                        Log.e("notes_err", e.getLocalizedMessage());
                    }

                    Log.e("notes_err", "saved: " + fleks.getPath());

                    try {
                        last_pos = last_pos + 1;
                        NoteItem noteItem = new NoteItem(NoteItemsAdapter.getId(), rowID, null, fleks.getPath(), last_pos, 0, 0);
                        noteItemsDao.insert(noteItem);
                        add(last_pos, noteItem);
                    } catch (IndexOutOfBoundsException e) {
                        Log.e("notes_err", e.getLocalizedMessage());
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });

                    dao.updateNoteTime(System.currentTimeMillis(), rowID);
                }
            }).start();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_menu, menu);

        MenuItem pic = menu.findItem(R.id.attach);

        if (UselessUtils.ifCustomTheme()) {
            pic.setIcon(UselessUtils.setTint(getResources().getDrawable(R.drawable.ic_add_black_24dp), ThemesEngine.iconsColor));
        } else if (UselessUtils.getBool("night", true)) {
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
