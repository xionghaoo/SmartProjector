package com.ubtedu.alpha1x.ui.recyclerview;

/**
 * Created by qinicy on 2017/7/24.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.alpha1x.ui.recyclerview.item.SpaceItemDecoration;

/**
 * Created by ivan
 */

public class AutoFitGridRecyclerView extends RecyclerView {
    private AutoFitGridLayoutManager manager;
    private int columnWidth = -1;

    public AutoFitGridRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public AutoFitGridRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoFitGridRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArray = {
                    android.R.attr.columnWidth
            };
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            columnWidth = array.getDimensionPixelSize(0, -1);
            array.recycle();
        }

        manager = new AutoFitGridLayoutManager(getContext());
        setLayoutManager(manager);
        addItemDecoration(new SpaceItemDecoration(0));
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (columnWidth > 0) {
            int spanCount = Math.max(1, getMeasuredWidth() / columnWidth);
            manager.setSpanCount(spanCount);
        }
    }
    public void setScrollEnabled(boolean flag) {
        manager.setScrollEnabled(flag);
    }
    private static class AutoFitGridLayoutManager extends GridLayoutManager {
        private boolean isScrollEnabled = true;

        public AutoFitGridLayoutManager(Context context) {
            super(context, 1);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            return isScrollEnabled && super.canScrollVertically();
        }
    }
}