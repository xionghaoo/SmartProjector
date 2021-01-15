package com.ubtedu.deviceconnect.libs.base.product.core.message;

import java.util.Arrays;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public abstract class URoBaseMessage {

    private final byte[] encodedData;

    public URoBaseMessage(byte[] encodedData) {
        if(encodedData == null) {
            this.encodedData = new byte[0];
        } else {
            this.encodedData = Arrays.copyOf(encodedData, encodedData.length);
        }
    }

    public byte[] data() {
        return encodedData;
    }

    public int length() {
        return encodedData.length;
    }
}
