package com.ubtedu.ukit.bluetooth.ota;

import android.content.Intent;
import android.text.TextUtils;

import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.ukit.application.ServerConfig;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.common.ota.OtaComponent;
import com.ubtedu.ukit.common.ota.RxOtaHelper;

import java.io.File;

/**
 * @Author naOKi
 * @Date 2018/12/12
 **/
public class OtaHelper {

    private static final String MIN_BOARD_INFO = "Jimu_p1.79";

    private static final String MIN_OTA_BOARD2_VERSION = "UKIT2.0_V1.66";
    private static final String OTA_BY_MCU_BOARD2_VERSION= "UKIT2.0_V2.05";

    private static final int BOARD2_VERSION_MCU_VB= 1;
    private static final int BOARD2_VERSION_MCU_VE= 3;

    private static final OtaVersionInfo INTERNAL_BOARD_INFO = new OtaVersionInfo("Jimu_p1.79", FileHelper.join(FileHelper.getOtaPath(), "Jimu2primary_p1.79.bin"), true);
    private static final OtaVersionInfo INTERNAL_STEERINGGEAR_INFO = new OtaVersionInfo("41165101", FileHelper.join(FileHelper.getOtaPath(), "jimu2_app_41165101.bin"),
            ServerConfig.getOtaStorage() + "/upgrade/2019/03/1552370830844/jimu2_app_41165101.bin",
            "5ace1b5b0fd651339c62d10235841875",
            7764, true);
    private static final OtaVersionInfo INTERNAL_INFRARED_INFO = new OtaVersionInfo("15161220", FileHelper.join(FileHelper.getOtaPath(), "Jimu2_infrared_sensor_15161220.bin"),
            ServerConfig.getOtaStorage() + "/upgrade/2019/03/1552358417080/Jimu2_infrared_sensor_15161220.bin",
            "f8a948d442d0f6883918e05aac55e900",
            7816, true);
    private static final OtaVersionInfo INTERNAL_SPEAKER_INFO = new OtaVersionInfo("01170320", FileHelper.join(FileHelper.getOtaPath(), "Jimu2_speaker_sensor_01170320.bin"),
            ServerConfig.getOtaStorage() + "/upgrade/2019/06/1559549309601/Jimu2_speaker_sensor_01170320.bin",
            "27e0fc8977b9089bfa4d71bd03840513",
            10460, true);
    private static final OtaVersionInfo INTERNAL_LIGHTING_INFO = new OtaVersionInfo("60180502", FileHelper.join(FileHelper.getOtaPath(), "Jimu2_light1.0_sensor_60180502.bin"),
            ServerConfig.getOtaStorage() + "/upgrade/2019/03/1552358546238/Jimu2_light1.0_sensor_60180502.bin",
            "ca22355625a23a159c2d3c0fdb2e4137",
            15444, true);
    private static final OtaVersionInfo INTERNAL_TOUCH_INFO = new OtaVersionInfo("15161220", FileHelper.join(FileHelper.getOtaPath(), "Jimu2_touch_sensor_15161220.bin"),
            ServerConfig.getOtaStorage() + "/upgrade/2019/03/1552358475275/Jimu2_touch_sensor_15161220.bin",
            "7b166998c997c128f8c3cbeed1772609",
            7128, true);

    private OtaHelper() {
    }

    public static void init() {
        extractInternal();
    }

    private static void extractInternal() {
        try {
            UKitApplication app = UKitApplication.getInstance();
            Intent intent = new Intent(app, OtaExtractInternalService.class);
            app.startService(intent);
        } catch (Exception e) {
            //捕获Crash，Bugly经常收到这个错误，其实是Android Studio Debug运行的时候的错误，不是正常启动运行的错误
            //LogUtil.e(e.getMessage());
        }
    }

    public static boolean isBoardUpgradeable() {
        URoMainBoardInfo boardInfoData = BluetoothHelper.getBoardInfoData();
        if (boardInfoData == null || TextUtils.equals(boardInfoData.boardVersion, "00000000")) {
            return false;
        }
        OtaVersionInfo otaVersionInfo = getBoardOtaVersionInfo();
        if (otaVersionInfo == null) {
            return false;
        }
        if (otaVersionInfo.isInternal()) {
            if (MIN_BOARD_INFO.compareTo(otaVersionInfo.getVersion()) > 0) {
                // 内置版本小于最小要求版本，不需要使用内置版本升级
                return false;
            } else {
                // 内置版本大于等于最小要求版本 且 连接主控版本小于最小要求版本，需要使用内置版本升级
                return MIN_BOARD_INFO.compareTo(boardInfoData.boardVersion) > 0;
            }
        } else {
            // 如果配置来源于OTA配置，则不管是否满足最小要求版本均需要升级
            return !TextUtils.equals(otaVersionInfo.getVersion(), boardInfoData.boardVersion);
        }
    }

    public static boolean isSteeringGearUpgradeable() {
        URoMainBoardInfo boardInfoData = BluetoothHelper.getBoardInfoData();
        if (boardInfoData == null || TextUtils.equals(boardInfoData.steeringGear.getVersion(), "00000000")) {
            return false;
        }
        OtaVersionInfo otaVersionInfo = getSteeringGearOtaVersionInfo();
        if (otaVersionInfo == null || otaVersionInfo.isInternal()) {
            return false;
        }
        return !TextUtils.equals(otaVersionInfo.getVersion(), boardInfoData.steeringGear.getVersion());
    }

    public static boolean isSensorUpgradeable(URoComponentType sensorType) {
        URoMainBoardInfo boardInfoData = BluetoothHelper.getBoardInfoData();
        if (boardInfoData == null) {
            return false;
        }
        URoComponentInfo sensorHwData = boardInfoData.getComponentInfo(sensorType);
        if (sensorHwData == null || TextUtils.equals(sensorHwData.getVersion(), "00000000")) {
            return false;
        }
        OtaVersionInfo otaVersionInfo = getSensorOtaVersion(sensorType);
        if (otaVersionInfo == null || otaVersionInfo.isInternal()) {
            return false;
        }
        return !TextUtils.equals(otaVersionInfo.getVersion(), sensorHwData.getVersion());
    }

    public static OtaVersionInfo getBoardOtaVersionInfo() {
        SharedPreferenceUtils helper = SharedPreferenceUtils.getInstance(UKitApplication.getInstance());
        String prefix = "";
        if (BluetoothHelper.isSmartVersion()) {
            URoMainBoardInfo mainBoardInfo = BluetoothHelper.getBoardInfoData();
            if (mainBoardInfo == null) {
                return null;
            }
            String boardVersion = mainBoardInfo.boardVersion;
            if (compareBoardVersion(boardVersion, MIN_OTA_BOARD2_VERSION) < 0) {
                //低于V1.66版本，不能通过ota升级，因为低于V1.66版本会断开蓝牙连接，当前需求下，不会执行到此处
                return null;
            }
            if (compareBoardVersion(boardVersion, OTA_BY_MCU_BOARD2_VERSION) < 0) {
                //低于UKIT2.0_V2.06使用Mainboard2 OTA信息
                prefix = OtaComponent.Mainboard2.getName();
            } else {
                //UKIT2.0_V2.06及以上根据mcuVersion区分VB和VE版本
                int mcuVersion = mainBoardInfo.mcuVersion;
                if (mcuVersion == BOARD2_VERSION_MCU_VB) {//VB类型
                    prefix = OtaComponent.MainboardVB.getName();
                } else if (mcuVersion == BOARD2_VERSION_MCU_VE) {//VE类型使用Mainboard2
                    prefix = OtaComponent.Mainboard2.getName();
                } else {
                    //mcu版本号获取失败时，不升级，返回空，因为mcu版本错误时会断开蓝牙，当前需求下，不会执行到此处，
                    return null;
                }
            }
        }

        String version = helper.getStringValue(prefix + RxOtaHelper.MAIN_BOARD_VERSION_CODE);
        String otaFile = helper.getStringValue(prefix + RxOtaHelper.MAIN_BOARD_VERSION_FILE);
//        return new OtaVersionInfo("Jimu_p1.79", "/sdcard/ota/Jimu2primary_p1.79.bin");
//        return new OtaVersionInfo("Jimu_p1.68", "/sdcard/ota/Jimu2primary_p1.68.bin");
        if (TextUtils.isEmpty(version) || TextUtils.isEmpty(otaFile) || !new File(otaFile).exists()) {
            return BluetoothHelper.isSmartVersion() ? null : INTERNAL_BOARD_INFO;
        }
        int fileSize = helper.getIntValue(prefix + RxOtaHelper.MAIN_BOARD_VERSION_FILE_SIZE);
        String md5 = helper.getStringValue(prefix + RxOtaHelper.MAIN_BOARD_VERSION_FILE_MD5);
        String url = helper.getStringValue(prefix + RxOtaHelper.MAIN_BOARD_VERSION_URL);
//        fileSize=2589760;
//        md5="e971980e37f5f4ee6158999d66698211";
//        url="http://testqiniu.ubtrobot.com/upgrade/2019/10/1572348388876/UKIT2.0_V1.05.bin";
        return new OtaVersionInfo(version, otaFile, url, md5, fileSize);
    }

    public static OtaVersionInfo getSteeringGearOtaVersionInfo() {
        SharedPreferenceUtils helper = SharedPreferenceUtils.getInstance(UKitApplication.getInstance());
        String version = helper.getStringValue(RxOtaHelper.STEERING_VERSION_CODE);
        String otaFile = helper.getStringValue(RxOtaHelper.STEERING_VERSION_FILE);
//        return new OtaVersionInfo("41165101", "","https://testqiniu.ubtrobot.com/upgrade/2019/03/1552370830844/jimu2_app_41165101.bin","5ace1b5b0fd651339c62d10235841875",7764);
//        return new OtaVersionInfo("41161701", "","https://testqiniu.ubtrobot.com/upgrade/2019/03/1552360421317/jimu2_app_41161701.bin","fa2ab7962febe6eca91f623b6fd9972a",7468);
//        return new OtaVersionInfo("41161701", "/sdcard/ota/jimu2_app_41161701.bin");
        if (TextUtils.isEmpty(version) || TextUtils.isEmpty(otaFile) || !new File(otaFile).exists()) {
            return INTERNAL_STEERINGGEAR_INFO;
        }
        int fileSize = helper.getIntValue(RxOtaHelper.STEERING_VERSION_FILE_SIZE);
        String md5 = helper.getStringValue(RxOtaHelper.STEERING_VERSION_FILE_MD5);
        String url = helper.getStringValue(RxOtaHelper.STEERING_VERSION_URL);
        return new OtaVersionInfo(version, otaFile, url, md5, fileSize);
    }

    public static OtaVersionInfo getSensorOtaVersion(URoComponentType sensorType) {
        SharedPreferenceUtils helper = SharedPreferenceUtils.getInstance(UKitApplication.getInstance());
        String version = null, otaFile = null, sensorName = null;
        switch (sensorType) {
            case INFRAREDSENSOR:
                sensorName = OtaComponent.Infrared.getName();
                version = helper.getStringValue(OtaComponent.Infrared.getName() + RxOtaHelper.COMPONENT_VERSION);
                otaFile = helper.getStringValue(OtaComponent.Infrared.getName() + RxOtaHelper.COMPONENT_FILE_PATH);
//            return new OtaVersionInfo("14160923", "/sdcard/ota/Jimu2_infrared_sensor_14160923.bin");
                break;

            case SPEAKER:
                sensorName = OtaComponent.Speaker.getName();
                version = helper.getStringValue(OtaComponent.Speaker.getName() + RxOtaHelper.COMPONENT_VERSION);
                otaFile = helper.getStringValue(OtaComponent.Speaker.getName() + RxOtaHelper.COMPONENT_FILE_PATH);
//            return new OtaVersionInfo("01161227", "/sdcard/ota/Jimu2_speaker_sensor_01161227.bin");
                break;

            case LED:
                sensorName = OtaComponent.Light.getName();
                version = helper.getStringValue(OtaComponent.Light.getName() + RxOtaHelper.COMPONENT_VERSION);
                otaFile = helper.getStringValue(OtaComponent.Light.getName() + RxOtaHelper.COMPONENT_FILE_PATH);
//            return new OtaVersionInfo("42170301", "/sdcard/ota/Jimu2_light_sensor_42170301.bin");
                break;

            case TOUCHSENSOR:
                sensorName = OtaComponent.Touch.getName();
                version = helper.getStringValue(OtaComponent.Touch.getName() + RxOtaHelper.COMPONENT_VERSION);
                otaFile = helper.getStringValue(OtaComponent.Touch.getName() + RxOtaHelper.COMPONENT_FILE_PATH);
//            return new OtaVersionInfo("14161215", "/sdcard/ota/Jimu2_touch_sensor_14161215.bin");
//        return new OtaVersionInfo("15161220", "","https://edustorge.ubtrobot.com/upgrade/2019/03/1552358475275/Jimu2_touch_sensor_15161220.bin","7b166998c997c128f8c3cbeed1772609",7128);
                break;

            case LED_BELT:
                sensorName = OtaComponent.LEDBox.getName();
                version = helper.getStringValue(OtaComponent.LEDBox.getName() + RxOtaHelper.COMPONENT_VERSION);
                otaFile = helper.getStringValue(OtaComponent.LEDBox.getName() + RxOtaHelper.COMPONENT_FILE_PATH);
//                return new OtaVersionInfo("00000107", "","https://testqiniu.ubtrobot.com/upgrade/2020/05/1589441203540/led_belt_sensor_app_0017.bin","dc897b7294c59968de02a57cce456d45",25010);
                break;

            default:
                break;
        }
        if (TextUtils.isEmpty(version) || TextUtils.isEmpty(otaFile) || !new File(otaFile).exists()) {
            switch (sensorType) {
                case INFRAREDSENSOR:
                    return INTERNAL_INFRARED_INFO;
                case SPEAKER:
                    return INTERNAL_SPEAKER_INFO;
                case LED:
                    return INTERNAL_LIGHTING_INFO;
                case TOUCHSENSOR:
                    return INTERNAL_TOUCH_INFO;
                default:
                    return null;
            }
        }
        int fileSize = helper.getIntValue(sensorName + RxOtaHelper.COMPONENT_FILE_SIZE);
        String md5 = helper.getStringValue(sensorName + RxOtaHelper.COMPONENT_FILE_MD5);
        String url = helper.getStringValue(sensorName + RxOtaHelper.COMPONENT_FILE_URL);
        return new OtaVersionInfo(version, otaFile, url, md5, fileSize);
    }

    /*
     * 输入版本字符串，返回数字版本号
     * ver: String 版本号字符串，如UKIT2.0_V1.68、UKIT2.0_V1.61OB、UKIT2.0_V1.61Z、UKIT2.0_V2.05
     * 返回值：数字格式的版本号，如1.68、1.61、2.05
     */
    public static String formatVersion(String ver) {
        return ver.replaceAll("(^UKIT2\\.0_V|[a-zA-Z_]*$)", "");
    }

    /*
     * 输入两个版本号进行比较
     * ver1: String 版本号字符串1
     * ver2: String 版本号字符串2
     * 返回值：
     *   当ver1==ver2时，返回0
     *   当ver1<ver2时，返回-1
     *   当ver1>ver2时，返回1
     */

    public static int compareBoardVersion(String ver1, String ver2) {
        String numVer1 = formatVersion(ver1);
        String numVer2 = formatVersion(ver2);
        String[] splitVer1 = numVer1.split("\\.");
        String[] splitVer2 = numVer2.split("\\.");
        int minLen = Math.min(splitVer1.length, splitVer2.length);
        for (int i = 0; i < minLen; i++) {
            int partResult = splitVer1[i].compareTo(splitVer2[i]);
            if (partResult == 0) {
                continue;
            }
            try {
                long p1 = Long.parseLong(splitVer1[i]);
                long p2 = Long.parseLong(splitVer2[i]);
                if (p1 == p2) continue;
                return p1 < p2 ? -1 : 1;
            } catch (NumberFormatException e) {
                return partResult;
            }
        }
        if (splitVer1.length == splitVer2.length) return 0;
        else return splitVer1.length < splitVer2.length ? -1 : 1;
    }

    public static boolean isBoardVersionInfoError() {
        if(!BluetoothHelper.isSmartVersion()) {
            return false;
        }
        URoMainBoardInfo mainBoardInfo=BluetoothHelper.getBoardInfoData();
        if (mainBoardInfo==null){
            return true;
        }
        String boardVersion = mainBoardInfo.boardVersion;
        int mcuVersion = mainBoardInfo.mcuVersion;
        return compareBoardVersion(boardVersion, OTA_BY_MCU_BOARD2_VERSION) >= 0 && mcuVersion != BOARD2_VERSION_MCU_VB && mcuVersion != BOARD2_VERSION_MCU_VE;
    }

    public static boolean isDeprecatedBoardVersion() {
        if(!BluetoothHelper.isSmartVersion()) {
            return false;
        }
        URoMainBoardInfo mainBoardInfo=BluetoothHelper.getBoardInfoData();
        if (mainBoardInfo==null){
            return true;
        }
        String boardVersion = mainBoardInfo.boardVersion;
        return compareBoardVersion(boardVersion, MIN_OTA_BOARD2_VERSION) < 0;
    }

    public static class OtaVersionInfo {
        private final String version;
        private final String file;
        private final boolean internal;
        private int fileSize;
        private String fileMd5;
        private String url;

        private OtaVersionInfo(String version, String file) {
            this(version, file, false);
        }

        private OtaVersionInfo(String version, String file, boolean internal) {
            this(version, file, null, null, 0, internal);
        }

        private OtaVersionInfo(String version, String file, String url, String fileMd5, int fileSize) {
            this(version, file, url, fileMd5, fileSize, false);
        }

        private OtaVersionInfo(String version, String file, String url, String fileMd5, int fileSize, boolean internal) {
            this.version = version;
            this.file = file;
            this.url = url;
            this.fileMd5 = fileMd5;
            this.fileSize = fileSize;
            this.internal = internal;
        }

        public String getVersion() {
            return version;
        }

        public String getFile() {
            return file;
        }

        public boolean isInternal() {
            return internal;
        }

        public int getFileSize() {
            return fileSize;
        }

        public String getFileMd5() {
            return fileMd5;
        }

        public String getUrl() {
            return url;
        }
    }

}
