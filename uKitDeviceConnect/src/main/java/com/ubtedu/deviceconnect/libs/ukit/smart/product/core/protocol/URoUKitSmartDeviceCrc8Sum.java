package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol;

import java.util.zip.Checksum;

/**
 * @Author naOKi
 * @Date 2019/07/01
 **/
public class URoUKitSmartDeviceCrc8Sum implements Checksum {

    private final int init;
    private final int polynomial;
    private int value;

    public URoUKitSmartDeviceCrc8Sum(int polynomial, int init) {
        this.value = init;
        this.init = init;
        this.polynomial = polynomial;
    }

    @Override
    public void update(int b) {
        update((byte) b);
    }

    private void update(final byte b) {
        value ^= b;
        for (int j = 0; j < 8; j++) {
            if ((value & 0x80) != 0) {
                value = ((value << 1) ^ polynomial);
            } else {
                value <<= 1;
            }
        }
        value &= 0xFF;
    }

    public void update(byte[] input) {
        update(input, 0, input.length);
    }

    @Override
    public void update(byte[] input, int offset, int len) {
        for (int i = 0; i < len; i++) {
            update(input[offset + i]);
        }
    }

    @Override
    public long getValue() {
        return (value & 0xFF);
    }

    @Override
    public void reset() {
        value = init;
    }

}
