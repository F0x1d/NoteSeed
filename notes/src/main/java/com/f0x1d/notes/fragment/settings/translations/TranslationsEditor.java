package com.f0x1d.notes.fragment.settings.translations;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.BuildConfig;
import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.translations.EditTranslation;
import com.f0x1d.notes.utils.translations.IncorrectTranslationError;
import com.f0x1d.notes.utils.translations.Translations;
import com.f0x1d.notes.view.CenteredToolbar;
import com.f0x1d.notes.view.theming.MyEditText;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class TranslationsEditor extends Fragment {

    public HashMap<Integer, EditTranslation> translations = new HashMap<>();
    private CenteredToolbar toolbar;
    private RecyclerView recyclerView;
    private TranslationsEditAdapter adapter;
    private File translationToEdit;
    private List<Pair<String, String>> keys;
    private HashMap<String, String> valuesAndKeys = null;

    public static TranslationsEditor newInstance(String pathToFile) {
        Bundle args = new Bundle();
        if (pathToFile != null)
            args.putString("path", pathToFile);

        TranslationsEditor fragment = new TranslationsEditor();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments().getString("path") != null)
            translationToEdit = new File(getArguments().getString("path"));
        View view = inflater.inflate(R.layout.translations_layout, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.edit_translation);
        toolbar.setTitle(getString(R.string.edit_translation));
        toolbar.getMenu().findItem(R.id.apply).setIcon(UselessUtils.getDrawableForToolbar(R.drawable.ic_done_black_24dp)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        toolbar.getMenu().findItem(R.id.apply).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (translationToEdit == null) {
                    File file = new File(new File("data/data/" + getContext().getPackageName() + "/files/translations"), System.currentTimeMillis() + " translation.txt");
                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                        writer.write(Translations.createTranslation(adapter.getTranslations()));
                        writer.flush();
                        writer.close();

                        Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();

                        Translations.setCurrentTranslation(file);
                        restart();
                    } catch (IOException | IncorrectTranslationError e) {
                        Logger.log(e);
                    }
                } else {
                    File file = translationToEdit;
                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                        writer.write(Translations.createTranslation(adapter.getTranslations()));
                        writer.flush();
                        writer.close();

                        Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();

                        Translations.setCurrentTranslation(file);
                        restart();

                        MainActivity.instance.getSupportFragmentManager().popBackStack();
                    } catch (IOException | IncorrectTranslationError e) {
                        Logger.log(e);
                    }
                }
                return false;
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);

        keys = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(UselessUtils.readFile(new File(new File(Environment.getExternalStorageDirectory() + "/Notes/utils"),
                    "strings " + BuildConfig.VERSION_NAME + ".json")));
            for (int i = 0; i < array.length(); i++){
                keys.add(new Pair<>(array.getString(i), Translations.getString(array.getString(i))));
            }
        } catch (Exception e) {
            Logger.log(e);
        }

        Collections.sort(keys, new Comparator<Pair<String, String>>() {
            @Override
            public int compare(Pair<String, String> o1, Pair<String, String> o2) {
                return o1.first.compareTo(o2.first);
            }
        });

        if (translationToEdit != null) {
            try {
                valuesAndKeys = Translations.parseFileWithTranslation(translationToEdit);
            } catch (IncorrectTranslationError incorrectTranslationError) {
                Logger.log(incorrectTranslationError);
            }
        }

        adapter = new TranslationsEditAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.apply:
                if (translationToEdit == null) {
                    File file = new File(new File("data/data/" + getContext().getPackageName() + "/files/translations"), System.currentTimeMillis() + " translation.txt");
                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                        writer.write(Translations.createTranslation(adapter.getTranslations()));
                        writer.flush();
                        writer.close();

                        Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();

                        Translations.setCurrentTranslation(file);
                        restart();
                    } catch (IOException | IncorrectTranslationError e) {
                        Logger.log(e);
                    }
                } else {
                    File file = translationToEdit;
                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                        writer.write(Translations.createTranslation(adapter.getTranslations()));
                        writer.flush();
                        writer.close();

                        Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();

                        Translations.setCurrentTranslation(file);
                        restart();

                        MainActivity.instance.getSupportFragmentManager().popBackStack();
                    } catch (IOException | IncorrectTranslationError e) {
                        Logger.log(e);
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void restart() {
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("change_l", true).apply();

        Intent i1 = getActivity().getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
        i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(i1);
        getActivity().finish();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_translation, menu);
        menu.findItem(R.id.apply).setIcon(UselessUtils.getDrawableForToolbar(R.drawable.ic_done_black_24dp)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public class TranslationsEditAdapter extends RecyclerView.Adapter<TranslationsEditAdapter.EditTextViewHolder> {

        public TranslationsEditAdapter() {
            if (valuesAndKeys == null)
                return;

            for (int i = 0; i < keys.size(); i++) {
                if (valuesAndKeys.get(keys.get(i).first) != null) {
                    translations.put(i, new EditTranslation(keys.get(i).first, valuesAndKeys.get(keys.get(i).first)));
                }
            }
        }

        @NonNull
        @Override
        public EditTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new EditTextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull TranslationsEditAdapter.EditTextViewHolder holder, int position) {
            holder.editText.setHint(keys.get(position).second + " (" + keys.get(position).first + ")");

            holder.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    translations.put(position, new EditTranslation(keys.get(position).first, s.toString()));
                }
            });

            if (valuesAndKeys != null) {
                if (valuesAndKeys.get(keys.get(position).first) != null) {
                    holder.editText.setText(valuesAndKeys.get(keys.get(position).first));
                }
            }

            if (translations.get(position) != null) {
                holder.editText.setText(translations.get(position).value);
            }
        }

        @Override
        public void onViewRecycled(@NonNull TranslationsEditAdapter.EditTextViewHolder holder) {
            super.onViewRecycled(holder);
            holder.editText.clearTextChangedListeners();
            holder.editText.setText("");
        }

        @Override
        public int getItemCount() {
            return keys.size();
        }

        public HashMap<String, String> getTranslations() {
            HashMap<String, String> map = new HashMap<>();

            for (int i = 0; i < getItemCount(); i++) {
                EditTranslation translation = translations.get(i);
                try {
                    map.put(translation.key, translation.value);
                } catch (Exception e) {
                    continue;
                }
            }

            return map;
        }

        public class EditTextViewHolder extends RecyclerView.ViewHolder {

            public MyEditText editText;

            public EditTextViewHolder(@NonNull View itemView) {
                super(itemView);
                editText = itemView.findViewById(R.id.edit_text);
            }
        }
    }

}
