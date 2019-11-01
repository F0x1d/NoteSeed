package com.f0x1d.notes.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.db.daos.NoteItemsDao;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.fragment.editing.NoteEditFragment;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.bottomSheet.BottomSheetCreator;
import com.f0x1d.notes.utils.bottomSheet.Element;
import com.f0x1d.notes.view.theming.MyEditText;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;

import static com.f0x1d.notes.App.getContext;

public class NoteItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int CHECKBOX = 2;
    public static final int FILE = 3;

    public boolean editMode = false;
    public boolean openedKeyboard = false;
    public MyEditText firstEditText;
    private boolean openKeyboard;
    private List<EditText> editTexts = new ArrayList<>();

    private List<NoteItem> noteItems;
    private NoteItemsDao dao;

    private Activity activity;
    private NoteEditFragment fragment;

    public NoteItemsAdapter(List<NoteItem> noteItems, Activity activity, NoteEditFragment fragment, boolean openKeyboard) {
        this.noteItems = noteItems;
        this.activity = activity;
        this.fragment = fragment;
        this.openKeyboard = openKeyboard;

        setHasStableIds(true);

        if (App.getDefaultSharedPreferences().getBoolean("auto_editmode", false))
            editMode = true;
        else if (fragment.editMode)
            editMode = true;
    }

    public static long getId() {
        long maxId = 0;
        for (NoteItem noteItem : App.getInstance().getDatabase().noteItemsDao().getAll()) {
            if (noteItem.id > maxId) {
                maxId = noteItem.id;
            }
        }

        return maxId + 1;
    }

    @Override
    public long getItemId(int position) {
        return noteItems.get(position).id;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TEXT) {
            return new TextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false));
        } else if (viewType == IMAGE) {
            return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false));
        } else if (viewType == CHECKBOX) {
            return new CheckBoxViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkbox, parent, false));
        } else if (viewType == FILE) {
            return new FileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false));
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        dao = App.getInstance().getDatabase().noteItemsDao();

        switch (holder.getItemViewType()) {
            case TEXT:
                setupText((TextViewHolder) holder, position);
                break;
            case IMAGE:
                setupImage((ImageViewHolder) holder, position);
                break;
            case CHECKBOX:
                setCheckbox((CheckBoxViewHolder) holder, position);
                break;
            case FILE:
                setFile((FileViewHolder) holder, position);
                break;
        }
    }

    private void setFile(FileViewHolder holder, int position) {
        holder.editText.setText(getFileNameFromUri(Uri.parse(noteItems.get(position).picRes)));
    }

    private void setCheckbox(CheckBoxViewHolder holder, int position) {
        if (editMode)
            holder.editText.setText(getText(noteItems.get(position).id));
        else
            holder.editText.setText(Html.fromHtml(getText(noteItems.get(position).id).replace("\n", "<br />")));

        if (getChecked(noteItems.get(position).id) == 0)
            holder.checkBox.setChecked(false);
        else if (getChecked(noteItems.get(position).id) == 1)
            holder.checkBox.setChecked(true);
    }

    @SuppressLint("CheckResult")
    private void setupImage(ImageViewHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .placeholder(new ColorDrawable(Color.WHITE));
        if (!App.getDefaultSharedPreferences().getBoolean("shakal", true)) {
            options
                    .dontTransform()
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        } else
            options.fitCenter();

        Glide.with(getContext()).load(noteItems.get(position).picRes).apply(options).into(holder.image);
    }

    private void setupText(TextViewHolder holder, int position) {
        if (editMode)
            holder.editText.setText(getText(noteItems.get(position).id));
        else
            holder.editText.setText(Html.fromHtml(getText(noteItems.get(position).id).replace("\n", "<br />")));

        if (openKeyboard) {
            if (!openedKeyboard) {
                holder.editText.requestFocus();
                UselessUtils.showKeyboard(holder.editText, activity);
                openedKeyboard = true;
            }
        }
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
                        formattedText = "<a href=\"" + link + "\">" + text.substring(editText.getSelectionStart(), editText.getSelectionEnd()) + "</a> ";
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

    public String getText(long id) {
        return dao.getById(id).text;
    }

    private int getChecked(long id) {
        return dao.getById(id).checked;
    }

    private int getPosition(long id) {
        return dao.getById(id).position;
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

        if (holder.getItemViewType() == IMAGE) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
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
            if (item.picRes == null) {
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
                    dao.updateNoteTime(System.currentTimeMillis(), noteItems.get(position).toId);

                    dao.deleteItem(noteItems.get(position).id);
                    noteItems.remove(position);

                    for (int i = 0; i < noteItems.size(); i++) {
                        if (getPosition(noteItems.get(i).id) != i) {
                            dao.updateElementPos(i, noteItems.get(i).id);
                        }
                    }

                    notifyDataSetChanged();
                } catch (Exception e) {
                    Logger.log(e);
                    Toast.makeText(activity, "error: " + e, Toast.LENGTH_SHORT).show();
                }

                Snackbar.make(rootView, activity.getString(R.string.deleted), Snackbar.LENGTH_SHORT).show();
                creator.customBottomSheet.dismiss();
            }
        }));
        creator.addElement(new Element(activity.getString(R.string.cancel), activity.getDrawable(R.drawable.ic_clear_white_24dp), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(position);
                creator.customBottomSheet.dismiss();
            }
        }));
        creator.show("", false);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.picture);
        }
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dao.updateElementTextByPos(s.toString(), noteItems.get(getAdapterPosition()).toId, noteItems.get(getAdapterPosition()).position);
                dao.updateNoteTime(System.currentTimeMillis(), noteItems.get(getAdapterPosition()).toId);
            }
        };

        public MyEditText editText;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);

            editText = itemView.findViewById(R.id.edit_text);
            if (editMode)
                editText.addTextChangedListener(textWatcher);
            else {
                editText.setFocusableInTouchMode(false);
                editText.setFocusable(false);
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragment.enterEditMode();
                    }
                });
            }

            editText.setTextSize(Integer.parseInt(App.getDefaultSharedPreferences().getString("text_size", "15")));
            if (UselessUtils.getBool("mono", false))
                editText.setTypeface(Typeface.MONOSPACE);
            BetterLinkMovementMethod.linkify(Linkify.ALL, editText);
            editText.setHint(activity.getString(R.string.note));

            if (firstEditText == null)
                firstEditText = editText;
            editTexts.add(editText);
        }
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {

        public MyEditText editText;
        public ImageView icon;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(Uri.parse(noteItems.get(getAdapterPosition()).picRes), "*/*");
                    activity.startActivity(intent);
                } catch (Exception e) {
                    Logger.log(e);
                }
            });

            icon = itemView.findViewById(R.id.icon);
            icon.setImageDrawable(UselessUtils.getDrawableForToolbar(R.drawable.ic_insert_drive_file_black_24dp));

            editText = itemView.findViewById(R.id.edit_text);
            editText.setFocusableInTouchMode(false);
            editText.setFocusable(false);
            editText.setTextSize(Integer.parseInt(App.getDefaultSharedPreferences().getString("text_size", "15")));
            if (UselessUtils.getBool("mono", false))
                editText.setTypeface(Typeface.MONOSPACE);

            editTexts.add(editText);
        }
    }

    public class CheckBoxViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;
        public MyEditText editText;
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    dao.updateElementTextByPos(s.toString(), noteItems.get(getAdapterPosition()).toId, noteItems.get(getAdapterPosition()).position);
                    dao.updateNoteTime(System.currentTimeMillis(), noteItems.get(getAdapterPosition()).toId);
                } catch (Exception e) {
                }
            }
        };

        public CheckBoxViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkBox);
            editText = itemView.findViewById(R.id.edit_text);
            if (editMode)
                editText.addTextChangedListener(textWatcher);
            else {
                editText.setFocusableInTouchMode(false);
                editText.setFocusable(false);
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragment.enterEditMode();
                    }
                });
            }

            editText.setTextSize(Integer.parseInt(App.getDefaultSharedPreferences().getString("text_size", "15")));
            if (UselessUtils.getBool("mono", false))
                editText.setTypeface(Typeface.MONOSPACE);
            BetterLinkMovementMethod.linkify(Linkify.ALL, editText);
            editText.setHint(activity.getString(R.string.note));

            editTexts.add(editText);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        dao.updateIsChecked(1, noteItems.get(getAdapterPosition()).id);
                    } else {
                        dao.updateIsChecked(0, noteItems.get(getAdapterPosition()).id);
                    }

                    dao.updateNoteTime(System.currentTimeMillis(), noteItems.get(getAdapterPosition()).toId);
                }
            });
        }
    }
}