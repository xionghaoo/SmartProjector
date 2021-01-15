package com.ubtedu.alpha1x.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/12.
 */
public class GsonUtil extends Json {
    private Gson gson = new Gson();

    @Override
    public String toJson(Object src) {
        return gson.toJson(src);
    }

    @Override
    public <T> T toObject(String json, Class<T> claxx) {
        try {
            return gson.fromJson(json, claxx);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> ArrayList<T> toObjectList(String json, Class<T> claxx) {
        try {
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(json).getAsJsonArray();

            ArrayList<T> arrayList = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                T object = gson.fromJson(element, claxx);
                arrayList.add(object);
            }
            return arrayList;
        }catch (Exception e) {
            LogUtil.e("Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public <T> T toObject(String json, Type type) {
        return gson.fromJson(json, type);
    }

    @Override
    public <T> T toObject(byte[] bytes, Class<T> claxx) {
        return gson.fromJson(new String(bytes), claxx);
    }

    /**
     * PS：不支持嵌套json的转换。
     * @param jsons
     * @return
     */
    public static String jsonToFormUrlEncodedParams(String jsons){
        String s = jsons;
        s = s.replaceAll("\",\"","&");

        s = s.replaceAll("\":\"","=");

        s = s.replaceAll("\":","=");
        s = s.replaceAll(",\"","&");
        if (s.length() > 2){
            s = s.substring(2);
        }
        if (s.endsWith("\"}")){
            s = s.substring(0,s.length() - 2);
        }else {
            s = s.substring(0,s.length() - 1);
        }

        return s;
    }
}
