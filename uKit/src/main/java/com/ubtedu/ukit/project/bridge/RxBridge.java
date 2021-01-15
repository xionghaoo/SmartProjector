/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge;

import com.ubtedu.bridge.BridgeImpl;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.IBridgeHandler;
import com.ubtedu.bridge.OnCallback;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author qinicy
 * @Date 2018/12/14
 **/
public class RxBridge extends BridgeImpl {
    public RxBridge(IBridgeHandler bridgeHandler) {
        super(bridgeHandler);
    }

    public Observable<BridgeResult> call(final String funName, final Object[] args) {
        return Observable.create(new ObservableOnSubscribe<BridgeResult>() {
            @Override
            public void subscribe(final ObservableEmitter<BridgeResult> emitter) throws Exception {
                RxBridge.super.call(funName, args, new OnCallback() {
                    @Override
                    public void onCallback(BridgeResult result) {
                        if (!emitter.isDisposed()) {
                            emitter.onNext(result);
                            emitter.onComplete();
                        }
                    }
                });
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }
}
