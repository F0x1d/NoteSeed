package com.f0x1d.notes.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.db.daos.NoteItemsDao;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.fragment.editing.NoteEdit;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.bottomSheet.BottomSheetCreator;
import com.f0x1d.notes.utils.bottomSheet.Element;
import com.f0x1d.notes.view.theming.MyEditText;

import java.lang.reflect.InvocationHandler;
import java.util.List;

public class NoteItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int CHECKBOX = 2;

    List<NoteItem> noteItems;
    Activity activity;

    NoteItemsDao dao;

    TextWatcher textWatcher = null;

    public NoteItemsAdapter(List<NoteItem> noteItems, Activity activity) {
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
        if (viewType == TEXT) {
            return new textViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false));
        } else if (viewType == IMAGE) {
            return new imageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false));
        } else if (viewType == CHECKBOX) {
            return new checkBoxViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkbox, parent, false));
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        dao = App.getInstance().getDatabase().noteItemsDao();

        switch (holder.getItemViewType()) {
            case TEXT:
                setupText((textViewHolder) holder, position);
                break;
            case IMAGE:
                setupImage((imageViewHolder) holder, position);
                break;
            case CHECKBOX:
                setCheckbox((checkBoxViewHolder) holder, position);
                break;
        }
    }

    private void setCheckbox(checkBoxViewHolder holder, int position) {
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
                    if (!App.getInstance().getClass().getName().equals("com.f0x1d.notes.App")) {
                        return;
                    }
                    if (InvocationHandler.class.isAssignableFrom(App.class)) {
                        return;
                    }
                    if (UselessUtils.ifPMSHook()) {
                        return;
                    }

                    dao.updateElementTextByPos(s.toString(), noteItems.get(position).to_id, noteItems.get(position).position);
                    dao.updateNoteTime(System.currentTimeMillis(), noteItems.get(position).to_id);
                } catch (Exception e) {}
            }
        };

        holder.editText.clearTextChangedListeners();

        holder.editText.setTextSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(activity).getString("text_size", "15")));

        Typeface face;
        if (UselessUtils.getBool("mono", false)) {
            face = Typeface.MONOSPACE;

            holder.editText.setTypeface(face);
        }

        for (NoteItem noteItem : dao.getAll()) {
            if (noteItem.id == noteItems.get(position).id) {
                holder.editText.setText(getText(noteItems.get(position).id));

                if (getChecked(noteItems.get(position).id) == 0)
                    holder.checkBox.setChecked(false);
                else if (getChecked(noteItems.get(position).id) == 1)
                    holder.checkBox.setChecked(true);

                break;
            }
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dao.updateIsChecked(1, noteItems.get(position).id);
                } else {
                    dao.updateIsChecked(0, noteItems.get(position).id);
                }

                dao.updateNoteTime(System.currentTimeMillis(), noteItems.get(position).to_id);
            }
        });

        holder.editText.addTextChangedListener(textWatcher);
    }

    @SuppressLint("CheckResult")
    private void setupImage(imageViewHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .placeholder(new ColorDrawable(Color.WHITE));
        if (!PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("shakal", true)){
            options
                    .dontTransform()
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        } else
            options.fitCenter();

        Glide.with(App.getContext()).load(noteItems.get(position).pic_res).apply(options).into(holder.image);
    }

    private void setupText(textViewHolder holder, int position) {
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
                    if (!App.getInstance().getClass().getName().equals("com.f0x1d.notes.App")) {
                        return;
                    }
                    if (InvocationHandler.class.isAssignableFrom(App.class)) {
                        return;
                    }
                    if (UselessUtils.ifPMSHook()) {
                        return;
                    }

                    dao.updateElementTextByPos(s.toString(), noteItems.get(position).to_id, noteItems.get(position).position);
                    dao.updateNoteTime(System.currentTimeMillis(), noteItems.get(position).to_id);
                } catch (Exception e) {}
            }
        };

        holder.editText.clearTextChangedListeners();

        holder.editText.setTextSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(activity).getString("text_size", "15")));

        Typeface face;
        if (UselessUtils.getBool("mono", false)) {
            face = Typeface.MONOSPACE;

            holder.editText.setTypeface(face);
        }

        for (NoteItem noteItem : dao.getAll()) {
            if (noteItem.id == noteItems.get(position).id) {
                holder.editText.setText(getText(noteItems.get(position).id));
                break;
            }
        }

        holder.editText.addTextChangedListener(textWatcher);
    }

    private String getText(long id) {
        String text = "";

        for (NoteItem noteItem : dao.getAll()) {
            if (noteItem.id == id) {
                text = noteItem.text;
                break;
            }
        }

        return text;
    }

    private int getChecked(long id) {
        int checked = -1;

        for (NoteItem noteItem : dao.getAll()) {
            if (noteItem.id == id) {
                checked = noteItem.checked;
                break;
            }
        }

        return checked;
    }

    private int getPosition(long id) {
        int pos = 0;

        for (NoteItem noteItem : dao.getAll()) {
            if (noteItem.id == id) {
                pos = noteItem.position;
                break;
            }
        }

        return pos;
    }

    public void onItemMoved(int fromPosition, int toPosition) {
        dao.updateElementPos(toPosition, noteItems.get(fromPosition).id);
        Log.e("notes_err", "updated: " + getText(noteItems.get(fromPosition).id) + " from: " + fromPosition + " to: " + toPosition);

        NoteItem targetUser = noteItems.get(fromPosition);
        noteItems.remove(fromPosition);
        noteItems.add(toPosition, targetUser);

        if (fromPosition < toPosition) {
            for (int i = 0; i < noteItems.size(); i++) {
                if (getPosition(noteItems.get(i).id) != i) {
                    dao.updateElementPos(i, noteItems.get(i).id);
                }
            }
        } else {
            for (int i = noteItems.size() - 1; i > 0; i--) {
                if (getPosition(noteItems.get(i).id) != i) {
                    dao.updateElementPos(i, noteItems.get(i).id);
                }
            }
        }

        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(fromPosition);
        notifyItemChanged(toPosition);
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        if (holder.getItemViewType() == TEXT) {
            textViewHolder textViewHolder = (NoteItemsAdapter.textViewHolder) holder;
            textViewHolder.editText.clearTextChangedListeners();
        } else if (holder.getItemViewType() == CHECKBOX) {
            checkBoxViewHolder checkBoxViewHolder = (NoteItemsAdapter.checkBoxViewHolder) holder;
            checkBoxViewHolder.editText.clearTextChangedListeners();
        } else if (holder.getItemViewType() == IMAGE) {
            imageViewHolder imageViewHolder = (NoteItemsAdapter.imageViewHolder) holder;
            Glide.with(activity).clear(imageViewHolder.image);
        }
    }

    @Override
    public int getItemCount() {
        return noteItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        NoteItem item = noteItems.get(position);

        if (item.type == 0) {
            if (item.pic_res == null) {
                return TEXT;
            } else {
                return IMAGE;
            }
        } else if (item.type == 1) {
            return CHECKBOX;
        } else {
            throw new RuntimeException();
        }
    }

    public static long getId() {
        long max_id = 0;

        for (NoteItem noteItem : App.getInstance().getDatabase().noteItemsDao().getAll()) {
            if (noteItem.id > max_id) {
                max_id = noteItem.id;
            }
        }

        return max_id + 1;
    }

    public void delete(int position) {
        BottomSheetCreator creator = new BottomSheetCreator((FragmentActivity) activity);
        creator.addElement(new Element(activity.getString(R.string.delete), activity.getDrawable(R.drawable.ic_done_white_24dp), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dao.updateNoteTime(System.currentTimeMillis(), noteItems.get(position).to_id);

                    dao.deleteItem(noteItems.get(position).id);
                    remove(position);

                    for (int i = 0; i < noteItems.size(); i++) {
                        if (getPosition(noteItems.get(i).id) != i) {
                            dao.updateElementPos(i, noteItems.get(i).id);
                        }
                    }

                    NoteEdit.last_pos = NoteEdit.last_pos - 1;

                    notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("notes_err", e.getLocalizedMessage());
                    Toast.makeText(activity, "error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                try {
                    creator.customBottomSheet.dismiss();
                } catch (Exception e) {}
            }
        }));
        creator.addElement(new Element(activity.getString(R.string.cancel), activity.getDrawable(R.drawable.ic_clear_white_24dp), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(position);

                try {
                    creator.customBottomSheet.dismiss();
                } catch (Exception e) {
                }
            }
        }));
        creator.show("", false);
    }

    private void add(int pos, NoteItem item) {
        try {
            noteItems.add(pos, item);
        } catch (IndexOutOfBoundsException e) {
            add(pos - 1, item);
        }
    }

    private void remove(int pos) {
        try {
            noteItems.remove(pos);
        } catch (IndexOutOfBoundsException e) {
            remove(pos - 1);
        }
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

    class checkBoxViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        MyEditText editText;

        public checkBoxViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkBox);
            editText = itemView.findViewById(R.id.edit_text);
        }
    }
}
