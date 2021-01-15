package com.ubtedu.ukit.bluetooth.connect;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;

public class BluetoothReconnectTask implements URoConnectStatusChangeListener {
    private BluetoothReconnectTask() {
    }

    private static class Holder {
        private static final BluetoothReconnectTask INSTANCE = new BluetoothReconnectTask();
    }

    public static BluetoothReconnectTask getInstance() {
        return Holder.INSTANCE;
    }

    private final long CONNECT_TIMEOUT = 60000L;
    private final long CONNECT_DELAY = 5000L;
    private final long CONNECT_DELAY_FIRST_TIME_AFTER_REBOOT = 15000L;
    private long mStartConnectTime = 0L;

    private boolean mIsRebooting = false;

    public void reboot(IReconnectCallback reconnectCallback) {
        mIsRebooting = true;
        if (reconnectCallback != null) {
            mReconnectCallback = reconnectCallback;
            mReconnectCallback.onStartReconnect();
        }
        BluetoothHelper.addConnectStatusChangeListener(this);
        BluetoothHelper.getBtHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothHelper.addCommand(BtInvocationFactory.restartBoard(null));
            }
        }, 1000L);
    }

    private Runnable mConnectRunnable = new Runnable() {
        @Override
        public void run() {
            BluetoothHelper.connect(BluetoothHelper.getConnectDevice(), new URoCompletionCallback<Void>() {
                @Override
                public void onComplete(URoCompletionResult<Void> result) {
                    if (!result.isSuccess()) {
                        startConnect();
                    }
                }
            });
        }
    };

    private void startConnect() {
        if (mStartConnectTime == 0) {
            //重启后的第1次重连
            mStartConnectTime = System.currentTimeMillis();
            BluetoothHelper.getBtHandler().postDelayed(mConnectRunnable, CONNECT_DELAY_FIRST_TIME_AFTER_REBOOT);
        } else if (System.currentTimeMillis() - mStartConnectTime > CONNECT_TIMEOUT) {
            stopConnect(false);
        } else {
            //第1+次重连
            BluetoothHelper.getBtHandler().postDelayed(mConnectRunnable, CONNECT_DELAY);
        }
    }

    private void stopConnect(boolean isConnectSuccess) {
        if (mReconnectCallback != null) {
            mReconnectCallback.onReconnectComplete(isConnectSuccess);
            mReconnectCallback = null;
        }
        mIsRebooting = false;
        mStartConnectTime = 0L;
        BluetoothHelper.getBtHandler().removeCallbacks(mConnectRunnable);
        BluetoothHelper.removeConnectStatusChangeListener(this);
    }

    @Override
    public void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        if (connectStatus == URoConnectStatus.CONNECTED) {
            stopConnect(true);
        } else if (connectStatus == URoConnectStatus.DISCONNECTED) {
            //开始重连
            startConnect();
        }
    }

    public boolean isRebooting() {
        return mIsRebooting;
    }

    private IReconnectCallback mReconnectCallback;

    public interface IReconnectCallback {
        void onStartReconnect();

        void onReconnectComplete(boolean isSuccess);
    }

}
