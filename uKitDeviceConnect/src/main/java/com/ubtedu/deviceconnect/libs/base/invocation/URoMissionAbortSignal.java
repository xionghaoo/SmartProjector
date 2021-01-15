package com.ubtedu.deviceconnect.libs.base.invocation;

import com.ubtedu.deviceconnect.libs.base.mission.URoMission;

import java.lang.ref.WeakReference;

/**
 * @Author naOKi
 * @Date 2019/09/20
 **/
public class URoMissionAbortSignal {

    private WeakReference<URoMission> missionReference;

    public URoMissionAbortSignal(URoMission mission) {
        this.missionReference = new WeakReference<>(mission);
    }

    private boolean isAbort = false;

    public boolean isAbort() {
        return isAbort;
    }

    public void abort() {
        isAbort = true;
        if(missionReference != null && missionReference.get() != null) {
            missionReference.get().stop();
        }
    }
}
