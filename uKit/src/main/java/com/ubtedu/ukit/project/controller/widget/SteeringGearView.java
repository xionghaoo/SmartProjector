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
public class SteeringGearView extends FrameLayout {

    private ImageView mIconIv;
    private TextView mIdTv;
    
    public static final int MODE_ALL = 0;
    public static final int MODE_WHEEL = 1;
    public static final int MODE_ANGULAR = 2;

    private int mMode = MODE_ANGULAR;
    private int mId = -1;

    public SteeringGearView(@NonNull Context context) {
        super(context);
    }

    public SteeringGearView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if(isInEditMode()) {
            return;
        }
        View view = inflateView(R.layout.item_steering_gear);
        mIconIv = view.findViewById(R.id.item_steering_gear_icon_iv);
        mIdTv = view.findViewById(R.id.item_steering_gear_id_tv);
        addView(view);
        setMode(mMode);
        setIdValue(mId);
    }

    private View inflateView(@LayoutRes int layoutRes) {
        return LayoutInflater.from(getContext()).inflate(layoutRes, null);
    }

    public void setMode(int mode) {
        if(MODE_WHEEL != mode && MODE_ANGULAR != mode) {
            return;
        }
        mMode = mode;
        if(MODE_ANGULAR == mode) {
            mIconIv.setImageResource(R.drawable.servo);
        } else {
            mIconIv.setImageResource(R.drawable.servo2);
        }
    }

    public void setIdValue(int id) {
        if(id < 1 || id > 32) {
            mId = -1;
            mIdTv.setText(null);
            mIconIv.setImageResource(R.drawable.project_remote_set_run_servo_add_btn);
        } else {
            mId = id;
            mIdTv.setText(String.valueOf(id));
            if(MODE_ANGULAR == mMode) {
                mIconIv.setImageResource(R.drawable.servo);
            } else {
                mIconIv.setImageResource(R.drawable.servo2);
            }
        }
    }

    public int getMode() {
        return mMode;
    }

    public int getIdValue() {
        return mId;
    }

}
