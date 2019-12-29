package com.f0x1d.notes.fragment.bottomSheet;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.db.daos.NotifyDao;
import com.f0x1d.notes.db.entities.Notify;
import com.f0x1d.notes.receiver.NotifyServiceReceiver;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.view.theming.MyButton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

public class SetNotifyDialog extends BottomSheetDialogFragment {

    private int myHour = 0;
    private int myMinute = 00;
    private int myYear = 2018;
    private int myMonth = 12;
    private int myDay = 00;

    private Calendar myCalendar = Calendar.getInstance();

    private TextView time;
    private TextView date;
    DatePickerDialog.OnDateSetListener myCallBack2 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.YEAR, year);

            myYear = year;
            myMonth = monthOfYear;
            myMonth = myMonth + 1;
            myDay = dayOfMonth;
            date.setText(myDay + "." + myMonth + "." + myYear);
        }
    };
    private RelativeLayout chooseTime;
    private RelativeLayout chooseDate;
    private MyButton ok;
    private MyButton delete;
    private boolean exists = false;
    private String title = null;
    private String text = null;
    private long timeAlready;
    private long toId = 0;
    private long id = 0;
    private NotifyDao dao;
    private Notify notify;
    private TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);

            myHour = hourOfDay;
            myMinute = minute;

            String minutes = String.valueOf(myMinute);
            String hour = String.valueOf(myHour);

            if (String.valueOf(myMinute).length() <= 1)
                minutes = "0" + minutes;

            if (String.valueOf(myHour).length() <= 1)
                hour = "0" + hour;

            time.setText(hour + ":" + minutes);
        }
    };

    public SetNotifyDialog(Notify notify) {
        this.notify = notify;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        dao = App.getInstance().getDatabase().notifyDao();

        View v = LayoutInflater.from(requireActivity()).inflate(R.layout.set_notify, null);

        LinearLayout layout = v.findViewById(R.id.background);
        if (UselessUtils.isCustomTheme()) {
            layout.setBackgroundColor(ThemesEngine.background);
        } else if (App.getDefaultSharedPreferences().getBoolean("night", false)) {
            layout.setBackgroundColor(requireActivity().getResources().getColor(R.color.statusbar));
        } else {
            layout.setBackgroundColor(Color.WHITE);
        }

        chooseDate = v.findViewById(R.id.choose_date_layout);
        chooseTime = v.findViewById(R.id.choose_time_layout);

        time = v.findViewById(R.id.choose_time);
        time.setText(getString(R.string.choose_time));
        chooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                myHour = c.get(Calendar.HOUR_OF_DAY);
                myMinute = c.get(Calendar.MINUTE);

                if (UselessUtils.getBool("night", false)) {
                    TimePickerDialog tpd = new TimePickerDialog(requireActivity(), R.style.TimePicker, myCallBack, myHour, myMinute, true);
                    tpd.show();
                } else {
                    TimePickerDialog tpd = new TimePickerDialog(requireActivity(), myCallBack, myHour, myMinute, true);
                    tpd.show();
                }
            }
        });

        date = v.findViewById(R.id.choose_date);
        date.setText(getString(R.string.choose_date));
        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                myDay = c.get(Calendar.DATE);
                myMonth = c.get(Calendar.MONTH);
                myYear = c.get(Calendar.YEAR);

                if (UselessUtils.getBool("night", false)) {
                    DatePickerDialog dpd = new DatePickerDialog(requireActivity(), R.style.DatePicker, myCallBack2, myYear, myMonth, myDay);
                    dpd.show();
                } else {
                    DatePickerDialog dpd = new DatePickerDialog(requireActivity(), myCallBack2, myYear, myMonth, myDay);
                    dpd.show();
                }
            }
        });

        ok = v.findViewById(R.id.ok);
        ok.setText(getString(R.string.ok));
        delete = v.findViewById(R.id.delete);
        delete.setText(getString(R.string.delete_notify));
        if (UselessUtils.getBool("night", false)) {
            ok.setBackgroundTintList(ColorStateList.valueOf(App.getContext().getResources().getColor(R.color.statusbar)));
            delete.setBackgroundTintList(ColorStateList.valueOf(App.getContext().getResources().getColor(R.color.statusbar)));

            date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_date_range_white_24dp, 0, 0, 0);
            time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_access_time_white_24dp, 0, 0, 0);
        } else {
            date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_date_range_black_24dp, 0, 0, 0);
            time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_access_time_black_24dp, 0, 0, 0);
        }

        for (Notify notify : dao.getAll()) {
            if (this.notify.toId == notify.toId) {
                title = notify.title;
                text = notify.text;
                id = notify.id;
                toId = notify.toId;
                timeAlready = notify.time;

                exists = true;
            }
        }

        if (exists) {
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(id);
                    Intent myIntent = new Intent(requireActivity(), NotifyServiceReceiver.class);
                    PendingIntent service = PendingIntent.getBroadcast(requireActivity(), (int) toId, myIntent, 0);

                    AlarmManager am = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
                    am.cancel(service);

                    dialog.dismiss();
                }
            });
        } else {
            delete.setVisibility(View.GONE);
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!exists) {
                    if (!date.getText().toString().equals(getString(R.string.choose_date)) && !time.getText().toString().equals(getString(R.string.choose_time))) {
                        notify.time = myCalendar.getTimeInMillis();
                        dao.insert(notify);

                        Intent myIntent = new Intent(requireActivity(), NotifyServiceReceiver.class);
                        PendingIntent service = PendingIntent.getBroadcast(requireActivity(), (int) notify.toId,
                                myIntent, 0);

                        AlarmManager am = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, myCalendar.getTimeInMillis(), service);
                        } else {
                            am.setExact(AlarmManager.RTC_WAKEUP, myCalendar.getTimeInMillis(), service);
                        }

                        dialog.dismiss();
                    } else {
                        Toast.makeText(requireActivity(), "Hmmmmm...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!date.getText().toString().equals(getString(R.string.choose_date)) && !time.getText().toString().equals(getString(R.string.choose_time))) {
                        delete(id);

                        notify.time = myCalendar.getTimeInMillis();
                        dao.insert(notify);

                        Intent myIntent = new Intent(requireActivity(), NotifyServiceReceiver.class);
                        PendingIntent service = PendingIntent.getBroadcast(requireActivity(), (int) notify.toId,
                                myIntent, 0);

                        AlarmManager am = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
                        am.cancel(service);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, myCalendar.getTimeInMillis(), service);
                        } else {
                            am.setExact(AlarmManager.RTC_WAKEUP, myCalendar.getTimeInMillis(), service);
                        }

                        dialog.dismiss();
                    } else {
                        Toast.makeText(requireActivity(), "Hmmmmm...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        dialog.setContentView(v);
    }

    public void delete(long id) {
        dao.delete(id);
    }

    @SuppressLint("NewApi")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return super.onCreateDialog(savedInstanceState);

        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(UselessUtils.getNavColor());

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }

        return dialog;
    }
}
