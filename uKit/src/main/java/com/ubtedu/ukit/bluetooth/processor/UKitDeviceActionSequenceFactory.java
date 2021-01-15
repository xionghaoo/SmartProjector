///**
// * @2018 UBTECH Robotics Corp. All rights reserved (C)
// **/
//package com.ubtedu.ukit.bluetooth.processor;
//
//import android.bluetooth.BluetoothDevice;
//
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.UKitCommandRequest;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.UKitCommandResponse;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.UKitCommandType;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.board.UKitCmdBoardHandshake;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.board.UKitCmdBoardInfo;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.steeringgear.UKitCmdSteeringGearAngle;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.device.UKitRemoteDevice;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.executor.UKitCommandPriority;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.interfaces.IUKitDeviceMessageResponseListener;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.smart.command.protobuffer.S105FileRecvMaxPackSize;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.smart.command.protobuffer.S106FileRecvStart;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.smart.command.protobuffer.S107FileRecving;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.smart.command.protobuffer.S108FileRecvOver;
//import com.ubtedu.deviceconnect.libs.utils.IoUtils;
//import com.ubtedu.deviceconnect.libs.utils.LogUtils;
//import com.ubtedu.ukit.bluetooth.BluetoothHelper;
//import com.ubtedu.ukit.bluetooth.utils.BtLogUtils;
//import com.ubtedu.ukit.project.vo.Motion;
//import com.ubtedu.ukit.project.vo.MotionFrame;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.io.RandomAccessFile;
//import java.util.List;
//import java.util.zip.CRC32;
//
///**
// * @Author naOKi
// * @Date 2018/11/27
// **/
//public class UKitDeviceActionSequenceFactory {
//
//    private static UKitDeviceActionSequence lastStopActionSequence = null;
//
//    private UKitDeviceActionSequenceFactory() {}
//
//    public static UKitDeviceActionSequence createConnectActionSequence(BluetoothDevice device) {
//        return createConnectActionSequence(device, 0, null);
//    }
//
//    public static UKitDeviceActionSequence createConnectActionSequence(BluetoothDevice device, IActionSequenceCallback callback) {
//        return createConnectActionSequence(device, 0, callback);
//    }
//
//    public static UKitDeviceActionSequence createConnectActionSequence(BluetoothDevice device, long delayMs) {
//        return createConnectActionSequence(device, delayMs, null);
//    }
//
////    public static UKitDeviceActionSequence createConnectActionSequence(BluetoothDevice device, long delayMs, IActionSequenceCallback callback) {
////        UKitDeviceActionSequence actionSequence = UKitDeviceActionSequence.newActionSequence();
////        if(delayMs > 0) {
////            actionSequence.sleep(delayMs);
////        }
////        actionSequence
////            .connect(device)
////            .command(UKitCmdBoardBattery.newInstance())
////            .add(createRefreshBoardInfoActionSequence())
////            .callback(callback);
////        return actionSequence;
////    }
//
//    private static int getCrc32(byte[] data) {
//        int result = 0;
//        ByteArrayInputStream bais = null;
//        try {
//            bais = new ByteArrayInputStream(data);
//            result = getCrc32(bais);
//        } catch (Exception e) {
//            // ignore
//        } finally {
//            IoUtils.close(bais);
//        }
//        return result;
//    }
//
//    private static int getCrc32(File file) {
//        int result = 0;
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(file);
//            result = getCrc32(fis);
//        } catch (Exception e) {
//            // ignore
//        } finally {
//            IoUtils.close(fis);
//        }
//        return result;
//    }
//
//    private static int getCrc32(InputStream is) {
//        CRC32 crc32 = new CRC32();
//        try {
//            byte[] buffer = new byte[8 * 1024];
//            int readLen;
//            while((readLen = is.read(buffer, 0, buffer.length)) > 0) {
//                crc32.update(buffer, 0, readLen);
//            }
//        } catch (Exception e) {
//            // ignore
//        }
//        return (int)crc32.getValue();
//    }
//
//    public static UKitDeviceActionSequence createConnectActionSequence(BluetoothDevice device, long delayMs, IActionSequenceCallback callback) {
////        return createConnectActionSequenceSend(device, delayMs, callback);
////        return createConnectActionSequenceRecv(device, delayMs, callback);
////        return createConnectActionSequenceDelete(device, delayMs, callback);
////        return createConnectActionSequenceRename(device, delayMs, callback);
//        return createConnectActionSequenceState(device, delayMs, callback);
////        return createConnectActionSequenceDir(device, delayMs, callback);
//    }
//
//    public static UKitDeviceActionSequence createConnectActionSequenceDelete(BluetoothDevice device, long delayMs, IActionSequenceCallback callback) {
//        UKitDeviceActionSequence sequence = createConnectActionSequenceSend(device, delayMs, callback);
//        IActionSequenceCallback callbackWarp = new IActionSequenceCallback() {
//            IActionSequenceCallback _callback = callback;
//            @Override
//            public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                if(isSuccess) {
//                    UKitCommandRequest request = new UKitCommandRequest(UKitCommandType.CMD_FILE_DEL);
//                    request.setParam("path", "12.py");
//                    BluetoothHelper.addCommand(request);
//                }
//                if(_callback != null) {
//                    _callback.onActionSequenceFinished(bas, isSuccess);
//                }
//            }
//        };
//        sequence.callback(callbackWarp);
//        return sequence;
//    }
//
//    public static UKitDeviceActionSequence createConnectActionSequenceRename(BluetoothDevice device, long delayMs, IActionSequenceCallback callback) {
//        UKitDeviceActionSequence sequence = createConnectActionSequenceSend(device, delayMs, callback);
//        IActionSequenceCallback callbackWarp = new IActionSequenceCallback() {
//            IActionSequenceCallback _callback = callback;
//            @Override
//            public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                if(isSuccess) {
//                    UKitCommandRequest request = new UKitCommandRequest(UKitCommandType.CMD_FILE_RENAME);
//                    request.setParam("old_name", "12.py");
//                    request.setParam("new_name", "21.py");
//                    BluetoothHelper.addCommand(request);
//                }
//                if(_callback != null) {
//                    _callback.onActionSequenceFinished(bas, isSuccess);
//                }
//            }
//        };
//        sequence.callback(callbackWarp);
//        return sequence;
//    }
//
//    public static UKitDeviceActionSequence createConnectActionSequenceState(BluetoothDevice device, long delayMs, IActionSequenceCallback callback) {
//        UKitDeviceActionSequence sequence = createConnectActionSequenceSend(device, delayMs, callback);
//        IActionSequenceCallback callbackWarp = new IActionSequenceCallback() {
//            IActionSequenceCallback _callback = callback;
//            @Override
//            public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                if(isSuccess) {
//                    UKitCommandRequest request = new UKitCommandRequest(UKitCommandType.CMD_FILE_STATE);
//                    request.setParam("path", "12.py");
//                    BluetoothHelper.addCommand(request);
//                }
//                if(_callback != null) {
//                    _callback.onActionSequenceFinished(bas, isSuccess);
//                }
//            }
//        };
//        sequence.callback(callbackWarp);
//        return sequence;
//    }
//
//    public static UKitDeviceActionSequence createConnectActionSequenceDir(BluetoothDevice device, long delayMs, IActionSequenceCallback callback) {
//        UKitDeviceActionSequence actionSequence = UKitDeviceActionSequence.newActionSequence();
//        if(delayMs > 0) {
//            actionSequence.sleep(delayMs);
//        }
//        actionSequence
//                .connect(device)
//                .callback(callback);
//        UKitCommandRequest request;
//        request = new UKitCommandRequest(UKitCommandType.CMD_DIR_MAKE);
//        request.setParam("path", "abc1");
//        actionSequence.command(request);
//        request = new UKitCommandRequest(UKitCommandType.CMD_DIR_DEL);
//        request.setParam("path", "abc1");
//        actionSequence.command(request);
//        request = new UKitCommandRequest(UKitCommandType.CMD_DIR_MAKE);
//        request.setParam("path", "abc1");
//        actionSequence.command(request);
//        request = new UKitCommandRequest(UKitCommandType.CMD_DIR_MAKE);
//        request.setParam("path", "abc1/123");
//        actionSequence.command(request);
//        request = new UKitCommandRequest(UKitCommandType.CMD_DIR_LIST_INFO);
//        request.setParam("path", "abc1");
//        actionSequence.command(request);
//        request = new UKitCommandRequest(UKitCommandType.CMD_DIR_GET_INFO);
//        request.setParam("path", "abc1");
//        request.setParam("file_idx", 1);
//        actionSequence.command(request);
//        request = new UKitCommandRequest(UKitCommandType.CMD_DIR_DEL);
//        request.setParam("path", "abc1/123");
//        actionSequence.command(request);
//        request = new UKitCommandRequest(UKitCommandType.CMD_DIR_DEL);
//        request.setParam("path", "abc1");
//        actionSequence.command(request);
//        return actionSequence;
//    }
//
//    public static UKitDeviceActionSequence createConnectActionSequenceSend(BluetoothDevice device, long delayMs, IActionSequenceCallback callback) {
//        UKitDeviceActionSequence actionSequence = UKitDeviceActionSequence.newActionSequence();
//        if(delayMs > 0) {
//            actionSequence.sleep(delayMs);
//        }
//        actionSequence
//                .connect(device)
//                .callback(callback);
//        File file = new File("/sdcard/send.py");
//        if(file.exists()) {
//            UKitCommandRequest request;
//            request = new UKitCommandRequest(UKitCommandType.CMD_FILE_SEND_MAX_PACK_SIZE);
//            actionSequence.command(request);
//            int maxPackageSize = 256;
//            RandomAccessFile randomAccessFile = null;
//            try {
//                randomAccessFile = new RandomAccessFile(file, "r");
//                int fileLength = (int) file.length();
//                int pkgNum = (int) ((fileLength + maxPackageSize - 1) / maxPackageSize);
//                BtLogUtils.e("pkgNum: %d", pkgNum);
//                request = new UKitCommandRequest(UKitCommandType.CMD_FILE_SEND_START);
//                request.setParam("path", "12.py");
//                request.setParam("pack_num", pkgNum);
//                actionSequence.command(request);
//                for (int i = 0; i < pkgNum; i++) {
//                    request = new UKitCommandRequest(UKitCommandType.CMD_FILE_SENDING);
//                    request.setParam("pack_idx", i + 1);
//                    int offset = i * maxPackageSize;
//                    int size = Math.min(maxPackageSize, fileLength - offset);
//                    byte[] data = new byte[size];
//                    randomAccessFile.seek(offset);
//                    randomAccessFile.readFully(data);
//                    request.setParam("data", data);
//                    actionSequence.command(request);
//                }
//                request = new UKitCommandRequest(UKitCommandType.CMD_FILE_SEND_OVER);
//                request.setParam("file_size", fileLength);
//                request.setParam("crc32", getCrc32(file));
//                actionSequence.command(request);
//            } catch (Exception e) {
//                BtLogUtils.e(e);
//            } finally {
//                IoUtils.close(randomAccessFile);
//            }
//        }
//        return actionSequence;
//    }
//
//    private static class RecvHelper implements IUKitDeviceMessageResponseListener {
//        private String remoteFile;
//        private String localFile;
//        private int maxPackageSize = 256;
//        private int pack_num = 0;
//        private int pack_idx = 0;
//        private int file_size = 0;
//        private int crc32 = 0;
//        private ByteArrayOutputStream baos = null;
//
//        private Runnable timeout = new Runnable() {
//            @Override
//            public void run() {
//                LogUtils.e("Timeout");
//                finish(false);
//            }
//        };
//
//        public RecvHelper(String remoteFile, String localFile) {
//            this.remoteFile = remoteFile;
//            this.localFile = localFile;
//        }
//
//        private void resetTimeout() {
//            BluetoothHelper.getBtHandler().removeCallbacks(timeout);
//            BluetoothHelper.getBtHandler().postDelayed(timeout, 5000);
//        }
//
//        private void finish(boolean isDone) {
//            try {
//                BluetoothHelper.removeResponseMsgListener(UKitCommandType.CMD_FILE_RECV_MAX_PACK_SIZE, this);
//                BluetoothHelper.removeResponseMsgListener(UKitCommandType.CMD_FILE_RECV_START, this);
//                BluetoothHelper.removeResponseMsgListener(UKitCommandType.CMD_FILE_RECV_STOP, this);
//                BluetoothHelper.removeResponseMsgListener(UKitCommandType.CMD_FILE_RECVING, this);
//                BluetoothHelper.removeResponseMsgListener(UKitCommandType.CMD_FILE_RECV_OVER, this);
//                BluetoothHelper.getBtHandler().removeCallbacks(timeout);
//                if (isDone) {
//                    byte[] data = baos.toByteArray();
//                    if(data.length != file_size) {
//                        LogUtils.e("Length not match!");
//                        return;
//                    }
//                    if(getCrc32(data) != crc32) {
//                        LogUtils.e("Crc32 not match!");
//                        return;
//                    }
//                    LogUtils.e("Recv Success!");
//                }
//            } catch (Exception e) {
//                LogUtils.e(e);
//            } finally {
//                IoUtils.close(baos);
//                baos = null;
//            }
//        }
//
//        @Override
//        public void onResponseMsgReceived(UKitRemoteDevice device, UKitCommandType cmdType, UKitCommandResponse response) {
//            if(!response.isSuccess()) {
//                LogUtils.e("Recv failure [%s]", cmdType.name());
//                finish(false);
//                return;
//            }
//            try {
//                UKitCommandRequest request;
//                switch (cmdType) {
//                case CMD_FILE_RECV_MAX_PACK_SIZE:
//                    S105FileRecvMaxPackSize.S105FileRecvMaxPackSizeResponse response1 = (S105FileRecvMaxPackSize.S105FileRecvMaxPackSizeResponse) response.getData();
//                    maxPackageSize = response1.getMaxPackSz();
//                    request = new UKitCommandRequest(UKitCommandType.CMD_FILE_RECV_START);
//                    request.setParam("path", remoteFile);
//                    BluetoothHelper.addCommand(request);
//                    break;
//                case CMD_FILE_RECV_START:
//                case CMD_FILE_RECVING:
//                    if (cmdType.equals(UKitCommandType.CMD_FILE_RECV_START)) {
//                        S106FileRecvStart.S106FileRecvStartResponse response2 = (S106FileRecvStart.S106FileRecvStartResponse) response.getData();
//                        pack_num = response2.getPackNum();
//                    } else if (cmdType.equals(UKitCommandType.CMD_FILE_RECVING)) {
//                        S107FileRecving.S107FileRecvingResponse response3 = (S107FileRecving.S107FileRecvingResponse) response.getData();
//                        pack_idx = response3.getPackIdx();
//                        baos.write(response3.getData().toByteArray());
//                    }
//                    if (pack_idx < pack_num) {
//                        request = new UKitCommandRequest(UKitCommandType.CMD_FILE_RECVING);
//                        request.setParam("pack_idx", pack_idx + 1);
//                        BluetoothHelper.addCommand(request);
//                    } else if (pack_idx == pack_num) {
//                        request = new UKitCommandRequest(UKitCommandType.CMD_FILE_RECV_OVER);
//                        BluetoothHelper.addCommand(request);
//                    }
//                    break;
//                case CMD_FILE_RECV_OVER:
//                    S108FileRecvOver.S108FileRecvOverResponse response4 = (S108FileRecvOver.S108FileRecvOverResponse) response.getData();
//                    file_size = response4.getFileSize();
//                    crc32 = response4.getCrc32();
//                    finish(true);
//                    break;
//                }
//            } catch (Exception e) {
//                LogUtils.e(e);
//                finish(false);
//            }
//        }
//
//        public void start() {
//            baos = new ByteArrayOutputStream();
//            BluetoothHelper.addResponseMsgListener(UKitCommandType.CMD_FILE_RECV_MAX_PACK_SIZE, this);
//            BluetoothHelper.addResponseMsgListener(UKitCommandType.CMD_FILE_RECV_START, this);
//            BluetoothHelper.addResponseMsgListener(UKitCommandType.CMD_FILE_RECV_STOP, this);
//            BluetoothHelper.addResponseMsgListener(UKitCommandType.CMD_FILE_RECVING, this);
//            BluetoothHelper.addResponseMsgListener(UKitCommandType.CMD_FILE_RECV_OVER, this);
//            BluetoothHelper.addCommand(new UKitCommandRequest(UKitCommandType.CMD_FILE_RECV_MAX_PACK_SIZE));
//            resetTimeout();
//        }
//    }
//
//    public static UKitDeviceActionSequence createConnectActionSequenceRecv(BluetoothDevice device, long delayMs, IActionSequenceCallback callback) {
//        UKitDeviceActionSequence sequence = createConnectActionSequenceSend(device, delayMs, callback);
//        IActionSequenceCallback callbackWarp = new IActionSequenceCallback() {
//            IActionSequenceCallback _callback = callback;
//            @Override
//            public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                if(isSuccess) {
//                    RecvHelper helper = new RecvHelper("12.py", "/sdcard/12.py");
//                    helper.start();
//                }
//                if(_callback != null) {
//                    _callback.onActionSequenceFinished(bas, isSuccess);
//                }
//            }
//        };
//        sequence.callback(callbackWarp);
//        return sequence;
//    }
//
//    public static UKitDeviceActionSequence createRefreshBoardInfoActionSequence() {
//        return createRefreshBoardInfoActionSequence(0, null);
//    }
//
//    public static UKitDeviceActionSequence createRefreshBoardInfoActionSequence(long delayMs) {
//        return createRefreshBoardInfoActionSequence(delayMs, null);
//    }
//
//    public static UKitDeviceActionSequence createRefreshBoardInfoActionSequence(IActionSequenceCallback callback) {
//        return createRefreshBoardInfoActionSequence(0, callback);
//    }
//
//    public static UKitDeviceActionSequence createRefreshBoardInfoActionSequence(long delayMs, IActionSequenceCallback callback) {
//        return createRefreshBoardInfoActionSequence(delayMs, false, callback);
//    }
//
//    public static UKitDeviceActionSequence createRefreshBoardInfoActionSequence(long delayMs, boolean ignoreFailure, IActionSequenceCallback callback) {
//        UKitDeviceActionSequence actionSequence = UKitDeviceActionSequence.newActionSequence();
//        if(delayMs > 0) {
//            actionSequence.sleep(delayMs);
//        }
//        actionSequence
//            .command(UKitCmdBoardHandshake.newInstance(), ignoreFailure)
//            .sleep(3000)
//            .command(UKitCmdBoardInfo.newInstance(), true)
//            .command(UKitCmdBoardInfo.newInstance(), ignoreFailure)
//            .callback(callback);
//        return actionSequence;
//    }
//
//    public static UKitDeviceActionSequence createMotionActionSequence(Motion motion) {
//        return createMotionActionSequence(motion, false, null);
//    }
//
//    public static UKitDeviceActionSequence createMotionActionSequence(Motion motion, IActionSequenceCallback callback) {
//        return createMotionActionSequence(motion, false, callback);
//    }
//
//    public static UKitDeviceActionSequence createMotionActionSequence(Motion motion, boolean ignoreFailure, IActionSequenceCallback callback) {
//        UKitDeviceActionSequence actionSequence = UKitDeviceActionSequence.newActionSequence();
//        actionSequence.callback(callback);
//        if(motion != null) {
//            List<MotionFrame> frames = motion.frames;
//            List<Integer> servos = motion.servos;
//            if(frames == null || frames.isEmpty() || servos == null || servos.isEmpty()) {
//                //格式错误
//                return UKitDeviceActionSequence.newActionSequence();
//            }
//            for(MotionFrame frame : frames) {
//                if(frame.actived.isEmpty() || frame.angles.isEmpty() || frame.angles.size() != servos.size()) {
//                    //格式错误
////                    return UKitDeviceActionSequence.newActionSequence();
//                    actionSequence.sleep(frame.time + frame.waittime);
//                    continue;
//                }
//                UKitCmdSteeringGearAngle.Builder builder = new UKitCmdSteeringGearAngle.Builder();
//                builder.setRotateTime(frame.time);
//                builder.setPaddingTime(frame.waittime);
//                for(Integer id : frame.actived) {
//                    int index = servos.indexOf(id);
//                    if(index == -1) {
//                        //格式错误
//                        return UKitDeviceActionSequence.newActionSequence();
//                    }
//                    int angle = frame.angles.get(index);
//                    builder.addParam(id, angle);
//                }
//                actionSequence.command(builder.build(), ignoreFailure, UKitCommandPriority.HIGH, null);
//                actionSequence.delay(frame.time + frame.waittime);
//            }
//        }
//        return actionSequence;
//    }
//
//    public static UKitDeviceActionSequence createStopActionSequence() {
//        return createStopActionSequence(null);
//    }
//
//    public static UKitDeviceActionSequence createStopActionSequence(IActionSequenceCallback callback) {
////        UKitDeviceActionSequence actionSequence = UKitDeviceActionSequence.newActionSequence();
////        final UKitDeviceActionSequence lastStopActionSequence = UKitDeviceActionSequenceFactory.lastStopActionSequence;
////        UKitDeviceActionSequenceFactory.lastStopActionSequence = actionSequence;
////        boolean hasSendMotor = false;
////        boolean hasSendSteeringGear = false;
////        actionSequence.callback(callback);
////        actionSequence.listener(new IActionSequenceProcessListener() {
////            @Override
////            public void onProcessChanged(UKitDeviceActionSequence bas, int currentIndex, int totalAction) {}
////            @Override
////            public void onProcessBegin(UKitDeviceActionSequence bas) {
////                if(lastStopActionSequence != null && !lastStopActionSequence.hasFinish()) {
////                    lastStopActionSequence.abort();
////                }
////                PeripheralErrorCollector.getInstance().setBlocked(true);
////            }
////            @Override
////            public void onProcessEnd(UKitDeviceActionSequence bas) {
////                PeripheralErrorCollector.getInstance().setBlocked(false);
////            }
////        });
////        actionSequence.command(UKitCmdBoardSelfCheck.newInstance(true), true);
////        actionSequence.command(UKitCmdBoardSelfCheck.newInstance(false), true);
////        actionSequence.command(UKitCmdSteeringGearFix.newInstance(), true);
////        actionSequence.command(UKitCmdLowMotorFix.newInstance(), true);
////        UKitBoardInfoData bid = BtInfoHolder.getBoardInfoData();
////        if(bid != null) {
////            //关LED灯
////            if(!bid.getSensorHwData(UKitPeripheralType.PERIPHERAL_SENSOR_LIGHTING).getAvailableIds().isEmpty()) {
////                UKitCmdLedFace.Builder builder = new UKitCmdLedFace.Builder();
////                builder.addIds(bid.getSensorHwData(UKitPeripheralType.PERIPHERAL_SENSOR_LIGHTING).getAvailableIds());
////                builder.setTime(0);
////                for (int i = 0; i < 8; i++) {
////                    builder.addColor(i + 1, 0);
////                }
////                actionSequence.command(builder.build(), true);
////            }
////            //关超声灯
////            if(!bid.getSensorHwData(UKitPeripheralType.PERIPHERAL_SENSOR_ULTRASOUND).getAvailableIds().isEmpty()) {
////                UKitCmdLedUltrasound.Builder builder = new UKitCmdLedUltrasound.Builder();
////                builder.addIds(bid.getSensorHwData(UKitPeripheralType.PERIPHERAL_SENSOR_ULTRASOUND).getAvailableIds());
////                builder.setMode(UKitCmdLedUltrasound.Mode.TURN_OFF);
////                actionSequence.command(builder.build(), true);
////            }
//////            //停舵机
//////            if(!bid.getSteeringGearHwData().getAvailableIds().isEmpty()) {
//////                UKitCmdSteeringGearLoop.Builder builder = new UKitCmdSteeringGearLoop.Builder();
//////                builder.setMode(UKitCmdSteeringGearLoop.Mode.STOP);
//////                builder.addIds(bid.getSteeringGearHwData().getAvailableIds());
//////                builder.setIgnoreConflict(true);
//////                actionSequence.command(builder.build(), true);
//////            }
////            //停电机
////            if(!bid.getSensorHwData(UKitPeripheralType.PERIPHERAL_LOW_MOTOR).getAvailableIds().isEmpty()) {
////                UKitCmdLowMotorStop.Builder builder = new UKitCmdLowMotorStop.Builder();
////                builder.addIds(bid.getSensorHwData(UKitPeripheralType.PERIPHERAL_LOW_MOTOR).getAvailableIds());
////                actionSequence.command(builder.build(), true);
////                hasSendMotor = true;
////            }
////        }
////        if(Workspace.getInstance() != null && Workspace.getInstance().getProject() != null && Workspace.getInstance().getProject().modelInfo != null) {
////            ModelInfo modelInfo = Workspace.getInstance().getProject().modelInfo;
////            //停舵机
////            if(!modelInfo.steeringGearWheel.isEmpty()) {
////                UKitCmdSteeringGearLoop.Builder builder = new UKitCmdSteeringGearLoop.Builder();
////                builder.setMode(UKitCmdSteeringGearLoop.Mode.STOP);
////                builder.addIds(modelInfo.steeringGearWheel);
////                builder.setIgnoreConflict(true);
////                actionSequence.command(builder.build(), true);
////                hasSendSteeringGear = true;
////            }
////        }
////        if(hasSendMotor || hasSendSteeringGear) {
////            actionSequence.command(UKitCmdBoardSelfCheck.newInstance(true), true);
////            if(hasSendMotor && hasSendSteeringGear) {
////                actionSequence.sleep(100);
////            }
////            actionSequence.command(UKitCmdBoardSelfCheck.newInstance(false), true);
////            actionSequence.command(UKitCmdSteeringGearFix.newInstance(), true);
////            actionSequence.command(UKitCmdLowMotorFix.newInstance(), true);
////        } else {
////            //添加一个等待1毫秒的指令，以防止空序列
////            actionSequence.sleep(1);
////        }
////        return actionSequence;
//        UKitDeviceActionSequence actionSequence = UKitDeviceActionSequence.newActionSequence();
//        actionSequence.sleep(1);
//        actionSequence.listener(new IActionSequenceProcessListener() {
//            @Override
//            public void onProcessChanged(UKitDeviceActionSequence bas, int currentIndex, int totalAction) {}
//            @Override
//            public void onProcessBegin(UKitDeviceActionSequence bas) {
//                BluetoothHelper.stopRobot();
//            }
//            @Override
//            public void onProcessEnd(UKitDeviceActionSequence bas) {
//            }
//        });
//        return actionSequence;
//    }
//
//}
