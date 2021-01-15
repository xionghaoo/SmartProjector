package com.ubtedu.deviceconnect.libs.base.mission;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.invocation.URoMissionAbortSignal;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public abstract class URoMission<T> {

    private boolean hasStarted = false;
    private boolean hasStopped = false;
    private boolean hasAbort = false;

    private URoMissionAbortSignal abortSignal;

    private URoCompletionCallback<T> callback;

    private WorkThread workThread;
    private Disposable timeoutDisposable;

    private int currentStep = -1;
    private int totalStep = -1;

    public URoMission() {
        this(null);
    }

    public URoMission(URoCompletionCallback<T> callback) {
        this.callback = callback;
    }

    public void setCallback(URoCompletionCallback<T> callback) {
        this.callback = callback;
    }

    protected URoCompletionCallback<T> getCallback() {
        return callback;
    }

    public boolean start() {
        synchronized (this) {
            if (hasAbort) {
                return false;
            }
            if (hasStarted) {
                return false;
            }
            hasStarted = true;
            abortSignal = new URoMissionAbortSignal(this);
        }
        try {
            onMissionBeginInternal();
        } catch (Throwable e) {
            URoLogUtils.e(e);
            notifyError(URoError.UNKNOWN);
        }
        return true;
    }

    public boolean stop() {
        synchronized (this) {
            if (hasAbort) {
                return false;
            }
            if (!hasStarted && !hasStopped) {
                return false;
            }
            hasStopped = true;
        }
        if(workThread != null) {
            workThread.cancel();
            workThread = null;
        }
        resetTimeout(0);
        try {
            onMissionStop();
            onMissionEndInternal();
        } catch (Throwable e) {
            URoLogUtils.e(e);
        }
        abortSignal = null;
        return true;
    }

    public boolean abort() {
        synchronized (this) {
            if (hasAbort) {
                return true;
            }
            hasAbort = true;
        }
        if(abortSignal != null) {
            abortSignal.abort();
        }
        stop();
        return true;
    }

    protected boolean isAbort() {
        synchronized (this) {
            return hasAbort || (abortSignal != null && abortSignal.isAbort());
        }
    }

    public boolean isStarted() {
        synchronized (this) {
            return hasStarted;
        }
    }

    public boolean isStopped() {
        synchronized (this) {
            return hasStopped;
        }
    }

    protected void notifyComplete(T data) {
        if(!stop()) {
            return;
        }
        URoCompletionCallbackHelper.sendSuccessCallback(data, callback);
        onMissionEndInternal();
    }

    protected void notifyError(URoError error) {
        if(!stop()) {
            return;
        }
        URoCompletionCallbackHelper.sendErrorCallback(error, callback);
        onMissionEndInternal();
    }

    protected boolean checkPerformResult(@NonNull URoCompletionResult result) {
        return result.isSuccess();
    }

    protected void onPerformComplete(URoCompletionResult result) {
        resetTimeout(0);
        if(checkPerformResult(result)) {
            try {
                onPreviousResult(result);
            } catch (Throwable e) {
                URoLogUtils.e(e);
                notifyError(URoError.UNKNOWN);
            }
        } else {
            notifyError(result.getError());
        }
    }

    protected <C> void performNext(Callable<URoCompletionResult<C>> nextAction) {
        performNext(nextAction, 0, 0);
    }

    protected <C> void performNext(Callable<URoCompletionResult<C>> nextAction, long timeoutMs) {
        performNext(nextAction, timeoutMs, 0);
    }

    protected <C> void performNext(Callable<URoCompletionResult<C>> nextAction, long timeoutMs, int retryTimes) {
        if (isAbort() || isStopped()) {
            return;
        }
        if(workThread != null) {
            return;
        }
        currentStep++;
        notifyStepChange(getCurrentStepRound(), totalStep);
        resetTimeout(timeoutMs);
//        URoLogUtils.d("performNext");
        workThread = new WorkThread<>(nextAction, retryTimes);
        workThread.start();
    }

    protected void resetTimeout(long timeoutMs) {
        if(timeoutDisposable != null) {
            timeoutDisposable.dispose();
            timeoutDisposable = null;
        }
        if(timeoutMs > 0) {
            timeoutDisposable = Observable.timer(timeoutMs, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe((aLong) -> {
                    onMissionTimeout();
                });
        }
    }

    protected abstract void onMissionStart() throws Throwable;
    protected abstract void onPreviousResult(URoCompletionResult result) throws Throwable;

    protected void onMissionCancelResult(URoCompletionResult result) throws Throwable {
        // Stub!
    }

    protected void onMissionStop() {
        // Stub!
    }

    protected void onMissionRelease() {
        // Stub!
    }

    protected void onMissionTimeout() {
        notifyError(URoError.MISSION_TIMEOUT);
    }

    private void onMissionBeginInternal() throws Throwable {
        URoCompletionCallbackHelper.sendMissionAccept(abortSignal, callback);
        URoCompletionCallbackHelper.sendMissionBegin(callback);
        onMissionStart();
    }

    private void onMissionEndInternal() {
        onMissionRelease();
        URoCompletionCallbackHelper.sendMissionEnd(callback);
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public int getCurrentStepRound() {
        if(currentStep != -1 && totalStep != -1) {
            return currentStep % totalStep;
        } else {
            return currentStep;
        }
    }

    protected void setTotalStep(int totalStep) {
        if(totalStep > 0) {
            this.totalStep = totalStep;
        }
    }

    protected void notifyStepChange(int currentStep, int totalStep) {
        URoLogUtils.e("Mission [%s] execute step: %d/%d", getClass().getSimpleName(), currentStep + 1, totalStep);
        URoCompletionCallbackHelper.sendMissionNextStep(new int[]{currentStep, totalStep}, callback);
    }

    private class WorkThread<C> extends Thread {

        private Callable<URoCompletionResult<C>> callable;

        private boolean cancel = false;
        private int retryTimes;

        private WorkThread(@Nullable Callable<URoCompletionResult<C>> callable, int retryTimes) {
            this.callable = callable;
            this.retryTimes = retryTimes;
        }

        @Override
        public void run() {
            try {
                int times = 0;
                URoCompletionResult<?> result = null;
                try {
                    do {
                        try {
                            if (!cancel && !isAbort()) {
                                result = callable.call();
                            }
                            break;
                        } catch (Throwable e) {
                            URoLogUtils.e(e);
                            if (retryTimes <= 0 || ++times > retryTimes) {
                                throw e;
                            }
                        }
                    } while (true);
                } finally {
                    if(this.equals(workThread)) {
                        workThread = null;
                    }
                }
                if(!cancel && !isAbort()) {
                    onPerformComplete(result);
                } else {
                    onMissionCancelResult(result);
                }
            } catch (Throwable e) {
                URoLogUtils.e(e);
                notifyError(URoError.UNKNOWN);
            }
        }

        public void cancel() {
            cancel = true;
        }

    }

}
