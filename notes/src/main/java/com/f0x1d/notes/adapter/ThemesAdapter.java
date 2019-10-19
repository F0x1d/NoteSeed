package com.f0x1d.notes.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.dialogs.ShowAlertDialog;
import com.f0x1d.notes.utils.theme.Theme;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class ThemesAdapter extends RecyclerView.Adapter<ThemesAdapter.ThemeViewHolder> {

    List<Theme> themes;

    Activity activity;
    boolean anim;

    public ThemesAdapter(List<Theme> themes, Activity activity, boolean anim) {
        this.activity = activity;
        this.themes = themes;
        this.anim = anim;

        setHasStableIds(true);
    }

    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme, parent, false);

        if (anim) {
            Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.push_down);
            animation.setDuration(400);
            view.startAnimation(animation);
        }

        return new ThemeViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return (long) themes.get(position).hashCode();
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, int position) {
        if (!UselessUtils.ifBrightColor(themes.get(position).cardColor)) {
            holder.name.setTextColor(Color.WHITE);
            holder.author.setTextColor(Color.WHITE);
        } else {
            holder.name.setTextColor(Color.BLACK);
            holder.author.setTextColor(Color.BLACK);
        }

        holder.name.setText(themes.get(position).name);
        holder.name.setTextColor(themes.get(position).cardTextColor);
        holder.author.setText(themes.get(position).author);
        holder.author.setTextColor(themes.get(position).cardTextColor);

        holder.card.setCardBackgroundColor(themes.get(position).cardColor);
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

    private void cardLongClick(int position) {
        if (position == 0) {
            Toast.makeText(activity, "Nope", Toast.LENGTH_SHORT).show();
        } else if (position == 1) {
            Toast.makeText(activity, "Nope", Toast.LENGTH_SHORT).show();
        } else {
            String[] variants = {activity.getString(R.string.delete)};

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
            builder.setItems(variants, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            if (App.getDefaultSharedPreferences().getString("path_theme", "").equals(themes.get(position).themeFile.getAbsolutePath())) {
                                themes.get(position).themeFile.delete();

                                App.getDefaultSharedPreferences().edit().putBoolean("night", false).apply();
                                App.getDefaultSharedPreferences().edit().putBoolean("custom_theme", false).apply();

                                activity.recreate();
                            } else {
                                themes.get(position).themeFile.delete();
                            }

                            themes.remove(position);
                            notifyDataSetChanged();
                            break;
                    }
                }
            });

            ShowAlertDialog.show(builder);
        }
    }

    private void cardClick(int position) {
        if (position == 0) {
            App.getDefaultSharedPreferences().edit().putBoolean("night", false).putBoolean("custom_theme", false).apply();
        } else if (position == 1) {
            App.getDefaultSharedPreferences().edit().putBoolean("night", true).putBoolean("custom_theme", false).apply();
        } else {
            ThemesEngine.setTheme(themes.get(position).themeFile);
        }
        activity.recreate();
    }

    public class ThemeViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView author;
        public CardView card;

        public ThemeViewHolder(@NonNull View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.author);
            name = itemView.findViewById(R.id.name);
            card = itemView.findViewById(R.id.card);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardClick(getAdapterPosition());
                }
            });

            card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    cardLongClick(getAdapterPosition());
                    return false;
                }
            });
        }
    }
}
