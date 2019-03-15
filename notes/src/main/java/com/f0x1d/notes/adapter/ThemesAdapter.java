package com.f0x1d.notes.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.model.Theme;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

import java.util.List;

public class ThemesAdapter extends RecyclerView.Adapter<ThemesAdapter.ThemeViewHolder> {

    List<Theme> themes;

    Activity activity;
    boolean anim;

    public ThemesAdapter(List<Theme> themes, Activity activity, boolean anim){
        this.activity = activity;
        this.themes = themes;
        this.anim = anim;
    }

    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme, parent, false);

        if (anim){
            Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.push_down);
            animation.setDuration(550);
            view.startAnimation(animation);
        }

        return new ThemeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, int position) {

        if (!UselessUtils.ifBrightColor(themes.get(position).card_color)){
            holder.name.setTextColor(Color.WHITE);
            holder.author.setTextColor(Color.WHITE);
        } else {
            holder.name.setTextColor(Color.BLACK);
            holder.author.setTextColor(Color.BLACK);
        }

        holder.name.setText(themes.get(position).name);
            holder.name.setTextColor(themes.get(position).card_text_color);
        holder.author.setText(themes.get(position).author);
            holder.author.setTextColor(themes.get(position).card_text_color);

        holder.card.setCardBackgroundColor(themes.get(position).card_color);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardClick(position);
            }
        });

        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cardLongClick(position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

    private void cardLongClick(int position){
        if (position == 0){
            Toast.makeText(activity, "Nope", Toast.LENGTH_SHORT).show();
        } else if (position == 1){
            Toast.makeText(activity, "Nope", Toast.LENGTH_SHORT).show();
        } else if (position == 2){
            Toast.makeText(activity, "Nope", Toast.LENGTH_SHORT).show();
        } else {
            String[] variants = {activity.getString(R.string.delete)};

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setItems(variants, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                            try {
                                if (PreferenceManager.getDefaultSharedPreferences(activity).getString("path_theme", "").equals(themes.get(position).theme_file.getAbsolutePath())){

                                    themes.get(position).theme_file.delete();

                                    PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("night", false).apply();
                                    PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("change", true).apply();
                                    PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("orange", false).apply();
                                    PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();

                                    Intent i1 = activity.getBaseContext().getPackageManager().
                                            getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
                                    i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    activity.startActivity(i1);
                                    activity.finish();
                                } else {
                                    themes.get(position).theme_file.delete();
                                }
                            } catch (Exception e){}

                            themes.remove(position);
                            notifyDataSetChanged();
                            break;
                    }
                }
            });

            AlertDialog dialog1337 = builder.create();

            dialog1337.show();

            if (UselessUtils.ifCustomTheme())
                dialog1337.getWindow().getDecorView().getBackground().setColorFilter(ThemesEngine.background, PorterDuff.Mode.SRC);
            else if (UselessUtils.getBool("night", false))
                dialog1337.getWindow().getDecorView().getBackground().setColorFilter(activity.getResources().getColor(R.color.statusbar_for_dialogs), PorterDuff.Mode.SRC);
            else
                dialog1337.getWindow().getDecorView().getBackground().setColorFilter(activity.getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC);
        }
    }

    private void cardClick(int position){
        if (position == 0){
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("night", false).apply();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("change", true).apply();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("orange", false).apply();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();

            Intent i = activity.getBaseContext().getPackageManager().
                    getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(i);
            activity.finish();
        } else if (position == 1){
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("night", false).apply();
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("change", true).apply();
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("orange", true).apply();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();

            Intent i = activity.getBaseContext().getPackageManager().
                    getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(i);
            activity.finish();
        } else if (position == 2){
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("night", true).apply();
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("change", true).apply();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();

            Intent i = activity.getBaseContext().getPackageManager().
                    getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(i);
            activity.finish();
        } else {
            new ThemesEngine().setTheme(themes.get(position).theme_file, activity);
        }
    }

class ThemeViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView author;
    CardView card;

    ThemeViewHolder(@NonNull View itemView) {
        super(itemView);

        author = itemView.findViewById(R.id.author);
        name = itemView.findViewById(R.id.name);
        card = itemView.findViewById(R.id.card);
    }
}
}
