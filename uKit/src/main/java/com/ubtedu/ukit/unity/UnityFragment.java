/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.unity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.project.vo.Motion;


/**
 * @Author qinicy
 * @Date 2018/11/5
 **/
public class UnityFragment extends UKitBaseFragment {
    private UKitUnityPlayer mUnityPlayer;

    private ViewGroup mContainer;

    private Motion mMotion;
    public UnityFragment() {

    }

    public static UnityFragment newInstance() {
        UnityFragment fragment = new UnityFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() instanceof UnityPlayerActivity) {
            UnityPlayerActivity activity = (UnityPlayerActivity) getActivity();
            mUnityPlayer = activity.mUnityPlayer;
        }

        LogUtil.i("Arguments:" + getArguments() + "  this:" + this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        Button button = new Button(getContext());
//        button.setText("Unity3D");
//        button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.test_tutorial));
//        View root = inflater.inflate(R.layout.fragment_ai,null);
//        View bg = root.findViewById(R.id.ai_background);
//        bg.setBackgroundResource(R.drawable.img_nonetwork);

        if (mContainer == null) {
            mContainer = new RelativeLayout(getContext());
        }
        mContainer.setBackgroundColor(Color.TRANSPARENT);
//        mUnityPlayer.setBackgroundColor(Color.WHITE);
//        mUnityPlayer.setTag("unity");
        return mContainer;
    }

    public Motion getMotion() {
        return mMotion;
    }

    public void setMotion(Motion motion) {
        mMotion = motion;
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        LogUtil.i("isVisibleToUser:" + isVisibleToUser);
//        if (isVisibleToUser) {
//            if (mUnityPlayer != null) {
//                attachUnityView();
//                mUnityPlayer.resume();
//            }
//        } else {
//            if (mUnityPlayer != null) {
//                detachUnityView();
//                mUnityPlayer.pause();
//            }
//        }
    }




    private void detachUnityView() {
        if (mContainer != null) {
            mContainer.removeView(mUnityPlayer);

        }
    }

    private void attachUnityView() {
        if (mContainer != null && mContainer.findViewWithTag("unity") == null) {
            if (mUnityPlayer.getParent() != null){
                ((ViewGroup) mUnityPlayer.getParent()).removeAllViews();
            }
            mContainer.addView(mUnityPlayer);
            mUnityPlayer.requestFocus();
        }
    }
}
