package com.ubtedu.ukit.bluetooth;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequestPriority;

public class UKitCommandPriority {
    public static final UKitCommandPriority HIGH = new UKitCommandPriority(URoRequestPriority.HIGH);
    public static final UKitCommandPriority MIDDLE = new UKitCommandPriority(URoRequestPriority.MIDDLE);
    public static final UKitCommandPriority NORMAL = new UKitCommandPriority(URoRequestPriority.NORMAL);
    public static final UKitCommandPriority LOW = new UKitCommandPriority(URoRequestPriority.LOW);

    private URoRequestPriority mCommandPriority;

    public URoRequestPriority getUroCommandPriority() {
        return mCommandPriority;
    }

    private UKitCommandPriority(URoRequestPriority commandPriority) {
        this.mCommandPriority = commandPriority;
    }
}
