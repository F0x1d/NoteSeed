package com.f0x1d.notes.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.db.daos.NoteItemsDao;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.fragment.editing.NoteEdit;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.view.theming.MyEditText;

import java.util.List;

public class NoteItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TEXT = 0;
    public static final int IMAGE = 1;

    List<NoteItem> noteItems;
    Activity activity;

    NoteItemsDao dao;

    TextWatcher textWatcher = null;

    public NoteItemsAdapter(List<NoteItem> noteItems, Activity activity){
        this.noteItems = noteItems;
        this.activity = activity;

        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return noteItems.get(position).id;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TEXT){
            return new textViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false));
        } else if (viewType == IMAGE){
            return new imageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false));
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        dao = App.getInstance().getDatabase().noteItemsDao();

        switch (holder.getItemViewType()){
            case TEXT:
                setupText((textViewHolder) holder, position);
                break;
            case IMAGE:
                setupImage((imageViewHolder) holder, position);
                break;
        }
    }

    private void setupImage(imageViewHolder holder, int position){
        Log.e("notes_err", "image setup: " + position);

        RequestOptions options = new RequestOptions()
                .placeholder(new ColorDrawable(Color.WHITE))
                .dontTransform()
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

        Glide.with(App.getContext()).load(noteItems.get(position).pic_res).apply(options).into(holder.image);

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                delete(position);
                return false;
            }
        });
    }

    private void setupText(textViewHolder holder, int position){
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (s.toString().length() == 0){
                        if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("dark_fon", false)){
                            holder.editText.setHintTextColor(Color.GRAY);
                        }
                    }

                    //dao.updateElementText(s.toString(), noteItems.get(position).id);
                    dao.updateElementTextByPos(s.toString(), noteItems.get(position).to_id, noteItems.get(position).position);
                    dao.updateNoteTime(System.currentTimeMillis(), noteItems.get(position).to_id);
                } catch (Exception e){}
            }
        };

        holder.editText.clearTextChangedListeners();

        Log.e("notes_err", "text setup: " + position);

        holder.editText.setTextSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(activity).getString("text_size", "15")));

        Typeface face;
        if (UselessUtils.getBool("mono", false)){
            face = Typeface.MONOSPACE;

            holder.editText.setTypeface(face);
        }

        for (NoteItem noteItem : dao.getAll()) {
            if ((noteItem.id == noteItems.get(position).id) || (noteItem.position == noteItems.get(position).position && noteItem.to_id == noteItems.get(position).to_id)){
                holder.editText.setText(noteItem.text);
                break;
            }
        }

        if (PreferenceManager.getDefaultSharedPreferences(activity).getInt("fon", 0) == 1){
            if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("dark_fon", false)){
                holder.editText.setTextColor(Color.WHITE);
            } else {
                holder.editText.setTextColor(Color.BLACK);
            }
        }

        holder.editText.addTextChangedListener(textWatcher);

    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        if (holder.getItemViewType() == TEXT){
            textViewHolder textViewHolder = (NoteItemsAdapter.textViewHolder) holder;
            textViewHolder.editText.clearTextChangedListeners();
        }
    }

    @Override
    public int getItemCount() {
        return noteItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        NoteItem item = noteItems.get(position);

        if (item.pic_res == null){
            return TEXT;
        } else {
            return IMAGE;
        }
    }

    public String getText(long id){
        String text = "error";

        for (NoteItem noteItem : dao.getAll()) {
            if (noteItem.id == id){
                text = noteItem.text;
                break;
            }
        }

        return text;
    }

    public void delete(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(R.string.confirm_delete);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dao.updateNoteTime(System.currentTimeMillis(), noteItems.get(position).to_id);

                    for (NoteItem noteItem : dao.getAll()) {
                        if (noteItem.to_id == noteItems.get(position).to_id && noteItem.position - 1 == noteItems.get(position).position){
                            int pos = noteItem.position - 2;
                            NoteItem elem = noteItems.get(pos);

                            String text = getText(elem.id) + "\n" + getText(noteItem.id);

                            dao.updateElementTextByPos(text, elem.to_id, elem.position);
                            dao.deleteByPos(noteItem.to_id, noteItem.position);
                            noteItems.remove(noteItem.position);
                        }
                    }

                    dao.deleteByPos(noteItems.get(position).to_id, position);
                    noteItems.remove(position);

                    NoteEdit.last_pos = NoteEdit.last_pos - 2;
                    notifyDataSetChanged();
                } catch (Exception e){
                    Log.e("notes_err", e.getLocalizedMessage());
                    Toast.makeText(activity, "error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog1337 =  builder.create();

        dialog1337.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog1) {
                if (PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("night", false)){
                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    dialog1337.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
                }
                if (UselessUtils.ifCustomTheme()){
                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ThemesEngine.textColor);
                    dialog1337.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(ThemesEngine.textColor);

                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                    dialog1337.getButton(DialogInterface.BUTTON_NEUTRAL).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                }
            }
        });

        dialog1337.show();
    }

    class imageViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public imageViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.picture);
        }
    }

    class textViewHolder extends RecyclerView.ViewHolder {

        MyEditText editText;

        public textViewHolder(@NonNull View itemView) {
            super(itemView);

            editText = itemView.findViewById(R.id.edit_text);
        }
    }
}
