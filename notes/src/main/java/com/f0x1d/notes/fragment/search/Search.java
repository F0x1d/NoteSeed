package com.f0x1d.notes.fragment.search;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import com.f0x1d.notes.R;
import com.f0x1d.notes.App;
import com.f0x1d.notes.adapter.ItemsAdapter;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Search extends Fragment {

    RecyclerView recyclerView;

    static List<NoteOrFolder> allList;
    static List<NoteOrFolder> searchedList;

    NoteOrFolderDao dao = App.getInstance().getDatabase().noteOrFolderDao();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if (UselessUtils.ifCustomTheme()){
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            getActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            getActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
        }

        return inflater.inflate(R.layout.search, container, false);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("in_folder_back_stack", false).apply();

        searchedList = new ArrayList<>();
        allList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.in_folder_id.equals(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("in_folder_id", "def"))){
                allList.add(noteOrFolder);
            }

        }

        ItemsAdapter adapter = new ItemsAdapter(allList, getActivity(), false);

        recyclerView.setAdapter(adapter);

        EditText text = view.findViewById(R.id.text);

        ImageButton button = view.findViewById(R.id.close);

        ImageButton buttonRes = view.findViewById(R.id.reset);
            button.setBackground(null);
            buttonRes.setBackground(null);
            buttonRes.setVisibility(View.INVISIBLE);

            if (UselessUtils.getBool("night", false)){
                button.setImageDrawable(getActivity().getDrawable(R.drawable.ic_arrow_back_white_24dp));
                buttonRes.setImageDrawable(getActivity().getDrawable(R.drawable.ic_clear_white_24dp));
            } else {
                button.setImageDrawable(getActivity().getDrawable(R.drawable.ic_arrow_back_black_24dp));
                buttonRes.setImageDrawable(getActivity().getDrawable(R.drawable.ic_clear_black_24dp));
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
                    UselessUtils.hideSoftKeyboard(text, getActivity());
                    getFragmentManager().popBackStack();
                }
            });

        UselessUtils.showKeyboard(text, getActivity());

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in);
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
                    if (s.toString().isEmpty()){
                        buttonRes.setVisibility(View.INVISIBLE);
                    } else {
                        buttonRes.setVisibility(View.VISIBLE);
                    }

                    searchedList.clear();
                    for (NoteOrFolder menuItem : allList) {
                        if (menuItem.is_folder == 1) {
                            if (menuItem.folder_name.toLowerCase().contains(s.toString().toLowerCase())){
                                searchedList.add(menuItem);
                            }
                        } else {
                            if (menuItem.text.toLowerCase().contains(s.toString().toLowerCase()) || menuItem.title.toLowerCase().contains(s.toString().toLowerCase())){
                                searchedList.add(menuItem);
                            }
                        }
                    }

                    ItemsAdapter adapter = new ItemsAdapter(searchedList, getActivity(), false);

                    recyclerView.setAdapter(adapter);
                }
            });
    }
}
