package com.ubtedu.deviceconnect.libs.ukit.smart.mission;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.utils.URoIoUtils;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @Author naOKi
 * @Date 2019/09/17
 **/
public class URoUkitSmartSendFileMission extends URoUkitSmartSendMission {

    private RandomAccessFile randomAccessFile = null;
    private String source;
    private int crc32;
    private int fileLength;

    @Deprecated
    public URoUkitSmartSendFileMission(@NonNull URoProduct product, String source, String target) {
        this(product, source, target, null);
    }

    public URoUkitSmartSendFileMission(@NonNull URoProduct product, String source, String target, URoComponentType componentType) {
        super(product, target, componentType);
        this.source = source;
    }

    @Override
    protected void close() {
        URoIoUtils.close(randomAccessFile);
        randomAccessFile = null;
    }

    @Override
    protected int getCrc32() {
        return crc32;
    }

    @Override
    protected void prepare() throws Throwable {
        File file = new File(source);
        randomAccessFile = new RandomAccessFile(file, "r");
        fileLength = (int) file.length();
        crc32 = getCrc32(file);
    }

    @Override
    protected int getLength() {
        return fileLength;
    }

    @Override
    protected int readData(int sourceOffset, byte[] buffer, int targetOffset, int length) throws Throwable {
        randomAccessFile.seek(sourceOffset);
        randomAccessFile.readFully(buffer, targetOffset, length);
        return length;
    }

}
