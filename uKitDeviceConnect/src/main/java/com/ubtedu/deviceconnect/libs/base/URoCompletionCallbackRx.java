package com.ubtedu.deviceconnect.libs.base;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public class URoCompletionCallbackRx {
}
//public class URoCompletionCallbackRx<T> implements Observer<T>, Disposable {
//
//    private URoCompletionCallback<T> callback;
//    private Disposable disposable;
//
//    public URoCompletionCallbackRx(@NonNull URoCompletionCallback<T> callback) {
//        this.callback = callback;
//    }
//
//    public void setCallback(URoCompletionCallback<T> callback) {
//        this.callback = callback;
//    }
//
//    @Override
//    public void onSubscribe(Disposable d) {
//        disposable = d;
//    }
//
//    @Override
//    public void onNext(T t) {
//        if(callback != null) {
//            if(t instanceof URoCompletionResult) {
//                callback.onComplete((URoCompletionResult<T>)t);
//            } else {
//                callback.onComplete(new URoCompletionResult<>(t, URoError.SUCCESS));
//            }
//        }
//        dispose();
//    }
//
//    @Override
//    public void onError(Throwable e) {
//        if(callback != null) {
//            callback.onComplete(new URoCompletionResult<>(null, new URoError(9999, "UNKNOWN", e)));
//        }
//        dispose();
//    }
//
//    @Override
//    public void onComplete() {
//    }
//
//    @Override
//    public void dispose() {
//        if(disposable != null) {
//            disposable.dispose();
//        }
//    }
//
//    @Override
//    public boolean isDisposed() {
//        return disposable != null && disposable.isDisposed();
//    }
//}
