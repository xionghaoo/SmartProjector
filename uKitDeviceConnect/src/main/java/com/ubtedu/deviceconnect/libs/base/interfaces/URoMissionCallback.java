package com.ubtedu.deviceconnect.libs.base.interfaces;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.invocation.URoMissionAbortSignal;

/**
 * @Author naOKi
 * @Date 2018/11/13
 **/
public abstract class URoMissionCallback<T> implements URoCompletionCallback<T>, URoProcessChangeCallback {

    private URoMissionAbortSignal abortSignal;

    public URoMissionAbortSignal getAbortSignal() {
        return abortSignal;
    }

    public abstract void onMissionNextStep(int currentStep, int totalStep);
    public abstract void onMissionBegin();
    public abstract void onMissionEnd();

    @Override
    public void onProcessPercentChanged(int percent) {
        // do nothing
    }

    public void onMissionAccept(URoMissionAbortSignal abortSignal) {
        this.abortSignal = abortSignal;
    }

}