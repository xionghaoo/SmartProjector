package com.ubtedu.alpha1x.ui.recyclerview;

import android.content.Context;

import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

/**
 * @author qinicy
 * @data 2017/12/28
 */

public abstract class AbstractSelectAdapter<T> extends CommonAdapter<T> {

    private Selector mSelector;

    private OnItemDoubleTapListener mDoubleTapListener;
    private long mLastClickTime;
    private int mLastPosition;
    private boolean isEnableDoubleTap;

    public AbstractSelectAdapter(Context context, int layoutId, List<T> datas) {
        super(context, layoutId, datas);
        mSelector = new Selector();
    }


    public void setDoubleTapListener(OnItemDoubleTapListener doubleTapListener) {
        mDoubleTapListener = doubleTapListener;
    }

    public void enableDoubleTap(boolean enable) {
        this.isEnableDoubleTap = enable;
    }

    public boolean isEnableDoubleTap() {
        return isEnableDoubleTap;
    }

    public void toggleSelection(int position) {

        boolean add;
        boolean doubleTap = false;
        if (isEnableDoubleTap) {
            doubleTap = isDoubleTap(position);
        }
        if (doubleTap) {
            add = true;
        } else {
            if (mSelector.contains(position)) {

                add = mSelector.size() != 1;
            } else {
                add = true;
            }
        }
        mSelector.clear();


        if (add) {
            mSelector.add(position);
        }
        notifyDataSetChanged();
        if (doubleTap && mDoubleTapListener != null) {
            mDoubleTapListener.onDoubleTap(position);
        }
    }

    private boolean isDoubleTap(int position) {
        if (position != mLastPosition) {
            mLastClickTime = System.currentTimeMillis();
            mLastPosition = position;
        } else {
            long now = System.currentTimeMillis();
            long time = now - mLastClickTime;
            mLastClickTime = now;
            return time < 300;
        }
        return false;
    }

    public boolean isPositionSelected(int position){
        return mSelector.contains(position);
    }
    public void select(int pos, boolean selected) {
        if (selected) {
            mSelector.add(pos);
        } else {
            mSelector.remove(pos);
        }
        notifyItemChanged(pos);
    }

    public void selectRange(int start, int end, boolean selected) {
        for (int i = start; i <= end; i++) {
            if (selected) {
                mSelector.add(i);
            } else {
                mSelector.remove(i);
            }
        }
        notifyItemRangeChanged(start, end - start + 1);
    }

    public void deselectAll() {
        mSelector.clear();
    }


    public void selectAll() {
        for (int i = 0; i < mDatas.size(); i++) {
            mSelector.add(i);
        }
        notifyDataSetChanged();
    }

    public int getCountSelected() {
        return mSelector.size();
    }

    public TreeSet<Integer> getSelection() {
        return mSelector.getSelection();
    }


    public interface OnItemDoubleTapListener {
        void onDoubleTap(int position);
    }

    protected static class Selector extends HashSet<Integer> {


        private boolean isChanged = true;
        private TreeSet<Integer> mSortSelects;


        @Override
        public boolean add(Integer e) {
            isChanged = true;
            return super.add(e);
        }

        @Override
        public boolean remove(Object o) {
            isChanged = true;
            return super.remove(o);
        }

        @Override
        public void clear() {
            isChanged = true;
            super.clear();
        }

        public TreeSet<Integer> getSelection() {
            if (isChanged) {
                mSortSelects = new TreeSet<>(this);
            }
            return mSortSelects;
        }
    }
}
