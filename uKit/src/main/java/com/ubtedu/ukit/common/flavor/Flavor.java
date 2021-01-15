/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.flavor;

import androidx.annotation.NonNull;

import com.ubtedu.alpha1x.utils.AppUtil;
import com.ubtedu.ukit.application.UKitApplication;


/**
 * @Author qinicy
 * @Date 2018/12/17
 **/
public class Flavor {
    public final static String META_DATA_CHANNEL = "CHANNEL";
    private static Channel sChannel;

    @NonNull
    public static Channel getChannel() {
        if (sChannel == null) {
            String channelStr = AppUtil.getApplicationMetaData(UKitApplication.getInstance(), META_DATA_CHANNEL);
            if (channelStr == null) {
                channelStr = Channel.ubtedu.name();
            }
            sChannel = Channel.fromName(channelStr);
        }
        return sChannel;
    }
}
