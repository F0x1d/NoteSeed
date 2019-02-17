package com.f0x1d.notes.adapter;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
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
import com.f0x1d.notes.db.daos.NoteItemsDao;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteItem;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.fragment.bottomSheet.SetNotify;
import com.f0x1d.notes.fragment.choose.ChooseFolder;
import com.f0x1d.notes.fragment.editing.NoteEdit;
import com.f0x1d.notes.fragment.lock.LockScreen;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.fragment.main.NotesInFolder;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.view.theming.MyColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<NoteOrFolder> items;
    Activity activity;

    private final int NOTE = 1;
    private final int FOLDER = 2;
    private final int NOTIFY = 3;

    private boolean anim;

    private NoteOrFolderDao dao;

    public ItemsAdapter(List<NoteOrFolder> items, Activity activity, boolean anim){
        this.items = items;
        this.activity = activity;
        this.anim = anim;

        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NOTE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note, parent, false);

            if (anim){
                Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.push_down);
                animation.setDuration(400);
                view.startAnimation(animation);
            }

            return new noteViewHolder(view);
        } else if (viewType == FOLDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder, parent, false);

            if (anim){
                Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.push_down);
                animation.setDuration(400);
                view.startAnimation(animation);
            }

            return new folderViewHolder(view);
        } else if (viewType == NOTIFY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notify, parent, false);

            if (anim){
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

    private void initLayoutNotify(notifyViewHolder holder, int position){
        try {
            holder.cardView.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(getColorFromDataBase(position))));

            if (UselessUtils.ifBrightColor(Color.parseColor(getColorFromDataBase(position)))){
                if (UselessUtils.ifCustomTheme()){
                    holder.title.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.text.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.notify_text.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.pinned.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_priority_high_black_24dp), ThemesEngine.lightColorIconColor));
                } else {
                    holder.title.setTextColor(Color.BLACK);
                    holder.text.setTextColor(Color.BLACK);
                    holder.notify_text.setTextColor(Color.BLACK);
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_black_24dp));
                }
            } else {
                if (UselessUtils.ifCustomTheme()){
                    holder.title.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.text.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.notify_text.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.pinned.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_priority_high_white_24dp), ThemesEngine.darkColorIconColor));
                } else {
                    holder.title.setTextColor(Color.WHITE);
                    holder.text.setTextColor(Color.WHITE);
                    holder.notify_text.setTextColor(Color.WHITE);
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_white_24dp));
                }
            }
        } catch (Exception e){
            if (UselessUtils.ifCustomTheme()){
                holder.cardView.setCardBackgroundColor(Color.BLACK);
            }

            if (UselessUtils.ifBrightColor(holder.cardView.getCardBackgroundColor().getDefaultColor())){
                if (UselessUtils.ifCustomTheme()){
                    holder.title.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.text.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.notify_text.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.pinned.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_priority_high_black_24dp), ThemesEngine.lightColorIconColor));
                } else {
                    holder.title.setTextColor(Color.BLACK);
                    holder.text.setTextColor(Color.BLACK);
                    holder.notify_text.setTextColor(Color.BLACK);
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_black_24dp));
                }
            } else {
                if (UselessUtils.ifCustomTheme()){
                    holder.title.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.text.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.notify_text.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.pinned.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_priority_high_white_24dp), ThemesEngine.darkColorIconColor));
                } else {
                    holder.title.setTextColor(Color.WHITE);
                    holder.text.setTextColor(Color.WHITE);
                    holder.notify_text.setTextColor(Color.WHITE);
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_white_24dp));
                }
            }
        }

        holder.notify_text.setText(Html.fromHtml("<b>" + activity.getString(R.string.notify) + "</b>"));

        holder.title.setText(getNotifyTitle(items.get(position).id));
        holder.text.setText(getNotifyText(items.get(position).id));

        if (items.get(position).pinned == 1){
            holder.pinned.setVisibility(View.VISIBLE);
        } else {
            holder.pinned.setVisibility(View.INVISIBLE);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setItems(new String[]{activity.getString(R.string.now), activity.getString(R.string.set_time)}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        String name1 = "Напоминания";
                                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                        NotificationChannel channel = new NotificationChannel("com.f0x1d.notes", name1, importance);
                                        // Register the channel with the system; you can't change the importance
                                        // or other notification behaviors after this
                                        channel.enableVibration(true);
                                        channel.enableLights(true);
                                        NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);
                                        notificationManager.createNotificationChannel(channel);
                                    }

                                    // Create Notification
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(activity)
                                            .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                                            .setContentTitle(items.get(position).title)
                                            .setContentText(items.get(position).text)
                                            .setContentIntent(PendingIntent.getActivity(App.getContext(), 228, new Intent(App.getContext(), MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                                            .setAutoCancel(true)
                                            .setVibrate(new long[]{1000L, 1000L, 1000L})
                                            .setChannelId("com.f0x1d.notes");

                                    NotificationManager notificationManager =
                                            (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);

                                    notificationManager.notify((int) items.get(position).id, builder.build());
                                    break;
                                case 1:
                                    FragmentActivity activity1 = (FragmentActivity) activity;

                                    PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("notify_title", items.get(position).title).putString("notify_text", items.get(position).text)
                                            .putInt("notify_id", (int) items.get(position).id).apply();
                                    SetNotify notify = new SetNotify();
                                    notify.show(activity1.getSupportFragmentManager(), "TAG");
                                    break;
                            }
                        }
                    });
                    builder.show();
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getNotifyDialog(position);
                return false;
            }
        });
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

        holder.name.setText(getFolderNameFromDataBase(items.get(position).id, position));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotesInFolder.in_ids.add(getFolderNameFromDataBase(items.get(position).id, position));

                activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out)
                        .replace(android.R.id.content, new NotesInFolder(), "in_folder").addToBackStack(null).commit();
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getFoldersDialog(position);
                return false;
            }
        });
    }

    public void deleteNote(long id){
        dao.deleteNote(id);
    }

    public void deleteFolder(final String folder_name){
        try {
            dao.deleteFolder(folder_name);
            dao.deleteFolder2(folder_name);

            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    String folderName = folder_name;
                    boolean smthDeleted = false;

                    for (int i = 0; i < dao.getAll().size(); i++){
                        NoteOrFolder noteOrFolder = dao.getAll().get(i);

                        if (noteOrFolder.in_folder_id.equals(folderName) && noteOrFolder.is_folder == 1){
                            dao.deleteFolder2(noteOrFolder.folder_name);
                            dao.deleteFolder(noteOrFolder.folder_name);

                            folderName = noteOrFolder.folder_name;

                            smthDeleted = true;
                        }

                        if (i == dao.getAll().size() - 1 && smthDeleted){
                            i = 0;
                            smthDeleted = false;
                        }
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Deleted all", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();*/
        } catch (IndexOutOfBoundsException e){
            Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
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

    public String getFolderNameFromDataBase(long id, int pos){
        String name = "";

        for (NoteOrFolder noteOrFolder : App.getInstance().getDatabase().noteOrFolderDao().getAll()) {
            if (noteOrFolder.id == id){
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

            if (UselessUtils.ifBrightColor(Color.parseColor(getColorFromDataBase(position)))){
                if (UselessUtils.ifCustomTheme()){
                    holder.title.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.text.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.time.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.pinned.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_priority_high_black_24dp), ThemesEngine.lightColorIconColor));
                    holder.time_pic.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_edit_black_24dp), ThemesEngine.lightColorIconColor));
                } else {
                    holder.title.setTextColor(Color.BLACK);
                    holder.text.setTextColor(Color.BLACK);
                    holder.time.setTextColor(Color.BLACK);
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_black_24dp));
                    holder.time_pic.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_black_24dp));
                }
            } else {
                if (UselessUtils.ifCustomTheme()){
                    holder.title.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.text.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.time.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.pinned.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_priority_high_white_24dp), ThemesEngine.darkColorIconColor));
                    holder.time_pic.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_edit_white_24dp), ThemesEngine.darkColorIconColor));
                } else {
                    holder.title.setTextColor(Color.WHITE);
                    holder.text.setTextColor(Color.WHITE);
                    holder.time.setTextColor(Color.WHITE);
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_white_24dp));
                    holder.time_pic.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_white_24dp));
                }
            }
        } catch (Exception e){
            if (UselessUtils.ifCustomTheme()){
                holder.note_card.setCardBackgroundColor(Color.BLACK);
            }

            if (UselessUtils.ifBrightColor(holder.note_card.getCardBackgroundColor().getDefaultColor())){
                if (UselessUtils.ifCustomTheme()){
                    holder.title.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.text.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.time.setTextColor(ThemesEngine.lightColorTextColor);
                    holder.pinned.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_priority_high_black_24dp), ThemesEngine.lightColorIconColor));
                    holder.time_pic.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_edit_black_24dp), ThemesEngine.lightColorIconColor));
                } else {
                    holder.title.setTextColor(Color.BLACK);
                    holder.text.setTextColor(Color.BLACK);
                    holder.time.setTextColor(Color.BLACK);
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_black_24dp));
                    holder.time_pic.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_black_24dp));
                }
            } else {
                if (UselessUtils.ifCustomTheme()){
                    holder.title.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.text.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.time.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.pinned.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_priority_high_white_24dp), ThemesEngine.darkColorIconColor));
                    holder.time_pic.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_edit_white_24dp), ThemesEngine.darkColorIconColor));
                } else {
                    holder.title.setTextColor(Color.WHITE);
                    holder.time.setTextColor(Color.WHITE);
                    holder.text.setTextColor(Color.WHITE);
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_white_24dp));
                    holder.time_pic.setImageDrawable(activity.getDrawable(R.drawable.ic_edit_white_24dp));
                }
            }
        }

        holder.title.setText(items.get(position).title);

        if (items.get(position).pinned == 1){
            holder.pinned.setVisibility(View.VISIBLE);
        } else {
            holder.pinned.setVisibility(View.INVISIBLE);
        }

        String text = "null";

        NoteItemsDao dao = App.getInstance().getDatabase().noteItemsDao();
        for (NoteItem noteItem : dao.getAll()) {
            if (noteItem.to_id == items.get(position).id){
                if (noteItem.position == 0){
                    text = noteItem.text;
                    break;
                }
            }
        }

        try {
            boolean oneLine;

            oneLine = !Pattern.compile("\\r?\\n").matcher(text).find();

            for (String retval : text.split("\\r?\\n")) {
                if (oneLine){
                    holder.text.setText(retval);
                } else {
                    holder.text.setText(retval + "\n...");
                }
                break;
            }
        } catch (Exception e){}

        if (holder.text.getText().toString().equals("")){
            holder.text.setText(Html.fromHtml("<i>" + activity.getString(R.string.empty_note) + "</i>"));
        }

        if (items.get(position).locked == 1){
            holder.text.setText(Html.fromHtml("<i>" + activity.getString(R.string.blocked) + "</i>"));
        }

        Date currentDate = new Date(items.get(position).edit_time);
        DateFormat df = new SimpleDateFormat("HH:mm | dd.MM.yyyy");

        holder.time.setText(df.format(currentDate));

        holder.note_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getNotesDialog(position);
                return false;
            }
        });

        holder.note_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                    args.putLong("id", items.get(position).id);
                    args.putInt("locked", items.get(position).locked);
                    args.putString("title", items.get(position).title);

                            if (!String.valueOf(items.get(position).in_folder_id).equals("def")){
                                PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("in_folder_edit", true).apply();
                            } else {
                                PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("in_folder_edit", false).apply();
                            }

                            if (items.get(position).locked == 1){
                                if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("lock", false)){
                                    Bundle lockargs = new Bundle();
                                        lockargs.putLong("id", items.get(position).id);
                                        lockargs.putInt("locked", items.get(position).locked);
                                        lockargs.putString("title", items.get(position).title);
                                        //lockargs.putString("text", items.get(position).text);
                                        lockargs.putBoolean("to_note", true);

                                    activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                                            android.R.id.content, LockScreen.newInstance(lockargs), "lock").addToBackStack(null).commit();
                                } else {
                                    Toast.makeText(activity, activity.getString(R.string.enable_pin), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                activity.getFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                                        android.R.id.content, NoteEdit.newInstance(args), "edit").addToBackStack(null).commit();
                            }

                        }
        });
    }

    private String getNotifyTitle(long id){
        String flex = "null";

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.id == id) {
                flex = noteOrFolder.title;
                break;
            }
        }

        return flex;
    }

    private String getNotifyText(long id){
        String flex = "null";

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.id == id) {
                flex = noteOrFolder.text;
                break;
            }
        }

        return flex;
    }

    private void getNotifyDialog(int position){
        String[] hm;

        try {
            Color.parseColor(getColorFromDataBase(position));

            if (items.get(position).pinned == 1){
                hm = new String[]{activity.getString(R.string.change), activity.getString(R.string.unpin), activity.getString(R.string.color), activity.getString(R.string.restore_color)};
            } else {
                hm = new String[]{activity.getString(R.string.change), activity.getString(R.string.pin), activity.getString(R.string.color), activity.getString(R.string.restore_color)};
            }
        } catch (Exception e){
            if (items.get(position).pinned == 1){
                hm = new String[]{activity.getString(R.string.change), activity.getString(R.string.unpin), activity.getString(R.string.color)};
            } else {
                hm = new String[]{activity.getString(R.string.change), activity.getString(R.string.pin), activity.getString(R.string.color)};
            }
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);

        builder1.setItems(hm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        View v = LayoutInflater.from(activity).inflate(R.layout.dialog_two_edit_texts, null);

                        EditText title = v.findViewById(R.id.edit_text_one);
                        title.setBackground(null);
                        title.setHint(activity.getString(R.string.title));

                        EditText text = v.findViewById(R.id.edit_text_two);
                        text.setBackground(null);
                        text.setHint(activity.getString(R.string.text));

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setView(v);

                        AlertDialog dialog1337 = builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog228, int which) {
                                dao.updateNoteTitle(title.getText().toString(), items.get(position).id);
                                dao.updateNoteText(text.getText().toString(), items.get(position).id);

                                notifyItemChanged(position);
                            }
                        }).create();

                        dialog1337.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog1) {
                                if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("night", false)){
                                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                }
                                if (UselessUtils.ifCustomTheme()){
                                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ThemesEngine.textColor);
                                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                                }
                            }
                        });

                        dialog1337.show();
                        break;
                    case 1:

                        if (items.get(position).pinned == 1){
                            dao.updateNotePinned(0, items.get(position).id);
                        } else {
                            dao.updateNotePinned(1, items.get(position).id);
                        }

                        if (NotesInFolder.in_ids.size() != 0){
                            activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                                    android.R.id.content, new NotesInFolder(), "in_folder").commit();
                        } else {
                            activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                                    android.R.id.content, new Notes(), "notes").commit();
                        }
                        break;
                    case 2:

                        int currentColor;

                        try {
                            currentColor = Color.parseColor(getColorFromDataBase(position));
                        } catch (Exception e){
                            currentColor = 0xffffffff;
                        }

                        MyColorPickerDialog colorPickerDialog = MyColorPickerDialog.newBuilderNew().setColor(currentColor).create();
                        colorPickerDialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                            @Override
                            public void onColorSelected(int dialogId, int color) {
                                Log.e("notes_err", "onColorSelected: " + "#" + Integer.toHexString(color));

                                dao.updateNoteColor("#" + Integer.toHexString(color), items.get(position).id);

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
                        dao.updateNoteColor("", items.get(position).id);
                        notifyItemChanged(position);
                        break;
                }
            }
        });

        builder1.show();
    }

    private void getFoldersDialog(int position){
        String[] hm;

        try {
            Color.parseColor(getColorFromDataBase(position));

            if (items.get(position).pinned == 1){
                hm = new String[]{activity.getString(R.string.rename), activity.getString(R.string.unpin), activity.getString(R.string.color), activity.getString(R.string.restore_color)};
            } else {
                hm = new String[]{activity.getString(R.string.rename), activity.getString(R.string.pin), activity.getString(R.string.color), activity.getString(R.string.restore_color)};
            }
        } catch (Exception e){
            if (items.get(position).pinned == 1){
                hm = new String[]{activity.getString(R.string.rename), activity.getString(R.string.unpin), activity.getString(R.string.color)};
            } else {
                hm = new String[]{activity.getString(R.string.rename), activity.getString(R.string.pin), activity.getString(R.string.color)};
            }
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);

        builder1.setItems(hm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        View v = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_text, null);

                        EditText text = v.findViewById(R.id.edit_text);
                        text.setBackground(null);
                        text.setText(getFolderNameFromDataBase(items.get(position).id, position));
                        text.setHint(activity.getString(R.string.name));

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                        builder.setView(v);
                        builder.setTitle(activity.getString(R.string.folder_name));

                        AlertDialog dialog1337 =  builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog228, int which) {
                                boolean create = true;

                                for (NoteOrFolder noteOrFolder : dao.getAll()) {
                                    if (noteOrFolder.is_folder == 1 && noteOrFolder.folder_name.equals(text.getText().toString())){
                                        create = false;
                                        Toast.makeText(App.getContext(), activity.getString(R.string.folder_error), Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }

                                if (create){
                                    for (NoteOrFolder noteOrFolder : dao.getAll()) {
                                        if (noteOrFolder.in_folder_id.equals(getFolderNameFromDataBase(items.get(position).id, position))){
                                            dao.updateInFolderIdById(text.getText().toString(), noteOrFolder.id);
                                        }
                                    }
                                    dao.updateFolderTitle(text.getText().toString(), getFolderNameFromDataBase(items.get(position).id, position));
                                    dao.updateInFolderId(text.getText().toString(), getFolderNameFromDataBase(items.get(position).id, position));

                                    notifyItemChanged(position);
                                }
                            }
                        }).create();

                        dialog1337.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog1) {
                                if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("night", false)){
                                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                }
                                if (UselessUtils.ifCustomTheme()){
                                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ThemesEngine.textColor);
                                    dialog1337.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                                }
                            }
                        });
                        dialog1337.show();
                        break;
                    case 1:
                        if (items.get(position).pinned == 1){
                            dao.updateFolderPinned(0, getFolderNameFromDataBase(items.get(position).id, position));
                        } else {
                            dao.updateFolderPinned(1, getFolderNameFromDataBase(items.get(position).id, position));
                        }

                        if (NotesInFolder.in_ids.size() != 0){
                            activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                                    android.R.id.content, new NotesInFolder(), "in_folder").commit();
                        } else {
                            activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                                    android.R.id.content, new Notes(), "notes").commit();
                        }
                        break;
                    case 2:
                        int currentColor;

                        try {
                            currentColor = Color.parseColor(getColorFromDataBase(position));
                        } catch (Exception e){
                            currentColor = 0xffffffff;
                        }

                        MyColorPickerDialog colorPickerDialog = MyColorPickerDialog.newBuilderNew().setColor(currentColor).create();
                        colorPickerDialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                            @Override
                            public void onColorSelected(int dialogId, int color) {
                                Log.e("notes_err", "onColorSelected: " + "#" + Integer.toHexString(color));

                                dao.updateFolderColor("#" + Integer.toHexString(color), getFolderNameFromDataBase(items.get(position).id, position));

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
                        dao.updateFolderColor("", getFolderNameFromDataBase(items.get(position).id, position));
                        notifyItemChanged(position);
                        break;
                }
            }
        });

        builder1.show();

    }

    private void getNotesDialog(int position) {
        String[] hm;
        try {
            Color.parseColor(getColorFromDataBase(position));

            if (items.get(position).pinned == 1) {
                hm = new String[]{activity.getString(R.string.unpin), activity.getString(R.string.color), activity.getString(R.string.move_ro_folder), activity.getString(R.string.restore_color)};
            } else {
                hm = new String[]{activity.getString(R.string.pin), activity.getString(R.string.color), activity.getString(R.string.move_ro_folder), activity.getString(R.string.restore_color)};
            }
        } catch (Exception e){
            if (items.get(position).pinned == 1) {
                hm = new String[]{activity.getString(R.string.unpin), activity.getString(R.string.color), activity.getString(R.string.move_ro_folder)};
            } else {
                hm = new String[]{activity.getString(R.string.pin), activity.getString(R.string.color), activity.getString(R.string.move_ro_folder)};
            }
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);

        builder1.setItems(hm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (items.get(position).pinned == 1){
                            dao.updateNotePinned(0, items.get(position).id);
                        } else {
                            dao.updateNotePinned(1, items.get(position).id);
                        }

                        if (NotesInFolder.in_ids.size() != 0) {
                            activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, new NotesInFolder(), "in_folder").commit();
                        } else {
                            activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, new Notes(), "notes").commit();
                        }

                        break;
                    case 1:

                        int currentColor;

                        try {
                            currentColor = Color.parseColor(getColorFromDataBase(position));
                        } catch (Exception e){
                            currentColor = 0xffffffff;
                        }

                        MyColorPickerDialog colorPickerDialog = MyColorPickerDialog.newBuilderNew().setColor(currentColor).create();
                            colorPickerDialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                                @Override
                                public void onColorSelected(int dialogId, int color) {
                                    Log.e("notes_err", "onColorSelected: " + "#" + Integer.toHexString(color) + " id: " + items.get(position).id);

                                    dao.updateNoteColor("#" + Integer.toHexString(color), items.get(position).id);

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
                        Bundle args = new Bundle();
                            ChooseFolder.in_ids.clear();
                            ChooseFolder.in_ids.add("def");

                            args.putLong("id", items.get(position).id);

                        activity.getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, ChooseFolder.newInstance(args), "choose_folder")
                                .addToBackStack(null).commit();
                        break;
                    case 3:
                        dao.updateNoteColor("", items.get(position).id);

                        notifyItemChanged(position);
                        break;
                }
            }
        });

        builder1.show();

    }

    @Override
    public int getItemViewType(int position) {
        NoteOrFolder item = items.get(position);
        if (item.is_folder == 0) {
            return NOTE;
        } else if (item.is_folder == 1) {
            return FOLDER;
        } else if (item.is_folder == 2){
            return NOTIFY;
        } else {
            return -1;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
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

    class noteViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView text;
        CardView note_card;
        ImageView pinned;
        TextView time;
        ImageView time_pic;

        noteViewHolder(@NonNull View itemView) {
            super(itemView);

            note_card = itemView.findViewById(R.id.note_card);
            title = itemView.findViewById(R.id.textView_title);
            text = itemView.findViewById(R.id.textView_text);
            pinned = itemView.findViewById(R.id.pinned);
            time = itemView.findViewById(R.id.time);
            time_pic = itemView.findViewById(R.id.time_ic);
        }
    }

    class notifyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView text;
        ImageView pinned;
        TextView notify_text;
        CardView cardView;

        notifyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textView_title);
            text = itemView.findViewById(R.id.textView_text);
            pinned = itemView.findViewById(R.id.pinned);
            cardView = itemView.findViewById(R.id.note_card);
            notify_text = itemView.findViewById(R.id.notify_text);
        }
    }
}