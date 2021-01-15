/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.controller.settings.icon;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.project.controller.interfaces.IControllerItemClickListener;
import com.ubtedu.ukit.project.controller.settings.adapter.SettingIconItemAdapter;
import com.ubtedu.ukit.project.controller.settings.interfaces.IControllerIconSelectListener;

/**
 * @Author naOKi
 * @Date 2018/11/30
 **/
public class ControllerIconDialogFragment extends UKitBaseDialogFragment {
    
    private final static String EXTRA_SELECTED_INDEX = "_extra_selected_index";

    private int mSelectedIndex = -1;
    
    private Button mCancelBtn;
    private Button mConfirmBtn;
    private RecyclerView mIconListRv;
    
    private SettingIconItemAdapter mAdapter;
    
    private IControllerIconSelectListener mControllerIconSelectListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            initArguments(savedInstanceState);
        } else {
            initArguments(getArguments());
        }
    }

    private void initArguments(Bundle args) {
        if(args != null) {
            mSelectedIndex = args.getInt(EXTRA_SELECTED_INDEX);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_SELECTED_INDEX, mSelectedIndex);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_project_controller_icon, null);
        if (isCancelable()) {
            super.mRootView = view.findViewById(R.id.dialog_fragment_root_view);
        }
        mCancelBtn = view.findViewById(R.id.dialog_project_controller_icon_cancel_btn);
        bindSafeClickListener(mCancelBtn);
        mConfirmBtn = view.findViewById(R.id.dialog_project_controller_icon_confirm_btn);
        bindSafeClickListener(mConfirmBtn);
        mIconListRv = view.findViewById(R.id.dialog_project_controller_icon_list);
    
        mAdapter = new SettingIconItemAdapter();
        mIconListRv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mIconListRv.setAdapter(mAdapter);
        mIconListRv.setLayoutManager(new GridLayoutManager(getContext(), 5));
        mIconListRv.setHasFixedSize(true);
        mAdapter.setItemClickListener(new IControllerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mSelectedIndex = position;
                mAdapter.setSelectedItem(mSelectedIndex);
                updateButtonState();
            }
        });
    
        mAdapter.setSelectedItem(mSelectedIndex);
        updateButtonState();
        
        return view;
    }
    
    private void updateButtonState() {
//        if(mSelectedIndex == -1) {
//            mConfirmBtn.setAlpha(0.5f);
//            mConfirmBtn.setEnabled(false);
//        } else {
//            mConfirmBtn.setAlpha(1.0f);
//            mConfirmBtn.setEnabled(true);
//        }
    }


    @Override
    public void onClick(View v, boolean isSafeClick) {
        if(v == mConfirmBtn) {
            if(mControllerIconSelectListener != null) {
                mControllerIconSelectListener.onIconSelected(mSelectedIndex);
            }
            dismiss();
        }
        if(v == mCancelBtn) {
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        if (mDismissListener != null) {
            mDismissListener.onDismiss();
        }
        if (getActivity() != null && !getActivity().isFinishing()) {
            super.dismissAllowingStateLoss();
        }
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        private Context mContext;
        private int mSelectedIndex;
        private IControllerIconSelectListener mControllerIconSelectListener;
        public Builder(Context context) {
            mContext = context;
        }
        public Builder setSelectedIndex(int selectedIndex) {
            this.mSelectedIndex = selectedIndex;
            return this;
        }
        public Builder setIconSelectListener(IControllerIconSelectListener iconSelectListener){
            this.mControllerIconSelectListener = iconSelectListener;
            return this;
        }
        public ControllerIconDialogFragment build() {
            ControllerIconDialogFragment fragment = new ControllerIconDialogFragment();
            fragment.setCancelable(true);
            fragment.mControllerIconSelectListener = mControllerIconSelectListener;
            Bundle args = new Bundle();
            args.putInt(EXTRA_SELECTED_INDEX, mSelectedIndex);
            fragment.setArguments(args);
            return fragment;
        }
    }

}
