package com.f0x1d.notes.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.db.Database;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.db.entities.Notify;
import com.f0x1d.notes.fragment.bottomSheet.SetNotifyDialog;
import com.f0x1d.notes.fragment.choose.ChooseFolderFragment;
import com.f0x1d.notes.fragment.editing.NoteEditFragment;
import com.f0x1d.notes.fragment.lock.LockScreenFragment;
import com.f0x1d.notes.fragment.main.NotesFragment;
import com.f0x1d.notes.fragment.main.NotesMovingFragment;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.dialogs.ShowAlertDialog;
import com.f0x1d.notes.view.theming.ItemCardView;
import com.f0x1d.notes.view.theming.MyColorPickerDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int NOTE = 1;
    private final int FOLDER = 2;
    private final int NOTIFY = 3;

    public boolean ableToMove;

    public ItemTouchHelper touchHelper;
    public List<NoteOrFolder> items;
    public Activity activity;
    private NoteOrFolderDao dao = App.getInstance().getDatabase().noteOrFolderDao();

    public ItemsAdapter(List<NoteOrFolder> items, Activity activity, boolean ableToMove, ItemTouchHelper touchHelper) {
        this.items = items;
        this.activity = activity;
        this.ableToMove = ableToMove;
        this.touchHelper = touchHelper;

        setHasStableIds(true);
    }

    public static String getFolderNameFromDataBase(long id) {
        return App.getInstance().getDatabase().noteOrFolderDao().getById(id).folderName;
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NOTE) {
            return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(ableToMove ? R.layout.note_drag : R.layout.note, parent, false));
        } else if (viewType == FOLDER) {
            return new FolderViewHolder(LayoutInflater.from(parent.getContext()).inflate(ableToMove ? R.layout.folder_drag : R.layout.folder, parent, false));
        } else if (viewType == NOTIFY) {
            return new NotifyViewHolder(LayoutInflater.from(parent.getContext()).inflate(ableToMove ? R.layout.notify_drag : R.layout.notify, parent, false));
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int listPosition) {
        switch (holder.getItemViewType()) {
            case NOTE:
                initLayoutNote((NoteViewHolder) holder, listPosition);
                break;
            case FOLDER:
                initLayoutFolder((FolderViewHolder) holder, listPosition);
                break;
            case NOTIFY:
                initLayoutNotify((NotifyViewHolder) holder, listPosition);
                break;
            default:
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initLayoutNotify(NotifyViewHolder holder, int position) {
        boolean dark;
        try {
            holder.cardView.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(getColorFromDataBase(position))));
            dark = !UselessUtils.ifBrightColor(Color.parseColor(getColorFromDataBase(position)));
        } catch (Exception e) {
            holder.cardView.setThemedCardBackgroundColor();
            dark = !UselessUtils.ifBrightColor(holder.cardView.getCardBackgroundColor().getDefaultColor());
        }
        if (dark) {
            holder.title.setTextColor(Color.WHITE);
            holder.text.setTextColor(Color.WHITE);
            holder.notifyPic.setImageDrawable(activity.getDrawable(R.drawable.ic_notifications_active_white_24dp));
            if (holder.drag != null)
                holder.drag.setImageResource(R.drawable.ic_drag_indicator_white_24dp);
        } else {
            holder.title.setTextColor(Color.BLACK);
            holder.text.setTextColor(Color.BLACK);
            holder.notifyPic.setImageDrawable(activity.getDrawable(R.drawable.ic_notifications_active_black_24dp));
            if (holder.drag != null)
                holder.drag.setImageResource(R.drawable.ic_drag_indicator_black_24dp);
        }

        holder.title.setText(getNotifyTitle(items.get(position).id));
        holder.text.setText(getNotifyText(items.get(position).id));
    }

    public void onItemsChanged(int lastPos, int newPos) {
        Collections.swap(items, lastPos, newPos);
        for (int i = 0; i < items.size(); i++) {
            dao.updatePosition(i, items.get(i).id);
        }

        notifyItemMoved(lastPos, newPos);
        notifyItemChanged(lastPos);
        notifyItemChanged(newPos);
        notifyDataSetChanged();
    }

    @SuppressLint("ClickableViewAccessibility")
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
            if (holder.drag != null)
                holder.drag.setImageResource(R.drawable.ic_drag_indicator_white_24dp);
        } else {
            holder.name.setTextColor(Color.BLACK);
            holder.folderImage.setImageDrawable(activity.getDrawable(R.drawable.ic_folder_black_24dp));
            if (holder.drag != null)
                holder.drag.setImageResource(R.drawable.ic_drag_indicator_black_24dp);
        }

        String folderName = getFolderNameFromDataBase(items.get(position).id, position);
        holder.name.setText(folderName);

        if (App.getDefaultSharedPreferences().getBoolean("show_things", false))
            holder.name.setText(folderName + " | " + Database.thingsInFolder(folderName));
    }

    public void deleteNote(long id) {
        dao.deleteNote(id);
    }

    public void deleteFolder(final String folderName) {
        try {
            deleteFolderFull(folderName);
        } catch (IndexOutOfBoundsException e) {
            Logger.log(e);
        }
    }

    private void deleteFolderFull(String folderName) {
        dao.deleteFolder(folderName);
        for (NoteOrFolder noteOrFolder : dao.getByInFolderId(folderName)) {
            if (noteOrFolder.isFolder == 1) {
                deleteFolderFull(noteOrFolder.folderName);
                dao.deleteFolder(noteOrFolder.folderName);
            } else {
                dao.deleteNote(noteOrFolder.id);
            }
        }
    }

    private String getColorFromDataBase(int position) {
        long id = items.get(position).id;
        return dao.getById(id).color;
    }

    public String getFolderNameFromDataBase(long id, int pos) {
        String name = "";
        name = dao.getById(id).folderName;

        if (name.equals(""))
            return items.get(pos).folderName;

        return name;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initLayoutNote(NoteViewHolder holder, int position) {
        boolean dark;
        try {
            holder.noteCard.setCardBackgroundColor(Color.parseColor(getColorFromDataBase(position)));
            dark = !UselessUtils.ifBrightColor(Color.parseColor(getColorFromDataBase(position)));
        } catch (Exception e) {
            holder.noteCard.setThemedCardBackgroundColor();
            dark = !UselessUtils.ifBrightColor(holder.noteCard.getCardBackgroundColor().getDefaultColor());
        }
        if (dark) {
            holder.title.setTextColor(Color.WHITE);
            holder.time.setTextColor(Color.WHITE);
            holder.text.setTextColor(Color.WHITE);
            holder.timePic.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_white_24dp));
            if (holder.drag != null)
                holder.drag.setImageResource(R.drawable.ic_drag_indicator_white_24dp);
        } else {
            holder.title.setTextColor(Color.BLACK);
            holder.text.setTextColor(Color.BLACK);
            holder.time.setTextColor(Color.BLACK);
            holder.timePic.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_black_24dp));
            if (holder.drag != null)
                holder.drag.setImageResource(R.drawable.ic_drag_indicator_black_24dp);
        }

        holder.title.setText(Html.fromHtml(items.get(position).title.replace("\n", "<br />")));

        String text = "null";
        text = App.getInstance().getDatabase().noteItemsDao().getAllByToId(items.get(position).id).get(0).text;

        if (!Pattern.compile("\\r?\\n").matcher(text).find()) {
            holder.text.setText(Html.fromHtml(text.split("\\r?\\n")[0]));
        } else {
            holder.text.setText(Html.fromHtml(text.split("\\r?\\n")[0] + "..."));
        }

        if (holder.text.getText().toString().equals("")) {
            holder.text.setText(Html.fromHtml("<i>" + activity.getString(R.string.empty_note) + "</i>"));
        }

        if (items.get(position).locked == 1) {
            holder.text.setText(Html.fromHtml("<i>" + activity.getString(R.string.blocked) + "</i>"));
        }

        Date currentDate = new Date(items.get(position).editTime);
        try {
            DateFormat df = new SimpleDateFormat(App.getDefaultSharedPreferences().getString("date", "HH:mm | dd.MM.yyyy"), Locale.US);
            holder.time.setText(df.format(currentDate));
        } catch (Exception e) {
            holder.time.setText("Error");
        }
    }

    private String getNotifyTitle(long id) {
        return dao.getById(id).title;
    }

    private String getNotifyText(long id) {
        return dao.getById(id).text;
    }

    public void getNotifyDialog(long id, int position) {
        String[] hm;

        try {
            Color.parseColor(getColorFromDataBase(id));

            hm = new String[]{activity.getString(R.string.change), activity.getString(R.string.move), activity.getString(R.string.color), activity.getString(R.string.restore_color)};
        } catch (Exception e) {
            hm = new String[]{activity.getString(R.string.change), activity.getString(R.string.move), activity.getString(R.string.color)};
        }

        MaterialAlertDialogBuilder builder1 = new MaterialAlertDialogBuilder(activity);

        builder1.setItems(hm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        View v = LayoutInflater.from(activity).inflate(R.layout.dialog_two_edit_texts, null);

                        EditText title = v.findViewById(R.id.edit_text_one);
                        title.setBackground(null);
                        title.setText(getNotifyTitle(id));
                        title.setHint(activity.getString(R.string.title));

                        EditText text = v.findViewById(R.id.edit_text_two);
                        text.setBackground(null);
                        text.setText(getNotifyText(id));
                        text.setHint(activity.getString(R.string.text));

                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
                        builder.setView(v);

                        builder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog228, int which) {
                                dao.updateNoteTitle(title.getText().toString(), id);
                                dao.updateNoteText(text.getText().toString(), id);

                                notifyItemChanged(position);
                            }
                        }).create();

                        ShowAlertDialog.show(builder);
                        break;
                    case 1:
                        UselessUtils.replace((AppCompatActivity) activity, NotesMovingFragment.newInstance(items.get(0).inFolderId), "moving", true, null);
                        break;
                    case 2:
                        int currentColor;

                        try {
                            currentColor = Color.parseColor(getColorFromDataBase(id));
                        } catch (Exception e) {
                            currentColor = 0xffffffff;
                        }

                        MyColorPickerDialog colorPickerDialog = MyColorPickerDialog.newBuilderNew().setColor(currentColor).create();
                        colorPickerDialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                            @Override
                            public void onColorSelected(int dialogId, int color) {
                                dao.updateNoteColor("#" + Integer.toHexString(color), id);

                                notifyItemChanged(position);
                            }

                            @Override
                            public void onDialogDismissed(int dialogId) {

                            }
                        });

                        FragmentActivity fragmentActivity = (FragmentActivity) activity;

                        colorPickerDialog.show(fragmentActivity.getSupportFragmentManager(), "");
                        break;
                    case 3:
                        dao.updateNoteColor("", id);
                        notifyItemChanged(position);
                        break;
                }
            }
        });

        ShowAlertDialog.show(builder1);
    }

    public void getFoldersDialog(long id, int position) {
        String[] hm;

        try {
            Color.parseColor(getColorFromDataBase(id));

            hm = new String[]{activity.getString(R.string.rename), activity.getString(R.string.move), activity.getString(R.string.color), activity.getString(R.string.restore_color)};
        } catch (Exception e) {
            hm = new String[]{activity.getString(R.string.rename), activity.getString(R.string.move), activity.getString(R.string.color)};
        }

        MaterialAlertDialogBuilder builder1 = new MaterialAlertDialogBuilder(activity);

        builder1.setItems(hm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        View v = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_text, null);

                        EditText text = v.findViewById(R.id.edit_text);
                        text.setBackground(null);
                        text.setText(getFolderNameFromDataBase(id));
                        text.setHint(activity.getString(R.string.name));

                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);

                        builder.setView(v);
                        builder.setTitle(activity.getString(R.string.folder_name));

                        builder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog228, int which) {
                                boolean create = true;

                                for (NoteOrFolder noteOrFolder : dao.getAll()) {
                                    if (noteOrFolder.isFolder == 1 && noteOrFolder.folderName.equals(text.getText().toString())) {
                                        create = false;
                                        Toast.makeText(App.getContext(), activity.getString(R.string.folder_error), Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }

                                if (create) {
                                    for (NoteOrFolder noteOrFolder : dao.getAll()) {
                                        if (noteOrFolder.inFolderId.equals(getFolderNameFromDataBase(id))) {
                                            dao.updateInFolderIdById(text.getText().toString(), noteOrFolder.id);
                                        }
                                    }
                                    dao.updateFolderTitle(text.getText().toString(), getFolderNameFromDataBase(id));
                                    dao.updateInFolderId(text.getText().toString(), getFolderNameFromDataBase(id));

                                    notifyItemChanged(position);
                                }
                            }
                        }).create();

                        ShowAlertDialog.show(builder);
                        break;
                    case 1:
                        UselessUtils.replace((AppCompatActivity) activity, NotesMovingFragment.newInstance(items.get(0).inFolderId), "moving", true, null);
                        break;
                    case 2:
                        int currentColor;

                        try {
                            currentColor = Color.parseColor(getColorFromDataBase(id));
                        } catch (Exception e) {
                            currentColor = 0xffffffff;
                        }

                        MyColorPickerDialog colorPickerDialog = MyColorPickerDialog.newBuilderNew().setColor(currentColor).create();
                        colorPickerDialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                            @Override
                            public void onColorSelected(int dialogId, int color) {
                                Logger.log("onColorSelected: " + "#" + Integer.toHexString(color));
                                dao.updateFolderColor("#" + Integer.toHexString(color), getFolderNameFromDataBase(id));

                                notifyItemChanged(position);
                            }

                            @Override
                            public void onDialogDismissed(int dialogId) {

                            }
                        });

                        FragmentActivity fragmentActivity = (FragmentActivity) activity;
                        colorPickerDialog.show(fragmentActivity.getSupportFragmentManager(), "");
                        break;
                    case 3:
                        dao.updateFolderColor("", getFolderNameFromDataBase(id));
                        notifyItemChanged(position);
                        break;
                }
            }
        });

        ShowAlertDialog.show(builder1);
    }

    public void getNotesDialog(long id, int position) {
        String[] hm;
        try {
            Color.parseColor(getColorFromDataBase(id));

            hm = new String[]{activity.getString(R.string.color), activity.getString(R.string.move), activity.getString(R.string.move_ro_folder), activity.getString(R.string.restore_color)};
        } catch (Exception e) {
            hm = new String[]{activity.getString(R.string.color), activity.getString(R.string.move), activity.getString(R.string.move_ro_folder)};
        }

        MaterialAlertDialogBuilder builder1 = new MaterialAlertDialogBuilder(activity);

        builder1.setItems(hm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        int currentColor;

                        try {
                            currentColor = Color.parseColor(getColorFromDataBase(id));
                        } catch (Exception e) {
                            currentColor = 0xffffffff;
                        }

                        MyColorPickerDialog colorPickerDialog = MyColorPickerDialog.newBuilderNew().setColor(currentColor).create();
                        colorPickerDialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                            @Override
                            public void onColorSelected(int dialogId, int color) {
                                Logger.log("onColorSelected: " + "#" + Integer.toHexString(color) + " id: " + id);

                                dao.updateNoteColor("#" + Integer.toHexString(color), id);
                                notifyItemChanged(position);
                            }

                            @Override
                            public void onDialogDismissed(int dialogId) {

                            }
                        });
                        colorPickerDialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), "");
                        break;
                    case 1:
                        UselessUtils.replace((AppCompatActivity) activity, NotesMovingFragment.newInstance(items.get(0).inFolderId), "moving", true, null);
                        break;
                    case 2:
                        UselessUtils.replace((AppCompatActivity) activity, ChooseFolderFragment.newInstance(id, "def"), "choose_folder", true, null);
                        break;
                    case 3:
                        dao.updateNoteColor("", id);
                        notifyItemChanged(position);
                        break;
                }
            }
        });

        ShowAlertDialog.show(builder1);
    }

    private String getColorFromDataBase(long id) {
        return App.getInstance().getDatabase().noteOrFolderDao().getById(id).color;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemViewType(int position) {
        NoteOrFolder item = items.get(position);
        if (item.isFolder == 0) {
            return NOTE;
        } else if (item.isFolder == 1) {
            return FOLDER;
        } else if (item.isFolder == 2) {
            return NOTIFY;
        } else {
            return -1;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ItemCardView cardView;
        public ImageView folderImage;
        public ImageView drag;

        @SuppressLint("ClickableViewAccessibility")
        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);

            folderImage = itemView.findViewById(R.id.folder_image);
            cardView = itemView.findViewById(R.id.note_card);
            name = itemView.findViewById(R.id.name);
            drag = itemView.findViewById(R.id.drag);

            if (!ableToMove) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        getFoldersDialog(items.get(getAdapterPosition()).id, getAdapterPosition());
                        return false;
                    }
                });

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UselessUtils.replace((AppCompatActivity) activity, NotesFragment.newInstance(getFolderNameFromDataBase(items.get(getAdapterPosition()).id,
                                getAdapterPosition())), "in_folder", true, null);
                    }
                });
            } else {
                drag.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        touchHelper.startDrag(FolderViewHolder.this);
                        return false;
                    }
                });
            }
        }
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView text;
        public ItemCardView noteCard;
        public TextView time;
        public ImageView timePic;
        public ImageView drag;

        @SuppressLint("ClickableViewAccessibility")
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteCard = itemView.findViewById(R.id.note_card);
            title = itemView.findViewById(R.id.textView_title);
            text = itemView.findViewById(R.id.textView_text);
            time = itemView.findViewById(R.id.time);
            timePic = itemView.findViewById(R.id.time_ic);
            drag = itemView.findViewById(R.id.drag);

            if (!ableToMove) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        getNotesDialog(items.get(getAdapterPosition()).id, getAdapterPosition());
                        return false;
                    }
                });

                noteCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (items.get(getAdapterPosition()).locked == 1) {
                            if (App.getDefaultSharedPreferences().getBoolean("lock", false)) {
                                UselessUtils.replace((AppCompatActivity) activity,
                                        LockScreenFragment.newInstance(true, items.get(getAdapterPosition()).id), "lock", true, "editor");
                            } else {
                                Toast.makeText(activity, activity.getString(R.string.enable_pin), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            UselessUtils.replace((AppCompatActivity) activity,
                                    NoteEditFragment.newInstance(false, items.get(getAdapterPosition()).id, null), "edit", true, "editor");
                        }

                    }
                });
            } else {
                drag.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        touchHelper.startDrag(NoteViewHolder.this);
                        return false;
                    }
                });
            }
        }
    }

    public class NotifyViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView text;
        public ImageView notifyPic;
        public ItemCardView cardView;
        public ImageView drag;

        @SuppressLint("ClickableViewAccessibility")
        public NotifyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textView_title);
            text = itemView.findViewById(R.id.textView_text);
            cardView = itemView.findViewById(R.id.note_card);
            notifyPic = itemView.findViewById(R.id.notify_pic);
            drag = itemView.findViewById(R.id.drag);

            if (!ableToMove) {
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] itemsAlert = new String[]{activity.getString(R.string.now), activity.getString(R.string.set_time),
                                activity.getString(R.string.pin_in_status_bar), activity.getString(R.string.change)};

                        final boolean pinned = activity.getSharedPreferences("notifications", Context.MODE_PRIVATE)
                                .getBoolean("notify " + items.get(getAdapterPosition()).id, false);

                        if (pinned)
                            itemsAlert = new String[]{activity.getString(R.string.now), activity.getString(R.string.set_time),
                                    activity.getString(R.string.unpin_from_status_bar), activity.getString(R.string.change)};

                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
                        builder.setItems(itemsAlert, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            String name = activity.getString(R.string.notification);
                                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                            NotificationChannel channel = new NotificationChannel("com.f0x1d.notes.notifications", name, importance);
                                            channel.enableVibration(true);
                                            channel.enableLights(true);
                                            NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);
                                            notificationManager.createNotificationChannel(channel);
                                        }

                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity)
                                                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                                                .setContentTitle(getNotifyTitle(items.get(getAdapterPosition()).id))
                                                .setContentText(getNotifyText(items.get(getAdapterPosition()).id))
                                                .setContentIntent(PendingIntent.getActivity(App.getContext(), 228, new Intent(App.getContext(), MainActivity.class),
                                                        PendingIntent.FLAG_CANCEL_CURRENT))
                                                .setAutoCancel(true)
                                                .setVibrate(new long[]{1000L, 1000L, 1000L})
                                                .setChannelId("com.f0x1d.notes.notifications");

                                        NotificationManager notificationManager =
                                                (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);

                                        notificationManager.notify((int) items.get(getAdapterPosition()).id, builder.build());
                                        break;
                                    case 1:
                                        FragmentActivity activity1 = (FragmentActivity) activity;
                                        SetNotifyDialog notify = new SetNotifyDialog(new Notify(getNotifyTitle(items.get(getAdapterPosition()).id), getNotifyText(items.get(getAdapterPosition()).id),
                                                0, items.get(getAdapterPosition()).id));
                                        notify.show(activity1.getSupportFragmentManager(), "TAG");
                                        break;
                                    case 2:
                                        NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

                                        if (pinned) {
                                            manager.cancel((int) items.get(getAdapterPosition()).id + 1);
                                            activity.getSharedPreferences("notifications", Context.MODE_PRIVATE).edit().putBoolean("notify " + items.get(getAdapterPosition()).id, false).apply();
                                            break;
                                        }

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            String name = activity.getString(R.string.notification);
                                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                            NotificationChannel channel = new NotificationChannel("com.f0x1d.notes.notifications", name, importance);
                                            channel.enableVibration(true);
                                            channel.enableLights(true);
                                            manager.createNotificationChannel(channel);
                                        }

                                        Notification.Builder builder2 = new Notification.Builder(activity);
                                        builder2.setSmallIcon(R.drawable.ic_notifications_active_black_24dp);
                                        builder2.setContentTitle(getNotifyTitle(items.get(getAdapterPosition()).id));
                                        builder2.setContentText(getNotifyText(items.get(getAdapterPosition()).id));
                                        builder2.setOngoing(true);
                                        builder2.setStyle(new Notification.BigTextStyle().bigText(getNotifyText(items.get(getAdapterPosition()).id)));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                            builder2.setChannelId("com.f0x1d.notes.notifications");

                                        manager.notify((int) items.get(getAdapterPosition()).id + 1, builder2.build());

                                        activity.getSharedPreferences("notifications", Context.MODE_PRIVATE).edit().putBoolean("notify " + items.get(getAdapterPosition()).id, true).apply();
                                        break;
                                    case 3:
                                        getNotifyDialog(items.get(getAdapterPosition()).id, getAdapterPosition());
                                        break;
                                }
                            }
                        });
                        ShowAlertDialog.show(builder);
                    }
                });
            } else {
                drag.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        touchHelper.startDrag(NotifyViewHolder.this);
                        return false;
                    }
                });
            }
        }
    }
}
