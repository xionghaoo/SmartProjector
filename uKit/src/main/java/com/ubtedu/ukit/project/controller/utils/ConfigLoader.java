/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.controller.utils;

import com.ubtedu.ukit.application.UKitApplication;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;

/**
 * @Author naOKi
 * @Date 2018/11/28
 **/
public class ConfigLoader {

    private ConfigLoader() {}

    /**
     * 不想每一个配置都定义一个实体类，所以统一解析成JSONObject
     * @param path
     * @return
     */
    public static JSONObject loadFromAssets(String path) {
        JSONObject result = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            is = UKitApplication.getInstance().getAssets().open(path);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8 * 1024];
            int readLen;
            while((readLen = is.read(buffer, 0, buffer.length)) > 0) {
                baos.write(buffer, 0 , readLen);
            }
            String jsonString = new String(baos.toByteArray(), "UTF-8");
            result = new JSONObject(jsonString);
        } catch (Exception e) {
            // ignore
        } finally {
            close(is);
            close(baos);
        }
        return result;
    }

    public static void close(Closeable closeable) {
        if(closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            // ignore
        }
    }

}
