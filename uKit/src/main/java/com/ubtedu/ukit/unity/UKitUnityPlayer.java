/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.unity;

import android.content.Context;
import android.view.InputEvent;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.unity3d.player.UnityPlayer;

/**
 * @Author qinicy
 * @Date 2018/11/8
 **/
public class UKitUnityPlayer extends UnityPlayer {
    private static final int UNITY_FULL_LIFE_CYCLE = 4;
    private static UKitUnityPlayer sUnityPlayer;
    private boolean isResume;

    private int mLifecycle;
    private boolean isBlockQuit;
    private boolean isLoadingComplete;

    public static UKitUnityPlayer getInstance(Context context) {
        if (sUnityPlayer == null) {
            sUnityPlayer = new UKitUnityPlayer(context.getApplicationContext());
            int glesMode = sUnityPlayer.getSettings().getInt("gles_mode", 1);
            sUnityPlayer.init(glesMode, false);
        }
        return sUnityPlayer;
    }

    private UKitUnityPlayer(Context context) {
        super(context);
        mLifecycle = UNITY_FULL_LIFE_CYCLE;
    }

    @Override
    public void start() {
        LogUtil.d("UKitUnityPlayer start");
        if (mLifecycle < UNITY_FULL_LIFE_CYCLE) {
            mLifecycle++;
            return;
        }
        super.start();
    }

    @Override
    public void resume() {
        LogUtil.d("UKitUnityPlayer resume");
        if (mLifecycle < UNITY_FULL_LIFE_CYCLE) {
            mLifecycle++;
            return;
        }
        super.resume();

        isResume = true;
    }


    @Override
    public void pause() {
        LogUtil.d("UKitUnityPlayer pause");
        if (mLifecycle < UNITY_FULL_LIFE_CYCLE) {
            mLifecycle++;
            return;
        }
        super.pause();

        isResume = false;
    }

    @Override
    public void stop() {
        LogUtil.d("UKitUnityPlayer stop");
        if (mLifecycle < UNITY_FULL_LIFE_CYCLE) {
            mLifecycle++;
            return;
        }
        super.stop();
    }

    @Override
    public void quit() {
        super.quit();
        LogUtil.d("UKitUnityPlayer quit");
    }

    @Override
    public boolean injectEvent(InputEvent inputEvent) {
        super.injectEvent(inputEvent);
        return false;
    }

    public void blockLifecycleOneTime() {
        mLifecycle = 0;
    }

    public boolean isResume() {
        return isResume;
    }

    public boolean isBlockQuit() {
        return isBlockQuit;
    }

    public void setBlockQuit(boolean blockQuit) {
        isBlockQuit = blockQuit;
    }

    public boolean isLoadingComplete() {
        return isLoadingComplete;
    }

    public void setLoadingComplete(boolean loadingComplete) {
        isLoadingComplete = loadingComplete;
    }
}
