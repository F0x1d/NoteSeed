package com.f0x1d.notes.fragment.main;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.fragment.editing.NoteAdd;
import com.f0x1d.notes.fragment.search.Search;
import com.f0x1d.notes.App;
import com.f0x1d.notes.adapter.ItemsAdapter;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.view.CenteredToolbar;
import com.f0x1d.notes.view.theming.MyButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.f0x1d.notes.utils.UselessUtils.getFileName;

public class Notes extends Fragment {

    public static RecyclerView recyclerView;

    static List<NoteOrFolder> allList;

    TextView nothing;

    CenteredToolbar toolbar;

    NoteOrFolderDao dao;

    ItemsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notes_layout, container, false);

        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.notes));
        toolbar.inflateMenu(R.menu.search_menu);
        toolbar.getMenu().findItem(R.id.settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        if (UselessUtils.getBool("night", false)){
            if (UselessUtils.ifCustomTheme()){
                toolbar.setNavigationIcon(UselessUtils.setTint(getResources().getDrawable(R.drawable.ic_search_white_24dp), ThemesEngine.iconsColor));
                toolbar.getMenu().findItem(R.id.settings).setIcon(UselessUtils.setTint(getResources().getDrawable(R.drawable.ic_settings_white_24dp), ThemesEngine.iconsColor));
            } else {
                toolbar.getMenu().findItem(R.id.settings).setIcon(R.drawable.ic_settings_white_24dp);
                toolbar.setNavigationIcon(R.drawable.ic_search_white_24dp);
            }
        } else {
            if (UselessUtils.ifCustomTheme()){
                toolbar.setNavigationIcon(UselessUtils.setTint(getResources().getDrawable(R.drawable.ic_search_black_24dp), ThemesEngine.iconsColor));
                toolbar.getMenu().findItem(R.id.settings).setIcon(UselessUtils.setTint(getResources().getDrawable(R.drawable.ic_settings_black_24dp), ThemesEngine.iconsColor));
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_search_black_24dp);
                toolbar.getMenu().findItem(R.id.settings).setIcon(R.drawable.ic_settings_black_24dp);
            }
        }

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("fon", 0) == 1){
            if (UselessUtils.ifCustomTheme()){
                getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
                getActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
                getActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
            } else {
                if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", false)){
                    getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.statusbar)));
                    getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.statusbar));
                    getActivity().getWindow().setNavigationBarColor(getActivity().getResources().getColor(R.color.statusbar));
                } else {
                    getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        getActivity().getWindow().setStatusBarColor(Color.WHITE);
                    } else {
                        getActivity().getWindow().setStatusBarColor(Color.GRAY);
                    }

                    getActivity().getWindow().setNavigationBarColor(Color.BLACK);
                }
            }
        }

        if (UselessUtils.ifCustomTheme()){
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            getActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            getActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);

            if (ThemesEngine.toolbarTransparent){
                toolbar.setBackgroundColor(ThemesEngine.toolbarColor);
            }
        }

        getActivity().setActionBar(toolbar);
        return v;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UselessUtils.replace(getActivity(), new Search(), "search");
            }
        });

        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("in_folder_id", "def").apply();

        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("in_folder_back_stack", false).apply();

        allList = new ArrayList<>();

        dao = App.getInstance().getDatabase().noteOrFolderDao();

        List<NoteOrFolder> notPinned = new ArrayList<>();

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.pinned == 1){
                if (noteOrFolder.in_folder_id.equals("def")){
                    allList.add(noteOrFolder);
                }
            } else {
                if (noteOrFolder.in_folder_id.equals("def")){
                    notPinned.add(noteOrFolder);
                }
            }
        }

        allList.addAll(notPinned);

        nothing = view.findViewById(R.id.nothing);

        if (allList.isEmpty()){
            nothing.setVisibility(View.VISIBLE);
        } else {
            nothing.setVisibility(View.INVISIBLE);
        }

        recyclerView = view.findViewById(R.id.notes_view);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        adapter = new ItemsAdapter(allList, getActivity(), true);

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

                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
                delete(viewHolder.getPosition(), view);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        MyButton fab = view.findViewById(R.id.new_note);

        ImageButton fab1 = view.findViewById(R.id.new_folder);
        ImageButton fab2 = view.findViewById(R.id.new_notify);

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up);
        animation.setDuration(400);
        fab.startAnimation(animation);

        Animation animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in);
        animation2.setDuration(400);
        fab1.startAnimation(animation2);

        Animation animation3 = AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_in);
        animation3.setDuration(400);
        fab2.startAnimation(animation3);

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", false)){
            fab1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_create_new_folder_white_24dp));
            fab.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            fab2.setImageDrawable(getActivity().getDrawable(R.drawable.ic_notification_create_white_24dp));
        } else {
            fab1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_create_new_folder_black_24dp));
            fab2.setImageDrawable(getActivity().getDrawable(R.drawable.ic_notification_create_black_24dp));
            if (UselessUtils.ifCustomTheme()){
                fab.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            }
        }

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UselessUtils.replace(getActivity(), new NoteAdd(), "add");
                }
            });

            fab.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getActivity(), getString(R.string.import_db), Toast.LENGTH_SHORT).show();
                    openFile("*/*", 228, getActivity());
                    return false;
                }
            });

            fab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createFolder();
                }
            });

            fab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createNotify();
                }
            });
    }

    private void createNotify(){
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_two_edit_texts, null);

        EditText title = v.findViewById(R.id.edit_text_one);
        title.setBackground(null);
        title.setHint(getString(R.string.title));

        EditText text = v.findViewById(R.id.edit_text_two);
        text.setBackground(null);
        text.setHint(getString(R.string.text));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dao.insert(new NoteOrFolder(title.getText().toString(), text.getText().toString(), 0, 0, "def", 2, null, 0, "", System.currentTimeMillis()));
                UselessUtils.replaceNoBackStack(getActivity(), new Notes(), "notes");
            }
        });

        AlertDialog dialog1337 = builder.create();

        dialog1337.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog1) {
                if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", false)){
                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                }
                if (UselessUtils.ifCustomTheme()){
                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ThemesEngine.textColor);
                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                }
            }
        });

        dialog1337.show();
    }

    private void createFolder(){
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_text, null);

        EditText text = v.findViewById(R.id.edit_text);
        text.setBackground(null);
        text.setHint(getString(R.string.name));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(getString(R.string.folder_name));

        AlertDialog dialog1337 =  builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog228, int which) {
                if (text.getText().toString().isEmpty()) {
                    text.setText(generateName());
                }

                boolean create = true;

                for (NoteOrFolder noteOrFolder : dao.getAll()) {
                    if (noteOrFolder.is_folder == 1 && noteOrFolder.folder_name.equals(text.getText().toString())){
                        create = false;
                        Toast.makeText(getActivity(), getString(R.string.folder_error), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                if (create){

                    dao.insert(new NoteOrFolder(null, null, 0, 0, "def", 1, text.getText().toString(),  0, "", 0));
                    UselessUtils.replaceNoBackStack(getActivity(), new Notes(), "notes");
                }
            }
        }).create();

        dialog1337.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog1) {
                if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", false)){
                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                }
                if (UselessUtils.ifCustomTheme()){
                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ThemesEngine.textColor);
                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                }
            }
        });

        dialog1337.show();
    }

    public String generateName(){
        int first_number = 1;

        String name = getString(R.string.new_folder);

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.is_folder == 0 && noteOrFolder.title.equals(name)){
                name = getString(R.string.new_folder) + first_number;
                first_number++;
            }
        }

        return name;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                UselessUtils.replace(getActivity(), MainActivity.settings, "settings");
                break;
        }

        return super.onOptionsItemSelected(item);
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
        if (data != null){
            if (requestCode == 228){

                InputStream fstream = null;

                if (!getFileName(data.getData()).contains(".txt") || getFileName(data.getData()).contains(".fb2")){
                    Toast.makeText(getActivity(), getString(R.string.import_note_error1), Toast.LENGTH_SHORT).show();
                }

                String title = getFileName(data.getData());
                String text = null;
                try {
                    fstream = getActivity().getContentResolver().openInputStream(data.getData());
                    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                    boolean first = true;
                    String strLine;
                    while ((strLine = br.readLine()) != null){
                        if (first){
                            text = strLine;
                            first = false;
                        } else {
                            text = text + "\n" + strLine;
                        }
                    }
                } catch (IOException e) {
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                long time = System.currentTimeMillis();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putLong("time_to_insert", time).apply();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("title_to_insert", title).apply();

                dao.insert(new NoteOrFolder(title, null, 0, 0, "def", 0, null, 0, "", time));

                UselessUtils.replace(getActivity(), new Notes(), "notes");

                time = PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong("time_to_insert", System.currentTimeMillis());
                title = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("title_to_insert", "");

                for (NoteOrFolder noteOrFolder : dao.getAll()) {
                    if (noteOrFolder.edit_time == time && noteOrFolder.is_folder == 0){
                        App.getInstance().getDatabase().noteItemsDao().insert(new NoteItem(0, noteOrFolder.id, text, null, 0));
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void delete(int position, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setTitle(R.string.confirm_delete);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (allList.get(position).is_folder == 1){
                        adapter.deleteFolder(allList.get(position).folder_name);
                    } else {
                        adapter.deleteNote(allList.get(position).id);
                    }

                    allList.remove(position);

                    Snackbar.make(view, R.string.deleted, Snackbar.LENGTH_SHORT).show();

                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            });

            builder.setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    recyclerView.getAdapter().notifyItemChanged(position);

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
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.settings);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", false)){
                item.setIcon(R.drawable.ic_settings_white_24dp);
            }

        super.onCreateOptionsMenu(menu, inflater);
    }
}
