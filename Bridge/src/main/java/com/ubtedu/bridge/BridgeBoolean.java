package com.ubtedu.bridge;

/**
 * bridge框架定义的boolean类型在java中的取值.
 * <br>
 * <b>Bridge数据类型:</b>
 * <ul>
 * <li><b>string</b>:指各个平台的字符串类型，如在Java中的String、Javascript中的string等</li>
 * <li><b>number</b>:指各个平台中的数值类型，如Java中的int、long、double、Number等都属于number类型</li>
 * <li><b>boolean</b>:指布尔类型，bridge框架中统一使用1表示true和0表示false</li>
 * <li><b>object</b>:指各个平台的Json Object，比如Java中的JSONObject,iOS中的泛型Dictionary等</li>
 * <li><b>array</b>:指各个平台的Json Array，比如Java中的JSONArray,iOS中的泛型Array等</li>
 * </ul>
 */
public class BridgeBoolean {
    public static int wrap(boolean source) {
        return source ? TRUE() : FALSE();
    }

    public static boolean isTrue(Number bridgeBoolean) {
        if (bridgeBoolean != null){
            return bridgeBoolean.intValue() == TRUE();
        }
        return false;
    }

    public static int TRUE() {
        return 1;
    }

    public static int FALSE() {
        return 0;
    }
}
