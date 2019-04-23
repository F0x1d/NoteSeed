package com.f0x1d.notes.fragment.lock;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.view.theming.MyButton;

public class СhoosePin extends Fragment {

    EditText pass;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if (UselessUtils.ifCustomTheme()) {
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            getActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            getActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
        }

        return inflater.inflate(R.layout.lock_choose, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MyButton odin = view.findViewById(R.id.odin);
        MyButton dva = view.findViewById(R.id.dva);
        MyButton tri = view.findViewById(R.id.tri);

        MyButton cheture = view.findViewById(R.id.cheture);
        MyButton pat = view.findViewById(R.id.pat);
        MyButton shest = view.findViewById(R.id.shest);

        MyButton sem = view.findViewById(R.id.sem);
        MyButton vosem = view.findViewById(R.id.vosem);
        MyButton devat = view.findViewById(R.id.devat);

        MyButton nol = view.findViewById(R.id.nol);
        ImageButton done = view.findViewById(R.id.done);
        done.setBackground(null);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", true)) {
            done.setImageDrawable(getActivity().getDrawable(R.drawable.ic_done_white_24dp));
        } else {
            done.setImageDrawable(getActivity().getDrawable(R.drawable.ic_done_black_24dp));
        }

        MyButton back = view.findViewById(R.id.back);

        pass = view.findViewById(R.id.pass);
        pass.setRawInputType(0x00000000);

        View.OnClickListener oclBtn = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.odin:
                        pass.setText(pass.getText().toString() + "1");
                        break;
                    case R.id.dva:
                        pass.setText(pass.getText().toString() + "2");
                        break;
                    case R.id.tri:
                        pass.setText(pass.getText().toString() + "3");
                        break;
                    case R.id.cheture:
                        pass.setText(pass.getText().toString() + "4");
                        break;
                    case R.id.pat:
                        pass.setText(pass.getText().toString() + "5");
                        break;
                    case R.id.shest:
                        pass.setText(pass.getText().toString() + "6");
                        break;
                    case R.id.sem:
                        pass.setText(pass.getText().toString() + "7");
                        break;
                    case R.id.vosem:
                        pass.setText(pass.getText().toString() + "8");
                        break;
                    case R.id.devat:
                        pass.setText(pass.getText().toString() + "9");
                        break;
                    case R.id.nol:
                        pass.setText(pass.getText().toString() + "0");
                        break;
                    case R.id.back:
                        if (!pass.getText().toString().isEmpty()) {
                            pass.setText("");
                        }
                        break;
                    case R.id.done:
                        if (pass.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), R.string.empty_pass, Toast.LENGTH_SHORT).show();
                        } else {
                            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("pass", pass.getText().toString()).apply();
                            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("lock", true).apply();

                            UselessUtils.clear_back_stack();
                            Toast.makeText(getActivity(), R.string.success, Toast.LENGTH_SHORT).show();
                            UselessUtils.replaceNoBackStack(new Notes(), "notes");
                        }
                        break;

                }
            }
        };

        odin.setOnClickListener(oclBtn);
        dva.setOnClickListener(oclBtn);
        tri.setOnClickListener(oclBtn);

        cheture.setOnClickListener(oclBtn);
        pat.setOnClickListener(oclBtn);
        shest.setOnClickListener(oclBtn);

        sem.setOnClickListener(oclBtn);
        vosem.setOnClickListener(oclBtn);
        devat.setOnClickListener(oclBtn);

        nol.setOnClickListener(oclBtn);
        done.setOnClickListener(oclBtn);
        back.setOnClickListener(oclBtn);

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up);
        animation.setDuration(400);
        back.startAnimation(animation);
        done.startAnimation(animation);

        Animation animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.push_down);
        animation2.setDuration(400);

        ImageView icon = view.findViewById(R.id.icon);
        icon.startAnimation(animation2);

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", true)) {
            odin.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            dva.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            tri.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            cheture.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            pat.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            shest.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            sem.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            vosem.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            devat.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            nol.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            back.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            done.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pass.getText().toString().isEmpty()) {
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("lock", false).apply();
        }
    }
}
