package com.ubtedu.deviceconnect.libs.ukit.smart.model;

import com.ubtedu.deviceconnect.libs.base.model.URoSerialNumberInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSnGet;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitSmartSerialNumberInfo extends URoSerialNumberInfo {

    public URoUkitSmartSerialNumberInfo(PbSnGet.SnGetResponse source) {
        if(source != null) {
            valid = true;
//            StringBuilder sb = new StringBuilder();
            byte[] byteArray = source.getSn().toByteArray();
//            for(int i = 0; i < byteArray.length; i++) {
//                sb.append(String.format("%02X", byteArray[i] & 0xFF));
//            }
//            serialNumber = sb.toString();
            serialNumber = new String(byteArray);
        }
    }

}
