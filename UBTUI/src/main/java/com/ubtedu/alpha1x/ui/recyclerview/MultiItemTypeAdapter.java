package com.ubtedu.alpha1x.ui.recyclerview;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.alpha1x.ui.recyclerview.base.ItemViewDelegate;
import com.ubtedu.alpha1x.ui.recyclerview.base.ItemViewDelegateManager;
import com.ubtedu.alpha1x.ui.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * Created by zhy on 16/4/9.
 */
public class MultiItemTypeAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;
    protected List<T> mDatas;

    protected ItemViewDelegateManager mItemViewDelegateManager;
    protected OnItemClickListener mOnItemClickListener;
    protected SparseBooleanArray mCheckStates;

    protected int mChoiceMode;

    public MultiItemTypeAdapter(Context context, List<T> datas) {
        mContext = context;
        mDatas = datas;
        mItemViewDelegateManager = new ItemViewDelegateManager();
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * {@link android.widget.ListView#CHOICE_MODE_SINGLE},{@link android.widget.ListView#CHOICE_MODE_MULTIPLE}
     *
     * @param mode
     */
    public void setChoiceMode(int mode) {
        this.mChoiceMode = mode;
        initCheckStates();
    }

    private void initCheckStates() {
        mCheckStates = new SparseBooleanArray();

        for (int i = mDatas.size() - 1; i >= 0; i--) {
            T t = mDatas.get(i);
            if (mChoiceMode == ListView.CHOICE_MODE_SINGLE) {
                mCheckStates.put(i, isItemChecked(t, i));
                if (isItemChecked(t, i)) {
                    break;
                }
            } else if (mChoiceMode == ListView.CHOICE_MODE_MULTIPLE) {
                mCheckStates.put(i, isItemChecked(t, i));
            }
        }

    }

    public void clearChoices() {
        if (mCheckStates != null) {
            mCheckStates.clear();
        }
    }

    public SparseBooleanArray getCheckItemPositions() {
        if (mChoiceMode != ListView.CHOICE_MODE_NONE) {
            return mCheckStates;
        }
        return null;
    }

    public boolean isItemChecked(T t, int position) {
        position = position % mDatas.size(); // 处理当需要无限循环时，会出现越界问题
        if (mCheckStates != null) {
            return mCheckStates.get(position);
        }
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        position = position % mDatas.size(); // 处理当需要无限循环时，会出现越界问题
        if (!useItemViewDelegateManager()) return super.getItemViewType(position);
        return mItemViewDelegateManager.getItemViewType(mDatas.get(position), position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType);
        int layoutId = itemViewDelegate.getItemViewLayoutId();
        ViewHolder holder = ViewHolder.createViewHolder(mContext, parent, layoutId);
        onViewHolderCreated(holder, holder.getConvertView());
        setListener(parent, holder, viewType);
        return holder;
    }

    public void onViewHolderCreated(ViewHolder holder, View itemView) {

    }

    public void convert(ViewHolder holder, T t) {
        mItemViewDelegateManager.convert(holder, t, holder.getAdapterPosition());
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }


    protected void setListener(final ViewGroup parent, final ViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int realPosition = viewHolder.getAdapterPosition();
                boolean oldValue;
                ViewHolder holder = viewHolder;
                if (mChoiceMode == ListView.CHOICE_MODE_SINGLE) {
                    oldValue = mCheckStates.get(realPosition);
                    if (!oldValue) { //not checked -> checked
                        clearChoices();
                        mCheckStates.put(realPosition, true);
                        notifyDataSetChanged();
                    } else { // checked -> not checked
                        mCheckStates.put(realPosition, false);
                        holder.setItemViewChecked(false);
                    }

                } else if (mChoiceMode == ListView.CHOICE_MODE_MULTIPLE) {
                    oldValue = mCheckStates.get(realPosition);
                    mCheckStates.put(realPosition, !oldValue);
//                        notifyDataSetChanged();
                    holder.setItemViewChecked(!oldValue);
                }
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder, position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        position = position % mDatas.size(); // 处理当需要无限循环时，会出现越界问题
        boolean isCheck = isItemChecked(mDatas.get(position), position);
        holder.setItemViewChecked(isCheck);
        convert(holder, mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }


    public void setDatas(List<T> datas) {
        mDatas = datas;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public MultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    public MultiItemTypeAdapter addItemViewDelegate(int viewType, ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate);
        return this;
    }

    protected boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
