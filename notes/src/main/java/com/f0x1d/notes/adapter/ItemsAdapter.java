package com.f0x1d.notes.adapter;

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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.db.Database;
import com.f0x1d.notes.db.daos.NoteItemsDao;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.db.entities.Notify;
import com.f0x1d.notes.fragment.bottomSheet.SetNotify;
import com.f0x1d.notes.fragment.choose.ChooseFolder;
import com.f0x1d.notes.fragment.editing.NoteEdit;
import com.f0x1d.notes.fragment.lock.LockNote;
import com.f0x1d.notes.fragment.main.NotesInFolder;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.dialogs.ShowAlertDialog;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.view.theming.MyColorPickerDialog;
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
    List<NoteOrFolder> items;
    Activity activity;
    private boolean anim;

    private NoteOrFolderDao dao;

    public ItemsAdapter(List<NoteOrFolder> items, Activity activity, boolean anim) {
        this.items = items;
        this.activity = activity;
        this.anim = anim;

        setHasStableIds(true);
    }

    public static String getFolderNameFromDataBaseStatic(long id, int pos) {
        String name = "";

        for (NoteOrFolder noteOrFolder : App.getInstance().getDatabase().noteOrFolderDao().getAll()) {
            if (noteOrFolder.id == id) {
                name = noteOrFolder.folder_name;
                break;
            }
        }

        return name;
    }

    public static String getFolderNameFromDataBase(long id) {
        String name = "";

        for (NoteOrFolder noteOrFolder : App.getInstance().getDatabase().noteOrFolderDao().getAll()) {
            if (noteOrFolder.id == id) {
                name = noteOrFolder.folder_name;
                break;
            }
        }

        return name;
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NOTE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note, parent, false);

            if (anim) {
                Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.push_down);
                animation.setDuration(400);
                view.startAnimation(animation);
            }

            return new noteViewHolder(view);
        } else if (viewType == FOLDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder, parent, false);

            if (anim) {
                Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.push_down);
                animation.setDuration(400);
                view.startAnimation(animation);
            }

            return new folderViewHolder(view);
        } else if (viewType == NOTIFY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notify, parent, false);

            if (anim) {
                Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.push_down);
                animation.setDuration(400);
                view.startAnimation(animation);
            }

            return new notifyViewHolder(view);
        } else {
            throw new RuntimeException("The type has to be NOTE or FOLDER or NOTIFY");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int listPosition) {
        dao = App.getInstance().getDatabase().noteOrFolderDao();

        switch (holder.getItemViewType()) {
            case NOTE:
                initLayoutNote((noteViewHolder) holder, listPosition);
                break;
            case FOLDER:
                initLayoutFolder((folderViewHolder) holder, listPosition);
                break;
            case NOTIFY:
                initLayoutNotify((notifyViewHolder) holder, listPosition);
                break;
            default:
                break;
        }
    }

    private void initLayoutNotify(notifyViewHolder holder, int position) {
        try {
            holder.cardView.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(getColorFromDataBase(position))));

            if (UselessUtils.ifBrightColor(Color.parseColor(getColorFromDataBase(position)))) {
                if (UselessUtils.ifCustomTheme()) {
                    holder.title.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.text.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.notify_pic.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_notifications_active_black_24dp), ThemesEngine.lightColorIconColor));
                } else {
                    holder.title.setTextColor(Color.BLACK);
                    holder.text.setTextColor(Color.BLACK);
                    holder.notify_pic.setImageDrawable(activity.getDrawable(R.drawable.ic_notifications_active_black_24dp));
                }
            } else {
                if (UselessUtils.ifCustomTheme()) {
                    holder.title.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.text.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.notify_pic.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_notifications_active_white_24dp), ThemesEngine.darkColorIconColor));
                } else {
                    holder.title.setTextColor(Color.WHITE);
                    holder.text.setTextColor(Color.WHITE);
                    holder.notify_pic.setImageDrawable(activity.getDrawable(R.drawable.ic_notifications_active_white_24dp));
                }
            }
        } catch (Exception e) {
            if (UselessUtils.ifCustomTheme()) {
                holder.cardView.setCardBackgroundColor(Color.BLACK);
            }

            if (UselessUtils.ifBrightColor(holder.cardView.getCardBackgroundColor().getDefaultColor())) {
                if (UselessUtils.ifCustomTheme()) {
                    holder.title.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.text.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.notify_pic.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_notifications_active_black_24dp), ThemesEngine.lightColorIconColor));
                } else {
                    holder.title.setTextColor(Color.BLACK);
                    holder.text.setTextColor(Color.BLACK);
                    holder.notify_pic.setImageDrawable(activity.getDrawable(R.drawable.ic_notifications_active_black_24dp));
                }
            } else {
                if (UselessUtils.ifCustomTheme()) {
                    holder.title.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.text.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.notify_pic.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_notifications_active_white_24dp), ThemesEngine.darkColorIconColor));
                } else {
                    holder.title.setTextColor(Color.WHITE);
                    holder.text.setTextColor(Color.WHITE);
                    holder.notify_pic.setImageDrawable(activity.getDrawable(R.drawable.ic_notifications_active_white_24dp));
                }
            }
        }

        holder.title.setText(getNotifyTitle(items.get(position).id));
        holder.text.setText(getNotifyText(items.get(position).id));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] itemsAlert = new String[]{activity.getString(R.string.now), activity.getString(R.string.set_time),
                        activity.getString(R.string.pin_in_status_bar), activity.getString(R.string.change)};

                final boolean pinned = activity.getSharedPreferences("notifications", Context.MODE_PRIVATE).getBoolean("notify " + items.get(position).id, false);

                if (pinned)
                    itemsAlert = new String[]{activity.getString(R.string.now), activity.getString(R.string.set_time),
                            activity.getString(R.string.unpin_from_status_bar), activity.getString(R.string.change)};

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setItems(itemsAlert, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    String name1 = activity.getString(R.string.notification);
                                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                    NotificationChannel channel = new NotificationChannel("com.f0x1d.notes.notifications", name1, importance);
                                    channel.enableVibration(true);
                                    channel.enableLights(true);
                                    NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);
                                }

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(activity)
                                        .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                                        .setContentTitle(getNotifyTitle(items.get(position).id))
                                        .setContentText(getNotifyText(items.get(position).id))
                                        .setContentIntent(PendingIntent.getActivity(App.getContext(), 228, new Intent(App.getContext(), MainActivity.class),
                                                PendingIntent.FLAG_CANCEL_CURRENT))
                                        .setAutoCancel(true)
                                        .setVibrate(new long[]{1000L, 1000L, 1000L})
                                        .setChannelId("com.f0x1d.notes.notifications");

                                NotificationManager notificationManager =
                                        (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);

                                notificationManager.notify((int) items.get(position).id, builder.build());
                                break;
                            case 1:
                                FragmentActivity activity1 = (FragmentActivity) activity;
                                SetNotify notify = new SetNotify(new Notify(getNotifyTitle(items.get(position).id), getNotifyText(items.get(position).id), 0, items.get(position).id));
                                notify.show(activity1.getSupportFragmentManager(), "TAG");
                                break;
                            case 2:
                                NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

                                if (pinned) {
                                    manager.cancel((int) items.get(position).id + 1);
                                    activity.getSharedPreferences("notifications", Context.MODE_PRIVATE).edit().putBoolean("notify " + items.get(position).id, false).apply();
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
                                builder2.setContentTitle(getNotifyTitle(items.get(position).id));
                                builder2.setContentText(getNotifyText(items.get(position).id));
                                builder2.setOngoing(true);
                                builder2.setStyle(new Notification.BigTextStyle().bigText(getNotifyText(items.get(position).id)));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                    builder2.setChannelId("com.f0x1d.notes.notifications");

                                manager.notify((int) items.get(position).id + 1, builder2.build());

                                activity.getSharedPreferences("notifications", Context.MODE_PRIVATE).edit().putBoolean("notify " + items.get(position).id, true).apply();
                                break;
                            case 3:
                                getNotifyDialog(items.get(position).id, position);
                                break;
                        }
                    }
                });
                ShowAlertDialog.show(builder.create());
            }
        });
    }

    private int getPosition(long id) {
        int pos = 0;

        for (NoteOrFolder note : dao.getAll()) {
            if (note.id == id) {
                pos = note.position;
                break;
            }
        }

        return pos;
    }

    public void onItemsChanged(int lastPos, int newPos) {
        dao.updatePosition(newPos, items.get(lastPos).id);

        Collections.swap(items, lastPos, newPos);

        if (lastPos < newPos) {
            for (int i = 0; i < items.size(); i++) {
                if (getPosition(items.get(i).id) != i) {
                    dao.updatePosition(i, items.get(i).id);
                }
            }
        } else {
            for (int i = items.size() - 1; i > 0; i--) {
                if (getPosition(items.get(i).id) != i) {
                    dao.updatePosition(i, items.get(i).id);
                }
            }
        }

        notifyItemMoved(lastPos, newPos);
        notifyItemChanged(lastPos);
        notifyItemChanged(newPos);
        notifyDataSetChanged();
    }

    private void initLayoutFolder(folderViewHolder holder, int position) {
        try {
            holder.cardView.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(getColorFromDataBase(position))));

            if (UselessUtils.ifBrightColor(Color.parseColor(getColorFromDataBase(position)))) {
                if (UselessUtils.ifCustomTheme()) {
                    holder.name.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.folder_image.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_folder_black_24dp), ThemesEngine.lightColorIconColor));
                } else {
                    holder.name.setTextColor(Color.BLACK);
                    holder.folder_image.setImageDrawable(activity.getDrawable(R.drawable.ic_folder_black_24dp));
                }
            } else {
                if (UselessUtils.ifCustomTheme()) {
                    holder.name.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.folder_image.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_folder_white_24dp), ThemesEngine.darkColorIconColor));
                } else {
                    holder.name.setTextColor(Color.WHITE);
                    holder.folder_image.setImageDrawable(activity.getDrawable(R.drawable.ic_folder_white_24dp));
                }
            }
        } catch (Exception e) {
            if (UselessUtils.ifCustomTheme()) {
                holder.cardView.setCardBackgroundColor(Color.BLACK);
            }

            if (UselessUtils.ifBrightColor(holder.cardView.getCardBackgroundColor().getDefaultColor())) {
                if (UselessUtils.ifCustomTheme()) {
                    holder.name.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.folder_image.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_folder_black_24dp), ThemesEngine.lightColorIconColor));
                } else {
                    holder.name.setTextColor(Color.BLACK);
                    holder.folder_image.setImageDrawable(activity.getDrawable(R.drawable.ic_folder_black_24dp));
                }
            } else {
                if (UselessUtils.ifCustomTheme()) {
                    holder.name.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.folder_image.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_folder_white_24dp), ThemesEngine.darkColorIconColor));
                } else {
                    holder.name.setTextColor(Color.WHITE);
                    holder.folder_image.setImageDrawable(activity.getDrawable(R.drawable.ic_folder_white_24dp));
                }
            }
        }

        holder.name.setText(getFolderNameFromDataBase(items.get(position).id, position));

        if (PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("show_things", false))
            holder.name.setText(getFolderNameFromDataBase(items.get(position).id, position) + " | " + Database.thingsInFolder(getFolderNameFromDataBase(items.get(position).id, position)));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.instance.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out)
                        .replace(R.id.container, NotesInFolder.newInstance(getFolderNameFromDataBase(items.get(position).id, position)), "in_folder").addToBackStack(null).commit();
            }
        });
    }

    public void deleteNote(long id) {
        dao.deleteNote(id);
    }

    public void deleteFolder(final String folder_name) {
        try {
            deleteFolderFull(folder_name);
        } catch (IndexOutOfBoundsException e) {
            Logger.log(e);
        }
    }

    private void deleteFolderFull(String folderName) {
        dao.deleteFolder(folderName);
        for (NoteOrFolder noteOrFolder : dao.getByInFolderId(folderName)) {
            if (noteOrFolder.is_folder == 1) {
                deleteFolderFull(noteOrFolder.folder_name);
                dao.deleteFolder(noteOrFolder.folder_name);
            } else {
                dao.deleteNote(noteOrFolder.id);
            }
        }
    }

    private String getColorFromDataBase(int position) {
        long id = items.get(position).id;

        String color = "0xffffffff";

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.id == id) {
                color = noteOrFolder.color;
            }
        }

        return color;
    }

    public String getFolderNameFromDataBase(long id, int pos) {
        String name = "";

        for (NoteOrFolder noteOrFolder : App.getInstance().getDatabase().noteOrFolderDao().getAll()) {
            if (noteOrFolder.id == id) {
                name = noteOrFolder.folder_name;
                break;
            }
        }

        if (name.equals(""))
            return items.get(pos).folder_name;

        return name;
    }

    private void initLayoutNote(noteViewHolder holder, int position) {
        try {
            holder.note_card.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(getColorFromDataBase(position))));

            if (UselessUtils.ifBrightColor(Color.parseColor(getColorFromDataBase(position)))) {
                if (UselessUtils.ifCustomTheme()) {
                    holder.title.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.text.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.time.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.time_pic.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_edit_black_24dp), ThemesEngine.lightColorIconColor));
                } else {
                    holder.title.setTextColor(Color.BLACK);
                    holder.text.setTextColor(Color.BLACK);
                    holder.time.setTextColor(Color.BLACK);
                    holder.time_pic.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_black_24dp));
                }
            } else {
                if (UselessUtils.ifCustomTheme()) {
                    holder.title.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.text.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.time.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.time_pic.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_edit_white_24dp), ThemesEngine.darkColorIconColor));
                } else {
                    holder.title.setTextColor(Color.WHITE);
                    holder.text.setTextColor(Color.WHITE);
                    holder.time.setTextColor(Color.WHITE);
                    holder.time_pic.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_white_24dp));
                }
            }
        } catch (Exception e) {
            if (UselessUtils.ifCustomTheme()) {
                holder.note_card.setCardBackgroundColor(Color.BLACK);
            }

            if (UselessUtils.ifBrightColor(holder.note_card.getCardBackgroundColor().getDefaultColor())) {
                if (UselessUtils.ifCustomTheme()) {
                    holder.title.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.text.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.time.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.time_pic.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_edit_black_24dp), ThemesEngine.lightColorIconColor));
                } else {
                    holder.title.setTextColor(Color.BLACK);
                    holder.text.setTextColor(Color.BLACK);
                    holder.time.setTextColor(Color.BLACK);
                    holder.time_pic.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_black_24dp));
                }
            } else {
                if (UselessUtils.ifCustomTheme()) {
                    holder.title.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.text.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.time.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.time_pic.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_edit_white_24dp), ThemesEngine.darkColorIconColor));
                } else {
                    holder.title.setTextColor(Color.WHITE);
                    holder.time.setTextColor(Color.WHITE);
                    holder.text.setTextColor(Color.WHITE);
                    holder.time_pic.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_white_24dp));
                }
            }
        }

        holder.title.setText(Html.fromHtml(items.get(position).title.replace("\n", "<br />")));

        String text = "null";

        NoteItemsDao dao = App.getInstance().getDatabase().noteItemsDao();
        for (NoteItem noteItem : dao.getAll()) {
            if (noteItem.to_id == items.get(position).id) {
                if (noteItem.position == 0) {
                    text = noteItem.text;
                    break;
                }
            }
        }

        try {
            boolean oneLine;

            oneLine = !Pattern.compile("\\r?\\n").matcher(text).find();

            if (oneLine) {
                holder.text.setText(Html.fromHtml(text.split("\\r?\\n")[0]));
            } else {
                holder.text.setText(Html.fromHtml(text.split("\\r?\\n")[0] + "..."));
            }
        } catch (Exception e) {
            Logger.log(e);
        }

        if (holder.text.getText().toString().equals("")) {
            holder.text.setText(Html.fromHtml("<i>" + activity.getString(R.string.empty_note) + "</i>"));
        }

        if (items.get(position).locked == 1) {
            holder.text.setText(Html.fromHtml("<i>" + activity.getString(R.string.blocked) + "</i>"));
        }

        Date currentDate = new Date(items.get(position).edit_time);
        try {
            DateFormat df = new SimpleDateFormat(PreferenceManager.getDefaultSharedPreferences(activity).getString("date", "HH:mm | dd.MM.yyyy"), Locale.US);
            holder.time.setText(df.format(currentDate));
        } catch (Exception e) {
            holder.time.setText("Error");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putLong("id", items.get(position).id);
                args.putInt("locked", items.get(position).locked);
                args.putString("title", items.get(position).title);

                if (items.get(position).locked == 1) {
                    if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("lock", false)) {
                        MainActivity.instance.getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                                R.id.container, LockNote.newInstance(args), "edit").addToBackStack("editor").commit();
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.enable_pin), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MainActivity.instance.getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                            R.id.container, NoteEdit.newInstance(args), "edit").addToBackStack("editor").commit();
                }

            }
        });
    }

    private String getNotifyTitle(long id) {
        String flex = "null";

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.id == id) {
                flex = noteOrFolder.title;
                break;
            }
        }

        return flex;
    }

    private String getNotifyText(long id) {
        String flex = "null";

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.id == id) {
                flex = noteOrFolder.text;
                break;
            }
        }

        return flex;
    }

    public void getNotifyDialog(long id, int position) {
        String[] hm;

        try {
            Color.parseColor(getColorFromDataBase(id));

            hm = new String[]{activity.getString(R.string.change), activity.getString(R.string.color), activity.getString(R.string.restore_color)};
        } catch (Exception e) {
            hm = new String[]{activity.getString(R.string.change), activity.getString(R.string.color)};
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);

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

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setView(v);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog228, int which) {
                                App.getInstance().getDatabase().noteOrFolderDao().updateNoteTitle(title.getText().toString(), id);
                                App.getInstance().getDatabase().noteOrFolderDao().updateNoteText(text.getText().toString(), id);

                                notifyItemChanged(position);
                            }
                        }).create();

                        ShowAlertDialog.show(builder.create());
                        break;
                    case 1:

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
                                App.getInstance().getDatabase().noteOrFolderDao().updateNoteColor("#" + Integer.toHexString(color), id);

                                notifyItemChanged(position);
                            }

                            @Override
                            public void onDialogDismissed(int dialogId) {

                            }
                        });

                        FragmentActivity fragmentActivity = (FragmentActivity) activity;

                        colorPickerDialog.show(fragmentActivity.getSupportFragmentManager(), "");
                        break;
                    case 2:
                        App.getInstance().getDatabase().noteOrFolderDao().updateNoteColor("", id);
                        notifyItemChanged(position);
                        break;
                }
            }
        });

        ShowAlertDialog.show(builder1.create());
    }

    public void getFoldersDialog(long id, NotesInFolder notesInFolder) {
        String[] hm;

        try {
            Color.parseColor(getColorFromDataBase(id));

            hm = new String[]{activity.getString(R.string.rename), activity.getString(R.string.color), activity.getString(R.string.restore_color)};
        } catch (Exception e) {
            hm = new String[]{activity.getString(R.string.rename), activity.getString(R.string.color)};
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);

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

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                        builder.setView(v);
                        builder.setTitle(activity.getString(R.string.folder_name));

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog228, int which) {
                                boolean create = true;

                                for (NoteOrFolder noteOrFolder : App.getInstance().getDatabase().noteOrFolderDao().getAll()) {
                                    if (noteOrFolder.is_folder == 1 && noteOrFolder.folder_name.equals(text.getText().toString())) {
                                        create = false;
                                        Toast.makeText(App.getContext(), activity.getString(R.string.folder_error), Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }

                                if (create) {
                                    for (NoteOrFolder noteOrFolder : App.getInstance().getDatabase().noteOrFolderDao().getAll()) {
                                        if (noteOrFolder.in_folder_id.equals(getFolderNameFromDataBase(id))) {
                                            App.getInstance().getDatabase().noteOrFolderDao().updateInFolderIdById(text.getText().toString(), noteOrFolder.id);
                                        }
                                    }
                                    App.getInstance().getDatabase().noteOrFolderDao().updateFolderTitle(text.getText().toString(), getFolderNameFromDataBase(id));
                                    App.getInstance().getDatabase().noteOrFolderDao().updateInFolderId(text.getText().toString(), getFolderNameFromDataBase(id));

                                    notesInFolder.in_folder_id = text.getText().toString();
                                }
                            }
                        }).create();

                        ShowAlertDialog.show(builder.create());
                        break;
                    case 1:
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
                                App.getInstance().getDatabase().noteOrFolderDao().updateFolderColor("#" + Integer.toHexString(color), getFolderNameFromDataBase(id));
                            }

                            @Override
                            public void onDialogDismissed(int dialogId) {

                            }
                        });

                        FragmentActivity fragmentActivity = (FragmentActivity) activity;
                        colorPickerDialog.show(fragmentActivity.getSupportFragmentManager(), "");
                        break;
                    case 2:
                        App.getInstance().getDatabase().noteOrFolderDao().updateFolderColor("", getFolderNameFromDataBase(id));
                        break;
                }
            }
        });

        ShowAlertDialog.show(builder1.create());
    }

    public void getNotesDialog(long id) {
        String[] hm;
        try {
            Color.parseColor(getColorFromDataBase(id));

            hm = new String[]{activity.getString(R.string.color), activity.getString(R.string.move_ro_folder), activity.getString(R.string.restore_color)};
        } catch (Exception e) {
            hm = new String[]{activity.getString(R.string.color), activity.getString(R.string.move_ro_folder)};
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);

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

                                App.getInstance().getDatabase().noteOrFolderDao().updateNoteColor("#" + Integer.toHexString(color), id);
                            }

                            @Override
                            public void onDialogDismissed(int dialogId) {

                            }
                        });

                        FragmentActivity fragmentActivity = (FragmentActivity) activity;

                        colorPickerDialog.show(fragmentActivity.getSupportFragmentManager(), "");
                        break;
                    case 1:
                        Bundle args = new Bundle();
                        args.putString("in_id", "def");
                        args.putLong("id", id);

                        MainActivity.instance.getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                                R.id.container, ChooseFolder.newInstance(args), "choose_folder")
                                .addToBackStack(null).commit();
                        break;
                    case 2:
                        App.getInstance().getDatabase().noteOrFolderDao().updateNoteColor("", id);
                        break;
                }
            }
        });

        ShowAlertDialog.show(builder1.create());

    }

    private String getColorFromDataBase(long id) {
        String color = "0xffffffff";

        for (NoteOrFolder noteOrFolder : App.getInstance().getDatabase().noteOrFolderDao().getAll()) {
            if (noteOrFolder.id == id) {
                color = noteOrFolder.color;
            }
        }

        return color;
    }

    @Override
    public int getItemViewType(int position) {
        dao = App.getInstance().getDatabase().noteOrFolderDao();

        NoteOrFolder item = items.get(position);
        if (item.is_folder == 0) {
            return NOTE;
        } else if (item.is_folder == 1) {
            return FOLDER;
        } else if (item.is_folder == 2) {
            return NOTIFY;
        } else {
            return -1;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class folderViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CardView cardView;
        ImageView folder_image;

        folderViewHolder(@NonNull View itemView) {
            super(itemView);

            folder_image = itemView.findViewById(R.id.folder_image);
            cardView = itemView.findViewById(R.id.note_card);
            name = itemView.findViewById(R.id.name);
        }
    }

    class noteViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView text;
        CardView note_card;
        TextView time;
        ImageView time_pic;

        noteViewHolder(@NonNull View itemView) {
            super(itemView);

            note_card = itemView.findViewById(R.id.note_card);
            title = itemView.findViewById(R.id.textView_title);
            text = itemView.findViewById(R.id.textView_text);
            time = itemView.findViewById(R.id.time);
            time_pic = itemView.findViewById(R.id.time_ic);
        }
    }

    class notifyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView text;
        ImageView notify_pic;
        CardView cardView;

        notifyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textView_title);
            text = itemView.findViewById(R.id.textView_text);
            cardView = itemView.findViewById(R.id.note_card);
            notify_pic = itemView.findViewById(R.id.notify_pic);
        }
    }
}
