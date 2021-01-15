package com.ubtedu.deviceconnect.libs.ukit.legacy.mission;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.URoDataWrapper;
import com.ubtedu.deviceconnect.libs.base.mission.URoProductInitMission;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author naOKi
 * @Date 2019/09/17
 **/
public class URoUkitLegacyBoardInfoMission extends URoProductInitMission {

    private Disposable disposable;

    public URoUkitLegacyBoardInfoMission(@NonNull URoProduct product) {
        super(product);
    }

    @Override
    protected void onMissionStart() throws Throwable {
        disposable = Observable.just(new URoDataWrapper<Object>(null)).delay(100, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe(new Consumer<URoDataWrapper<Object>>() {
                @Override
                public void accept(URoDataWrapper<Object> warpper) throws Exception {
                    URoRequest request = new URoRequest(URoCommandConstants.CMD_BOARD_HANDSHAKE);
                    performNext(URoCommandConstants.CMD_BOARD_HANDSHAKE, request);
                    resetTimeout(5000);
                }
            });
    }

    @Override
    protected void onMissionRelease() {
        super.onMissionRelease();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    @Override
    protected void onPreviousResult(Object identity, URoCompletionResult result) throws Throwable {
        if (URoCommandConstants.CMD_BOARD_HANDSHAKE.equals(identity)) {
            disposable = Observable.just(new URoDataWrapper<Object>(null)).delay(5000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Consumer<URoDataWrapper<Object>>() {
                        @Override
                        public void accept(URoDataWrapper<Object> warpper) throws Exception {
                            URoRequest request = new URoRequest(URoCommandConstants.CMD_BOARD_INFO);
                            performNext(URoCommandConstants.CMD_BOARD_INFO, request);
                            resetTimeout(5000);
                            sendRequestDirectly(new URoRequest(URoCommandConstants.CMD_BOARD_SERIAL_NUMBER));
                        }
                    });
        } else if (URoCommandConstants.CMD_BOARD_INFO.equals(identity)) {
            URoRequest request = new URoRequest(URoCommandConstants.CMD_BOARD_BATTERY);
            performNext(URoCommandConstants.CMD_BOARD_BATTERY, request);
            resetTimeout(5000);
        } else if (URoCommandConstants.CMD_BOARD_BATTERY.equals(identity)) {
            notifyComplete(null);
        }
    }

}
