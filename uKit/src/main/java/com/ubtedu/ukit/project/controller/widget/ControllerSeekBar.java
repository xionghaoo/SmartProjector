package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.ubtedu.ukit.project.controller.ControllerModeHolder;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;

/**
 * @Author naOKi
 * @Date 2019/03/06
 **/
public class ControllerSeekBar extends AppCompatSeekBar {

    private boolean downPressed = false;

    public ControllerSeekBar(Context context) {
        super(context);
    }

    public ControllerSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControllerSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void resetDownPressed() {
        this.downPressed = false;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(ControllerManager.getBackgroundFlag()) {
            resetDownPressed();
            return false;
        }
        if(ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_EXECUTION &&ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_PREVIEW) {
            return false;
        } else {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                downPressed = true;
            } else if(event.getAction() == MotionEvent.ACTION_MOVE) {
                if(!downPressed) {
                    return false;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP||event.getAction() == MotionEvent.ACTION_CANCEL){
                downPressed=false;
            }
        }
        return super.onTouchEvent(event);
    }

}
