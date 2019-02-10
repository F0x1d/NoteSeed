package com.f0x1d.notes.fragment.choose;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.adapter.ChooseFolderAdapter;
import com.f0x1d.notes.adapter.ItemsAdapter;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.view.CenteredToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ChooseFolder extends Fragment {

    public static ChooseFolder newInstance(Bundle args) {
        ChooseFolder fragment = new ChooseFolder();
        fragment.setArguments(args);
        return fragment;
    }

    public static List<String> in_ids = new ArrayList<>();

    private RecyclerView recyclerView;
    private CenteredToolbar toolbar;
    private FloatingActionButton fab;

    private String in_id;
    private long id;

    private NoteOrFolderDao dao = App.getInstance().getDatabase().noteOrFolderDao();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        in_id = in_ids.get(in_ids.size() - 1);
        id = getArguments().getLong("id");
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.choose_folder, container, false);
        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(in_id);
        if (in_id.equals("def"))
            toolbar.setTitle(getString(R.string.notes));
        getActivity().setActionBar(toolbar);

        recyclerView = v.findViewById(R.id.recyclerView);
        fab = v.findViewById(R.id.ok);

        List<NoteOrFolder> allList = new ArrayList<>();

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.is_folder == 1 && noteOrFolder.in_folder_id.equals(in_id)){
                allList.add(noteOrFolder);
            }
        }

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        if (UselessUtils.getBool("two_rows", false)){
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            recyclerView.setLayoutManager(llm);
        }

        ChooseFolderAdapter adapter = new ChooseFolderAdapter(allList, getActivity(), id);

        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dao.updateInFolderIdById(in_id, id);
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                        android.R.id.content, new Notes(), "notes").commit();
            }
        });

        return v;
    }


}
