package com.ubtedu.ukit.project.controller.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.ukit.project.controller.factory.ControllerWidgetViewFactory;
import com.ubtedu.ukit.project.controller.interfaces.IControllerItemClickListener;
import com.ubtedu.ukit.project.controller.interfaces.IControllerItemLongClickListener;
import com.ubtedu.ukit.project.controller.interfaces.IControllerWidgetListener;
import com.ubtedu.ukit.project.controller.model.define.ToolbarType;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;
import com.ubtedu.ukit.project.controller.model.item.WidgetItem;
import com.ubtedu.ukit.project.controller.widget.ControllerWidgetView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static com.ubtedu.ukit.project.controller.utils.ControllerConstValue.BG_RADIUS_PIXEL;
import static com.ubtedu.ukit.project.controller.utils.ControllerConstValue.CELL_HEIGHT;
import static com.ubtedu.ukit.project.controller.utils.ControllerConstValue.CELL_WIDTH;
import static com.ubtedu.ukit.project.controller.utils.ControllerConstValue.TITLE_HEIGHT_PIXEL;

/**
 * @Author naOKi
 * @Date 2018/11/17
 **/
public class ControllerWidgetItemAdapter extends RecyclerView.Adapter<ControllerWidgetItemAdapter.WidgetItemViewHolder> {

    private ArrayList<WidgetItem> items;
    private ArrayList<WidgetItem> filterItems;
    private ToolbarType selectGroup = null;

    private boolean hasJoystick = false;
    
    private IControllerItemClickListener itemClickListener;
    private IControllerItemLongClickListener itemLongClickListener;

    private IControllerWidgetListener buttonTouchListener;

    public ControllerWidgetItemAdapter() {
        this(null, null);
    }

    public ControllerWidgetItemAdapter(IControllerItemClickListener itemClickListener, IControllerItemLongClickListener itemLongClickListener) {
        this.items = new ArrayList<>();
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
    }
    
    public void setItemLongClickListener(IControllerItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }
    
    public IControllerItemLongClickListener getItemLongClickListener() {
        return itemLongClickListener;
    }
    
    public void setItemClickListener(IControllerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public IControllerItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public IControllerWidgetListener getButtonTouchListener() {
        return buttonTouchListener;
    }

    public void setButtonTouchListener(IControllerWidgetListener buttonTouchListener) {
        this.buttonTouchListener = buttonTouchListener;
    }

    public void add(@Nullable WidgetItem object) {
        items.add(object);
        applyFilter();
    }

    public void addAll(@NonNull Collection<? extends WidgetItem> collection) {
        items.addAll(collection);
        applyFilter();
    }

    public void addAll(WidgetItem... items) {
        this.items.addAll(Arrays.asList(items));
        applyFilter();
    }

    public void clear() {
        items.clear();
        applyFilter();
    }

    public void remove(@Nullable WidgetItem object) {
        items.remove(object);
        applyFilter();
    }

    public void setSelectGroup(ToolbarType selectGroup) {
        this.selectGroup = selectGroup;
        applyFilter();
    }

    public void setHasJoystick(boolean hasJoystick) {
        this.hasJoystick = hasJoystick;
        applyFilter();
    }

    private void applyFilter() {
        filterItems = null;
        if (selectGroup != null && !items.isEmpty()) {
            filterItems = filterButtonItem();
        }
        notifyDataSetChanged();
    }

    private ArrayList<WidgetItem> filterButtonItem() {
        ArrayList<WidgetItem> acceptItems = new ArrayList<>();
        ArrayList<WidgetItem> originalItems = new ArrayList<>(items);
        for (WidgetItem item : originalItems) {
            if (!accept(item)) {
                continue;
            }
            acceptItems.add(item);
        }
        return acceptItems;
    }

    private boolean accept(WidgetItem item) {
        if(hasJoystick && WidgetType.WIDGET_UKROUNDJOYSTICK.equals(item.getWidgetType())) {
            return false;
        }
        return selectGroup != null && selectGroup.equals(item.getGroup());
    }

    @NonNull
    @Override
    public WidgetItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        WidgetType type = WidgetType.values()[i];
        ControllerWidgetView itemView = ControllerWidgetViewFactory.createViewByWidgetType(viewGroup.getContext(), type);
        itemView.setShowBackground(false);
        WidgetItemViewHolder vh = new WidgetItemViewHolder(itemView);
        return vh;
    }

    private ViewGroup.MarginLayoutParams getLayoutParams(int i, int spanH, int spanV) {
        ViewGroup.MarginLayoutParams result = new ViewGroup.MarginLayoutParams(CELL_WIDTH * spanH, CELL_HEIGHT * spanV);
        int marginTop = 0;
        int marginBottom = CELL_HEIGHT / 2;
        int marginLeft = CELL_WIDTH / 2;
        int marginRight = CELL_WIDTH / 2;
        if(i == 0) {
            marginTop = TITLE_HEIGHT_PIXEL + BG_RADIUS_PIXEL;
        }
        if(i == getItemCount() - 1) {
            marginBottom = BG_RADIUS_PIXEL;
        }
        result.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull WidgetItemViewHolder viewHolder, int i) {
        WidgetItem item = filterItems.get(i);
        ViewGroup.MarginLayoutParams layoutParams = getLayoutParams(i, item.getSpanH(), item.getSpanV());
        viewHolder.itemView.setLayoutParams(layoutParams);
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
        if(itemLongClickListener != null) {
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    boolean result = false;
                    if(itemLongClickListener != null) {
                        result = itemLongClickListener.onToolbarItemLongClick(v, (int)v.getTag());
                    }
                    return result;
                }
            });
        }
        if(buttonTouchListener != null) {
            viewHolder.image.setWidgetTouchListener(buttonTouchListener);
        }
    }

    @Override
    public int getItemViewType(int i) {
        WidgetItem item = filterItems.get(i);
        return item.getWidgetType().ordinal();
    }

    public WidgetItem getItem(int i) {
        if(i < 0 || i >= filterItems.size()) {
            return null;
        }
        return filterItems.get(i);
    }

    @Override
    public int getItemCount() {
        return filterItems != null ? filterItems.size() : 0;
    }

    public static class WidgetItemViewHolder extends RecyclerView.ViewHolder {
        private WidgetItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.image = (ControllerWidgetView)itemView;
        }
        private ControllerWidgetView image;
    }
    
}
