package com.ubtedu.ukit.project.controller;

import androidx.annotation.IntDef;

import com.ubtedu.ukit.project.controller.interfaces.IControllerModeChangeListener;

import java.lang.annotation.Retention;
import java.util.HashSet;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @Author naOKi
 * @Date 2018/11/17
 **/
public class ControllerModeHolder {

    public static final int MODE_EDIT = 0;
    public static final int MODE_EXECUTION = 1;
    public static final int MODE_PREVIEW = 2;
    @Retention(SOURCE)
    @IntDef({MODE_EDIT, MODE_EXECUTION,MODE_PREVIEW})
    public @interface ControllerMode {}

    private static HashSet<IControllerModeChangeListener> listeners = new HashSet<>();

    @ControllerMode
    private static int controllerMode = MODE_EDIT;

    private static final Object lock = new Object();

    private ControllerModeHolder() {}

    @ControllerMode
    public static int getControllerMode() {
        synchronized (lock) {
            return controllerMode;
        }
    }

    public static void toggleControllerMode() {
        synchronized (lock) {
            if(controllerMode == MODE_EDIT) {
                setControllerMode(MODE_EXECUTION);
            } else {
                setControllerMode(MODE_EDIT);
            }
        }
    }

    public static void setControllerMode(@ControllerMode int controllerMode) {
        synchronized (lock) {
            if(ControllerModeHolder.controllerMode == controllerMode) {
                return;
            }
            ControllerModeHolder.controllerMode = controllerMode;
            notifyControllerModeChanged(controllerMode);
        }
    }

    public static void addControllerModeChangeListener(IControllerModeChangeListener listener) {
        synchronized (lock) {
            if(listener == null) {
                return;
            }
            listeners.add(listener);
        }
    }

    public static void removeControllerModeChangeListener(IControllerModeChangeListener listener) {
        synchronized (lock) {
            if(listener == null) {
                return;
            }
            listeners.remove(listener);
        }
    }

    private static void notifyControllerModeChanged(int controllerStatus) {
        synchronized (lock) {
            for(IControllerModeChangeListener listener : listeners) {
                listener.onControllerModeChanged(controllerStatus);
            }
        }
    }

}
