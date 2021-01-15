package com.ubtedu.deviceconnect.libs.base;

import com.ubtedu.deviceconnect.libs.base.interfaces.URoMissionCallback;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoProcessChangeCallback;
import com.ubtedu.deviceconnect.libs.base.invocation.URoMissionAbortSignal;
import com.ubtedu.deviceconnect.libs.base.model.URoError;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * @Author naOKi
 * @Date 2019/09/16
 **/
public class URoCompletionCallbackHelper {

    private URoCompletionCallbackHelper() {}

    public static <T> void sendErrorCallback(URoError error, URoCompletionCallback<T> completionCallback) {
        if(completionCallback == null || error == null) {
            return;
        }
        Observable.just(new URoDataWrapper<URoError>(error))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<URoDataWrapper<URoError>>() {
                    @Override
                    public void accept(URoDataWrapper<URoError> warpper) throws Exception {
                        if(completionCallback != null) {
                            completionCallback.onComplete(new URoCompletionResult<>(null, warpper.getData()));
                        }
                    }
                });
    }

    public static <T> void sendSuccessCallback(T data, URoCompletionCallback<T> completionCallback) {
        if(completionCallback == null) {
            return;
        }
        Observable.just(new URoDataWrapper<T>(data))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<URoDataWrapper<T>>() {
                    @Override
                    public void accept(URoDataWrapper<T> warpper) throws Exception {
                        if(completionCallback != null) {
                            completionCallback.onComplete(new URoCompletionResult<>(warpper.getData(), URoError.SUCCESS));
                        }
                    }
                });
    }

    public static <T> void sendCallback(URoCompletionResult<T> result, URoCompletionCallback<T> completionCallback) {
        if(completionCallback == null) {
            return;
        }
        Observable.just(new URoDataWrapper<URoCompletionResult<T>>(result))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<URoDataWrapper<URoCompletionResult<T>>>() {
                    @Override
                    public void accept(URoDataWrapper<URoCompletionResult<T>> warpper) throws Exception {
                        if(completionCallback != null) {
                            completionCallback.onComplete(warpper.getData());
                        }
                    }
                });
    }

    public static <T> void sendProcessPercentCallback(int percent, URoCompletionCallback<T> completionCallback) {
        if(completionCallback == null) {
            return;
        }
        if(!(completionCallback instanceof URoProcessChangeCallback)) {
            return;
        }
        Observable.just(new URoDataWrapper<Integer>(percent))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<URoDataWrapper<Integer>>() {
                    @Override
                    public void accept(URoDataWrapper<Integer> warpper) throws Exception {
                        if(completionCallback != null) {
                            ((URoProcessChangeCallback)completionCallback).onProcessPercentChanged(warpper.getData());
                        }
                    }
                });
    }

    public static <T> void sendMissionNextStep(int[] steps, URoCompletionCallback<T> completionCallback) {
        if(completionCallback == null) {
            return;
        }
        if(!(completionCallback instanceof URoMissionCallback)) {
            return;
        }
        Observable.just(new URoDataWrapper<int[]>(steps))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<URoDataWrapper<int[]>>() {
                    @Override
                    public void accept(URoDataWrapper<int[]> warpper) throws Exception {
                        if(completionCallback != null) {
                            ((URoMissionCallback)completionCallback).onMissionNextStep(warpper.getData()[0], warpper.getData()[1]);
                        }
                    }
                });
    }

    public static <T> void sendMissionBegin(URoCompletionCallback<T> completionCallback) {
        if(completionCallback == null) {
            return;
        }
        if(!(completionCallback instanceof URoMissionCallback)) {
            return;
        }
        Observable.just(new URoDataWrapper<Void>(null))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<URoDataWrapper<Void>>() {
                    @Override
                    public void accept(URoDataWrapper<Void> warpper) throws Exception {
                        if(completionCallback != null) {
                            ((URoMissionCallback)completionCallback).onMissionBegin();
                        }
                    }
                });
    }

    public static <T> void sendMissionEnd(URoCompletionCallback<T> completionCallback) {
        if(completionCallback == null) {
            return;
        }
        if(!(completionCallback instanceof URoMissionCallback)) {
            return;
        }
        Observable.just(new URoDataWrapper<Void>(null))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<URoDataWrapper<Void>>() {
                    @Override
                    public void accept(URoDataWrapper<Void> warpper) throws Exception {
                        if(completionCallback != null) {
                            ((URoMissionCallback)completionCallback).onMissionEnd();
                        }
                    }
                });
    }

    public static <T> void sendMissionAccept(URoMissionAbortSignal abortSignal, URoCompletionCallback<T> completionCallback) {
        if(completionCallback == null) {
            return;
        }
        if(!(completionCallback instanceof URoMissionCallback)) {
            return;
        }
        Observable.just(new URoDataWrapper<URoMissionAbortSignal>(abortSignal))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<URoDataWrapper<URoMissionAbortSignal>>() {
                    @Override
                    public void accept(URoDataWrapper<URoMissionAbortSignal> warpper) throws Exception {
                        if(completionCallback != null) {
                            ((URoMissionCallback)completionCallback).onMissionAccept(warpper.getData());
                        }
                    }
                });
    }

}
