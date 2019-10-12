package com.f0x1d.notes.fragment.main;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.fragment.editing.NoteAdd;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.bottomSheet.Element;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.view.CenteredToolbar;
import com.f0x1d.notes.view.theming.MyFAB;

import java.util.ArrayList;
import java.util.List;

public class NotesMoving extends Fragment {

    public static NotesMoving newInstance(String inFolderId) {
        Bundle args = new Bundle();
        args.putString("in_f_id", inFolderId);

        NotesMoving fragment = new NotesMoving();
        fragment.setArguments(args);
        return fragment;
    }

    public RecyclerView recyclerView;
    public CenteredToolbar toolbar;
    public MyFAB apply;

    public ArrayList<NoteOrFolder> allList;
    public NoteOrFolderDao dao;
    public ItemsAdapter adapter;
    public String inFolderId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inFolderId = getArguments().getString("in_f_id");

        View view = inflater.inflate(R.layout.notes_moving_layout, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        toolbar = view.findViewById(R.id.toolbar);
        apply = view.findViewById(R.id.apply);

        toolbar.setTitle(getString(R.string.move));

        apply.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_done_black_24dp));
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        if (UselessUtils.ifCustomTheme()) {
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            getActivity().getWindow().setStatusBarColor(MainActivity.instance.viewModel.statusBarColor.getValue());
            getActivity().getWindow().setNavigationBarColor(MainActivity.instance.viewModel.navBarColor.getValue());

            toolbar.setBackgroundColor(ThemesEngine.toolbarColor);
        }

        getActivity().setActionBar(toolbar);

        allList = new ArrayList<>();

        dao = App.getInstance().getDatabase().noteOrFolderDao();
        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.in_folder_id.equals(inFolderId)) {
                allList.add(noteOrFolder);
            }
        }

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

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

            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        adapter = new ItemsAdapter(allList, getActivity(), true, true, touchHelper);
        recyclerView.setAdapter(adapter);

        touchHelper.attachToRecyclerView(recyclerView);

        return view;
    }
}
