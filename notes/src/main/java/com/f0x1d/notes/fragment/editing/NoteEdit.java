package com.f0x1d.notes.fragment.editing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.f0x1d.notes.R;
import com.f0x1d.notes.adapter.NoteItemsAdapter;
import com.f0x1d.notes.db.daos.NoteItemsDao;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.App;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.fragment.bottom_sheet.SetNotify;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.view.CenteredToolbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NoteEdit extends Fragment {

    EditText title;
    RecyclerView recyclerView;

    FragmentActivity activity;

    String id_str;
    long id;
    int locked;

    NoteOrFolderDao dao;

    String titleStr;

    NoteItemsDao noteItemsDao;

    CenteredToolbar toolbar;

    List<NoteItem> noteItems;

    public static int last_pos;

    Bundle args;

    @Override
    public void onAttach(Activity activity) {
        this.activity = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    public static NoteEdit newInstance(Bundle args) {
        NoteEdit myFragment = new NoteEdit();
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notes_editing_layout, container, false);

        setHasOptionsMenu(true);

        if (getArguments() != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    titleStr = getArguments().getString("title");

                    id = getArguments().getLong("id");
                    id_str = String.valueOf(getArguments().getLong("id"));
                    locked = getArguments().getInt("locked");
                }
            }).start();
        }

            toolbar = v.findViewById(R.id.toolbar);

        toolbar.inflateMenu(R.menu.edit_menu);


        if (UselessUtils.ifCustomTheme()){
            toolbar.setNavigationIcon(UselessUtils.setTint(getActivity().getDrawable(R.drawable.ic_timer_black_24dp), ThemesEngine.iconsColor));
        } else if (UselessUtils.getBool("night", false)){
            toolbar.setNavigationIcon(getActivity().getDrawable(R.drawable.ic_timer_white_24dp));
        } else {
            toolbar.setNavigationIcon(getActivity().getDrawable(R.drawable.ic_timer_black_24dp));
        }

            if (getArguments().getInt("locked") == 1){
                MenuItem myItem = toolbar.getMenu().findItem(R.id.lock);
                myItem.setChecked(true);
            } else {
                MenuItem myItem = toolbar.getMenu().findItem(R.id.lock);
                myItem.setChecked(false);
            }

            toolbar.setTitle(getString(R.string.editing));

            Log.e("notes_err", "listener set");

            if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("fon", 0) == 1){
                if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_fon", false)){
                    toolbar.setTitleColor(Color.WHITE);
                    getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
                    getActivity().getWindow().setNavigationBarColor(Color.TRANSPARENT);
                } else {
                    toolbar.setTitleColor(Color.BLACK);
                    getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
                    getActivity().getWindow().setNavigationBarColor(Color.TRANSPARENT);
                }

                if (!ThemesEngine.toolbarTransparent){
                    toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
            }

        Typeface face;
        if (UselessUtils.getBool("mono", false)){
            face = Typeface.MONOSPACE;

            toolbar.setTypeFace(face);
        }

            if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("fon", 0) == 1) {
                final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
                final Drawable wallpaperDrawable = wallpaperManager.getDrawable();

                getActivity().getWindow().setBackgroundDrawable(wallpaperDrawable);
            } else {
                if (UselessUtils.ifCustomTheme()){
                    getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
                    getActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
                    getActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);

                    if (ThemesEngine.toolbarTransparent){
                        toolbar.setBackgroundColor(ThemesEngine.toolbarColor);
                    }
                }
            }

        getActivity().setActionBar(toolbar);
        return v;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NoteAdd.new_note = false;

        toolbar.setNavigationOnClickListener(v1 -> {
            NoteItem item = null;
            for (NoteItem noteItem : noteItemsDao.getAll()) {
                if (id == noteItem.to_id && noteItem.position == 0){
                    item = noteItem;
                }
            }

            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("notify_title", title.getText().toString()).putString("notify_text", item.text)
                    .putInt("notify_id", PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("id", 0)).apply();
            SetNotify notify = new SetNotify();
            notify.show(activity.getSupportFragmentManager(), "TAG");
        });

        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("in_folder_back_stack", false).apply();

        setHasOptionsMenu(true);

        getActivity().invalidateOptionsMenu();

        title = view.findViewById(R.id.edit_title);
            title.setTextSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("text_size", "15")));

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("fon", 0) == 1){
            if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_fon", false)){
                title.setTextColor(Color.WHITE);
            } else {
                title.setTextColor(Color.BLACK);
            }
        }

        Typeface face;
        if (UselessUtils.getBool("mono", false)){
            face = Typeface.MONOSPACE;

            title.setTypeface(face);
        }

        dao = App.getInstance().getDatabase().noteOrFolderDao();
        noteItemsDao = App.getInstance().getDatabase().noteItemsDao();

        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        noteItems = new ArrayList<>();

        for (NoteItem item : noteItemsDao.getAll()) {
            if (item.to_id == id){
                add(item.position, item);
            }
        }

        last_pos = noteItems.size() - 1;

        NoteItemsAdapter adapter = new NoteItemsAdapter(noteItems, getActivity());

        recyclerView.setAdapter(adapter);

        if (getArguments() != null){
            title.setText(getArguments().getString("title"));
            args = getArguments();
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
                    if (s.toString().length() == 0){
                        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_fon", false)){
                            title.setHintTextColor(Color.GRAY);
                        }
                    }

                    dao.updateNoteTitle(title.getText().toString(), id);
                    dao.updateNoteTime(System.currentTimeMillis(), id);
                }
            });
    }

    private void add(int pos, NoteItem item){
        try {
            noteItems.add(pos, item);
        } catch (IndexOutOfBoundsException e){
            add(pos - 1, item);
        }
    }

    private void remove(int pos){
        try {
            noteItems.remove(pos);
        } catch (IndexOutOfBoundsException e){
            remove(pos - 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.clear:
                title.setText("");

                dao.updateNoteTime(System.currentTimeMillis(), id);
                for (NoteItem noteItem : noteItemsDao.getAll()) {
                    if (noteItem.to_id == id){
                        if (noteItem.position != 0){
                            noteItemsDao.deleteItem(noteItem.id);
                            remove(noteItem.position);
                            recyclerView.getAdapter().notifyItemRemoved(noteItem.position);
                        } else {
                            noteItemsDao.updateElementText("", noteItem.id);
                            recyclerView.getAdapter().notifyItemChanged(noteItem.position);
                        }
                    }
                }
                break;
            /*case R.id.export:
                File noteDir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/" + "/Exported notes");
                if (!noteDir.exists()){
                    noteDir.mkdirs();
                }
                File note = new File(noteDir, title.getText().toString() + ".txt");
                try {
                    FileWriter writer = new FileWriter(note);
                    writer.append(text.getText().toString());
                    writer.flush();
                    writer.close();
                    Toast.makeText(getActivity(), getString(R.string.saved) + " " + note.getAbsolutePath(), Toast.LENGTH_LONG).show();

                    if (text.getText().toString().toLowerCase().contains("желе") || title.getText().toString().toLowerCase().contains("желе")){
                        Snackbar.make(getView(), "Желе лох", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                break;*/
            case R.id.attach:
                String[] items = new String[]{getString(R.string.pic)};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                openFile("image/*", 228, getActivity());
                                break;
                        }
                    }
                });

                builder.show();
                break;
            case R.id.lock:
                if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("lock", false)){
                    if (item.isChecked()){
                        item.setChecked(false);
                    } else {
                        item.setChecked(true);
                    }

                    if (item.isChecked()){
                        locked = 1;
                        dao.updateNoteLocked(1, id);
                    } else {
                        locked = 0;
                        dao.updateNoteLocked(0, id);
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.enable_pin), Toast.LENGTH_SHORT).show();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openFile(String minmeType, int requestCode, Context c) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(minmeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.f0x1d.talkandtools.main.PICK_DATA");
        // if you want any file type, you can skip next line
        sIntent.putExtra("CONTENT_TYPE", minmeType);
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;
        if (c.getPackageManager().resolveActivity(sIntent, 0) != null){
            // it is device with samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "Open file");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intent});
        }
        else {
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
        if (data != null && requestCode == 228){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File picture = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/" + "/pics");
                    if (!picture.exists()){
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

                        fleks = new File(picture, id + UselessUtils.getFileName(data.getData()));

                        copy(inputStream, fleks);
                    } catch (FileNotFoundException e) {
                        Log.e("notes_err", e.getLocalizedMessage());
                    } catch (IOException e) {
                        Log.e("notes_err", e.getLocalizedMessage());
                    }

                    Log.e("notes_err", "saved: " + fleks.getPath());

                    try {
                        last_pos = last_pos + 1;
                        NoteItem noteItem = new NoteItem(id, null, fleks.getPath(), last_pos);
                        noteItems.add(last_pos, noteItem);
                        noteItemsDao.insert(noteItem);

                        last_pos = last_pos + 1;
                        NoteItem noteItem2 = new NoteItem(id, "", null, last_pos);
                        noteItems.add(last_pos, noteItem2);
                        noteItemsDao.insert(noteItem2);

                        Log.e("notes_err", "added to: " + noteItem2.position);
                    } catch (IndexOutOfBoundsException e){
                        Log.e("notes_err", e.getLocalizedMessage());
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });

                    dao.updateNoteTime(System.currentTimeMillis(), id);
                }
            }).start();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void copy(InputStream in, File dst) throws IOException {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_menu, menu);

        if (getArguments().getInt("locked") == 1){
            MenuItem myItem = menu.findItem(R.id.lock);
            myItem.setChecked(true);
        } else {
            MenuItem myItem = menu.findItem(R.id.lock);
            myItem.setChecked(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }
}
