/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ubtedu.alpha1x.core.base.widget.IToastView;
import com.ubtedu.alpha1x.ui.widgets.CustomToastView;
import com.ubtedu.ukit.R;

/**
 * 由于ChromeOS不支持Toast,所以不再使用android系统Toast实现，改用{@link ChromeToastView}
 * @Author qinicy
 * @Date 2018/11/6
 **/
@Deprecated
public class UKitToastView implements IToastView {
    private Toast mToast;
    private TextView mToastTextView;
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public UKitToastView(Context context, CharSequence msg, int duration) {
        if (context != null) {
            mContext = context;
            View toastView = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
            mToastTextView = toastView.findViewById(R.id.message_tv);
            mToastTextView.setText(msg);
            mToast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
            mToast.setDuration(duration);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setView(toastView);
        }
    }

    public static CustomToastView makeText(Context context, CharSequence msg, int duration) {
        return new CustomToastView(context, msg, duration);
    }

    public void show() {
        if (mToast != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = new Toast(mContext);
                    toast.setView(mToast.getView());
                    toast.setDuration(mToast.getDuration());
                    toast.setGravity(mToast.getGravity(),mToast.getXOffset(),mToast.getYOffset());
                    mToast.cancel();
                    mToast = toast;
                    mToast.show();
                }
            });

        }
    }

    public void cancel() {
        if (mToast != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mToast.cancel();
                }
            });

        }
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        if (mToast != null) {
            mToast.setGravity(gravity, xOffset, yOffset);
        }
    }

    public void setDuration(int duration) {
        if (mToast != null) {
            mToast.setDuration(duration);
        }
    }

    public void setText(String message) {
        if (mToastTextView != null) {
            mToastTextView.setText(message);
        }
    }
}
