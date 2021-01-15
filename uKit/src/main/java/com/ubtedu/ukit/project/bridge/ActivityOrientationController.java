/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge;

import android.app.Activity;
import android.content.pm.ActivityInfo;

/**
 * @Author qinicy
 * @Date 2019/1/10
 **/
public class ActivityOrientationController {
    private boolean isLock;
    private int mOriginOrientation;
    private Activity mActivity;

    public ActivityOrientationController(Activity activity, int defaultOrientation) {
        this.mActivity = activity;
        mOriginOrientation = defaultOrientation;
    }

    /**
     * lock current activity orientation
     */
    public void lock() {
        isLock = true;
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }

    public void reverseOriginOrientation() {
        isLock = false;
        requestOrientation(mOriginOrientation);
    }


    public boolean isLock() {
        return isLock;
    }

    private void requestOrientation(int orientation) {
        mActivity.setRequestedOrientation(orientation);
    }
}
