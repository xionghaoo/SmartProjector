package com.ubtedu.ukit.project.controller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.project.controller.interfaces.IControllerItemClickListener;
import com.ubtedu.ukit.project.controller.model.item.ToolbarItem;
import com.ubtedu.ukit.project.controller.model.item.WidgetItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;

/**
 * @Author naOKi
 * @Date 2018/11/17
 **/
public class ControllerToolbarItemAdapter extends RecyclerView.Adapter<ControllerToolbarItemAdapter.ToolbarItemViewHolder> {
    private ArrayList<ToolbarItem> items;
    private BitSet selectFlag = new BitSet();

    private IControllerItemClickListener itemClickListener;


    public ControllerToolbarItemAdapter() {
        this(null);
    }

    public ControllerToolbarItemAdapter(IControllerItemClickListener itemClickListener) {
        this.items = new ArrayList<>();
        this.itemClickListener = itemClickListener;
    }

    public void setItemClickListener(IControllerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public IControllerItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void add(@Nullable ToolbarItem object) {
        items.add(object);
        apply();
    }

    public void addAll(@NonNull Collection<? extends ToolbarItem> collection) {
        items.addAll(collection);
        apply();
    }

    public void addAll(ToolbarItem... items) {
        this.items.addAll(Arrays.asList(items));
        apply();
    }

    public void clear() {
        items.clear();
        apply();
    }

    public void remove(@Nullable ToolbarItem item) {
        items.remove(item);
        apply();
    }

    public void setSelectedItem(int index, boolean isSelected) {
        if(selectFlag.get(index) == isSelected) {
            return;
        }
        if(isSelected) {
            selectFlag.clear();
        }
        selectFlag.set(index, isSelected);
        notifyDataSetChanged();
    }

    public void clearSelectedItem() {
        selectFlag.clear();
        notifyDataSetChanged();
    }

    public boolean isItemSelected(int index) {
        return selectFlag.get(index);
    }

    private void apply() {
        selectFlag.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ControllerToolbarItemAdapter.ToolbarItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_project_controller_toolbar, viewGroup, false);
        ControllerToolbarItemAdapter.ToolbarItemViewHolder vh = new ControllerToolbarItemAdapter.ToolbarItemViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ControllerToolbarItemAdapter.ToolbarItemViewHolder viewHolder, int i) {
        ToolbarItem item = items.get(i);
        viewHolder.icon.setImageResource(item.getType().getResId());
        viewHolder.label.setText(item.getType().getStringId());
        if(selectFlag.get(i)) {
            viewHolder.itemView.setBackgroundColor(item.getType().getSelectColor());//选中颜色
        } else {
            viewHolder.itemView.setBackgroundColor(i % 2 == 0 ? 0xFF252B38 : 0xFF303643);//错位颜色
        }
        viewHolder.itemView.setTag(i);
        if(itemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener != null) {
                        itemClickListener.onItemClick(v, (int)v.getTag());
                    }
                }
            });
        }
    }

    public ToolbarItem getItem(int i) {
        if(i < 0 || i >= items.size()) {
            return null;
        }
        return items.get(i);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ToolbarItemViewHolder extends RecyclerView.ViewHolder {
        private ToolbarItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.item_project_controller_toolbar_iv);
            this.label = itemView.findViewById(R.id.item_project_controller_toolbar_tv);
        }
        private ImageView icon;
        private TextView label;
    }
}
