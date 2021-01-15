package com.ubtedu.alpha1x.ui.layoutmanager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by qinicy on 2017/06/7
 */
public class FlowLayoutManager extends RecyclerView.LayoutManager {
    private static final String TAG = "FlowLayoutManager";

    /**
     * 水平居中偏移量
     */
    private int mHOffset;


    private int mVisibleLineCount;
    private int mLineItemCount;
    private int mItemHeight;
    private boolean isInterceptOnLayout;
    private int mChildrenCount;
    private int mPreviousLine;

    /**
     * Item的宽度是否都一样
     */
    private boolean isEquipartition = true;

    int mVerticalOffset = 0;
    private boolean isAutoScrollToBottom;

    public void setCanScrollVertically(boolean canScrollVertically) {
        mCanScrollVertically = canScrollVertically;
    }

    private boolean mCanScrollVertically = true;

    public void interceptOnLayout(boolean interceptOnLayout) {
        isInterceptOnLayout = interceptOnLayout;
    }

    public void setAutoScrollToBottom(boolean auto) {
        isAutoScrollToBottom = auto;
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
        if (mVisibleLineCount != 0) {

            int lineIndex = (position + 1) - mVisibleLineCount;


        }

    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);

        if (mLineItemCount == 0)
            return;
        int currentLine = mChildrenCount / mLineItemCount;
        //余数，判断是否一行的最后一个
        int m = mChildrenCount % mLineItemCount;

        boolean shouldScrollUp = currentLine >= mVisibleLineCount
                && currentLine > mPreviousLine
                && m == 0
                //判断是否满屏
                && getHeight() - (currentLine * mItemHeight - mVerticalOffset) < mItemHeight;
        if (isAutoScrollToBottom && shouldScrollUp) {
            mPreviousLine = currentLine;
            if (currentLine * mItemHeight - getHeight() - mVerticalOffset > 0) {
                //item在下方不可见，滚动上来
                mVerticalOffset = mItemHeight * (currentLine - mVisibleLineCount +1);
            } else {

                mVerticalOffset += mItemHeight;
            }
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        if (!isInterceptOnLayout) {
            if (getItemCount() <= 0 || state.isPreLayout()) {
                return;
            }
            detachAndScrapAttachedViews(recycler);
            int count = getItemCount();
            mChildrenCount = count;

            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            int paddingTop = getPaddingTop();
            int paddingBottom = getPaddingBottom();
            int width = getWidth();

            int forLeft = paddingLeft;
            int forRight = paddingLeft;
            int forTop = paddingTop;
            int forBottom = paddingBottom;



            for (int i = 0; i < count; i++) {
                View scrap = recycler.getViewForPosition(i);
                measureChildWithMargins(scrap, 0, 0);
                addView(scrap);
                int viewWidth = getDecoratedMeasuredWidth(scrap);

                int viewHeight = getDecoratedMeasuredHeight(scrap);

                mItemHeight = viewHeight;

                if (i == 0 && isEquipartition && viewWidth != 0) {
                    mHOffset = (width - paddingLeft - paddingRight) % viewWidth / 2;
                    mLineItemCount = (width - paddingLeft - paddingRight) / viewWidth;
                    mVisibleLineCount = getHeight() / viewHeight;
                    forLeft = mHOffset;
                    forRight = mHOffset;
                }


                ViewGroup.LayoutParams lp = scrap.getLayoutParams();

                if (i != 0) {
                    forLeft = forRight;
                }

                if (width - paddingRight - forLeft < viewWidth || (lp instanceof LayoutParams && ((LayoutParams) lp).isFull())) {

                    forTop += forBottom;
                    forLeft = mHOffset;
                    forBottom = 0;
                    forRight = mHOffset;
                }
                forBottom = Math.max(forBottom, viewHeight);
                forRight += viewWidth;
                layoutDecorated(scrap, forLeft, forTop - mVerticalOffset, forRight, forTop + forBottom - mVerticalOffset);
            }
        }
    }


    @Override
    public RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
        return new LayoutParams(c, attrs);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    public boolean canScrollVertically() {
        return mCanScrollVertically;
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {

        if (mVerticalOffset + dy < 0) {
            dy = -mVerticalOffset;
        }
        else if (mVerticalOffset + dy > getHeight() + 100) {
            dy = getHeight() - mVerticalOffset;
        }
        offsetChildrenVertical(-dy);
        onLayoutChildren(recycler, state);
        mVerticalOffset += dy;
        return dy;
    }

    public static class LayoutParams extends RecyclerView.LayoutParams {

        private boolean isFull;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(RecyclerView.LayoutParams source) {
            super(source);
        }

        public void setFull(boolean isFull) {
            this.isFull = isFull;
        }

        public boolean isFull() {
            return this.isFull;
        }
    }

}
