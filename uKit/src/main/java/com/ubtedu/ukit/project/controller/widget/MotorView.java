package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;

/**
 * @Author naOKi
 * @Date 2018/12/04
 **/
public class MotorView extends FrameLayout {

    private ImageView mIconIv;
    private TextView mIdTv;

    private int mId = -1;

    public MotorView(@NonNull Context context) {
        super(context);
    }

    public MotorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if(isInEditMode()) {
            return;
        }
        View view = inflateView(R.layout.item_motor);
        mIconIv = view.findViewById(R.id.item_motor_icon_iv);
        mIdTv = view.findViewById(R.id.item_motor_id_tv);
        addView(view);
        setIdValue(mId);
    }

    private View inflateView(@LayoutRes int layoutRes) {
        return LayoutInflater.from(getContext()).inflate(layoutRes, null);
    }

    public void setIdValue(int id) {
        if(id < 1 || id > 8) {
            mId = -1;
            mIdTv.setText(null);
            mIconIv.setImageResource(R.drawable.project_remote_set_run_servo_add_btn);
        } else {
            mId = id;
            mIdTv.setText(String.valueOf(id));
            mIconIv.setImageResource(R.drawable.motor9);
        }
    }

    public int getIdValue() {
        return mId;
    }

}
