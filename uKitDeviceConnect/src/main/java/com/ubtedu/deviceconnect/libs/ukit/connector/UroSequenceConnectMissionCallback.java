package com.ubtedu.deviceconnect.libs.ukit.connector;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoMissionCallback;

import java.util.ArrayList;

abstract public class UroSequenceConnectMissionCallback<T> extends URoMissionCallback<ArrayList<URoCompletionResult<T>>> {
    public void onStepResponse(int step, URoCompletionResult<T> result){};
}
