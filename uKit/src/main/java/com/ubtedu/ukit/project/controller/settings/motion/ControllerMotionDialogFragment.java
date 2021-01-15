/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.controller.settings.motion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.controller.interfaces.IControllerItemClickListener;
import com.ubtedu.ukit.project.controller.settings.adapter.MotionItemAdapter;
import com.ubtedu.ukit.project.controller.settings.interfaces.IControllerMotionSelectListener;

/**
 * @Author naOKi
 * @Date 2018/12/07
 **/
public class ControllerMotionDialogFragment extends UKitBaseDialogFragment {

    private final static String EXTRA_MOTION_ID = "_extra_motion_id";

    private String selectedMotionId = null;

    private Button mCloseBtn;
    private RecyclerView mMotionRv;
    
    private MotionItemAdapter mAdapter;
    
    private IControllerMotionSelectListener mControllerMotionSelectListener;

    private String mWidgetType;

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
            selectedMotionId = args.getString(EXTRA_MOTION_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_MOTION_ID, selectedMotionId);
    }

    @Override
    public void onPause() {
        if(mAdapter != null) {
            mAdapter.stopMotion();
        }
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_project_controller_motion, null);
        if (isCancelable()) {
            super.mRootView = view.findViewById(R.id.dialog_fragment_root_view);
        }
        mCloseBtn = view.findViewById(R.id.controller_project_motion_list_close_btn);
        bindSafeClickListener(mCloseBtn);
        mMotionRv = view.findViewById(R.id.controller_project_motion_list_rcv);

        mAdapter = new MotionItemAdapter();
        mAdapter.setUIDelegate(getUIDelegate());
        mMotionRv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mMotionRv.setAdapter(mAdapter);
        mMotionRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mMotionRv.setHasFixedSize(true);

        if(Workspace.getInstance() != null && Workspace.getInstance().getProject() != null) {
            mAdapter.setMotion(Workspace.getInstance().getProject().motionList);
        }

        mAdapter.setSelectedMotionId(selectedMotionId);

        mAdapter.setItemClickListener(new IControllerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mAdapter.stopMotion();
                if(mControllerMotionSelectListener != null) {
                    selectedMotionId = mAdapter.getSelectedMotionId();
                    mControllerMotionSelectListener.onMotionSelected(selectedMotionId);
                }
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        if(v == mCloseBtn) {
            mAdapter.stopMotion();
            dismiss();
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String selectedMotionId = null;
        private IControllerMotionSelectListener mControllerMotionSelectListener;
        private String mWidgetType;
        public Builder() {
        }
        public Builder setSelectedMotionId(String selectedMotionId) {
            this.selectedMotionId = selectedMotionId;
            return this;
        }
        public Builder setMotionSelectListener(IControllerMotionSelectListener motionSelectListener) {
            this.mControllerMotionSelectListener = motionSelectListener;
            return this;
        }

        public Builder setWidgetType(String widgetType) {
            this.mWidgetType = widgetType;
            return this;
        }

        public ControllerMotionDialogFragment build() {
            ControllerMotionDialogFragment fragment = new ControllerMotionDialogFragment();
            fragment.setCancelable(false);
            fragment.mControllerMotionSelectListener = mControllerMotionSelectListener;
            fragment.mWidgetType=mWidgetType;
            Bundle args = new Bundle();
            args.putString(EXTRA_MOTION_ID, selectedMotionId);
            fragment.setArguments(args);
            return fragment;
        }
    }

}
