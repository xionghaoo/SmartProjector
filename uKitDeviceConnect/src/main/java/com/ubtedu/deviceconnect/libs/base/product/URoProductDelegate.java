package com.ubtedu.deviceconnect.libs.base.product;

import com.ubtedu.deviceconnect.libs.base.interfaces.URoBatteryChangeListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoComponentChangeListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoComponentErrorListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoPushMessageReceivedListener;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public abstract class URoProductDelegate implements
        URoBatteryChangeListener,
        URoComponentErrorListener,
        URoComponentChangeListener,
        URoConnectStatusChangeListener,
        URoPushMessageReceivedListener {
}
