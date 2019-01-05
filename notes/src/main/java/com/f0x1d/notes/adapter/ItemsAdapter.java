package com.f0x1d.notes.adapter;

import android.app.Activity;
import android.app.Notification;
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
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.fragment.lock.LockScreen;
import com.f0x1d.notes.fragment.editing.NoteEdit;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.fragment.main.NotesInFolder;
import com.f0x1d.notes.App;
import com.f0x1d.notes.db.daos.NoteOrFolderDao;
import com.f0x1d.notes.db.entities.NoteOrFolder;
import com.f0x1d.notes.model.Theme;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.view.theming.MyColorPickerDialog;
import com.f0x1d.notes.view.theming.MyImageView;
import com.f0x1d.notes.view.theming.MyTextView;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

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

    // specify the row layout file and click for each row
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
            throw new RuntimeException("The type has to be NOTE or FOLDER");
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

        holder.title.setText(items.get(position).title);
        holder.text.setText(items.get(position).text);

        if (items.get(position).pinned == 1){
            holder.pinned.setVisibility(View.VISIBLE);
        } else {
            holder.pinned.setVisibility(View.INVISIBLE);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        holder.name.setText(getFolderNameFromDataBase(position));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                    args.putString("folder_name", items.get(position).folder_name);

                PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("in_folder_back_stack", true).apply();

                activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, NotesInFolder.newInstance(args), "in_folder").commit();
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

    public void deleteFolder(String folder_name){
        try {
            dao.deleteFolder(folder_name);
            dao.deleteFolder2(folder_name);
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

    private String getFolderNameFromDataBase(int position){
        long id = items.get(position).id;

        String name = "";

        for (NoteOrFolder noteOrFolder : dao.getAll()) {
            if (noteOrFolder.id == id){
                name = noteOrFolder.folder_name;
            }
        }

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
                } else {
                    holder.title.setTextColor(Color.BLACK);
                    holder.text.setTextColor(Color.BLACK);
                    holder.time.setTextColor(Color.BLACK);
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_black_24dp));
                }
            } else {
                if (UselessUtils.ifCustomTheme()){
                    holder.title.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.text.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.time.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.pinned.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_priority_high_white_24dp), ThemesEngine.darkColorIconColor));
                } else {
                    holder.title.setTextColor(Color.WHITE);
                    holder.text.setTextColor(Color.WHITE);
                    holder.time.setTextColor(Color.WHITE);
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_white_24dp));
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
                } else {
                    holder.title.setTextColor(Color.BLACK);
                    holder.text.setTextColor(Color.BLACK);
                    holder.time.setTextColor(Color.BLACK);
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_black_24dp));
                }
            } else {
                if (UselessUtils.ifCustomTheme()){
                    holder.title.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.text.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.time.setTextColor(ThemesEngine.darkColorTextColor);
                    holder.pinned.setImageDrawable(UselessUtils.setTint(activity.getDrawable(R.drawable.ic_priority_high_white_24dp), ThemesEngine.darkColorIconColor));
                } else {
                    holder.title.setTextColor(Color.WHITE);
                    holder.time.setTextColor(Color.WHITE);
                    holder.text.setTextColor(Color.WHITE);
                    holder.pinned.setImageDrawable(activity.getDrawable(R.drawable.ic_priority_high_white_24dp));
                }
            }
        }

        holder.title.setText(items.get(position).title);

        boolean oneLine;

        if (Pattern.compile("\\r?\\n").matcher(items.get(position).text).find()){
            oneLine = false;
        } else {
            oneLine = true;
        }

        for (String retval : items.get(position).text.split("\\r?\\n")) {
            if (oneLine){
                holder.text.setText(retval);
            } else {
                holder.text.setText(retval + "\n...");
            }
            break;
        }

        if (items.get(position).pinned == 1){
            holder.pinned.setVisibility(View.VISIBLE);
        } else {
            holder.pinned.setVisibility(View.INVISIBLE);
        }

        if (items.get(position).text.isEmpty()){
            holder.text.setText(Html.fromHtml("<i>" + activity.getString(R.string.empty_note) + "</i>"));
        }

        if (items.get(position).locked == 1){
            holder.text.setText(Html.fromHtml("<i>" + activity.getString(R.string.blocked) + "</i>"));
        }

        Date currentDate = new Date(items.get(position).edit_time);
        DateFormat df = new SimpleDateFormat("HH:mm:ss dd.MM.yy");

        holder.time.setText(activity.getString(R.string.last_change) + " " + df.format(currentDate));

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
                    args.putString("text", items.get(position).text);

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
                                        lockargs.putString("text", items.get(position).text);
                                        lockargs.putBoolean("to_note", true);

                                    activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, LockScreen.newInstance(lockargs), "lock").addToBackStack(null).commit();
                                } else {
                                    Toast.makeText(activity, activity.getString(R.string.enable_pin), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, NoteEdit.newInstance(args), "edit").addToBackStack(null).commit();
                                //activity.getFragmentManager().beginTransaction().replace(android.R.id.content, NoteEdit.newInstance(args)).addToBackStack(null).commit();
                            }

                        }
                        // переход на следующую строку
                        // а если следующей нет (текущая - последняя), то false -
                        // выходим из цикла
        });
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

                        AlertDialog dialog1337 =  builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog228, int which) {
                                dao.updateNoteTitle(title.getText().toString(), items.get(position).id);
                                dao.updateNoteText(text.getText().toString(), items.get(position).id);

                                if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("in_folder_back_stack", false)){
                                    activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, new NotesInFolder(), "in_folder").commit();
                                } else {
                                    activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, new Notes(), "notes").commit();
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
                            dao.updateNotePinned(0, items.get(position).id);
                        } else {
                            dao.updateNotePinned(1, items.get(position).id);
                        }

                        if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("in_folder_back_stack", false)){
                            activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, new NotesInFolder(), "in_folder").commit();
                        } else {
                            activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, new Notes(), "notes").commit();
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
                        text.setText(getFolderNameFromDataBase(position));
                        text.setHint(activity.getString(R.string.name));

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                        builder.setView(v);
                        builder.setTitle(activity.getString(R.string.folder_name));

                        AlertDialog dialog1337 =  builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog228, int which) {
                                boolean create = true;

                                for (NoteOrFolder noteOrFolder : dao.getAll()) {
                                    if (noteOrFolder.is_folder == 1 && getFolderNameFromDataBase(position).equals(text.getText().toString())){
                                        create = false;
                                        Toast.makeText(App.getContext(), activity.getString(R.string.folder_error), Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }

                                if (create){
                                    dao.updateFolderTitle(text.getText().toString(), items.get(position).folder_name);
                                    dao.updateInFolderId(text.getText().toString(), items.get(position).folder_name);

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
                            dao.updateFolderPinned(0, items.get(position).folder_name);
                        } else {
                            dao.updateFolderPinned(1, items.get(position).folder_name);
                        }

                        if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("in_folder_back_stack", false)){
                            activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, new NotesInFolder(), "in_folder").commit();
                        } else {
                            activity.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(android.R.id.content, new Notes(), "notes").commit();
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

                                dao.updateFolderColor("#" + Integer.toHexString(color), getFolderNameFromDataBase(position));

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
                        dao.updateFolderColor("", getFolderNameFromDataBase(position));

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
                hm = new String[]{activity.getString(R.string.unpin), activity.getString(R.string.color), activity.getString(R.string.restore_color)};
            } else {
                hm = new String[]{activity.getString(R.string.pin), activity.getString(R.string.color), activity.getString(R.string.restore_color)};
            }
        } catch (Exception e){
            if (items.get(position).pinned == 1) {
                hm = new String[]{activity.getString(R.string.unpin), activity.getString(R.string.color)};
            } else {
                hm = new String[]{activity.getString(R.string.pin), activity.getString(R.string.color)};
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

                        if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("in_folder_back_stack", false)) {
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
                    case 2:
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

        noteViewHolder(@NonNull View itemView) {
            super(itemView);

            note_card = itemView.findViewById(R.id.note_card);
            title = itemView.findViewById(R.id.textView_title);
            text = itemView.findViewById(R.id.textView_text);
            pinned = itemView.findViewById(R.id.pinned);
            time = itemView.findViewById(R.id.time);
        }
    }

    class notifyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView text;
        ImageView pinned;
        TextView notify_text;
        CardView cardView;

        public notifyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textView_title);
            text = itemView.findViewById(R.id.textView_text);
            pinned = itemView.findViewById(R.id.pinned);
            cardView = itemView.findViewById(R.id.note_card);
            notify_text = itemView.findViewById(R.id.notify_text);
        }
    }
}
