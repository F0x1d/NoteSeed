package com.f0x1d.notes.fragment.bottom_sheet;

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
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.f0x1d.notes.R;
import com.f0x1d.notes.App;
import com.f0x1d.notes.db.daos.NotifyDao;
import com.f0x1d.notes.db.entities.Notify;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.receiver.NotifyServiceReceiver;
import com.f0x1d.notes.view.theming.MyButton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

public class SetNotify extends BottomSheetDialogFragment {

    int myHour = 0;
    int myMinute = 00;
    int myYear = 2018;
    int myMonth = 12;
    int myDay = 00;

    Calendar myCalendar = Calendar.getInstance();

    TextView time;
    TextView date;

    RelativeLayout choose_time;
    RelativeLayout choose_date;

    ImageView time_icon;
    ImageView date_icon;

    MyButton ok;
    MyButton delete;

    boolean exists = false;

    String title = null;
    String text = null;
    long time_already;
    long to_id = 0;
    long id = 0;

    NotifyDao dao;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        dao = App.getInstance().getDatabase().notifyDao();

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_notify, null);

        LinearLayout layout = v.findViewById(R.id.background);

        if (UselessUtils.ifCustomTheme()){
            layout.setBackgroundColor(ThemesEngine.background);
        } else if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", false)){
            layout.setBackgroundColor(getActivity().getResources().getColor(R.color.statusbar));
        } else {
            layout.setBackgroundColor(Color.WHITE);
        }

        choose_date = v.findViewById(R.id.choose_date_layout);
        choose_time = v.findViewById(R.id.choose_time_layout);

        time_icon = v.findViewById(R.id.time_icon);
        date_icon = v.findViewById(R.id.date_icon);

        time = v.findViewById(R.id.choose_time);
            choose_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    myHour = c.get(Calendar.HOUR_OF_DAY);
                    myMinute = c.get(Calendar.MINUTE);
                    if (UselessUtils.getBool("night", false)){
                        TimePickerDialog tpd = new TimePickerDialog(getActivity(), R.style.TimePicker, myCallBack, myHour, myMinute, true);
                        tpd.show();
                    } else {
                        TimePickerDialog tpd = new TimePickerDialog(getActivity(), myCallBack, myHour, myMinute, true);
                        tpd.show();
                    }
                }
            });

        date = v.findViewById(R.id.choose_date);
            choose_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    myDay = c.get(Calendar.DATE);
                    myMonth = c.get(Calendar.MONTH);
                    myYear = c.get(Calendar.YEAR);

                    if (UselessUtils.getBool("night", false)){
                        DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.DatePicker, myCallBack2, myYear, myMonth, myDay);
                        dpd.show();
                    } else {
                        DatePickerDialog dpd = new DatePickerDialog(getActivity(), myCallBack2, myYear, myMonth, myDay);
                        dpd.show();
                    }
                }
            });

        ok = v.findViewById(R.id.ok);
        delete = v.findViewById(R.id.delete);
            if (UselessUtils.getBool("night", false)){
                ok.setBackgroundTintList(ColorStateList.valueOf(App.getContext().getResources().getColor(R.color.statusbar)));
                delete.setBackgroundTintList(ColorStateList.valueOf(App.getContext().getResources().getColor(R.color.statusbar)));

                date_icon.setImageDrawable(App.getContext().getDrawable(R.drawable.ic_date_range_white_24dp));
                time_icon.setImageDrawable(App.getContext().getDrawable(R.drawable.ic_access_time_white_24dp));
            }

        for (Notify notify : dao.getAll()) {
            if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("notify_id", 0) == notify.to_id) {
                title = notify.title;
                text = notify.text;
                id = notify.id;
                to_id = notify.to_id;
                time_already = notify.time;

                exists = true;
            }
        }



        if (exists){
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(id);
                    Intent myIntent = new Intent(getActivity(), NotifyServiceReceiver.class);
                    PendingIntent service = PendingIntent.getBroadcast(getActivity(), (int) to_id, myIntent, 0);

                    AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
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
                    if (!exists){
                        if (!date.getText().toString().equals(getActivity().getString(R.string.choose_date)) && !time.getText().toString().equals(getActivity().getString(R.string.choose_time))){

                            dao.insert(new Notify(PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getString("notify_title", ""),
                                    PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getString("notify_text", ""), myCalendar.getTimeInMillis(),
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("notify_id", 0)));

                            Intent myIntent = new Intent(getActivity(), NotifyServiceReceiver.class);
                            PendingIntent service = PendingIntent.getBroadcast(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("notify_id", 0), myIntent, 0);

                            AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, myCalendar.getTimeInMillis(), service);
                            } else {
                                am.setExact(AlarmManager.RTC_WAKEUP, myCalendar.getTimeInMillis(), service);
                            }

                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Hmmmmm...", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!date.getText().toString().equals(getActivity().getString(R.string.choose_date)) && !time.getText().toString().equals(getActivity().getString(R.string.choose_time))){

                            delete(id);

                            dao.insert(new Notify(PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getString("notify_title", ""),
                                    PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getString("notify_text", ""), myCalendar.getTimeInMillis(),
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("notify_id", 0)));

                            Intent myIntent = new Intent(getActivity(), NotifyServiceReceiver.class);
                            PendingIntent service = PendingIntent.getBroadcast(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("notify_id", 0), myIntent, 0);

                            AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                            am.cancel(service);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, myCalendar.getTimeInMillis(), service);
                            } else {
                                am.setExact(AlarmManager.RTC_WAKEUP, myCalendar.getTimeInMillis(), service);
                            }

                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Hmmmmm...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });



        dialog.setContentView(v);
    }

    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);

            myHour = hourOfDay;
            myMinute = minute;
            time.setText(myHour + ":" + myMinute);
        }
    };

    DatePickerDialog.OnDateSetListener myCallBack2 = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

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

    public void delete(long id){
        dao.delete(id);
    }
}
