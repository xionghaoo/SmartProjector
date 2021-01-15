package com.ubtedu.ukit.bluetooth;

import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;

public class UkitInvocation extends URoInvocation {
    boolean ignoreConflict = false;

    public UkitInvocation setIgnoreConflict(boolean ignoreConflict) {
        this.ignoreConflict = ignoreConflict;
        return this;
    }

    public UkitInvocation(String invocationName) {
        super(invocationName);
    }

    protected UkitInvocation(URoInvocation other) {
        super(other);
    }
}
