package com.ubtedu.deviceconnect.libs.base.product.core.queue;

import com.ubtedu.deviceconnect.libs.utils.URoNumberConversionUtil;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @Author naOKi
 * @Date 2019/02/20
 **/
public class URoUkitIdHelper {

    private URoUkitIdHelper() {}

    public static ArrayList<Integer> bytesToIds(byte... idBytes) {
        ArrayList<Integer> result = new ArrayList<>();
        if (idBytes == null || idBytes.length > 4) {
            return result;
        }
        StringBuilder sb = new StringBuilder();
        for (byte idByte : idBytes) {
            sb.append(String.format(Locale.US, "%02X", idByte));
        }
        int intValue = URoNumberConversionUtil.hex2Integer(sb.toString(), 16, 0);
        for (int i = 1; i <= 32; i++) {
            if ((intValue & (1 << (i - 1))) != 0) {
                result.add(i);
            }
        }
        return result;
    }

}
