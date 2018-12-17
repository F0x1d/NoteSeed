package com.f0x1d.notes.fragment.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.editing.NoteAdd;
import com.f0x1d.notes.fragment.search.Search;
import com.f0x1d.notes.fragment.settings.Settings;
import com.f0x1d.notes.help.App;
import com.f0x1d.notes.help.adapter.ItemsAdapter;
import com.f0x1d.notes.help.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.help.db.entities.NoteOrFolder;
import com.f0x1d.notes.help.utils.ThemesEngine;
import com.f0x1d.notes.help.utils.UselessUtils;
import com.f0x1d.notes.help.view.CenteredToolbar;
import com.f0x1d.notes.help.view.theming.MyButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.f0x1d.notes.help.utils.UselessUtils.getFileName;

public class in_folder_Notes extends Fragment {

    RecyclerView recyclerView;

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
        toolbar.setTitle(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("folder_name", ""));
        toolbar.inflateMenu(R.menu.search_menu);

        if (UselessUtils.getBool("night", false)){
            if (UselessUtils.ifCustomTheme()){
                toolbar.setNavigationIcon(UselessUtils.setTint(getResources().getDrawable(R.drawable.ic_search_white_24dp), ThemesEngine.iconsColor));
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_search_white_24dp);
            }
        } else {
            if (UselessUtils.ifCustomTheme()){
                toolbar.setNavigationIcon(UselessUtils.setTint(getResources().getDrawable(R.drawable.ic_search_black_24dp), ThemesEngine.iconsColor));
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_search_black_24dp);
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
                    getActivity().getWindow().setStatusBarColor(Color.WHITE);
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

    @SuppressLint({"WrongConstant", "RestrictedApi"})
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new Search()).addToBackStack(null).commit();
            }
        });

        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("in_folder_back_stack", true).apply();

        allList = new ArrayList<>();

        nothing = view.findViewById(R.id.nothing);

        dao = App.getInstance().getDatabase().noteOrFolderDao();

        List<NoteOrFolder> notPinned = new ArrayList<>();

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.pinned == 1){
                if (noteOrFolder.in_folder_id.equals(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("in_folder_id", "def"))){
                    allList.add(noteOrFolder);
                }
            } else {
                if (noteOrFolder.in_folder_id.equals(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("in_folder_id", "def"))){
                    notPinned.add(noteOrFolder);
                }
            }
        }

        allList.addAll(notPinned);

        if (allList.isEmpty()){
            nothing.setVisibility(View.VISIBLE);
        } else {
            nothing.setVisibility(View.INVISIBLE);
        }

        getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

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
        fab1.setVisibility(View.GONE);

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up);
        animation.setDuration(500);
        fab.startAnimation(animation);

        Animation animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in);
        animation2.setDuration(500);
        fab1.startAnimation(animation2);

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", false)){
            fab1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_create_new_folder_white_24dp));
            fab.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
        } else {
            fab1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_create_new_folder_black_24dp));
        }

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("f_in_f", false)){
            fab1.setVisibility(View.VISIBLE);
            fab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createFolder();
                }
            });
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new NoteAdd()).addToBackStack(null).commit();
            }
        });
    }

    private void createFolder(){
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_text, null);

        EditText text = v.findViewById(R.id.edit_text);
        text.setBackground(null);
        text.setHint(getString(R.string.name));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(getString(R.string.folder_name));

        AlertDialog dialog1337 = builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                    dao.insert(new NoteOrFolder(null, null, 0, 0, PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("in_folder_id", "def"), 1, text.getText().toString(), 0, ""));
                    getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new in_folder_Notes()).commit();
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
                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ThemesEngine.accentColor);
                }
            }
        });

        dialog1337.show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.import_note:
                openFile("*/*", 228, getActivity());
                break;
            case R.id.settings:
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new Settings()).addToBackStack(null).commit();
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

                dao.insert(new NoteOrFolder(title, text, 0, 0, PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("in_folder_id", "def"), 0, null, 0, ""));

                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out).replace(android.R.id.content, new Notes()).addToBackStack(null).commit();
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
                dialog.cancel();
            }
        });

        AlertDialog dialog1337 = builder.create();

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
        super.onCreateOptionsMenu(menu, inflater);
    }
}
