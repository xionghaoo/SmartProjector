/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * bridge框架定义的array类型在java中的具体实现
 * <br>
 * <br>
 * <b>Bridge数据类型:</b>
 * <ul>
 * <li><b>string</b>:指各个平台的字符串类型，如在Java中的String、Javascript中的string等</li>
 * <li><b>number</b>:指各个平台中的数值类型，如Java中的int、long、double、Number等都属于number类型</li>
 * <li><b>boolean</b>:指布尔类型，bridge框架中统一使用1表示true和0表示false</li>
 * <li><b>object</b>:指各个平台的Json Object，比如Java中的JSONObject,iOS中的泛型Dictionary等</li>
 * <li><b>array</b>:指各个平台的Json Array，比如Java中的JSONArray,iOS中的泛型Array等</li>
 * </ul>
 *
 * @Author qinicy
 * @Date 2019/3/26
 **/
public class BridgeArray extends JSONArray {
    public BridgeArray() {
        super();
    }

    public BridgeArray(Collection copyFrom) {
        super(copyFrom);
    }

    public BridgeArray(JSONTokener readFrom) throws JSONException {
        super(readFrom);
    }

    public BridgeArray(String json) throws JSONException {
        super(json);
    }

    public BridgeArray(Object array) throws JSONException {
        super(array);
    }

    /**
     * 把JSONArray转换成list
     *
     * @return
     */
    public List asList() {
        ArrayList<Object> arrayList = new ArrayList<>();
        try {
            String json = toString();
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(json).getAsJsonArray();
            for (JsonElement element : jsonArray) {
                Object object = new Gson().fromJson(element, Object.class);
                arrayList.add(object);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
