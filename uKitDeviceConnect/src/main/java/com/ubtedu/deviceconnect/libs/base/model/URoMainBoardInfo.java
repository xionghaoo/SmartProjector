package com.ubtedu.deviceconnect.libs.base.model;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public abstract class URoMainBoardInfo {
    public final static int BATTERY_TYPE_LITHIUM = 0; //锂电池
    public final static int BATTERY_TYPE_DRY_CELL = 1; //干电池
    //主板版本号
    public String boardVersion = "";
    //硬件版本号
    public int mcuVersion = 0;
    //电量等级
    public float batteryLevel = -1;
    //电池类型
    public int batteryType = -1;
    //外部Flash容量
    public int flashSize = -1;
    //舵机信息
    public URoComponentInfo steeringGear = new URoComponentInfo();
    //红外
    public URoComponentInfo infrared = new URoComponentInfo();
//    //陀螺仪
//    public URoComponentInfo gyro = new URoComponentInfo();
    //触碰
    public URoComponentInfo touch = new URoComponentInfo();
    //灯光
    public URoComponentInfo lighting = new URoComponentInfo();
//    //重力
//    public URoComponentInfo gravity = new URoComponentInfo();
    //超声
    public URoComponentInfo ultrasound = new URoComponentInfo();
//    //数码管
//    public URoComponentInfo led = new URoComponentInfo();
    //喇叭
    public URoComponentInfo speaker = new URoComponentInfo();
    //光感
    public URoComponentInfo envLight = new URoComponentInfo();
//    //大气压
//    public URoComponentInfo airPressure = new URoComponentInfo();
    //声音
    public URoComponentInfo sound = new URoComponentInfo();
    //温湿度
    public URoComponentInfo humiture = new URoComponentInfo();
    //颜色
    public URoComponentInfo color = new URoComponentInfo();
    //马达
    public URoComponentInfo motor = new URoComponentInfo();
//    //RGB灯珠
//    public URoComponentInfo rgbLed = new URoComponentInfo();
    //吸盘
    public URoComponentInfo sucker = new URoComponentInfo();
    //灯盒
    public URoComponentInfo<URoLEDStripInfo> ledBelt = new URoComponentInfo<>();
    //显示屏
    public URoComponentInfo lcd = new URoComponentInfo();
    //视觉模块
    public URoComponentInfo vision = new URoComponentInfo();

    public URoComponentInfo getServosInfo() {
        return steeringGear;
    }

    public URoComponentInfo getComponentInfo(URoComponentType componentType) {
        switch (componentType) {
        case INFRAREDSENSOR:
        return infrared;
        case COLORSENSOR:
            return color;
        case SOUNDSENSOR:
            return sound;
        case TOUCHSENSOR:
            return touch;
        case SPEAKER:
            return speaker;
        case ENVIRONMENTSENSOR:
            return humiture;
        case LED:
            return lighting;
        case BRIGHTNESSSENSOR:
            return envLight;
        case MOTOR:
            return motor;
        case ULTRASOUNDSENSOR:
            return ultrasound;
        case SERVOS:
            return steeringGear;
        case SUCKER:
            return sucker;
        case LED_BELT:
            return ledBelt;
        case LCD:
            return lcd;
        case VISION:
            return vision;
        default:
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("boardVersion:").append(boardVersion).append("\n");
        sb.append("mcuVersion:").append(mcuVersion).append("\n");
        sb.append("batteryLevel:").append(batteryLevel).append("\n");
        sb.append("batteryType:").append(batteryType).append("\n");
        sb.append("flashSize:").append(flashSize).append("\n");
        sb.append("steeringGear:").append(steeringGear).append("\n");
        sb.append("infrared:").append(infrared).append("\n");
//        sb.append("gyro:").append(gyro).append("\n");
        sb.append("touch:").append(touch).append("\n");
        sb.append("lighting:").append(lighting).append("\n");
//        sb.append("gravity:").append(gravity).append("\n");
        sb.append("ultrasound:").append(ultrasound).append("\n");
//        sb.append("led:").append(led).append("\n");
        sb.append("speaker:").append(speaker).append("\n");
        sb.append("envLight:").append(envLight).append("\n");
//        sb.append("airPressure:").append(airPressure).append("\n");
        sb.append("sound:").append(sound).append("\n");
        sb.append("humiture:").append(humiture).append("\n");
        sb.append("color:").append(color).append("\n");
        sb.append("motor:").append(motor).append("\n");
        sb.append("sucker:").append(sucker).append("\n");
        sb.append("ledBelt:").append(ledBelt).append("\n");
        sb.append("lcd:").append(lcd).append("\n");
        sb.append("vision:").append(vision).append("\n");
//        sb.append("rgbLed:").append(rgbLed).append("\n");
        sb.append("}");
        return sb.toString();
    }

}
