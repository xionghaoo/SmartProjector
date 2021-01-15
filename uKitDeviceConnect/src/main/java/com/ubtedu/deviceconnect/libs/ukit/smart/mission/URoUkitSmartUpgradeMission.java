package com.ubtedu.deviceconnect.libs.ukit.smart.mission;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.mission.URoCommandMission;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileDownloadProgressReport;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbUpdateStatusReport;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @Author naOKi
 * @Date 2019/09/17
 **/
public class URoUkitSmartUpgradeMission extends URoCommandMission {

    protected String url;
    protected String md5;
    protected int size;
    protected int dev;
    protected long startMs;
    protected HashSet<Integer> ids;
    protected HashMap<Integer, Boolean> result;

    public URoUkitSmartUpgradeMission(@NonNull URoProduct product, String url, String md5, int size, int dev, int id) {
        super(product);
        this.url = url;
        this.md5 = md5;
        this.size = size;
        this.dev = dev;
        this.ids = new HashSet<>();
        this.result = new HashMap<>();
        this.ids.add(id);
    }

    public URoUkitSmartUpgradeMission(@NonNull URoProduct product, String url, String md5, int size, int dev, ArrayList<Integer> ids) {
        super(product);
        this.url = url;
        this.md5 = md5;
        this.size = size;
        this.dev = dev;
        this.ids = new HashSet<>(ids);
        this.result = new HashMap<>();
    }

    @Override
    protected void onMissionStart() throws Throwable {
        startMs = System.currentTimeMillis();
        URoRequest request = createInfoRequest();
        performNext(URoCommandConstants.CMD_UPDATE_INFO_SET, request);
        setupIntercept();
    }

    @Override
    protected void onMissionRelease() {
        super.onMissionRelease();
        if(isAbort()) {
            // 取消升级，则需要发送停止升级指令
            URoRequest request = createStopRequest();
            if(request != null) {
                sendRequestDirectly(request);
            }
        }
        URoLogUtils.e("升级耗时 %d ms", System.currentTimeMillis() - startMs);
    }

    protected void sendUpgradePercent(int percent) {
        URoCompletionCallbackHelper.sendProcessPercentCallback(percent, getCallback());
    }

    @Override
    protected URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
        if (URoCommandConstants.CMD_FILE_DOWNLOAD_PROGRESS_REPORT.equals(cmd)) {
            PbFileDownloadProgressReport.FileDownloadProgressReportResponse downloadProgressResponse = (PbFileDownloadProgressReport.FileDownloadProgressReportResponse) response.getData();
            int downloadPercent = downloadProgressResponse.getPercent();
            int downloadStatus = downloadProgressResponse.getStatus();
            int downloadDev = downloadProgressResponse.getDev();
            URoLogUtils.e("Percent: %d %%, Status: %d, Dev: %d", downloadPercent, downloadStatus, downloadDev);
            sendUpgradePercent(downloadPercent);
            if (downloadStatus != 0) {
                notifyError(URoError.UNKNOWN);
                return null;
            }
            if (downloadDev == dev) {
                resetTimeout(20000);
            }
        } else if (URoCommandConstants.CMD_UPDATE_STATUS_REPORT.equals(cmd)) {
            PbUpdateStatusReport.UpdateStatusReportResponse statusReportResponse = (PbUpdateStatusReport.UpdateStatusReportResponse) response.getData();
            int upgradeResult = statusReportResponse.getStatus();
            int upgradeDev = statusReportResponse.getDev();
            int upgradeId = statusReportResponse.getId();
            URoLogUtils.e("Status: %d, Dev: %d, Id: %d", upgradeResult, upgradeDev, upgradeId);
            if (upgradeResult != 0) {
                notifyError(URoError.UNKNOWN);
                return null;
            }
            if (upgradeDev == dev && ids.contains(upgradeId)) {
                resetTimeout(20000);
                this.result.put(upgradeId, upgradeResult == 0);
                if (ids.size() == this.result.size()) {
                    if (!this.result.containsValue(Boolean.FALSE)) {
                        notifyComplete(null);
                    } else {
                        notifyError(URoError.UNKNOWN);
                    }
                }
            }
        }
        return super.onInterceptResponse(cmd, response);
    }

    @Override
    protected void notifyError(URoError error) {
        super.notifyError(error);
        URoRequest request = createStopRequest();
        if(request != null) {
            sendRequestDirectly(request);
        }
    }

    @Override
    protected void onPreviousResult(Object identity, URoCompletionResult result) throws Throwable {
        if (URoCommandConstants.CMD_UPDATE_INFO_SET.equals(identity)) {
            resetTimeout(20000);
        }
    }

    protected @Nullable URoRequest createStopRequest() {
        URoCommand cmd = getStopCmd();
        if(cmd == null) {
            return null;
        }
        return new URoRequest(cmd);
    }

    protected @Nullable URoCommand getStopCmd() {
        return URoCommandConstants.CMD_UPDATE_STOP;
    }

    protected @NonNull URoRequest createInfoRequest() {
        URoRequest request = new URoRequest(getInfoSetCmd());
        request.setParameter("packageUrl", url);
        request.setParameter("packageMd5", md5);
        request.setParameter("packageSize", size);
        request.setParameter("dev", dev);
        request.setParameter("ids", ids);
        return request;
    }

    protected @NonNull URoCommand getInfoSetCmd() {
        return URoCommandConstants.CMD_UPDATE_INFO_SET;
    }

}
