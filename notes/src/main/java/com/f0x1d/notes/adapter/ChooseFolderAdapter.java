package com.f0x1d.notes.adapter;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.fragment.choose.ChooseFolder;
import com.f0x1d.notes.fragment.main.NotesInFolder;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

import java.util.List;

public class ChooseFolderAdapter extends RecyclerView.Adapter<ChooseFolderAdapter.folderViewHolder> {

    List<NoteOrFolder> items;
    Activity activity;
    long note_id;

    NoteOrFolderDao dao = App.getInstance().getDatabase().noteOrFolderDao();

    public ChooseFolderAdapter(List<NoteOrFolder> notes, Activity activity, long note_id){
        this.items = notes;
        this.activity = activity;
        this.note_id = note_id;
    }

    @Override
    public folderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new folderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull folderViewHolder holder, int position) {
        initLayoutFolder(holder, position);
    }

    private void initLayoutFolder(folderViewHolder holder, int position) {
        try {
            holder.cardView.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(getColorFromDataBase(position))));

            if (UselessUtils.ifBrightColor(Color.parseColor(getColorFromDataBase(position)))){
                if (UselessUtils.ifCustomTheme()){
                    holder.name.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.folder_image.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_folder_black_24dp), ThemesEngine.lightColorIconColor));
                    holder.pinned.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_priority_high_black_24dp), ThemesEngine.lightColorIconColor));
                } else {
                    holder.name.setTextColor(Color.BLACK);
                    holder.folder_image.setImageDrawable(activity.getDrawable(R.drawable.ic_folder_black_24dp));
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_black_24dp));
                }
            } else {
                if (UselessUtils.ifCustomTheme()){
                    holder.name.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.folder_image.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_folder_white_24dp), ThemesEngine.darkColorIconColor));
                    holder.pinned.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_priority_high_white_24dp), ThemesEngine.darkColorIconColor));
                } else {
                    holder.name.setTextColor(Color.WHITE);
                    holder.folder_image.setImageDrawable(activity.getDrawable(R.drawable.ic_folder_white_24dp));
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_white_24dp));
                }
            }
        } catch (Exception e){
            if (UselessUtils.ifCustomTheme()){
                holder.cardView.setCardBackgroundColor(Color.BLACK);
            }

            if (UselessUtils.ifBrightColor(holder.cardView.getCardBackgroundColor().getDefaultColor())){
                if (UselessUtils.ifCustomTheme()){
                    holder.name.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.folder_image.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_folder_black_24dp), ThemesEngine.lightColorIconColor));
                    holder.pinned.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_priority_high_black_24dp), ThemesEngine.lightColorIconColor));
                } else {
                    holder.name.setTextColor(Color.BLACK);
                    holder.folder_image.setImageDrawable(activity.getDrawable(R.drawable.ic_folder_black_24dp));
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_black_24dp));
                }
            } else {
                if (UselessUtils.ifCustomTheme()){
                    holder.name.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.folder_image.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_folder_white_24dp), ThemesEngine.darkColorIconColor));
                    holder.pinned.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_priority_high_white_24dp), ThemesEngine.darkColorIconColor));
                } else {
                    holder.name.setTextColor(Color.WHITE);
                    holder.folder_image.setImageDrawable(activity.getDrawable(R.drawable.ic_folder_white_24dp));
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_white_24dp));
                }
            }
        }

        if (items.get(position).pinned == 1){
            holder.pinned.setVisibility(View.VISIBLE);
        } else {
            holder.pinned.setVisibility(View.INVISIBLE);
        }

        holder.name.setText(getFolderNameFromDataBase(position));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                ChooseFolder.in_ids.add(getFolderNameFromDataBase(position));
                args.putLong("id", note_id);

                activity.getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, ChooseFolder.newInstance(args), "choose_folder")
                        .addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String getColorFromDataBase(int position){
        long id = items.get(position).id;

        String color = "0xffffffff";

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.id == id){
                color = noteOrFolder.color;
            }
        }

        return color;
    }

    private String getFolderNameFromDataBase(int position){
        long id = items.get(position).id;

        String name = "";

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.id == id){
                name = noteOrFolder.folder_name;
            }
        }

        return name;
    }

    class folderViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        CardView cardView;
        ImageView folder_image;
        ImageView pinned;

        folderViewHolder(@NonNull View itemView) {
            super(itemView);

            folder_image = itemView.findViewById(R.id.folder_image);
            cardView = itemView.findViewById(R.id.note_card);
            name = itemView.findViewById(R.id.name);
            pinned = itemView.findViewById(R.id.pinned);
        }
    }
}
