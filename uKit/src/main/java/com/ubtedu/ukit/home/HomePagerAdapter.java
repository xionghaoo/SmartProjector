/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.home;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ubtedu.ukit.common.base.UKitBaseFragment;

import java.util.List;

/**
 * @Author qinicy
 * @Date 2018/11/9
 **/
public class HomePagerAdapter extends FragmentPagerAdapter {

    private List<UKitBaseFragment> mFragments;

    public HomePagerAdapter(FragmentManager fm, List<UKitBaseFragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int arg0) {
        return mFragments.get(arg0);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position);
    }
}