package com.f0x1d.notes.fragment.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.adapter.ItemsAdapter;
import com.f0x1d.notes.adapter.NoteItemsAdapter;
import com.f0x1d.notes.db.Database;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.db.entities.Notify;
import com.f0x1d.notes.fragment.bottomSheet.SetNotify;
import com.f0x1d.notes.fragment.editing.NoteAdd;
import com.f0x1d.notes.fragment.settings.MainSettings;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.bottomSheet.BottomSheetCreator;
import com.f0x1d.notes.utils.bottomSheet.Element;
import com.f0x1d.notes.utils.dialogs.ShowAlertDialog;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.view.CenteredToolbar;
import com.f0x1d.notes.view.theming.MyFAB;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.f0x1d.notes.utils.UselessUtils.getFileName;

public class Notes extends Fragment {

    public static RecyclerView recyclerView;
    NoteOrFolderDao dao;

    TextView nothing;
    CenteredToolbar toolbar;

    ItemsAdapter adapter;
    private List<NoteOrFolder> allList;

    public static long genId() {
        long id = 0;

        for (NoteOrFolder noteOrFolder : App.getInstance().getDatabase().noteOrFolderDao().getAll()) {
            if (noteOrFolder.id > id) {
                id = noteOrFolder.id;
            }
        }

        return id + 1;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notes_layout, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.notes));
        toolbar.goAnim("def");

        Drawable settings;
        if (UselessUtils.ifCustomTheme())
            settings = UselessUtils.setTint(getActivity().getDrawable(R.drawable.ic_settings_white_24dp), ThemesEngine.iconsColor);
        else if (UselessUtils.getBool("night", false))
            settings = getActivity().getDrawable(R.drawable.ic_settings_white_24dp);
        else
            settings = getActivity().getDrawable(R.drawable.ic_settings_black_24dp);

        toolbar.inflateMenu(R.menu.main);
        toolbar.getMenu().findItem(R.id.settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        toolbar.getMenu().findItem(R.id.settings).setIcon(settings);

        if (UselessUtils.ifCustomTheme()) {
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            getActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            getActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);

            toolbar.setBackgroundColor(ThemesEngine.toolbarColor);
        }

        getActivity().setActionBar(toolbar);

        MyFAB fab = view.findViewById(R.id.new_note);

        List<Element> elements = new ArrayList<>();
        elements.add(new Element(getString(R.string.new_notify), getActivity().getDrawable(R.drawable.ic_notification_create_black_24dp), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotify();
                fab.closeList();
            }
        }));
        elements.add(new Element(getString(R.string.new_folder), getActivity().getDrawable(R.drawable.ic_create_new_folder_black_24dp), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFolder();
                fab.closeList();
            }
        }));
        elements.add(new Element(getString(R.string.new_note), getActivity().getDrawable(R.drawable.ic_add_black_24dp), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.instance.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                        R.id.container, NoteAdd.newInstance("def"), "add").addToBackStack("editor").commit();
                fab.closeList();
            }
        }));

        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));

        recyclerView = view.findViewById(R.id.notes_view);

        allList = new ArrayList<>();

        dao = App.getInstance().getDatabase().noteOrFolderDao();

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.in_folder_id.equals("def")) {
                allList.add(noteOrFolder);
            }
        }

        nothing = view.findViewById(R.id.nothing);
        nothing.setText(getString(R.string.empty));

        if (allList.isEmpty()) {
            nothing.setVisibility(View.VISIBLE);
        } else {
            nothing.setVisibility(View.INVISIBLE);
        }

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        if (UselessUtils.getBool("two_rows", false)) {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            recyclerView.setLayoutManager(llm);
        }

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
            public boolean onMove(RecyclerView recyclerView2, RecyclerView.ViewHolder h1, RecyclerView.ViewHolder h2) {
                int fromPosition = h1.getAdapterPosition();
                int toPosition = h2.getAdapterPosition();

                adapter.onItemsChanged(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
                delete(viewHolder.getPosition());
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

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up);
        animation.setDuration(400);
        fab.startAnimation(animation);

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), getString(R.string.import_db), Toast.LENGTH_SHORT).show();
                openFile("*/*", 228, getActivity());
                return false;
            }
        });

        fab.setElements(elements, (ViewGroup) view);
        return view;
    }

    private void createNotify() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_two_edit_texts, null);

        EditText title = v.findViewById(R.id.edit_text_one);
        title.setBackground(null);
        title.setHint(getString(R.string.title));

        EditText text = v.findViewById(R.id.edit_text_two);
        text.setBackground(null);
        text.setHint(getString(R.string.text));

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setView(v);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long id = genId();
                int position = Database.getLastPosition("def");

                dao.insert(new NoteOrFolder(title.getText().toString(), text.getText().toString(), id, 0, "def", 2,
                        null, 0, "", System.currentTimeMillis(), position));
                allList.add(new NoteOrFolder(title.getText().toString(), text.getText().toString(), id, 0, "def", 2,
                        null, 0, "", System.currentTimeMillis(), position));
                recyclerView.getAdapter().notifyDataSetChanged();

                nothing.setVisibility(View.INVISIBLE);
            }
        });
        builder.setNeutralButton(getString(R.string.set_time), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long id = genId();
                int position = Database.getLastPosition("def");

                NoteOrFolder noteOrFolder = new NoteOrFolder(title.getText().toString(), text.getText().toString(), id, 0, "def", 2,
                        null, 0, "", System.currentTimeMillis(), position);

                dao.insert(noteOrFolder);
                allList.add(noteOrFolder);
                recyclerView.getAdapter().notifyDataSetChanged();

                nothing.setVisibility(View.INVISIBLE);

                SetNotify notify = new SetNotify(new Notify(noteOrFolder.title, noteOrFolder.text, 0, noteOrFolder.id));
                notify.show(getActivity().getSupportFragmentManager(), "TAG");
            }
        });

        ShowAlertDialog.show(builder);
    }

    private void createFolder() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_text, null);

        EditText text = v.findViewById(R.id.edit_text);
        text.setBackground(null);
        text.setHint(getString(R.string.name));

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setView(v);
        builder.setTitle(getString(R.string.folder_name));

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog228, int which) {
                if (text.getText().toString().isEmpty()) {
                    text.setText(generateName());
                }

                boolean create = true;

                for (NoteOrFolder noteOrFolder : dao.getAll()) {
                    if (noteOrFolder.is_folder == 1 && noteOrFolder.folder_name.equals(text.getText().toString())) {
                        create = false;
                        Toast.makeText(getActivity(), getString(R.string.folder_error), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                if (create) {
                    long id = genId();
                    int position = Database.getLastPosition("def");

                    dao.insert(new NoteOrFolder(null, null, id, 0, "def", 1, text.getText().toString(), 0, "", 0, position));
                    allList.add(new NoteOrFolder(null, null, id, 0, "def", 1, text.getText().toString(), 0, "", 0, position));
                    recyclerView.getAdapter().notifyDataSetChanged();

                    nothing.setVisibility(View.INVISIBLE);
                }
            }
        }).create();

        ShowAlertDialog.show(builder);
    }

    public String generateName() {
        int first_number = 1;

        String name = getString(R.string.new_folder);

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.is_folder == 1 && noteOrFolder.folder_name.equals(name)) {
                name = getString(R.string.new_folder) + first_number;
                first_number++;
            }
        }

        return name;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                UselessUtils.replace(new MainSettings(), "settings");
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
        if (data != null) {
            if (requestCode == 228) {
                InputStream fstream = null;

                String title = getFileName(data.getData());
                String text = null;
                try {
                    fstream = getActivity().getContentResolver().openInputStream(data.getData());
                    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                    boolean first = true;
                    String strLine;
                    while ((strLine = br.readLine()) != null) {
                        if (first) {
                            text = strLine;
                            first = false;
                        } else {
                            text = text + "\n" + strLine;
                        }
                    }
                } catch (IOException e) {
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Logger.log(e);
                }
                int position = Database.getLastPosition("def");
                NoteOrFolder noteOrFolder = new NoteOrFolder(title, null, genId(), 0, "def", 0, null, 0, "", System.currentTimeMillis(), position);

                dao.insert(noteOrFolder);
                allList.add(noteOrFolder);

                App.getInstance().getDatabase().noteItemsDao().insert(new NoteItem(NoteItemsAdapter.getId(), noteOrFolder.id, text, null, 0, 0, 0));

                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private int getPosition(long id) {
        int pos = 0;

        for (NoteOrFolder noteItem : dao.getAll()) {
            if (noteItem.id == id) {
                pos = noteItem.position;
                break;
            }
        }

        return pos;
    }

    public void delete(int position) {
        BottomSheetCreator creator = new BottomSheetCreator(getActivity());
        creator.addElement(new Element(getString(R.string.delete), getActivity().getDrawable(R.drawable.ic_done_white_24dp), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allList.get(position).is_folder == 1) {
                    if (ItemsAdapter.getFolderNameFromDataBaseStatic(allList.get(position).id, position).equals(""))
                        adapter.deleteFolder(allList.get(position).folder_name);
                    else
                        adapter.deleteFolder(ItemsAdapter.getFolderNameFromDataBaseStatic(allList.get(position).id, position));
                } else {
                    adapter.deleteNote(allList.get(position).id);
                    App.getInstance().getDatabase().noteItemsDao().deleteByToId(allList.get(position).id);
                }
                allList.remove(position);

                for (int i = 0; i < allList.size(); i++) {
                    if (getPosition(allList.get(i).id) != i) {
                        dao.updatePosition(i, allList.get(i).id);
                    }
                }

                recyclerView.getAdapter().notifyDataSetChanged();

                Snackbar.make(getView(), getString(R.string.deleted), Snackbar.LENGTH_SHORT).show();

                try {
                    creator.customBottomSheet.dismiss();
                } catch (Exception e) {
                }
            }
        }));
        creator.addElement(new Element(getString(R.string.cancel), getActivity().getDrawable(R.drawable.ic_clear_white_24dp), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.getAdapter().notifyItemChanged(position);

                try {
                    creator.customBottomSheet.dismiss();
                } catch (Exception e) {
                }
            }
        }));
        creator.show("", false);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.settings);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        Drawable settings;
        if (UselessUtils.ifCustomTheme())
            settings = UselessUtils.setTint(getActivity().getDrawable(R.drawable.ic_settings_white_24dp), ThemesEngine.iconsColor);
        else if (UselessUtils.getBool("night", false))
            settings = getActivity().getDrawable(R.drawable.ic_settings_white_24dp);
        else
            settings = getActivity().getDrawable(R.drawable.ic_settings_black_24dp);
        item.setIcon(settings);

        super.onCreateOptionsMenu(menu, inflater);
    }
}