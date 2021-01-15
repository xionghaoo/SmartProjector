package com.ubtedu.deviceconnect.libs.ukit.smart.mission;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2019/09/17
 **/
public class URoUkitSmartSendDataMission extends URoUkitSmartSendMission {

    private byte[] data;
    private int crc32;
    private int dataLength;

    @Deprecated
    public URoUkitSmartSendDataMission(@NonNull URoProduct product, byte[] data, String target) {
        this(product, data, target, null);
    }

    public URoUkitSmartSendDataMission(@NonNull URoProduct product, byte[] data, String target, URoComponentType componentType) {
        super(product, target, componentType);
        this.data = data;
    }

    @Override
    protected void close() {
        data = null;
    }

    @Override
    protected int getCrc32() {
        return crc32;
    }

    @Override
    protected void prepare() throws Throwable {
        dataLength = data.length;
        crc32 = getCrc32(data);
    }

    @Override
    protected int getLength() {
        return dataLength;
    }

    @Override
    protected int readData(int sourceOffset, byte[] buffer, int targetOffset, int length) throws Throwable {
        System.arraycopy(data, sourceOffset, buffer, targetOffset, length);
        return length;
    }

}
