/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Map;

/**
 * Bridge框架定义的Json object在java平台的类型
 * <br>
 * <b>Bridge数据类型:</b>
 * <ul>
 * <li><b>string</b>:指各个平台的字符串类型，如在Java中的String、Javascript中的string等</li>
 * <li><b>number</b>:指各个平台中的数值类型，如Java中的int、long、double、Number等都属于number类型</li>
 * <li><b>boolean</b>:指布尔类型，bridge框架中统一使用1表示true和0表示false</li>
 * <li><b>object</b>:指各个平台的Json Object，比如Java中的JSONObject,iOS中的泛型Dictionary等</li>
 * <li><b>array</b>:指各个平台的Json Array，比如Java中的JSONArray,iOS中的泛型Array等</li>
 * </ul>
 * @Author qinicy
 * @Date 2019/3/26
 **/
public class BridgeObject extends JSONObject {

    public BridgeObject() {
        super();
    }

    public BridgeObject(Map copyFrom) {
        super(copyFrom);
    }

    public BridgeObject(JSONTokener readFrom) throws JSONException {
        super(readFrom);
    }

    public BridgeObject(String json) throws JSONException {
        super(json);
    }

    public BridgeObject(JSONObject copyFrom, String[] names) throws JSONException {
        super(copyFrom, names);
    }
}
