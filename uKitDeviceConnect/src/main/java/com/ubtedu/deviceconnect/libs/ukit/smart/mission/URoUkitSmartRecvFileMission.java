package com.ubtedu.deviceconnect.libs.ukit.smart.mission;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.mission.URoCommandMission;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileRecvMaxPackSize;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileRecvOver;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileRecvStart;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileRecving;
import com.ubtedu.deviceconnect.libs.utils.URoIoUtils;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @Author naOKi
 * @Date 2019/09/17
 **/
public class URoUkitSmartRecvFileMission extends URoCommandMission {

    private FileOutputStream fos = null;
    private String source;
    private String target;
    private int crc32;
    private int maxPackageSize;
    private int pkgNum;
    private int fileLength;
    private long startMs;
    private URoComponentType componentType;

    @Deprecated
    public URoUkitSmartRecvFileMission(@NonNull URoProduct product, String source, String target) {
        this(product, source, target, null);
    }

    public URoUkitSmartRecvFileMission(@NonNull URoProduct product, String source, String target, URoComponentType componentType) {
        super(product);
        this.source = source;
        this.target = target;
        this.componentType = componentType;
    }

    @Override
    protected void onMissionStart() throws Throwable {
        startMs = System.currentTimeMillis();
        fos = new FileOutputStream(target);
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(URoFileCommandHelper.FileCommandType.FILE_RECV_MAX_PACK_SIZE, componentType));
        performNext(URoCommandConstants.CMD_FILE_RECV_MAX_PACK_SIZE, request);
    }

    @Override
    protected void onMissionRelease() {
        super.onMissionRelease();
        URoIoUtils.close(fos);
        fos = null;
        if(isAbort()) {
            // 取消接收，则需要发送停止指令
            sendRequestDirectly(new URoRequest(URoFileCommandHelper.getCommandByDev(URoFileCommandHelper.FileCommandType.FILE_RECV_STOP, componentType)));
        }
        URoLogUtils.e("传输文件结束：大小 %d bytes， 耗时 %d ms", fileLength, System.currentTimeMillis() - startMs);
    }

    @Override
    protected void onPreviousResult(Object identity, URoCompletionResult result) throws Throwable {
        if (URoCommandConstants.CMD_FILE_RECV_MAX_PACK_SIZE.equals(identity)) {
            PbFileRecvMaxPackSize.FileRecvMaxPackSizeResponse maxPackageSizeResponse = (PbFileRecvMaxPackSize.FileRecvMaxPackSizeResponse) result.getData();
            maxPackageSize = maxPackageSizeResponse.getMaxPackSz();
            URoLogUtils.e("maxPackageSize: %d", maxPackageSize);
            URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(URoFileCommandHelper.FileCommandType.FILE_RECV_START, componentType));
            request.setParameter("path", source);
            performNext(URoCommandConstants.CMD_FILE_RECV_START, request);
            URoCompletionCallbackHelper.sendProcessPercentCallback(0, getCallback());
        } else if (URoCommandConstants.CMD_FILE_RECVING.equals(identity)
                || URoCommandConstants.CMD_FILE_RECV_START.equals(identity)) {
            int i;
            if (URoCommandConstants.CMD_FILE_RECVING.equals(identity)) {
                PbFileRecving.FileRecvingResponse recvingResponse = (PbFileRecving.FileRecvingResponse) result.getData();
                i = recvingResponse.getPackIdx();
                fos.write(recvingResponse.getData().toByteArray());
            } else {
                PbFileRecvStart.FileRecvStartResponse recvStartResponse = (PbFileRecvStart.FileRecvStartResponse) result.getData();
                pkgNum = recvStartResponse.getPackNum();
                i = 0;
            }
            if (i < pkgNum) {
                URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(URoFileCommandHelper.FileCommandType.FILE_RECVING, componentType));
                request.setParameter("pack_idx", i + 1);
                performNext(URoCommandConstants.CMD_FILE_RECVING, request);
                URoCompletionCallbackHelper.sendProcessPercentCallback((i * 100) / pkgNum, getCallback());
            } else {
                URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(URoFileCommandHelper.FileCommandType.FILE_RECV_OVER, componentType));
                performNext(URoCommandConstants.CMD_FILE_RECV_OVER, request);
                URoCompletionCallbackHelper.sendProcessPercentCallback(100, getCallback());
            }
        } else if (URoCommandConstants.CMD_FILE_RECV_OVER.equals(identity)) {
            PbFileRecvOver.FileRecvOverResponse recvOverResponse = (PbFileRecvOver.FileRecvOverResponse) result.getData();
            crc32 = recvOverResponse.getCrc32();
            fileLength = recvOverResponse.getFileSize();
            URoIoUtils.close(fos);
            fos = null;
            File file = new File(target);
            if (file.length() == fileLength && getCrc32(file) == crc32) {
                notifyComplete(null);
            } else {
                notifyError(URoError.UNKNOWN);
            }
        }
    }

}
