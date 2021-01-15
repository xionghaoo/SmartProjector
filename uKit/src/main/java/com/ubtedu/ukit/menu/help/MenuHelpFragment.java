/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseFragment;

/**
 * @Author qinicy
 * @Date 2018/12/12
 **/
public class MenuHelpFragment extends UKitBaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_help,null);
        return root;

    }
}
