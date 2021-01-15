package com.ubtedu.deviceconnect.libs.ukit.legacy.link;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.link.URoBluetoothGattStreamInitiative;
import com.ubtedu.deviceconnect.libs.base.link.URoBluetoothGattStreamInitiativeLink;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.ukit.legacy.connector.URoUkitLegacyBleMgrDelegate;

/**
 * @Author naOKi
 * @Date 2019/08/15
 **/
public class URoUkitLegacyBleLink extends URoBluetoothGattStreamInitiativeLink<URoUkitLegacyBleMgrDelegate> {

    public URoUkitLegacyBleLink(@NonNull URoLinkModel model, @NonNull URoUkitLegacyBleMgrDelegate source) throws Exception {
        super(model, new URoBluetoothGattStreamInitiative<>(source));
    }

}
