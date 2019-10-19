package com.f0x1d.notes.fragment.choose;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.adapter.ChooseFolderAdapter;
import com.f0x1d.notes.db.Database;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.fragment.main.NotesFragment;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.view.CenteredToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ChooseFolderFragment extends Fragment {

    private RecyclerView recyclerView;
    private CenteredToolbar toolbar;
    private FloatingActionButton fab;
    private String inId;
    private long id;
    private NoteOrFolderDao dao = App.getInstance().getDatabase().noteOrFolderDao();

    public static ChooseFolderFragment newInstance(long id, String inId) {
        Bundle args = new Bundle();
        args.putLong("id", id);
        args.putString("inId", inId);

        ChooseFolderFragment fragment = new ChooseFolderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inId = getArguments().getString("inId");
        id = getArguments().getLong("id");
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.choose_folder, container, false);
        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(inId);
        if (inId.equals("def"))
            toolbar.setTitle(getString(R.string.notes));

        recyclerView = v.findViewById(R.id.recyclerView);
        fab = v.findViewById(R.id.ok);

        List<NoteOrFolder> allList = new ArrayList<>();
        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.isFolder == 1 && noteOrFolder.inFolderId.equals(inId)) {
                allList.add(noteOrFolder);
            }
        }

        ((TextView) v.findViewById(R.id.no_folders)).setText(getString(R.string.no_folders));

        if (allList.isEmpty())
            v.findViewById(R.id.no_folders).setVisibility(View.VISIBLE);
        else
            v.findViewById(R.id.no_folders).setVisibility(View.INVISIBLE);

        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        if (UselessUtils.getBool("two_rows", false)) {
            recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
        } else {
            recyclerView.setLayoutManager(llm);
        }

        ChooseFolderAdapter adapter = new ChooseFolderAdapter(allList, id, (AppCompatActivity) requireActivity());

        recyclerView.setAdapter(adapter);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dao.updateInFolderIdById(inId, id);
                dao.updatePosition(Database.getLastPosition(inId), id);

                UselessUtils.clearBackStack((AppCompatActivity) requireActivity());
                UselessUtils.replace((AppCompatActivity) requireActivity(), NotesFragment.newInstance("def"), "notes", false, null);
            }
        });

        return v;
    }


}
