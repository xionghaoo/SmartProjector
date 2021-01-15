package com.ubtedu.deviceconnect.libs.ukit.smart.mission;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.mission.URoCommandMission;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileSendMaxPackSize;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileSending;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * @Author naOKi
 * @Date 2019/09/17
 **/
public abstract class URoUkitSmartSendMission extends URoCommandMission {

    private String target;
    private int maxPackageSize;
    private int pkgNum;
    private long startMs;
    private URoComponentType componentType;

    @Deprecated
    public URoUkitSmartSendMission(@NonNull URoProduct product, String target) {
        this(product, target, null);
    }

    public URoUkitSmartSendMission(@NonNull URoProduct product, String target, URoComponentType componentType) {
        super(product);
        this.target = target;
        this.componentType = componentType;
    }

    @Override
    protected void onMissionStart() throws Throwable {
        startMs = System.currentTimeMillis();
        prepare();

        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(URoFileCommandHelper.FileCommandType.FILE_SEND_MAX_PACK_SIZE, componentType));
        performNext(URoCommandConstants.CMD_FILE_SEND_MAX_PACK_SIZE, request);
    }

    @Override
    protected void onMissionRelease() {
        super.onMissionRelease();
        close();
        if(isAbort()) {
            // 取消发送，则需要发送停止指令
            sendRequestDirectly(new URoRequest(URoFileCommandHelper.getCommandByDev(URoFileCommandHelper.FileCommandType.FILE_SEND_STOP, componentType)));
        }
        URoLogUtils.e("传输文件结束：大小 %d bytes， 耗时 %d ms", getLength(), System.currentTimeMillis() - startMs);
    }

    protected abstract int getCrc32();
    protected abstract void prepare() throws Throwable;
    protected abstract void close();
    protected abstract int getLength();
    protected abstract int readData(int sourceOffset, byte[] buffer, int targetOffset, int length) throws Throwable;

    @Override
    protected void onPreviousResult(Object identity, URoCompletionResult result) throws Throwable {
        if (URoCommandConstants.CMD_FILE_SEND_MAX_PACK_SIZE.equals(identity)) {
            PbFileSendMaxPackSize.FileSendMaxPackSizeResponse maxPackageSizeResponse = (PbFileSendMaxPackSize.FileSendMaxPackSizeResponse) result.getData();
            maxPackageSize = maxPackageSizeResponse.getMaxPackSz();
            URoLogUtils.e("maxPackageSize: %d", maxPackageSize);
            pkgNum = (int) ((getLength() + maxPackageSize - 1) / maxPackageSize);
            URoLogUtils.e("pkgNum: %d", pkgNum);
            URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(URoFileCommandHelper.FileCommandType.FILE_SEND_START, componentType));
            request.setParameter("path", target);
            request.setParameter("pack_num", pkgNum);
            request.setParameter("file_size", getLength());
            performNext(URoCommandConstants.CMD_FILE_SEND_START, request);
            URoCompletionCallbackHelper.sendProcessPercentCallback(0, getCallback());
        } else if (URoCommandConstants.CMD_FILE_SENDING.equals(identity)
                || URoCommandConstants.CMD_FILE_SEND_START.equals(identity)) {
            int i;
            if (URoCommandConstants.CMD_FILE_SENDING.equals(identity)) {
                PbFileSending.FileSendingResponse sendingResponse = (PbFileSending.FileSendingResponse) result.getData();
                i = sendingResponse.getPackIdx();
            } else {
                i = 0;
            }
            if (i < pkgNum) {
                URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(URoFileCommandHelper.FileCommandType.FILE_SENDING, componentType));
                request.setParameter("pack_idx", i + 1);
                int offset = i * maxPackageSize;
                int size = Math.min(maxPackageSize, getLength() - offset);
                byte[] buffer = new byte[size];
                readData(offset, buffer, 0, size);
                request.setParameter("data", buffer);
                performNext(URoCommandConstants.CMD_FILE_SENDING, request);
                URoCompletionCallbackHelper.sendProcessPercentCallback((offset * 100) / getLength(), getCallback());
            } else {
                URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(URoFileCommandHelper.FileCommandType.FILE_SEND_OVER, componentType));
                request.setParameter("file_size", getLength());
                request.setParameter("crc32", getCrc32());
                performNext(URoCommandConstants.CMD_FILE_SEND_OVER, request);
                URoCompletionCallbackHelper.sendProcessPercentCallback(100, getCallback());
            }
        } else if (URoCommandConstants.CMD_FILE_SEND_OVER.equals(identity)) {
            notifyComplete(null);
        }
    }

}
