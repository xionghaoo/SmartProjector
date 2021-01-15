package com.ubtedu.deviceconnect.libs.ukit.smart.mission;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileDownloadProgressQuery;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbPartDownloadProgressQuery;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author naOKi
 * @Date 2020/08/24
 **/
public class URoUkitSmartVisionUpgradeMission extends URoUkitSmartUpgradeMission {

    public static final String VISION_FIRMWARE = "firmware";
    public static final String VISION_WIFI = "wifi";
    public static final String VISION_MODEL = "model";

    private Disposable disposable;
    private String type;
    private int lastDownloadPercent = -1;
    private long lastUpdateTime = -1;

    private final static long UPDATE_TIMEOUT = 20 * 1000L;

    public URoUkitSmartVisionUpgradeMission(@NonNull URoProduct product, String url, String md5, int size, int dev, int id, String type) {
        super(product, url, md5, size, dev, id);
        this.type = type;
    }

    public URoUkitSmartVisionUpgradeMission(@NonNull URoProduct product, String url, String md5, int size, int dev, ArrayList<Integer> ids, String type) {
        super(product, url, md5, size, dev, ids);
        this.type = type;
    }

    @Override
    protected void onMissionStart() throws Throwable {
        super.onMissionStart();
        cleanupIntercept();
    }

    @Override
    protected void onMissionRelease() {
        super.onMissionRelease();
        if(disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    @Nullable
    @Override
    protected URoRequest createStopRequest() {
        URoRequest request = super.createStopRequest();
        if(request != null && TextUtils.equals(type, VISION_MODEL)) {
            request.setParameter("part", 4);
        }
        return request;
    }

    @Nullable
    @Override
    protected URoCommand getStopCmd() {
        if(TextUtils.equals(type, VISION_FIRMWARE)) {
            return URoCommandConstants.CMD_VISIONMODULE_DEV_UPDATE_STOP;
        } else if(TextUtils.equals(type, VISION_MODEL)) {
            return URoCommandConstants.CMD_VISIONMODULE_PART_UPDATE_STOP;
        }
        return null;
    }

    @NonNull
    @Override
    protected URoRequest createInfoRequest() {
        URoRequest request = super.createInfoRequest();
        if(TextUtils.equals(type, VISION_WIFI)) {
            request.setParameter("part", 1);
        } else if(TextUtils.equals(type, VISION_MODEL)) {
            request.setParameter("part", 4);
        }
        return request;
    }

    @NonNull
    @Override
    protected URoCommand getInfoSetCmd() {
        if(TextUtils.equals(type, VISION_FIRMWARE)) {
            return URoCommandConstants.CMD_VISIONMODULE_UPDATE_INFO_SET;
        } else if(TextUtils.equals(type, VISION_WIFI)) {
            return URoCommandConstants.CMD_VISIONMODULE_PART_INFO_SET;
        } else if(TextUtils.equals(type, VISION_MODEL)) {
            return URoCommandConstants.CMD_VISIONMODULE_PART_INFO_SET;
        }
        throw new InvalidParameterException();
    }

    protected URoRequest createQueryRequest() {
        URoRequest request = new URoRequest(getProgressQueryCmd());
        if(TextUtils.equals(type, VISION_WIFI)) {
            request.setParameter("part", 1);
        } else if(TextUtils.equals(type, VISION_MODEL)) {
            request.setParameter("part", 4);
        }
        return request;
    }

    protected URoCommand getProgressQueryCmd() {
        if(TextUtils.equals(type, VISION_FIRMWARE)) {
            return URoCommandConstants.CMD_VISIONMODULE_FILE_DOWNLOAD_PROGRESS_QUERY;
        } else if(TextUtils.equals(type, VISION_WIFI)) {
            return URoCommandConstants.CMD_VISIONMODULE_PART_DOWNLOAD_PROGRESS_QUERY;
        } else if(TextUtils.equals(type, VISION_MODEL)) {
            return URoCommandConstants.CMD_VISIONMODULE_PART_DOWNLOAD_PROGRESS_QUERY;
        }
        throw new InvalidParameterException();
    }

    @Override
    protected boolean checkPerformResult(@NonNull URoCompletionResult result) {
        return result.isSuccess() || URoError.TIMEOUT.equals(result.getError());
    }

    @Override
    protected boolean checkPreviousResult(boolean isSuccess, Object identity, @NonNull URoCompletionResult result) {
        if (URoCommandConstants.CMD_FILE_DOWNLOAD_PROGRESS_QUERY.equals(identity)) {
            return isSuccess || URoError.TIMEOUT.equals(result.getError());
        } else {
            return super.checkPreviousResult(isSuccess, identity, result);
        }
    }

    @Override
    protected void onPreviousResult(Object identity, URoCompletionResult result) throws Throwable {
        if (URoCommandConstants.CMD_FILE_DOWNLOAD_PROGRESS_QUERY.equals(identity)) {
            if(!result.isSuccess()) {
                return;
            }
            int downloadPercent;
            int downloadStatus;
            int downloadDev;
            if(TextUtils.equals(type, VISION_FIRMWARE)) {
                PbFileDownloadProgressQuery.FileDownloadProgressQueryResponse response = (PbFileDownloadProgressQuery.FileDownloadProgressQueryResponse) result.getData();
                downloadPercent = response.getPercent();
                downloadStatus = response.getStatus();
                downloadDev = response.getDev();
            } else {
                PbPartDownloadProgressQuery.PartDownloadProgressQueryResponse response = (PbPartDownloadProgressQuery.PartDownloadProgressQueryResponse) result.getData();
                downloadPercent = response.getPercent();
                downloadStatus = response.getStatus();
                downloadDev = URoCommandConstants.CMD_VISIONMODULE_PART_DOWNLOAD_PROGRESS_QUERY.getDev();
            }
            URoLogUtils.e("Percent: %d %%, Status: %d, Dev: %d", downloadPercent, downloadStatus, downloadDev);
            if (downloadStatus != 0) {
                notifyError(URoError.UNKNOWN);
                return;
            }
            if(lastDownloadPercent != downloadPercent) {
                sendUpgradePercent(downloadPercent);
                lastDownloadPercent = downloadPercent;
                lastUpdateTime = System.currentTimeMillis();
            } else {
                if(lastUpdateTime > 0 && System.currentTimeMillis() - lastUpdateTime >= UPDATE_TIMEOUT) {
                    notifyError(URoError.MISSION_TIMEOUT);
                    return;
                }
            }
            if(downloadPercent == 100) {
                notifyComplete(null);
            }
        } else {
            super.onPreviousResult(identity, result);
            if (URoCommandConstants.CMD_UPDATE_INFO_SET.equals(identity)) {
                lastUpdateTime = System.currentTimeMillis();
                disposable = Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.newThread())
                    .subscribe((aLong) -> {
                        URoRequest request = createQueryRequest();
                        performNext(URoCommandConstants.CMD_FILE_DOWNLOAD_PROGRESS_QUERY, request);
                        resetTimeout(UPDATE_TIMEOUT);
                    });
            }
        }
    }

}
