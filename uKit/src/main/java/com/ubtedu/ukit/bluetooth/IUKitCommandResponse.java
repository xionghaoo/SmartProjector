package com.ubtedu.ukit.bluetooth;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;

public abstract class IUKitCommandResponse<T> implements URoCompletionCallback {
    @Override
    public final void onComplete(URoCompletionResult result) {
        onUKitCommandResponse(result);
    }

    protected abstract void onUKitCommandResponse(URoCompletionResult<T> result);
}