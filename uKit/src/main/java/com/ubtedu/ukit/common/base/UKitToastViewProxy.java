/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.base;

import android.content.Context;
import android.widget.Toast;

import com.ubtedu.alpha1x.core.base.widget.IToastView;

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public class UKitToastViewProxy implements IToastView {
    UKitToastView mToastView;

    public UKitToastViewProxy(Context context) {
        mToastView = new UKitToastView(context,"",Toast.LENGTH_SHORT);
    }

    @Override
    public void setText(String message) {
        mToastView.setText(message);
    }

    @Override
    public void setGravity(int gravity, int xOffset, int yOffset) {
        mToastView.setGravity(gravity,xOffset,yOffset);
    }

    @Override
    public void setDuration(int duration) {
        mToastView.setDuration(duration);
    }

    @Override
    public void show() {
        mToastView.show();
    }

    @Override
    public void cancel() {
        mToastView.cancel();
    }
}
