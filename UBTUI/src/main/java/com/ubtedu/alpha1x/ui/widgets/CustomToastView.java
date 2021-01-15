package com.ubtedu.alpha1x.ui.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ubtedu.base.ui.R;

/**
 * Created by qinicy on 2017/7/7.
 */

public class CustomToastView {
    private Toast mToast;
    private TextView mToastTextView;
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public CustomToastView(Context context, CharSequence msg, int duration) {
        if (context != null) {
            mContext = context;
            View toastView = LayoutInflater.from(context).inflate(R.layout.view_custom_toast, null);
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
