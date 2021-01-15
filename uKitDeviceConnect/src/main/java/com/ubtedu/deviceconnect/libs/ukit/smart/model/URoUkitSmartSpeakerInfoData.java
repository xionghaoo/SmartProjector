package com.ubtedu.deviceconnect.libs.ukit.smart.model;

import com.ubtedu.deviceconnect.libs.base.model.URoSpeakerInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSpeakerGet;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitSmartSpeakerInfoData extends URoSpeakerInfo {

    public URoUkitSmartSpeakerInfoData(PbSpeakerGet.SpeakerGetResponse source) {
        super(null, null);
        StringBuilder sb = new StringBuilder();
        byte[] byteArray = source.getMac().toByteArray();
        for(int i = 0; i < byteArray.length; i++) {
            sb.append(String.format("%02X", byteArray[i] & 0xFF));
        }
        setMac(sb.toString());
        setName(source.getBtName());
    }

}
