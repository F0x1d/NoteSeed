package com.f0x1d.notes.fragment.choose;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.adapter.ChooseFolderAdapter;
import com.f0x1d.notes.db.Database;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.translations.Translations;
import com.f0x1d.notes.view.CenteredToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ChooseFolder extends Fragment {

    private RecyclerView recyclerView;
    private CenteredToolbar toolbar;
    private FloatingActionButton fab;
    private String in_id;
    private long id;
    private NoteOrFolderDao dao = App.getInstance().getDatabase().noteOrFolderDao();

    public static ChooseFolder newInstance(Bundle args) {
        ChooseFolder fragment = new ChooseFolder();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        in_id = getArguments().getString("in_id");
        id = getArguments().getLong("id");
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.choose_folder, container, false);
        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(in_id);
        if (in_id.equals("def"))
            toolbar.setTitle(Translations.getString("notes"));

        getActivity().setActionBar(toolbar);

        recyclerView = v.findViewById(R.id.recyclerView);
        fab = v.findViewById(R.id.ok);

        List<NoteOrFolder> allList = new ArrayList<>();

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.is_folder == 1 && noteOrFolder.in_folder_id.equals(in_id)) {
                allList.add(noteOrFolder);
            }
        }

        ((TextView) v.findViewById(R.id.no_folders)).setText(Translations.getString("no_folders"));

        if (allList.isEmpty())
            v.findViewById(R.id.no_folders).setVisibility(View.VISIBLE);
        else
            v.findViewById(R.id.no_folders).setVisibility(View.INVISIBLE);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        if (UselessUtils.getBool("two_rows", false)) {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            recyclerView.setLayoutManager(llm);
        }

        ChooseFolderAdapter adapter = new ChooseFolderAdapter(allList, id);

        recyclerView.setAdapter(adapter);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dao.updateInFolderIdById(in_id, id);
                dao.updatePosition(Database.getLastPosition(in_id), id);

                UselessUtils.clear_back_stack();
                MainActivity.instance.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                        R.id.container, new Notes(), "notes").commit();
            }
        });

        return v;
    }


}
