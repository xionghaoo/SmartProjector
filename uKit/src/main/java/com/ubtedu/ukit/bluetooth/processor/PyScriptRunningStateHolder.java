package com.ubtedu.ukit.bluetooth.processor;

import com.ubtedu.alpha1x.utils.LogUtil;

public class PyScriptRunningStateHolder {
    private RunningState mCurrentScriptState = RunningState.STOP;
    private CurrentScriptType mCurrentScriptType = CurrentScriptType.NONE;
    private static final String TAG = "PyScriptRunningState";
    private enum RunningState {
        START, EXECUTING, STOP
    }

    private enum CurrentScriptType {
        NONE, BLOCKLY, CONTROLLER
    }

    public static PyScriptRunningStateHolder getInstance() {
        return PyScriptRunningStateHolder.Holder.INSTANCE;
    }

    private static class Holder {
        private static final PyScriptRunningStateHolder INSTANCE = new PyScriptRunningStateHolder();
    }

    synchronized public void setControllerScriptStarted() {
        mCurrentScriptState = RunningState.START;
        mCurrentScriptType = CurrentScriptType.CONTROLLER;
        LogUtil.i(TAG,mCurrentScriptType.name()+" "+mCurrentScriptState.name());
    }

    synchronized public boolean isControllerScriptRunning() {
        return mCurrentScriptType == CurrentScriptType.CONTROLLER;
    }

    synchronized public void setBlocklyScriptStarted() {
        mCurrentScriptState = RunningState.START;
        mCurrentScriptType = CurrentScriptType.BLOCKLY;
        LogUtil.i(TAG,mCurrentScriptType.name()+" "+mCurrentScriptState.name());
    }

    synchronized public boolean isBlocklyScriptRunning() {
        return mCurrentScriptType == CurrentScriptType.BLOCKLY;
    }

    synchronized public void setScriptStopped() {
        mCurrentScriptState = RunningState.STOP;
        mCurrentScriptType = CurrentScriptType.NONE;
        LogUtil.i(TAG,mCurrentScriptState.name());
    }

    synchronized public void setScriptExecuting() {
        mCurrentScriptState = RunningState.EXECUTING;
        LogUtil.i(TAG,mCurrentScriptState.name());
    }

    synchronized public boolean isScriptExecuting() {
        return mCurrentScriptState == RunningState.EXECUTING;
    }

    synchronized public boolean isScriptStopped() {
        return mCurrentScriptState == RunningState.STOP;
    }

    synchronized public boolean isScriptStarted() {
        return mCurrentScriptState != RunningState.STOP;
    }
}
