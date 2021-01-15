package com.ubtedu.ukit.project;

import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseFragment;

/**
 * 空的Fragment，只是用来占坑以及数据统计
 * @Author qinicy
 * @Date 2019/6/4
 **/
public class MotionDesignFragment extends UKitBaseFragment {
    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        reportPageEvent(isVisibleToUser);
    }
    private void reportPageEvent(boolean isVisibleToUser) {
        String name = "motion_design";
        if (isVisibleToUser) {
            UBTReporter.onPageStart(name);
        } else {
            UBTReporter.onPageEnd(name);
        }
    }
}
