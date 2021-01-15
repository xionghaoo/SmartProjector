package com.ubtedu.deviceconnect.libs.base.model;

/**
 * @Author naOKi
 * @Date 2018/11/08
 **/
public class URoProductInfo {

//    private String deviceName = null;
//    private UKitSpeakerInfoData speakerInfo = null;
//    private UKitBoardInfoData boardInfoData = null;
//    private URoUkitBatteryInfo batteryInfoData = null;
//    private URoSerialNumberInfo serialNumberData = null;
//    private ArrayMap<UKitPeripheralType, SparseArray<SensorData>> sensorData;
//    private HashSet<UKitPeripheralErrorData> errorPeripheral;
//
//    public static final int INVALID_VALUE = -9999;
//
//    private URoSensorDataChangeListener sensorDataUpdater;
//    private URoBatteryChangeListener batteryChangeListener;
//    private URoComponentErrorListener peripheralErrorAppearListener;
//
//    public URoProductInfo() {
//        sensorData = new ArrayMap<>();
//        errorPeripheral = new HashSet<>();
//        for (UKitPeripheralType type : UKitPeripheralType.values()) {
//            sensorData.put(type, new SparseArray<>());
//        }
//    }
//
//    public boolean isLowBattery() {
//        return batteryInfoData != null && batteryInfoData.isLowBattery();
//    }
//
//    public boolean isEmptyBattery() {
//        return batteryInfoData != null && batteryInfoData.isEmptyBattery();
//    }
//
//    public boolean isCharging() {
//        return batteryInfoData != null && batteryInfoData.isCharging();
//    }
//
//    public void updateSensorData(UKitPeripheralType type, int id, Float value) {
//        SparseArray<SensorData> sa = sensorData.get(type);
//        SensorData sd = sa.get(id, null);
//        boolean isError = value == null;
//        if (sd == null) {
//            sd = new SensorData();
//            sa.put(id, sd);
//        }
//        if (!isError) {
//            sd.value = value;
//            sd.errorTimes = 0;
//            sd.errorFlag = false;
//            sd.lastUpdateTime = System.currentTimeMillis();
//        } else {
//            if (++sd.errorTimes >= SensorData.MAX_ERROR_TIMES) {
//                sd.value = null;
//                sd.errorTimes = 0;
//                sd.errorFlag = true;
//                sd.lastUpdateTime = System.currentTimeMillis();
//            }
//        }
//    }
//
//    private Float getSensorData(UKitPeripheralType type, int id) {
//        SparseArray<SensorData> sa = sensorData.get(type);
//        SensorData sd = sa.get(id, null);
//        if (sd == null) {
//            return null;
//        }
//        if (System.currentTimeMillis() - sd.lastUpdateTime > SensorData.MAX_VALIDITY_PERIOD) {
//            return null;
//        }
//        return sd.value;
//    }
//
//    public UKitSensorValueData getAllSensorValue() {
//        UKitSensorValueData result = new UKitSensorValueData();
//        result.setBatteryValue(isLowBattery());
//        for (UKitPeripheralType type : UKitPeripheralType.values()) {
//            SparseArray<SensorData> sa = sensorData.get(type);
//            if (sa != null && sa.size() > 0) {
//                for (int i = 0; i < sa.size(); i++) {
//                    int id = sa.keyAt(i);
//                    Float value;
//                    switch (type) {
//                    case PERIPHERAL_SENSOR_INFRARED:
//                        value = getSensorData(UKitPeripheralType.PERIPHERAL_SENSOR_INFRARED, id);
//                        result.addInfraredValue(id, value == null ? INVALID_VALUE : Math.round(value));
//                        break;
//                    case PERIPHERAL_SENSOR_TOUCH:
//                        value = getSensorData(UKitPeripheralType.PERIPHERAL_SENSOR_TOUCH, id);
//                        result.addTouchValue(id, value == null ? INVALID_VALUE : Math.round(value));
//                        break;
//                    case PERIPHERAL_SENSOR_ULTRASOUND:
//                        value = getSensorData(UKitPeripheralType.PERIPHERAL_SENSOR_ULTRASOUND, id);
//                        result.addUltrasoundValue(id, value == null ? INVALID_VALUE : Math.round(value));
//                        break;
//                    case PERIPHERAL_SENSOR_COLOR:
//                        value = getSensorData(UKitPeripheralType.PERIPHERAL_SENSOR_COLOR, id);
//                        result.addColorValue(id, value == null ? INVALID_VALUE : Math.round(value));
//                        break;
//                    case PERIPHERAL_SENSOR_HUMITURE:
//                        value = getSensorData(UKitPeripheralType.PERIPHERAL_SENSOR_HUMITURE, id);
//                        Float temperature = getSensorData(UKitPeripheralType.PERIPHERAL_SENSOR_TEMPERATURE, id);
//                        result.addHumitureValue(id, value == null ? INVALID_VALUE : Math.round(value), temperature == null ? INVALID_VALUE : Math.round(temperature));
//                        break;
//                    case PERIPHERAL_SENSOR_ENV_LIGHT:
//                        value = getSensorData(UKitPeripheralType.PERIPHERAL_SENSOR_ENV_LIGHT, id);
//                        result.addEnvLightValue(id, value == null ? INVALID_VALUE : Math.round(value));
//                        break;
//                    case PERIPHERAL_SENSOR_SOUND:
//                        value = getSensorData(UKitPeripheralType.PERIPHERAL_SENSOR_SOUND, id);
//                        result.addSoundValue(id, value == null ? INVALID_VALUE : Math.round(value));
//                        break;
//                    }
//                }
//            }
//        }
//        return result;
//    }
//
//    public int getSensorValue(UKitPeripheralType type, int id) {
//        Float value = getSensorData(type, id);
//        return value == null ? INVALID_VALUE : Math.round(value);
//    }
//
//    public HashMap<UKitPeripheralType, ArrayList<Integer>> getErrorSensors() {
//        final UKitPeripheralType[] types = {
//            UKitPeripheralType.PERIPHERAL_SENSOR_INFRARED,
//            UKitPeripheralType.PERIPHERAL_SENSOR_TOUCH,
//            UKitPeripheralType.PERIPHERAL_SENSOR_ENV_LIGHT,
//            UKitPeripheralType.PERIPHERAL_SENSOR_SOUND,
//            UKitPeripheralType.PERIPHERAL_SENSOR_COLOR,
//            UKitPeripheralType.PERIPHERAL_SENSOR_HUMITURE,
//            UKitPeripheralType.PERIPHERAL_SENSOR_ULTRASOUND
//        };
//        HashMap<UKitPeripheralType, ArrayList<Integer>> result = new HashMap<>();
//        for (UKitPeripheralType type : types) {
//            ArrayList<Integer> list = new ArrayList<>();
//            for (int id = 1; id <= 8; id++) {
//                SparseArray<SensorData> sa = sensorData.get(type);
//                SensorData sd = sa.get(id, null);
//                if (sd == null) {
//                    continue;
//                }
//                if (sd.errorFlag) {
//                    list.add(id);
//                }
//            }
//            if(!list.isEmpty()) {
//                result.put(type, list);
//            }
//        }
//        return result;
//    }
//
//    public void setBoardInfoData(UKitBoardInfoData boardInfoData) {
//        this.boardInfoData = boardInfoData;
//        if(boardInfoData != null) {
//            URoLogUtils.e("主控信息\n%s", boardInfoData.toString());
//        }
//    }
//
//    public void updateBoardVersion(String version) {
//        if (boardInfoData == null) {
//            return;
//        }
//        if (TextUtils.isEmpty(version)) {
//            return;
//        }
//        boardInfoData.boardVersion = version;
//    }
//
//    public void updateSteeringGearVersion(String version) {
//        if (boardInfoData == null) {
//            return;
//        }
//        if (TextUtils.isEmpty(version)) {
//            return;
//        }
//        boardInfoData.getSteeringGearHwData().updateVersion(version);
//    }
//
//    public void updateSensorVersion(UKitPeripheralType sensorType, String version) {
//        if (boardInfoData == null) {
//            return;
//        }
//        if (TextUtils.isEmpty(version)) {
//            return;
//        }
//        boardInfoData.getSensorHwData(sensorType).updateVersion(version);
//    }
//
//    public void resetBoardInfoData() {
//        boardInfoData = null;
//    }
//
//    public UKitBoardInfoData getBoardInfoData() {
//        return boardInfoData;
//    }
//
//    public void setBatteryInfoData(URoUkitBatteryInfo batteryInfoData) {
//        URoUkitBatteryInfo lastBatteryInfoData = this.batteryInfoData;
//        this.batteryInfoData = batteryInfoData;
//        if(batteryInfoData == null) {
//            return;
//        }
//        if(lastBatteryInfoData == null || lastBatteryInfoData.charging != batteryInfoData.charging || lastBatteryInfoData.level != batteryInfoData.level || lastBatteryInfoData.voltage != batteryInfoData.voltage) {
//            if(batteryChangeListener != null) {
//                batteryChangeListener.onBatteryStateChanged(null, lastBatteryInfoData, batteryInfoData);
//            }
//        }
//    }
//
//    public void resetBatteryInfoData() {
//        batteryInfoData = null;
//    }
//
//    public URoUkitBatteryInfo getBatteryInfoData() {
//        return batteryInfoData;
//    }
//
//    public void resetSensorData() {
//        for (UKitPeripheralType type : UKitPeripheralType.values()) {
//            SparseArray<SensorData> sd = sensorData.get(type);
//            if (sd != null) {
//                sd.clear();
//            }
//        }
//    }
//
//    public void resetSpeakerInfoData() {
//        speakerInfo = null;
//    }
//
//    public void setSpeakerInfoData(UKitSpeakerInfoData data) {
//        speakerInfo = data;
//    }
//
//    public UKitSpeakerInfoData getSpeakerInfoData() {
//        return speakerInfo;
//    }
//
//    public void resetDeviceName() {
//        deviceName = null;
//    }
//
//    public void setDeviceName(String deviceName) {
//        this.deviceName = deviceName;
//    }
//
//    public String getDeviceName() {
//        return TextUtils.isEmpty(deviceName) ? "" : deviceName;
//    }
//
//    public void resetSerialNumberData() {
//        serialNumberData = null;
//    }
//
//    public void setSerialNumberData(URoSerialNumberInfo data) {
//        serialNumberData = data;
//    }
//
//    public @NonNull URoSerialNumberInfo getSerialNumberData() {
//        if (serialNumberData == null) {
//            return URoSerialNumberInfo.EMPTY_DATA;
//        }
//        return serialNumberData;
//    }
//
//    public ArrayList<UKitPeripheralErrorData> getPeripheralError() {
//        return new ArrayList<>(errorPeripheral);
//    }
//
//    public void addPeripheralError(@NonNull UKitPeripheralType type, int id) {
//        UKitPeripheralErrorData peripheralErrorData = new UKitPeripheralErrorData(type, id);
//        if(!errorPeripheral.contains(peripheralErrorData)) {
//            errorPeripheral.add(peripheralErrorData);
//            if(peripheralErrorAppearListener != null) {
//                peripheralErrorAppearListener.onPeripheralErrorAppeared(null, peripheralErrorData);
//            }
//        }
//    }
//
//    public boolean isPeripheralError(@NonNull UKitPeripheralType type, int id) {
//        ArrayList<UKitPeripheralErrorData> errorList = getPeripheralError();
//        return errorList.contains(new UKitPeripheralErrorData(type, id));
//    }
//
//    public void resetPeripheralError() {
//        errorPeripheral.clear();
//    }
//
//    public void setSensorDataUpdater(URoSensorDataChangeListener updater) {
//        sensorDataUpdater = updater;
//    }
//
//    public void setBatteryChangeListener(URoBatteryChangeListener listener) {
//        batteryChangeListener = listener;
//    }
//
//    public void setPeripheralErrorAppearListener(URoComponentErrorListener listener) {
//        peripheralErrorAppearListener = listener;
//    }
//
//    public void cleanup() {
//        resetBoardInfoData();
//        resetSpeakerInfoData();
//        resetBatteryInfoData();
//        resetSerialNumberData();
//        resetSensorData();
//        resetPeripheralError();
//    }
//
//    public static class SensorData {
//        public Float value;
//        public int errorTimes = 0;
//        public boolean errorFlag = false;
//        public long lastUpdateTime = -1;
//        private static final long MAX_VALIDITY_PERIOD = 3 * 1000L;
//        private static final int MAX_ERROR_TIMES = 3;
//    }

}
