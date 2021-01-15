package com.ubtedu.deviceconnect.libs.ukit.legacy.model;

import com.ubtedu.deviceconnect.libs.base.model.URoSerialNumberInfo;

import java.util.Arrays;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacySerialNumberInfo extends URoSerialNumberInfo {

    public URoUkitLegacySerialNumberInfo(byte[] bizData) {
        if(bizData != null && bizData.length > 1) {
            valid = true;
            serialNumber = new String(Arrays.copyOfRange(bizData, 1, bizData.length));
        }
    }

}
