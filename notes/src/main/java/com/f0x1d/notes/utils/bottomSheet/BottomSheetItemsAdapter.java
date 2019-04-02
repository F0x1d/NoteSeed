package com.f0x1d.notes.utils.bottomSheet;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetItemsAdapter extends RecyclerView.Adapter<BottomSheetItemsAdapter.ItemViewHolder> {

    private List<Element> elements = new ArrayList<>();

    public BottomSheetItemsAdapter(List<Element> elements){
        this.elements = elements;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_bottomsheet, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.text.setText(elements.get(position).name);
        holder.text.setOnClickListener(elements.get(position).listener);

        if (UselessUtils.getBool("custom", false))
            holder.text.setCompoundDrawablesWithIntrinsicBounds(UselessUtils.setTint(elements.get(position).pic, ThemesEngine.iconsColor), null, null, null);
        else if (UselessUtils.getBool("night", true))
            holder.text.setCompoundDrawablesWithIntrinsicBounds(UselessUtils.setTint(elements.get(position).pic, Color.WHITE), null, null, null);
        else
            holder.text.setCompoundDrawablesWithIntrinsicBounds(UselessUtils.setTint(elements.get(position).pic, Color.BLACK), null, null, null);
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView text;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.item);
        }
    }
}
