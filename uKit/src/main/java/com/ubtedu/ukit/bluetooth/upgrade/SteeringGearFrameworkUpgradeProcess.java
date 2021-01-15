//package com.ubtedu.ukit.bluetooth.upgrade;
//
//import android.text.TextUtils;
//
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.UKitCommandRequest;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.UKitCommandResponse;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.UKitCommandType;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.steeringgear.upgrade.UKitCmdSteeringGearUpgradeAbort;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.steeringgear.upgrade.UKitCmdSteeringGearUpgradeData;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.steeringgear.upgrade.UKitCmdSteeringGearUpgradeEntry;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.device.UKitRemoteDevice;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.executor.IUKitCommandFilter;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.interfaces.IUKitDeviceConnectStateChangeListener;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.interfaces.IUKitDeviceMessageResponseListener;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.interfaces.IUKitDeviceUpgradeMsgListener;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.model.UKitBoardInfoData;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.model.UKitSensorHwData;
//import com.ubtedu.deviceconnect.libs.utils.LogUtils;
//import com.ubtedu.ukit.bluetooth.BluetoothHelper;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.RandomAccessFile;
//import java.util.zip.CRC32;
//
///**
// * @Author naOKi
// * @Date 2018/11/14
// **/
//public class SteeringGearFrameworkUpgradeProcess implements IUKitDeviceMessageResponseListener, IUKitDeviceConnectStateChangeListener {
//    private static final int MAX_FRAME_SIZE = 100;
//
//    private int id;
//    private final IUKitDeviceUpgradeMsgListener callback;
//    private File frameworkFile;
//    private final long fileSize;
//    private final int frameCount;
//    private int frameIndex = 0;
//    private RandomAccessFile randomAccessFile;
//
//    public final static int ERROR_NONE = 0;
//    public final static int ERROR_NO_BOARD_INFO = -1;
//    public final static int ERROR_READ_FILE_ERROR = -2;
//    public final static int ERROR_USER_ABORT = -3;
//    public final static int ERROR_OTHER_PROCESS_RUNNING = -4;
//    public final static int ERROR_ABNORMAL_ID = -5;
//    public final static int ERROR_NO_ID = -6;
//    public final static int ERROR_COMMAND_FAILURE = -7;
//    public final static int ERROR_TIMEOUT = -8;
//    public final static int ERROR_UNKNOWN = -9;
//
//    private static SteeringGearFrameworkUpgradeProcess instance = null;
//
//    private static final long SEND_TIMEOUT_THRESHOLD = 10000;
//    private static final long RESULT_TIMEOUT_THRESHOLD = 60000;
//
//    private Runnable TIMEOUT_CALLBACK = new Runnable() {
//        @Override
//        public void run() {
//            finish(ERROR_TIMEOUT, "升级超时");
//        }
//    };
//
//    public static boolean upgrade(String versionName, String fileName, IUKitDeviceUpgradeMsgListener callback) {
//        return upgrade(0, versionName, fileName, callback);
//    }
//
//    private static boolean upgrade(int id, String versionName, String fileName, IUKitDeviceUpgradeMsgListener callback) {
//        synchronized (SteeringGearFrameworkUpgradeProcess.class) {
//            if (callback == null) {
//                LogUtils.e("未实现回调方法");
//                return false;
//            }
//            if (instance != null) {
//                callback.onEndUpgrade(ERROR_OTHER_PROCESS_RUNNING, "正在升级中");
//                return false;
//            }
//            UKitBoardInfoData boardInfoData = BluetoothHelper.getBoardInfoData();
//            if (boardInfoData == null || boardInfoData.getSteeringGearHwData() == null) {
//                callback.onEndUpgrade(ERROR_NO_BOARD_INFO, "无法读取舵机版本信息");
//                return false;
//            }
//            UKitSensorHwData sensorHwData = boardInfoData.getSteeringGearHwData();
//            if (!sensorHwData.getAbnormalIds().isEmpty()) {
//                callback.onEndUpgrade(ERROR_ABNORMAL_ID, "存在异常舵机，无法升级");
//                return false;
//            }
//            if (sensorHwData.getAvailableIds().isEmpty() && sensorHwData.getConflictIds().isEmpty()) {
//                callback.onEndUpgrade(ERROR_NO_ID, "未检测到舵机");
//                return false;
//            }
//            if (TextUtils.equals(sensorHwData.getVersion(), versionName) && sensorHwData.getConflictIds().isEmpty()) {
//                callback.onEndUpgrade(ERROR_NONE, "已升级为相同版本");
//                return true;
//            }
//            File file = new File(fileName);
//            if (!file.exists() || !file.canRead() || file.length() == 0 || file.length() > (0xFFFF * MAX_FRAME_SIZE)) {
//                callback.onEndUpgrade(ERROR_READ_FILE_ERROR, "读取文件失败或文件大小不正确");
//                return false;
//            }
//            instance = new SteeringGearFrameworkUpgradeProcess(id, file, callback);
//            BluetoothHelper.registerConnectChanged(instance);
//            BluetoothHelper.addResponseMsgListener(UKitCommandType.CMD_STEERING_GEAR_UPGRADE_ENTRY, instance);
//            BluetoothHelper.addResponseMsgListener(UKitCommandType.CMD_STEERING_GEAR_UPGRADE_COMMIT, instance);
//            BluetoothHelper.addResponseMsgListener(UKitCommandType.CMD_STEERING_GEAR_UPGRADE_DATA, instance);
//            BluetoothHelper.addResponseMsgListener(UKitCommandType.CMD_STEERING_GEAR_UPGRADE_ABORT, instance);
//            BluetoothHelper.setExecutionFilter(new IUKitCommandFilter() {
//                @Override
//                public boolean accept(UKitCommandRequest cmd) {
//                    if (!UKitCommandType.CMD_STEERING_GEAR_UPGRADE_ENTRY.equals(cmd.cmdType())
//                            && !UKitCommandType.CMD_STEERING_GEAR_UPGRADE_COMMIT.equals(cmd.cmdType())
//                            && !UKitCommandType.CMD_STEERING_GEAR_UPGRADE_DATA.equals(cmd.cmdType())
//                            && !UKitCommandType.CMD_STEERING_GEAR_UPGRADE_ABORT.equals(cmd.cmdType())
//                            && !UKitCommandType.CMD_BOARD_HEARTBEAT.equals(cmd.cmdType())) {
//                        return false;
//                    }
//                    return true;
//                }
//            });
//            instance.beginTransaction();
//            return true;
//        }
//    }
//
//    private SteeringGearFrameworkUpgradeProcess(int id, File file, IUKitDeviceUpgradeMsgListener callback) {
//        this.id = id;
//        this.callback = callback;
//        this.frameworkFile = file;
//        try {
//            randomAccessFile = new RandomAccessFile(file, "r");
//        } catch (Exception e) {
//            // ignore
//        }
//        fileSize = file.length();
//        frameCount = (int)((fileSize + MAX_FRAME_SIZE - 1) / MAX_FRAME_SIZE);
//    }
//
//    private void prepareTimeout(long timeMs) {
//        resetTimeout();
//        timeMs = Math.max(1000, Math.min(120000, timeMs));
//        BluetoothHelper.getBtHandler().postDelayed(TIMEOUT_CALLBACK, timeMs);
//    }
//
//    private void resetTimeout() {
//        BluetoothHelper.getBtHandler().removeCallbacks(TIMEOUT_CALLBACK);
//    }
//
//    private void finish(int code, String msg) {
//        synchronized (SteeringGearFrameworkUpgradeProcess.class) {
//            if(instance == null) {
//                return;
//            }
//            if(code != 0) {
//                BluetoothHelper.addCommand(UKitCmdSteeringGearUpgradeAbort.newInstance());
//            }
//            resetTimeout();
//            try {
//                if (randomAccessFile != null) {
//                    randomAccessFile.close();
//                    randomAccessFile = null;
//                }
//            } catch (Exception e) {
//                // ignore
//            }
//            BluetoothHelper.unRegisterConnectChanged(instance);
//            BluetoothHelper.removeResponseMsgListener(UKitCommandType.CMD_STEERING_GEAR_UPGRADE_ENTRY, this);
//            BluetoothHelper.removeResponseMsgListener(UKitCommandType.CMD_STEERING_GEAR_UPGRADE_COMMIT, this);
//            BluetoothHelper.removeResponseMsgListener(UKitCommandType.CMD_STEERING_GEAR_UPGRADE_DATA, this);
//            BluetoothHelper.removeResponseMsgListener(UKitCommandType.CMD_STEERING_GEAR_UPGRADE_ABORT, this);
//            BluetoothHelper.cleanupExecutionFilter();
//            callback.onEndUpgrade(code, msg);
//            instance = null;
//        }
//    }
//
//    private int getFileCrc32() {
//        CRC32 crc32 = new CRC32();
//        FileInputStream fis = null;
//        try {
//            byte[] buffer = new byte[8 * 1024];
//            int readLen;
//            fis = new FileInputStream(frameworkFile);
//            while((readLen = fis.read(buffer, 0, buffer.length)) > 0) {
//                crc32.update(buffer, 0, readLen);
//            }
//            int frameSize = 64;//坑货64字节对齐！！！
//            int frameCount = (int)((fileSize + frameSize - 1) / frameSize);
//            int paddingSize = (int)((frameSize * frameCount) - fileSize);
//            while(paddingSize-- > 0) {
//                crc32.update(0);
//            }
//        } catch (Exception e) {
//            // ignore
//        } finally {
//            if(fis != null) {
//                try {
//                    fis.close();
//                } catch (Exception e) {
//                    // ignore
//                }
//            }
//        }
//        return (int)(~crc32.getValue());//坑货要取反！！！
//    }
//
//    private void beginTransaction() {
//        callback.onBeginUpgrade();
//        callback.onBeginTransmission();
//        int crc32 = getFileCrc32();
//        UKitCommandRequest cmd = UKitCmdSteeringGearUpgradeEntry.newInstance(id, fileSize, crc32);
//        BluetoothHelper.addCommand(cmd);
//        prepareTimeout(SEND_TIMEOUT_THRESHOLD);
//    }
//
//    private byte[] readDataFromFile(long offset, long length) {
//        if(randomAccessFile == null || offset >= fileSize) {
//            return null;
//        }
//        try {
//            int dataLength = (int)Math.min(length, fileSize - offset);
//            byte[] result = new byte[dataLength];
//            randomAccessFile.seek(offset);
//            randomAccessFile.read(result);
//            return result;
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    private void nextFrame() {
//        byte[] fileData = readDataFromFile((long)frameIndex * MAX_FRAME_SIZE, MAX_FRAME_SIZE);
//        int lastPercent = (frameIndex * 100) / frameCount;
//        frameIndex += 1;
//        int percent = (frameIndex * 100) / frameCount;
//        UKitCommandRequest cmd = UKitCmdSteeringGearUpgradeData.newInstance(fileData, frameIndex, frameIndex == frameCount);
//        BluetoothHelper.addCommand(cmd);
//        if(percent != lastPercent) {
//            callback.onPercentChanged(percent);
//        }
//    }
//
//    @Override
//    public void onResponseMsgReceived(UKitRemoteDevice device, UKitCommandType cmdType, UKitCommandResponse response) {
//        if (!UKitCommandType.CMD_STEERING_GEAR_UPGRADE_ENTRY.equals(cmdType)
//                && !UKitCommandType.CMD_STEERING_GEAR_UPGRADE_COMMIT.equals(cmdType)
//                && !UKitCommandType.CMD_STEERING_GEAR_UPGRADE_DATA.equals(cmdType)
//                && !UKitCommandType.CMD_STEERING_GEAR_UPGRADE_ABORT.equals(cmdType)) {
//            return;
//        }
//        if(!response.isSuccess()) {
//            if(UKitCommandType.CMD_STEERING_GEAR_UPGRADE_COMMIT.equals(cmdType)) {
//                callback.onEndWriteFlash();
//                if(response.getBizData() != null && response.getBizData()[0] == (byte)0xAA) {
//                    finish(ERROR_NONE, "升级成功");
//                } else {
//                    finish(ERROR_UNKNOWN, "升级失败");
//                }
//            } else {
//                if (UKitCommandType.CMD_STEERING_GEAR_UPGRADE_ABORT.equals(cmdType)) {
//                    LogUtils.e("取消升级失败");
//                } else {
//                    finish(ERROR_UNKNOWN, "升级失败");
//                }
//            }
//            return;
//        }
//        switch (cmdType) {
//        case CMD_STEERING_GEAR_UPGRADE_COMMIT:
//            resetTimeout();
//            callback.onEndTransmission();
//            callback.onBeginWriteFlash();
//            prepareTimeout(RESULT_TIMEOUT_THRESHOLD);
//            break;
//        case CMD_STEERING_GEAR_UPGRADE_ENTRY:
//        case CMD_STEERING_GEAR_UPGRADE_DATA:
//            resetTimeout();
//            nextFrame();
//            prepareTimeout(SEND_TIMEOUT_THRESHOLD);
//            break;
//        case CMD_STEERING_GEAR_UPGRADE_ABORT:
//            finish(ERROR_USER_ABORT, "用户取消升级");
//            break;
//        }
//    }
//
//    @Override
//    public void onConnectStateChanged(UKitRemoteDevice device, boolean oldState, boolean newState, boolean verified) {
//        if(!newState) {
//            finish(ERROR_COMMAND_FAILURE, "升级失败");
//        }
//    }
//
//}
