package com.ubtedu.deviceconnect.libs.ukit.connector;

import android.util.Log;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.mission.URoMission;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.UroProductInitDelegate;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class UroUkitBluetoothSequenceConnectMission<T> extends URoMission<ArrayList<URoCompletionResult<T>>> implements UroProductInitDelegate, URoCompletionCallback<T> {

    private ArrayList<URoUkitBluetoothInfo> mDevices;
    private long mTimeout;
    private URoInvocation mInvocation;
    private ArrayList<URoCompletionResult<T>> mResultList;
    private UroSequenceConnectMissionCallback mMissionCallback;
    private URoProduct mProduct;
    private static final String TAG = "uro_batch_wifi";

    @Override
    protected void onMissionStart() throws Throwable {
        if (mDevices.isEmpty()) {
            return;
        }
        performNext();
    }

    @Override
    protected void onPreviousResult(URoCompletionResult result) throws Throwable {
    }


    public UroUkitBluetoothSequenceConnectMission(ArrayList<URoUkitBluetoothInfo> devices, long timeout, URoInvocation invocation, UroSequenceConnectMissionCallback callback) {
        mDevices = new ArrayList<>();
        mResultList = new ArrayList<>();
        mDevices.addAll(devices);
        setTotalStep(mDevices.size());
        mMissionCallback = callback;
        setCallback(callback);
        mTimeout = timeout;
        mInvocation = invocation;
        mInvocation.setCompletionCallback(this);
    }

    private void startConnect(URoUkitBluetoothInfo device) {
        URoUkitBluetoothConnector.getInstance().connect(device, mTimeout, new URoCompletionCallback<Void>() {
            @Override
            public void onComplete(URoCompletionResult<Void> result) {
                if (!result.isSuccess()) {
                    URoCompletionResult<T> completionResult = new URoCompletionResult<>(null, URoError.NOT_CONNECTED);
                    executeNext(completionResult);
                }
            }
        }, this);
    }

    @Override
    public void onProductInit(URoProduct product) {
        mProduct = product;
        if (!mInvocation.sendToTarget(product)) {
            Log.i(TAG, "invocation send failed:");
            URoCompletionResult<T> completionResult = new URoCompletionResult<>(null, URoError.UNKNOWN);
            executeNext(completionResult);
        }
    }


    @Override
    public void onComplete(URoCompletionResult<T> result) {
        executeNext(result);
    }

    private void executeNext(URoCompletionResult<T> result) {
        Log.i(TAG, "result:" + result.isSuccess() + "  " + result.getError().domain);
        mResultList.add(result);
        if (mProduct != null) {
            mProduct.disconnect();
            mProduct = null;
        }
        int currentPercent = (int) ((float) (getCurrentStep() + 1) / (float) mDevices.size() * 100f);
        URoCompletionCallbackHelper.sendProcessPercentCallback(currentPercent, getCallback());
        Log.i(TAG, "currentPercent" + currentPercent);
        if (mMissionCallback != null) {
            mMissionCallback.onStepResponse(getCurrentStep(), result);
        }
        if (isStopped()){
            return;
        }
        if (getCurrentStep() < mDevices.size() - 1) {
            performNext();
        } else {
            notifyComplete(new ArrayList<>(mResultList));
        }
    }

    private void performNext() {
        performNext(new Callable<URoCompletionResult<Void>>() {
            @Override
            public URoCompletionResult<Void> call() throws Exception {
                startConnect(mDevices.get(getCurrentStep()));
                return new URoCompletionResult<Void>(null, URoError.SUCCESS);
            }
        });
    }

    @Override
    protected void onMissionRelease() {
        mProduct = null;
        mInvocation = null;
        mDevices.clear();
        mResultList.clear();
        mMissionCallback = null;
    }
}
