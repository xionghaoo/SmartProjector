package com.ubtedu.deviceconnect.libs.ukit.legacy.mission;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.mission.URoCommandMission;
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
public class URoUkitLegacyMainBoardUpgradeMission extends URoCommandMission {

    private static final int MAX_FRAME_SIZE = 100;

    private String path;
    private long startMs;
    private File file;
    private RandomAccessFile randomAccessFile;
    private long fileSize;

    private int frameCount;
    private int frameIndex = 0;

    public URoUkitLegacyMainBoardUpgradeMission(@NonNull URoProduct product, String path) {
        super(product);
        this.path = path;
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
        URoRequest request = new URoRequest(URoCommandConstants.CMD_BOARD_UPGRADE_ENTRY);
        request.setParameter("frameworkNameStr", filename);
        request.setParameter("frameworkSizeLong", fileSize);
        performNext(URoCommandConstants.CMD_BOARD_UPGRADE_ENTRY, request);
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
        if (URoCommandConstants.CMD_BOARD_UPGRADE_ENTRY.equals(identity)
                || URoCommandConstants.CMD_BOARD_UPGRADE_DATA.equals(identity)) {
            byte[] fileData = readDataFromFile((long)frameIndex * MAX_FRAME_SIZE, MAX_FRAME_SIZE);
            int lastPercent = (frameIndex * 100) / frameCount;
            frameIndex += 1;
            int percent = (frameIndex * 100) / frameCount;
            if(percent != lastPercent) {
                sendUpgradePercent(percent);
            }
            URoCommand cmd = frameIndex == frameCount ? URoCommandConstants.CMD_BOARD_UPGRADE_COMMIT : URoCommandConstants.CMD_BOARD_UPGRADE_DATA;
            URoRequest request = new URoRequest(cmd);
            request.setParameter("dataByteArray", fileData);
            request.setParameter("indexInt", frameIndex);
            performNext(cmd, request);
            resetTimeout(10000);
        } else if (URoCommandConstants.CMD_BOARD_UPGRADE_COMMIT.equals(identity)) {
            setupIntercept();
            resetTimeout(10000);
        }
    }

    @Override
    protected void onMissionCancelResult(URoCompletionResult result) throws Throwable {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_BOARD_UPGRADE_ABORT);
        sendRequestDirectly(request);
    }

    @Override
    protected URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
        if (URoCommandConstants.CMD_BOARD_UPGRADE_FLASH_BEGIN.equals(cmd)) {
            resetTimeout(30000);
        } else if (URoCommandConstants.CMD_BOARD_UPGRADE_FLASH_END.equals(cmd)) {
            if(response.isSuccess()) {
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
