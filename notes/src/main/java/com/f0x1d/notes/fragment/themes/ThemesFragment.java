package com.f0x1d.notes.fragment.themes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.f0x1d.notes.R;
import com.f0x1d.notes.adapter.ThemesAdapter;
import com.f0x1d.notes.model.Theme;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ThemesFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton import_fab;

    List<Theme> themes;

    public static ThemesFragment newInstance(boolean anim) {
        ThemesFragment myFragment = new ThemesFragment();
        Bundle args = new Bundle();
            args.putBoolean("anim", anim);

        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.themes_fragment, container, false);
        Toolbar toolbar = v.findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.themes));

        if (UselessUtils.ifCustomTheme()){
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            getActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            getActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
        }
        return v;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        themes = new ArrayList<>();
            themes.add(new Theme(null, getString(R.string.blue), "by F0x1d", 0xff64B5F6, 0xffffffff));
            themes.add(new Theme(null, getString(R.string.orange), "by F0x1d", 0xffffaa00, 0xff000000));
            themes.add(new Theme(null, getString(R.string.dark), "by F0x1d", 0xff303030, 0xffffffff));
            themes.addAll(new ThemesEngine().getThemes());

        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        ThemesAdapter adapter;

        if (getArguments().getBoolean("anim", false)){
            adapter = new ThemesAdapter(themes, getActivity(), true);
        } else {
            adapter = new ThemesAdapter(themes, getActivity(), false);
        }

        recyclerView.setAdapter(adapter);

        import_fab = view.findViewById(R.id.import_theme);

        if (!UselessUtils.ifCustomTheme()){
            import_fab.setImageResource(R.drawable.ic_add_black_24dp);
        }
            import_fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFile("*/*", 228, getActivity());
                }
            });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null){
            new ThemesEngine().importTheme(data.getData(), getActivity());

            UselessUtils.recreate(ThemesFragment.this, getActivity(), "themes");
        }
    }

    public void openFile(String minmeType, int requestCode, Activity activity) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(minmeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.f0x1d.talkandtools.main.PICK_DATA");
        // if you want any file type, you can skip next line
        sIntent.putExtra("CONTENT_TYPE", minmeType);
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;
        if (activity.getPackageManager().resolveActivity(sIntent, 0) != null){
            // it is device with samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "Open file");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intent});
        }
        else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        }

        try {
            ThemesFragment.this.startActivityForResult(chooserIntent, requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }
}
