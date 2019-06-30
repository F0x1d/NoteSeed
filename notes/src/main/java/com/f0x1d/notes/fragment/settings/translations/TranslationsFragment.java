package com.f0x1d.notes.fragment.settings.translations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.R;
import com.f0x1d.notes.adapter.TranslationsAdapter;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.translations.IncorrectTranslationError;
import com.f0x1d.notes.utils.translations.Translation;
import com.f0x1d.notes.utils.translations.Translations;
import com.f0x1d.notes.view.CenteredToolbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class TranslationsFragment extends Fragment {

    public static TranslationsFragment newInstance() {
        Bundle args = new Bundle();

        TranslationsFragment fragment = new TranslationsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView recyclerView;
    private CenteredToolbar toolbar;

    private TranslationsAdapter adapter;
    private List<Translation> translations = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.translations_layout, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(Translations.getString("translations"));
        getActivity().setActionBar(toolbar);

        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);

        translations.clear();
        translations.add(null);
        translations.add(new Translation(Translations.getString("default_name"), null));
        try {
            for (File file : Translations.getAvailableTranslations()){
                translations.add(new Translation(file.getName(), file));
            }
        } catch (Exception e){}

        adapter = new TranslationsAdapter(translations, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void restart(){
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("change_l", true).apply();

        Intent i1 = getActivity().getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
        i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(i1);
        getActivity().finish();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == 228){
            try {
                Translations.addTranslation((FileInputStream) getActivity().getContentResolver().openInputStream(data.getData()), UselessUtils.getFileName(data.getData()));
                restart();
            } catch (IncorrectTranslationError | FileNotFoundException incorrectTranslationError) {
                Logger.log(incorrectTranslationError);
            }
        }
    }
}
