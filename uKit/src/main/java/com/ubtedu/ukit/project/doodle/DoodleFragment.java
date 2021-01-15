/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.doodle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.project.vo.Doodle;

/**
 * 画板
 * @Author qinicy
 * @Date 2018/11/5
 **/
public class DoodleFragment extends UKitBaseFragment {
    private Doodle mDoodle;
    private boolean isModify;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_doodle,null);

        return root;
    }

    public Doodle getDoodle() {
        return mDoodle;
    }

    public void setDoodle(Doodle doodle) {
        mDoodle = doodle;
    }
    public boolean isDoodleModified(){
        return false;
    }


    public void resetModifyState(){
        isModify = false;
    }
}
