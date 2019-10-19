package com.f0x1d.notes.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.fragment.choose.ChooseFolderFragment;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.view.theming.ItemCardView;

import java.util.List;

public class ChooseFolderAdapter extends RecyclerView.Adapter<ChooseFolderAdapter.FolderViewHolder> {

    public AppCompatActivity activity;
    List<NoteOrFolder> items;
    long noteId;
    NoteOrFolderDao dao = App.getInstance().getDatabase().noteOrFolderDao();

    public ChooseFolderAdapter(List<NoteOrFolder> notes, long noteId, AppCompatActivity activity) {
        this.items = notes;
        this.noteId = noteId;
        this.activity = activity;
    }

    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        initLayoutFolder(holder, position);
    }

    private void initLayoutFolder(FolderViewHolder holder, int position) {
        boolean dark;
        try {
            holder.cardView.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(getColorFromDataBase(position))));
            dark = !UselessUtils.ifBrightColor(Color.parseColor(getColorFromDataBase(position)));
        } catch (Exception e) {
            holder.cardView.setThemedCardBackgroundColor();
            dark = !UselessUtils.ifBrightColor(holder.cardView.getCardBackgroundColor().getDefaultColor());
        }
        if (dark) {
            holder.name.setTextColor(Color.WHITE);
            holder.folderImage.setImageDrawable(activity.getDrawable(R.drawable.ic_folder_white_24dp));
        } else {
            holder.name.setTextColor(Color.BLACK);
            holder.folderImage.setImageDrawable(activity.getDrawable(R.drawable.ic_folder_black_24dp));
        }

        holder.name.setText(getFolderNameFromDataBase(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String getColorFromDataBase(int position) {
        long id = items.get(position).id;
        return dao.getById(id).color;
    }

    private String getFolderNameFromDataBase(int position) {
        long id = items.get(position).id;
        return dao.getById(id).folderName;
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ItemCardView cardView;
        public ImageView folderImage;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);

            folderImage = itemView.findViewById(R.id.folder_image);
            cardView = itemView.findViewById(R.id.note_card);
            name = itemView.findViewById(R.id.name);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UselessUtils.replace(activity, ChooseFolderFragment.newInstance(noteId, items.get(getAdapterPosition()).folderName), "choose_folder", true, null);
                }
            });
        }
    }
}
