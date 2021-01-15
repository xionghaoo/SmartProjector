package com.ubtedu.alpha1x.ui.recyclerview.item;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 左、右的space是bottom的一半
 * @author Bright. Create on 2017/10/27.
 */
public class HalfSpaceItemDecoration extends RecyclerView.ItemDecoration {
    int mSpace;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace * 2;
    }

    public HalfSpaceItemDecoration(int space) {
        this.mSpace = space;
    }
}
