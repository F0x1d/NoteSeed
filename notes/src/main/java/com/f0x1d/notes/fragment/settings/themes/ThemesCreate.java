package com.f0x1d.notes.fragment.settings.themes;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.R;
import com.f0x1d.notes.view.theming.MyColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;
import java.util.List;

public class ThemesCreate extends Fragment {

    List<Integer> colors = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    class itemAdapter extends RecyclerView.Adapter<ItemViewHolder>{

        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_theme_color, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            colors.add(-1);

            holder.chooseColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyColorPickerDialog colorPickerDialog = MyColorPickerDialog.newBuilderNew().setColor(0xfff).create();

                    colorPickerDialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                        @Override
                        public void onColorSelected(int dialogId, int color) {
                            Log.e("notes_err", "onColorSelected: " + "#" + Integer.toHexString(color));
                            colors.set(position, Integer.parseInt(Integer.toHexString(color)));
                        }
                        @Override
                        public void onDialogDismissed(int dialogId) {}
                    });

                    colorPickerDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "");
                }
            });
        }

        @Override
        public int getItemCount() {
            return colors.size();
        }
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        Button chooseColor;
        EditText textColor;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            chooseColor = itemView.findViewById(R.id.choose);
            textColor = itemView.findViewById(R.id.edit_text);
        }
    }
}
