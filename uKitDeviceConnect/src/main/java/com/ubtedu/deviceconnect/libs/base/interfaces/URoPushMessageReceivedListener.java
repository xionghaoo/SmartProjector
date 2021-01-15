package com.ubtedu.deviceconnect.libs.base.interfaces;

import com.ubtedu.deviceconnect.libs.base.model.event.URoPushMessageData;
import com.ubtedu.deviceconnect.libs.base.model.event.URoPushMessageType;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public interface URoPushMessageReceivedListener {
    void onPushMessageReceived(URoProduct product, URoPushMessageType type, int subType, URoPushMessageData data);
}
