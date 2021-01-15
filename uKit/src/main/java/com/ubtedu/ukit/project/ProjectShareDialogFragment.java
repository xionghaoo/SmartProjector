/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.ubtedu.alpha1x.ui.recyclerview.CommonAdapter;
import com.ubtedu.alpha1x.ui.recyclerview.base.ItemViewDelegate;
import com.ubtedu.alpha1x.ui.recyclerview.base.ViewHolder;
import com.ubtedu.alpha1x.ui.recyclerview.item.SpaceItemDecoration;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.project.vo.AppInfo;

import java.util.List;

/**
 * @Author hai.li
 * @Date 2020/05/12
 **/
public class ProjectShareDialogFragment extends UKitBaseDialogFragment {

    private TextView mTittleTv;
    private View mCloseBtn;
    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter;
    private Uri mUri;

    private List<AppInfo> mAppInfoList;

    public static ProjectShareDialogFragment newInstance(List<AppInfo> appInfoList, Uri uri) {
        ProjectShareDialogFragment fragment = new ProjectShareDialogFragment();
        fragment.setCancelable(true);
        fragment.mAppInfoList = appInfoList;
        fragment.mUri = uri;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_fragment_project_share, null);
        if (isCancelable()) {
            mRootView = root.findViewById(R.id.dialog_fragment_root_lyt);
        }

        mTittleTv = root.findViewById(R.id.project_share_title_tv);
        mTittleTv.setText(getString(R.string.project_share_app_select_title));
        mCloseBtn = root.findViewById(R.id.project_share_cancel_btn);
        bindClickListener(mCloseBtn);

        mRecyclerView = root.findViewById(R.id.project_share_rcv);

        mAdapter = new ListAdapter(getContext(), mAppInfoList);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 5);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getContext().getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_24px)));

        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        return root;
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mCloseBtn) {
            dismiss();
        }
    }

    View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            if (position >= 0 && position < mAppInfoList.size()) {
                LogUtil.d("mAppInfoList = " + mAppInfoList.get(position).getAppName());
                AppInfo appInfo = mAppInfoList.get(position);

                Intent shareIntent = new Intent();
                shareIntent.setComponent(new ComponentName(appInfo.getAppPackageName(), appInfo.getAppLauncherClassName()));
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addCategory(Intent.CATEGORY_DEFAULT);
                shareIntent.putExtra(Intent.EXTRA_STREAM, mUri);
                shareIntent.setType("application/ukt");
                shareIntent.setPackage(appInfo.getAppPackageName());

                Intent chooserIntent = Intent.createChooser(shareIntent, "");
                startActivity(chooserIntent);
                dismiss();
            }
        }
    };

    class ListAdapter extends CommonAdapter<AppInfo> {

        public ListAdapter(Context context, List<AppInfo> datas) {
            super(context, R.layout.item_project_share, datas);

            addItemViewDelegate(new ItemViewDelegate<AppInfo>() {
                @Override
                public int getItemViewLayoutId() {
                    return 0;
                }

                @Override
                public boolean isForViewType(AppInfo item, int position) {
                    return false;
                }

                @Override
                public void convert(ViewHolder holder, AppInfo appInfo, int position) {

                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        protected void convert(ViewHolder holder, AppInfo appInfo, int position) {
            holder.setTag(R.id.project_share_item_content_lyt, position);
            holder.setOnClickListener(R.id.project_share_item_content_lyt, mItemClickListener);

            ImageView iv = holder.getView(R.id.project_share_item_icon);
            iv.setImageDrawable(appInfo.getAppIcon());

            TextView tv = holder.getView(R.id.project_share_item_name);
            tv.setText(appInfo.getAppName());
        }
    }

}
