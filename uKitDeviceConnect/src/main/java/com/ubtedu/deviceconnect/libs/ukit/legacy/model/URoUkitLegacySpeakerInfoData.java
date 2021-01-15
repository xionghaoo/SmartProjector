package com.ubtedu.deviceconnect.libs.ukit.legacy.model;

import com.ubtedu.deviceconnect.libs.base.model.URoSpeakerInfo;

import java.util.Arrays;
import java.util.Locale;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacySpeakerInfoData extends URoSpeakerInfo {

    public URoUkitLegacySpeakerInfoData(byte[] bizData) {
        super(null, null);
        byte[] mac = Arrays.copyOfRange(bizData, 3, 9);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append(":");
            }
            sb.append(String.format(Locale.US, "%02X", mac[i]));
        }
        setMac(sb.toString());
        setName(new String(Arrays.copyOfRange(bizData, 9, bizData.length)));
    }

}
