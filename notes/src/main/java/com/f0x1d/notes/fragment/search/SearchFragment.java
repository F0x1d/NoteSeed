package com.f0x1d.notes.fragment.search;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.adapter.ItemsAdapter;
import com.f0x1d.notes.db.daos.NoteItemsDao;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    static List<NoteOrFolder> allList;
    static List<NoteOrFolder> searchedList;
    RecyclerView recyclerView;
    NoteOrFolderDao dao = App.getInstance().getDatabase().noteOrFolderDao();
    NoteItemsDao noteItemsDao = App.getInstance().getDatabase().noteItemsDao();
    private String id;

    public static SearchFragment newInstance(String in_folder_id) {

        Bundle args = new Bundle();
        args.putString("id", in_folder_id);

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search, container, false);
        id = getArguments().getString("id");

        if (UselessUtils.isCustomTheme()) {
            requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            requireActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            requireActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
        }

        searchedList = new ArrayList<>();
        allList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        if (UselessUtils.getBool("two_rows", false)) {
            recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
        } else {
            recyclerView.setLayoutManager(llm);
        }

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.inFolderId.equals(id)) {
                allList.add(noteOrFolder);
            }
        }

        ItemsAdapter adapter = new ItemsAdapter(allList, requireActivity(), false, null);

        recyclerView.setAdapter(adapter);

        EditText text = view.findViewById(R.id.text);
        text.setHint(getString(R.string.search));

        ImageButton button = view.findViewById(R.id.close);

        ImageButton buttonRes = view.findViewById(R.id.reset);
        button.setBackground(null);
        buttonRes.setBackground(null);
        buttonRes.setVisibility(View.INVISIBLE);

        if (UselessUtils.getBool("night", false)) {
            button.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            buttonRes.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_clear_white_24dp));
        } else {
            button.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_arrow_back_black_24dp));
            buttonRes.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_clear_black_24dp));
        }

        buttonRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("");
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UselessUtils.hideSoftKeyboard(text, requireActivity());
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        UselessUtils.showKeyboard(text, requireActivity());

        Animation animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.push_left_in);
        animation.setDuration(400);
        text.startAnimation(animation);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    buttonRes.setVisibility(View.INVISIBLE);
                } else {
                    buttonRes.setVisibility(View.VISIBLE);
                }

                searchedList.clear();
                for (NoteOrFolder menuItem : allList) {
                    if (menuItem.isFolder == 1) {
                        if (menuItem.folderName.toLowerCase().contains(s.toString().toLowerCase())) {
                            searchedList.add(menuItem);
                        }
                    } else {
                        boolean add = false;

                        for (NoteItem noteItem : noteItemsDao.getAll()) {
                            if (menuItem.id == noteItem.toId) {
                                if (noteItem.text != null && !noteItem.text.equals("null")) {
                                    if (noteItem.text.toLowerCase().contains(s.toString().toLowerCase())) {
                                        add = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (menuItem.text != null) {
                            if (menuItem.text.toLowerCase().contains(s.toString().toLowerCase()) || menuItem.title.toLowerCase().contains(s.toString().toLowerCase())) {
                                add = true;
                            }
                        } else {
                            if (menuItem.title.toLowerCase().contains(s.toString().toLowerCase())) {
                                add = true;
                            }
                        }

                        if (add)
                            searchedList.add(menuItem);
                    }
                }

                ItemsAdapter adapter = new ItemsAdapter(searchedList, requireActivity(), false, null);
                recyclerView.setAdapter(adapter);
            }
        });
        return view;
    }
}
