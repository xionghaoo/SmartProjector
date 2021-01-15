//package com.ubtedu.ukit.bluetooth.processor;
//
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.UKitCommandResponse;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.UKitCommandType;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.device.UKitRemoteDevice;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.interfaces.IUKitDeviceMessageResponseListener;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.model.UKitPeripheralType;
//
///**
// * @Author naOKi
// * @Date 2018/11/08
// **/
//public class SensorValueResponseMsgHandler implements IUKitDeviceMessageResponseListener {
//
//    private SensorValueResponseMsgHandler() {}
//
//    private static SensorValueResponseMsgHandler instance = null;
//
//    public static SensorValueResponseMsgHandler getInstance() {
//        synchronized (SensorValueResponseMsgHandler.class) {
//            if(instance == null) {
//                instance = new SensorValueResponseMsgHandler();
//            }
//            return instance;
//        }
//    }
//
//    @Override
//    public void onResponseMsgReceived(UKitRemoteDevice device, UKitCommandType cmdType, UKitCommandResponse bizData) {
////        if(cmdCode != UKitCommandType.READ_SENSOR_VALUE.getCode()) {
////            return;
////        }
////        if(bizData.length == 1) {
////            return;
////        }
////        int frameTotal = UKitCommandRequest.byte2Int(bizData[0]); // ignore frame total
////        int frameIndex = UKitCommandRequest.byte2Int(bizData[1]); // ignore frame index
////        int sensorTypeCount = UKitCommandRequest.byte2Int(bizData[2]); // ignore sensor type count
////        for(int i = 3; i < bizData.length; ) {
////            byte type = bizData[i++];
////            UKitPeripheralType sensorType = UKitPeripheralType.getInstance(type);
////            byte errId = bizData[i++];
////            byte ids = bizData[i++];
////            for(int j = 0; j < 8; j++) {
////                if((errId & (1 << j)) != 0) {
////                    int id = j + 1;
////                    BtInfoHolder.updateSensorData(sensorType, id, null);
////                    errId &= (~(1 << j)) & 0xFF;
////                    if(errId == 0) {
////                        break;
////                    }
////                }
////            }
////            int dataLength = getDataLength(sensorType);
////            for(int j = 0; j < 8; j++) {
////                if((ids & (1 << j)) != 0) {
////                    int id = j + 1;
////                    byte[] value = Arrays.copyOfRange(bizData, i, i + dataLength);
////                    BtInfoHolder.updateSensorData(sensorType, id, value);
////                    i += dataLength;
////                    ids &= (~(1 << j)) & 0xFF;
////                    if(ids == 0) {
////                        break;
////                    }
////                }
////            }
////        }
////        //接收完所有数据帧
//////        if(frameTotal == frameIndex) {
//////            LogUtils.e(BtInfoHolder.getAllSensorValue());
//////        }
//    }
//
//    private int getDataLength(UKitPeripheralType sensorType) {
//        switch (sensorType) {
//        case PERIPHERAL_SENSOR_HUMITURE:
//            return 4;
//        case PERIPHERAL_SENSOR_COLOR:
//            return 11;
//        case PERIPHERAL_SENSOR_TOUCH:
//            return 1;
//        default:
//            return 2;
//
//        }
//    }
//
//}
