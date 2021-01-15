package com.ubtedu.deviceconnect.libs.ukit.legacy.mission;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.mission.URoCommandMission;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.utils.URoIoUtils;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @Author naOKi
 * @Date 2019/09/17
 **/
public class URoUkitLegacySensorUpgradeMission extends URoCommandMission {

    private static final int MAX_FRAME_SIZE = 100;

    private String path;
    private long startMs;
    private File file;
    private RandomAccessFile randomAccessFile;
    private URoComponentType componentType;
    private long fileSize;

    private int frameCount;
    private int frameIndex = 0;

    public URoUkitLegacySensorUpgradeMission(@NonNull URoProduct product, String path, URoComponentType componentType) {
        super(product);
        this.path = path;
        this.componentType = componentType;
    }

    @Override
    protected void onMissionStart() throws Throwable {
        file = new File(path);
        randomAccessFile = new RandomAccessFile(file, "r");
        startMs = System.currentTimeMillis();
        String filename = file.getName();
        int index = filename.lastIndexOf(".");
        if(index > 0) {
            filename = filename.substring(0, index);
        }
        fileSize = file.length();
        frameCount = (int)((fileSize + MAX_FRAME_SIZE - 1) / MAX_FRAME_SIZE);
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SENSOR_UPGRADE_ENTRY);
        request.setParameter("id", 0);
        request.setParameter("sensorTypeObj", componentType);
        request.setParameter("frameworkNameStr", filename);
        request.setParameter("frameworkSizeLong", fileSize);
        performNext(URoCommandConstants.CMD_SENSOR_UPGRADE_ENTRY, request);
        resetTimeout(10000);
    }

    @Override
    protected void onMissionRelease() {
        super.onMissionRelease();
        URoLogUtils.e("升级耗时 %d ms", System.currentTimeMillis() - startMs);
        URoIoUtils.close(randomAccessFile);
    }

    private void sendUpgradePercent(int percent) {
        URoCompletionCallbackHelper.sendProcessPercentCallback(percent, getCallback());
    }

    @Override
    protected void onPreviousResult(Object identity, URoCompletionResult result) throws Throwable {
        if (URoCommandConstants.CMD_SENSOR_UPGRADE_ENTRY.equals(identity)
                || URoCommandConstants.CMD_SENSOR_UPGRADE_DATA.equals(identity)) {
            byte[] fileData = readDataFromFile((long)frameIndex * MAX_FRAME_SIZE, MAX_FRAME_SIZE);
            int lastPercent = (frameIndex * 100) / frameCount;
            frameIndex += 1;
            int percent = (frameIndex * 100) / frameCount;
            if(percent != lastPercent) {
                sendUpgradePercent(percent);
            }
            URoCommand cmd = frameIndex == frameCount ? URoCommandConstants.CMD_SENSOR_UPGRADE_COMMIT : URoCommandConstants.CMD_SENSOR_UPGRADE_DATA;
            URoRequest request = new URoRequest(cmd);
            request.setParameter("sensorTypeObj", componentType);
            request.setParameter("dataByteArray", fileData);
            request.setParameter("indexInt", frameIndex);
            performNext(cmd, request);
            resetTimeout(10000);
        } else if (URoCommandConstants.CMD_SENSOR_UPGRADE_COMMIT.equals(identity)) {
            setupIntercept();
            resetTimeout(30000);
        }
    }

    @Override
    protected void onMissionCancelResult(URoCompletionResult result) throws Throwable {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SENSOR_UPGRADE_ABORT);
        request.setParameter("sensorTypeObj", componentType);
        sendRequestDirectly(request);
    }

    @Override
    protected URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
        if (URoCommandConstants.CMD_SENSOR_UPGRADE_COMMIT.equals(cmd)) {
            if (response.getBizData() != null && response.getBizData()[1] == (byte) 0xAA) {
                notifyComplete(null);
            } else {
                notifyError(URoError.UNKNOWN);
            }
        }
        return null;
    }

    private byte[] readDataFromFile(long offset, long length) throws Throwable {
        if(randomAccessFile == null || offset >= fileSize) {
            throw new IndexOutOfBoundsException();
        }
        int dataLength = (int)Math.min(length, fileSize - offset);
        byte[] result = new byte[dataLength];
        randomAccessFile.seek(offset);
        randomAccessFile.read(result);
        return result;
    }

}
