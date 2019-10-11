package com.f0x1d.notes.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.db.daos.NoteItemsDao;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.fragment.editing.NoteAdd;
import com.f0x1d.notes.fragment.editing.NoteEdit;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.bottomSheet.BottomSheetCreator;
import com.f0x1d.notes.utils.bottomSheet.Element;
import com.f0x1d.notes.view.theming.MyEditText;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.f0x1d.notes.App.getContext;

public class NoteItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int CHECKBOX = 2;
    public static final int FILE = 3;

    public boolean editMode = false;
    List<NoteItem> noteItems;
    Activity activity;
    NoteItemsDao dao;
    TextWatcher textWatcher = null;
    Fragment fragment;

    List<EditText> editTexts = new ArrayList<>();

    public boolean openedKeyboard = false;
    private boolean openKeyboard;

    public NoteItemsAdapter(List<NoteItem> noteItems, Activity activity, Fragment fragment, boolean openKeyboard) {
        this.noteItems = noteItems;
        this.activity = activity;
        this.fragment = fragment;
        this.openKeyboard = openKeyboard;

        setHasStableIds(true);
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
        } else if (viewType == FILE) {
            return new fileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false));
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (fragment instanceof NoteAdd)
            editMode = true;
        else if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("auto_editmode", false))
            editMode = true;

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
            case FILE:
                setFile((fileViewHolder) holder, position);
                break;
        }
    }

    private void setFile(fileViewHolder holder, int position) {
        holder.editText.setTextSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(activity).getString("text_size", "15")));

        Typeface face;
        if (UselessUtils.getBool("mono", false)) {
            face = Typeface.MONOSPACE;

            holder.editText.setTypeface(face);
        }
        holder.editText.setMovementMethod(LinkMovementMethod.getInstance());

        for (NoteItem noteItem : dao.getAll()) {
            if (noteItem.id == noteItems.get(position).id) {
                String fileName = getFileNameFromUri(Uri.parse(noteItem.pic_res));
                holder.editText.setText(fileName);
                holder.editText.setFocusableInTouchMode(false);
                holder.editText.setFocusable(false);

                holder.icon.setImageDrawable(UselessUtils.getDrawableForToolbar(R.drawable.ic_insert_drive_file_black_24dp));

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(Uri.parse(noteItem.pic_res), "*/*");
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            Logger.log(e);
                        }
                    }
                };

                holder.background.setOnClickListener(clickListener);
                holder.icon.setOnClickListener(clickListener);
                holder.itemView.setOnClickListener(clickListener);
                holder.editText.setOnClickListener(clickListener);
                break;
            }
        }

        try {
            editTexts.remove(position);
        } catch (Exception e) {

        }
        editTexts.add(holder.editText);

        if (getItemCount() != 1) {
            ViewGroup.LayoutParams layoutParams = firstEditText.getLayoutParams();
            if (layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                firstEditText.setLayoutParams(layoutParams);
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
        if (uri.getPath() == null)
            return "???";

        String[] pathParts = uri.getPath().split("/");
        String fallbackName = pathParts[pathParts.length - 1];

        try (Cursor cursor = getContext().getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor == null)
                return fallbackName;

            cursor.moveToFirst();
            String name = cursor.getString(0);

            if (name == null)
                return fallbackName;

            return name;
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
                    dao.updateElementTextByPos(s.toString(), noteItems.get(position).to_id, noteItems.get(position).position);
                    dao.updateNoteTime(System.currentTimeMillis(), noteItems.get(position).to_id);
                } catch (Exception e) {
                }
            }
        };

        holder.editText.setHint(activity.getString(R.string.note));

        holder.editText.clearTextChangedListeners();

        holder.editText.setTextSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(activity).getString("text_size", "15")));

        Typeface face;
        if (UselessUtils.getBool("mono", false)) {
            face = Typeface.MONOSPACE;

            holder.editText.setTypeface(face);
        }
        holder.editText.setMovementMethod(LinkMovementMethod.getInstance());

        for (NoteItem noteItem : dao.getAll()) {
            if (noteItem.id == noteItems.get(position).id) {
                if (editMode) {
                    holder.editText.setText(getText(noteItems.get(position).id));
                    holder.editText.setOnClickListener(null);
                } else {
                    holder.editText.setText(Html.fromHtml(getText(noteItems.get(position).id).replace("\n", "<br />")));
                    holder.editText.setFocusableInTouchMode(false);
                    holder.editText.setFocusable(false);
                    holder.editText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((NoteEdit) fragment).enterEditMode();
                        }
                    });
                }

                holder.checkBox.setOnCheckedChangeListener(null);

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

        try {
            editTexts.remove(position);
        } catch (Exception e) {

        }
        editTexts.add(holder.editText);

        if (getItemCount() != 1) {
            ViewGroup.LayoutParams layoutParams = firstEditText.getLayoutParams();
            if (layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                firstEditText.setLayoutParams(layoutParams);
            }
        }
    }

    @SuppressLint("CheckResult")
    private void setupImage(imageViewHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .placeholder(new ColorDrawable(Color.WHITE));
        if (!PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("shakal", true)) {
            options
                    .dontTransform()
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        } else
            options.fitCenter();

        Glide.with(getContext()).load(noteItems.get(position).pic_res).apply(options).into(holder.image);

        if (getItemCount() != 1) {
            ViewGroup.LayoutParams layoutParams = firstEditText.getLayoutParams();
            if (layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                firstEditText.setLayoutParams(layoutParams);
            }
        }
    }

    public boolean hasAnySelection() {
        for (EditText editText : editTexts) {
            if (editText != null && editText.hasSelection()) {
                return true;
            }
        }
        return false;
    }

    public boolean applyFormat(String formatType, String link) {
        for (EditText editText : editTexts) {
            if (editText != null && editText.hasSelection()) {
                String text = editText.getText().toString();

                String formattedText = null;
                switch (formatType) {
                    case "bold":
                        formattedText = "<b>" + text.substring(editText.getSelectionStart(), editText.getSelectionEnd()) + "</b>";
                        break;
                    case "italic":
                        formattedText = "<i>" + text.substring(editText.getSelectionStart(), editText.getSelectionEnd()) + "</i>";
                        break;
                    case "link":
                        if (!link.startsWith("http"))
                            link = "https://" + link;
                        formattedText = "<a href=\"" + link + "\">" + text.substring(editText.getSelectionStart(), editText.getSelectionEnd()) + "</a>";
                        break;

                        default:
                            formattedText = text.substring(editText.getSelectionStart(), editText.getSelectionEnd());
                }

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(text);
                stringBuilder.replace(editText.getSelectionStart(),
                        editText.getSelectionEnd(),
                        formattedText);

                editText.setText(stringBuilder);
                return true;
            }
        }
        return false;
    }

    public MyEditText firstEditText;

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
                    dao.updateElementTextByPos(s.toString(), noteItems.get(position).to_id, noteItems.get(position).position);
                    dao.updateNoteTime(System.currentTimeMillis(), noteItems.get(position).to_id);
                } catch (Exception e) {
                }
            }
        };

        holder.editText.setHint(activity.getString(R.string.note));

        holder.editText.clearTextChangedListeners();

        holder.editText.setTextSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(activity).getString("text_size", "15")));

        Typeface face;
        if (UselessUtils.getBool("mono", false)) {
            face = Typeface.MONOSPACE;

            holder.editText.setTypeface(face);
        }
        holder.editText.setMovementMethod(LinkMovementMethod.getInstance());

        for (NoteItem noteItem : dao.getAll()) {
            if (noteItem.id == noteItems.get(position).id) {
                if (editMode) {
                    holder.editText.setText(getText(noteItems.get(position).id));
                    holder.editText.setFocusableInTouchMode(true);
                    holder.editText.setFocusable(true);
                    holder.editText.setOnClickListener(null);
                } else {
                    holder.editText.setText(Html.fromHtml(getText(noteItems.get(position).id).replace("\n", "<br />")));
                    holder.editText.setFocusableInTouchMode(false);
                    holder.editText.setFocusable(false);
                    holder.editText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((NoteEdit) fragment).enterEditMode();
                        }
                    });
                }
                break;
            }
        }

        if (openKeyboard) {
            if (!openedKeyboard) {
                holder.editText.requestFocus();
                UselessUtils.showKeyboard(holder.editText, activity);
                openedKeyboard = true;
            }
        }

        holder.editText.addTextChangedListener(textWatcher);

        try {
            editTexts.remove(position);
        } catch (Exception e) {

        }
        editTexts.add(holder.editText);

        if (position == 0)
            firstEditText = holder.editText;

        if (getItemCount() == 1 && position == 0) {
            ViewGroup.LayoutParams layoutParams = firstEditText.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            firstEditText.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = firstEditText.getLayoutParams();
            if (layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                firstEditText.setLayoutParams(layoutParams);
            }
        }
    }

    public void setEditing(boolean editing) {
        editMode = editing;

        for (int i = 0; i < noteItems.size(); i++) {
            notifyItemChanged(i);
        }
    }

    public String getText(long id) {
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
        } else if (item.type == 2) {
            return FILE;
        } else {
            throw new RuntimeException();
        }
    }

    public void delete(int position, View rootView) {
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
                    Logger.log(e);
                    Toast.makeText(activity, "error: " + e, Toast.LENGTH_SHORT).show();
                }

                Snackbar.make(rootView, activity.getString(R.string.deleted), Snackbar.LENGTH_SHORT).show();

                try {
                    creator.customBottomSheet.dismiss();
                } catch (Exception e) {
                }
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

        public ImageView image;

        public imageViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.picture);
        }
    }

    class textViewHolder extends RecyclerView.ViewHolder {

        public MyEditText editText;

        public textViewHolder(@NonNull View itemView) {
            super(itemView);

            editText = itemView.findViewById(R.id.edit_text);
        }
    }

    class fileViewHolder extends RecyclerView.ViewHolder {

        public MyEditText editText;
        public ImageView icon;
        public RelativeLayout background;

        public fileViewHolder(@NonNull View itemView) {
            super(itemView);

            editText = itemView.findViewById(R.id.edit_text);
            icon = itemView.findViewById(R.id.icon);
            background = itemView.findViewById(R.id.background);
        }
    }

    class checkBoxViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;
        public MyEditText editText;

        public checkBoxViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkBox);
            editText = itemView.findViewById(R.id.edit_text);
        }
    }
}
