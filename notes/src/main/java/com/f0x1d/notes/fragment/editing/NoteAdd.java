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
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.bottom_sheet.SetNotify;
import com.f0x1d.notes.App;
import com.f0x1d.notes.db.daos.FormatDao;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.Format;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.view.CenteredToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import static com.f0x1d.notes.fragment.editing.NoteEdit.copy;

public class NoteAdd extends Fragment {

    EditText title;
    EditText text;
    ImageView pic;

    long rowID;

    TextToSpeech mTTS;

    FragmentActivity activity;

    NoteOrFolderDao dao;
    FormatDao formatDao;

    boolean allowFormat;

    CenteredToolbar toolbar;

    ImageButton attach;
    RelativeLayout attach_layout;

    @Override
    public void onAttach(Activity activity) {
        this.activity = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notes_edit, container, false);

        toolbar = v.findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.new_note));

        getActivity().setActionBar(toolbar);

        allowFormat = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("format", false);

        if (!PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("force_save", false)){
            if (allowFormat){
                toolbar.inflateMenu(R.menu.edit_menu);
            } else {
                toolbar.inflateMenu(R.menu.edit_menu_no_format);
            }

            if (UselessUtils.ifCustomTheme()){
                toolbar.setNavigationIcon(UselessUtils.setTint(getActivity().getDrawable(R.drawable.ic_timer_black_24dp), ThemesEngine.iconsColor));
            } else if (UselessUtils.getBool("night", false)){
                toolbar.setNavigationIcon(getActivity().getDrawable(R.drawable.ic_timer_white_24dp));
            } else {
                toolbar.setNavigationIcon(getActivity().getDrawable(R.drawable.ic_timer_black_24dp));
            }

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("notify_title", title.getText().toString()).putString("notify_text", text.getText().toString())
                            .putInt("notify_id", PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("id", 0)).apply();
                    SetNotify notify = new SetNotify();
                    notify.show(activity.getSupportFragmentManager(), "TAG");
                }
            });
        }

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

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("fon", 0) == 1){
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
        return v;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dao = App.getInstance().getDatabase().noteOrFolderDao();
        formatDao = App.getInstance().getDatabase().formatDao();

        setHasOptionsMenu(true);

        getActivity().invalidateOptionsMenu();

        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("in_folder_back_stack", false).apply();

        title = view.findViewById(R.id.edit_title);
            title.setTextSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("text_size", "15")));
        text = view.findViewById(R.id.edit_text);
            text.setTextSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("text_size", "15")));
        pic = view.findViewById(R.id.picture);
            pic.setVisibility(View.GONE);


        UselessUtils.showKeyboard(title, getActivity());

        attach = view.findViewById(R.id.attach);
        attach_layout = view.findViewById(R.id.attach_layout);

        if (UselessUtils.ifCustomTheme()){
            attach.setImageDrawable(UselessUtils.setTint(getActivity().getDrawable(R.drawable.ic_attach_file_black_24dp), ThemesEngine.iconsColor));
        } else if (UselessUtils.getBool("night", false)){
            attach.setImageDrawable(getActivity().getDrawable(R.drawable.ic_attach_file_white_24dp));
        } else {
            attach.setImageDrawable(getActivity().getDrawable(R.drawable.ic_attach_file_black_24dp));
        }

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("fon", 0) == 1){
            if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_fon", false)){
                title.setTextColor(Color.WHITE);
                text.setTextColor(Color.WHITE);
            } else {
                title.setTextColor(Color.BLACK);
                text.setTextColor(Color.BLACK);
            }
        }

        Typeface face;
        if (UselessUtils.getBool("mono", false)){
            face = Typeface.MONOSPACE;

            title.setTypeface(face);
            text.setTypeface(face);
        }

        if (!PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("force_save", false)){
            rowID = dao.insert(new NoteOrFolder(generateName(), text.getText().toString(), 0, 0, PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("in_folder_id", "def"),
                    0, null, 0, "", System.currentTimeMillis(), null));

            attach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] items = null;

                    if (pic.getDrawable() != null){
                        items = new String[]{getString(R.string.edit_pic)};
                    } else {
                        items = new String[]{getString(R.string.attach)};
                    }

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
                }
            });
        } else {
            attach_layout.setVisibility(View.GONE);
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

                if (!PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("force_save", false)){
                    dao.updateNoteTitle(title.getText().toString(), rowID);
                    dao.updateNoteText(text.getText().toString(), rowID);
                    dao.updateNoteTime(System.currentTimeMillis(), rowID);
                }
            }
        });

        text.addTextChangedListener(new TextWatcher() {
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
                        text.setHintTextColor(Color.GRAY);
                    }
                }

                if (!PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("force_save", false)){
                    dao.updateNoteTitle(title.getText().toString(), rowID);
                    dao.updateNoteText(text.getText().toString(), rowID);
                    dao.updateNoteTime(System.currentTimeMillis(), rowID);
                }
            }
        });

        mTTS = new TextToSpeech(view.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(new Locale("ru"));

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        ImageButton save = view.findViewById(R.id.force_save);

        if (UselessUtils.ifCustomTheme()){
            save.setImageDrawable(UselessUtils.setTint(getActivity().getDrawable(R.drawable.ic_done_black_24dp), ThemesEngine.iconsColor));
        } else if (UselessUtils.getBool("night", false)){
            save.setImageDrawable(getActivity().getDrawable(R.drawable.ic_done_white_24dp));
        } else {
            save.setImageDrawable(getActivity().getDrawable(R.drawable.ic_done_black_24dp));
        }

            if (!PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("force_save", false)){
                save.setVisibility(View.GONE);
            }

            save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowID = dao.insert(new NoteOrFolder(title.getText().toString(), text.getText().toString(), 0, 0, PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("in_folder_id", "def"),
                        0, null, 0, "", System.currentTimeMillis(), null));

                getFragmentManager().popBackStack();

                }
            });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.clear:
                title.setText("");
                text.setText("");

                dao.updateNotePic(null, rowID);
                pic.setVisibility(View.GONE);

                dao.updateNoteTime(System.currentTimeMillis(), rowID);
                break;
            case R.id.speak:
                mTTS.speak(text.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.export:
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
                    Toast.makeText(getActivity(), getString(R.string.saved) + note.getAbsolutePath(), Toast.LENGTH_LONG).show();

                    if (text.getText().toString().toLowerCase().contains("желе") || title.getText().toString().toLowerCase().contains("желе")){
                        Snackbar.make(getView(), "Желе лох", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.lock:
                if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("lock", false)){
                    if (item.isChecked()){
                        item.setChecked(false);
                    } else {
                        item.setChecked(true);
                    }

                    if (item.isChecked()){
                        dao.updateNoteLocked(1, rowID);
                    } else {
                        dao.updateNoteLocked(0, rowID);
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.enable_pin), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clear_format:
                if (title.hasSelection()){
                    for (Format format : formatDao.getAll()) {
                        if (title.getSelectionStart() == format.start_position || title.getSelectionEnd() == format.end_position){
                            formatDao.deleteByStartPosition(format.start_position);
                            formatDao.deleteByEndPosition(format.end_position);
                        }
                    }

                    Toast.makeText(getActivity(), R.string.success, Toast.LENGTH_SHORT).show();
                } else if (text.hasSelection()){
                    for (Format format : formatDao.getAll()) {
                        if (text.getSelectionStart() == format.start_position || text.getSelectionEnd() == format.end_position){
                            formatDao.deleteByStartPosition(format.start_position);
                            formatDao.deleteByEndPosition(format.end_position);
                        }
                    }

                    Toast.makeText(getActivity(), R.string.success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.format_error, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.bold:
                if (title.hasSelection()){
                    title.getText().setSpan(new StyleSpan(Typeface.BOLD), title.getSelectionStart(), title.getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    for (Format format : formatDao.getAll()) {
                        if (title.getSelectionStart() == format.start_position || title.getSelectionEnd() == format.end_position){
                            formatDao.deleteByStartPosition(format.start_position);
                            formatDao.deleteByEndPosition(format.end_position);
                        }
                    }

                    formatDao.insert(new Format(0, title.getSelectionStart(), title.getSelectionEnd(), "bold", true, rowID));
                } else if (text.hasSelection()){
                    text.getText().setSpan(new StyleSpan(Typeface.BOLD), text.getSelectionStart(), text.getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    for (Format format : formatDao.getAll()) {
                        if (text.getSelectionStart() == format.start_position || text.getSelectionEnd() == format.end_position){
                            formatDao.deleteByStartPosition(format.start_position);
                            formatDao.deleteByEndPosition(format.end_position);
                        }
                    }

                    formatDao.insert(new Format(0, text.getSelectionStart(), text.getSelectionEnd(), "bold", false, rowID));
                } else {
                    Toast.makeText(getActivity(), R.string.format_error, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.italic:

                if (title.hasSelection()){
                    title.getText().setSpan(new StyleSpan(Typeface.ITALIC), title.getSelectionStart(), title.getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    for (Format format : formatDao.getAll()) {
                        if (title.getSelectionStart() == format.start_position || title.getSelectionEnd() == format.end_position){
                            formatDao.deleteByStartPosition(format.start_position);
                            formatDao.deleteByEndPosition(format.end_position);
                        }
                    }

                    formatDao.insert(new Format(0, title.getSelectionStart(), title.getSelectionEnd(), "italic", true, rowID));
                } else if (text.hasSelection()){
                    text.getText().setSpan(new StyleSpan(Typeface.ITALIC), text.getSelectionStart(), text.getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    for (Format format : formatDao.getAll()) {
                        if (text.getSelectionStart() == format.start_position || text.getSelectionEnd() == format.end_position){
                            formatDao.deleteByStartPosition(format.start_position);
                            formatDao.deleteByEndPosition(format.end_position);
                        }
                    }

                    formatDao.insert(new Format(0, text.getSelectionStart(), text.getSelectionEnd(), "italic", false, rowID));
                } else {
                    Toast.makeText(getActivity(), R.string.format_error, Toast.LENGTH_SHORT).show();
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

                        fleks = new File(picture, rowID + UselessUtils.getFileName(data.getData()));

                        copy(inputStream, fleks);
                    } catch (FileNotFoundException e) {
                        Log.e("notes_err", e.getLocalizedMessage());
                    } catch (IOException e) {
                        Log.e("notes_err", e.getLocalizedMessage());
                    }

                    Log.e("notes_err", "saved: " + fleks.getPath());

                    dao.updateNotePic(fleks.getPath(), rowID);

                    dao.updateNoteTime(System.currentTimeMillis(), rowID);

                    Drawable d = Drawable.createFromPath(fleks.getPath());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pic.setVisibility(View.VISIBLE);
                            pic.setImageDrawable(d);
                        }
                    });
                }
            }).start();

            pic.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    delete();
                    return false;
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void delete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle(R.string.confirm_delete);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dao.updateNotePic(null, rowID);
                pic.setVisibility(View.GONE);
                pic.setImageDrawable(null);

                dao.updateNoteTime(System.currentTimeMillis(), rowID);
            }
        });

        builder.setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog1337 =  builder.create();

        dialog1337.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog1) {
                if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", false)){
                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    dialog1337.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
                }
                if (UselessUtils.ifCustomTheme()){
                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ThemesEngine.textColor);
                    dialog1337.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(ThemesEngine.textColor);

                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                    dialog1337.getButton(DialogInterface.BUTTON_NEUTRAL).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                }
            }
        });

        dialog1337.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("force_save", false)){
            if (allowFormat){
                inflater.inflate(R.menu.edit_menu, menu);
            } else {
                inflater.inflate(R.menu.edit_menu_no_format, menu);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public String generateName(){
        int first_number = 1;

        String name = getString(R.string.new_note);

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.is_folder == 0 && noteOrFolder.title.equals(name)){
                name = getString(R.string.new_note) + first_number;
                first_number++;
            }
        }

        return name;
    }
}
