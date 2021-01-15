package com.ubtedu.deviceconnect.libs.ukit.smart.mission;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.mission.URoCommandMission;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbColorCalibrateStateGet;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author naOKi
 * @Date 2019/09/17
 **/
public class URoUkitSmartColorCalibrationMission extends URoCommandMission {

    private int id;
    private long startMs;
    private Disposable disposable;

    public URoUkitSmartColorCalibrationMission(@NonNull URoProduct product, int id) {
        super(product);
        this.id = id;
    }

    @Override
    protected void onMissionStart() throws Throwable {
        startMs = System.currentTimeMillis();
        URoRequest request = new URoRequest(URoCommandConstants.CMD_CLR_CAL_ONOFF);
        request.setParameter("id", id);
        request.setParameter("rgb", 0);
        request.setParameter("onoff", true);
        performNext(URoCommandConstants.CMD_CLR_CAL_ONOFF, request);
        disposable = Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe((aLong) -> {
                    URoRequest request1 = new URoRequest(URoCommandConstants.CMD_CLR_CAL_STATE_GET);
                    request1.setParameter("id", id);
                    request1.setParameter("rgb", 0);
                    performNext(URoCommandConstants.CMD_CLR_CAL_STATE_GET, request1);
                });
    }

    @Override
    protected void onMissionRelease() {
        super.onMissionRelease();
        URoLogUtils.e("校准耗时 %d ms", System.currentTimeMillis() - startMs);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    @Override
    protected void onPreviousResult(Object identity, URoCompletionResult result) throws Throwable {
        if (URoCommandConstants.CMD_CLR_CAL_ONOFF.equals(identity)) {
            // do nothing!
        } else if (URoCommandConstants.CMD_CLR_CAL_STATE_GET.equals(identity)) {
            PbColorCalibrateStateGet.ColorCalibrateStateGetResponse response = (PbColorCalibrateStateGet.ColorCalibrateStateGetResponse) result.getData();
            int rgb = response.getRgb();
            int state = response.getState();
            URoLogUtils.e("Rgb: 0x%08X, State: %d", rgb, state);
            if (state == 2) {
                notifyComplete(null);
                return;
            }
            if (state != 1) {
                notifyError(URoError.UNKNOWN);
                return;
            }
            if (rgb == 0xFFFFFF00) {
                URoLogUtils.e("请校准白色");
            } else if (rgb == 0x00000000) {
                URoLogUtils.e("请校准黑色");
            } else {
                notifyError(URoError.UNKNOWN);
            }
        }
    }

}
