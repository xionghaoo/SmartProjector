package com.ubtedu.deviceconnect.libs.ukit.legacy.link;

import android.bluetooth.BluetoothSocket;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.link.URoBluetoothSocketStreamInitiativeLink;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;

/**
 * @Author naOKi
 * @Date 2019/08/15
 **/
public class URoUkitLegacyStreamLink extends URoBluetoothSocketStreamInitiativeLink {

    public URoUkitLegacyStreamLink(@NonNull URoLinkModel model, @NonNull BluetoothSocket source) throws Exception {
        super(model, source);
    }

}
