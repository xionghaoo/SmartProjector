package com.ubtedu.deviceconnect.libs.ukit.legacy.connector;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.connector.URoBluetoothDevice;
import com.ubtedu.deviceconnect.libs.base.connector.URoConnectMission;
import com.ubtedu.deviceconnect.libs.ukit.connector.URoUkitBluetoothInfo;
import com.ubtedu.deviceconnect.libs.utils.URoIoUtils;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @Author naOKi
 * @Date 2019/08/15
 **/
public class URoUkitLegacyConnectMission extends URoConnectMission<URoUkitBluetoothInfo, BluetoothSocket> {

    private final int MAX_RETRY_COUNT = 3;
    private final long RETRY_DELAY = 2000L;
    private final long BOND_DELAY = 5000L;

    public URoUkitLegacyConnectMission(URoUkitBluetoothInfo connectItem) {
        super(connectItem);
    }

    public URoUkitLegacyConnectMission(URoUkitBluetoothInfo connectItem, @NonNull URoCompletionCallback<BluetoothSocket> callback) {
        super(connectItem, callback);
    }

    private boolean createBond(URoBluetoothDevice device) {
        if(device.getBondState() == BluetoothDevice.BOND_NONE) {
            try {
                Method createBond;
                boolean result;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    createBond = BluetoothDevice.class.getMethod("createBond", int.class);
                    result = (boolean) createBond.invoke(device.getBluetoothDevice(), BluetoothDevice.TRANSPORT_AUTO);
                } else {
                    createBond = BluetoothDevice.class.getMethod("createBond");
                    result = (boolean) createBond.invoke(device.getBluetoothDevice());
                }
                URoLogUtils.e(result ? "绑定请求发送成功" : "绑定请求发送失败");
                return result;
            } catch (Exception e) {
                URoLogUtils.e("绑定失败");
                URoLogUtils.e(e);
            }
        }
        return false;
    }

    @Override
    protected BluetoothSocket connect(URoUkitBluetoothInfo connectItem) throws Exception {
        URoBluetoothDevice device = connectItem.device;
        BluetoothSocket socket = URoUkitLegacyBtSocketFactory.createSocket(device.getBluetoothDevice());
        boolean isSuccess = false;
        for(int i = 0; i < MAX_RETRY_COUNT; i++) {
            do {
                if(isAbort() || isStopped()) {
                    break;
                }
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    URoLogUtils.e("正在绑定...");
                    Thread.sleep(500);
                    continue;
                }
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    URoLogUtils.e("开始绑定");
                    if(createBond(device)) {
                        Thread.sleep(500);
                    } else {
                        Thread.sleep(100);
                        continue;
                    }
                }
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    break;
                }
            } while (true);
            if(isAbort() || isStopped()) {
                break;
            }
            try {
                URoLogUtils.e("正在尝试第%s次连接", Integer.toString(i + 1));
                socket.connect();
                isSuccess = true;
                URoLogUtils.e("连接成功");
                break;
            } catch (IOException e) {
                Thread.sleep(RETRY_DELAY);
            }
        }
        if(!isSuccess || isAbort() || isStopped()) {
            socket.close();
        }
        return socket;
    }

    @Override
    protected void disconnect(BluetoothSocket connection) {
        URoIoUtils.close(connection);
    }

}
