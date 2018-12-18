package com.f0x1d.notes.fragment.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.f0x1d.notes.R;
import com.f0x1d.notes.App;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.view.CenteredToolbar;

import androidx.annotation.Nullable;

public class DebugSettings extends PreferenceFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings, container, false);

        CenteredToolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.debug));
        getActivity().setActionBar(toolbar);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.debug);

        Preference delete_all = findPreference("delete_all");
        delete_all.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                removeAll();
                Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public void removeAll()
    {
        NoteOrFolderDao dao = App.getInstance().getDatabase().noteOrFolderDao();
            dao.nukeTable();
            dao.nukeTable2();
    }
}
