package com.ubtedu.deviceconnect.libs.ukit.smart.link;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.link.URoBluetoothGattStreamInitiative;
import com.ubtedu.deviceconnect.libs.base.link.URoBluetoothGattStreamInitiativeLink;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.ukit.smart.connector.URoUkitSmartBleMgrDelegate;

/**
 * @Author naOKi
 * @Date 2019/08/15
 **/
public class URoUkitSmartBleLink extends URoBluetoothGattStreamInitiativeLink<URoUkitSmartBleMgrDelegate> {

    public URoUkitSmartBleLink(@NonNull URoLinkModel model, @NonNull URoUkitSmartBleMgrDelegate source) throws Exception {
        super(model, new URoBluetoothGattStreamInitiative<>(source));
    }

}
