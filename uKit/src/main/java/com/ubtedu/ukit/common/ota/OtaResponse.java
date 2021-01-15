/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.ota;

/**
 * @Author qinicy
 * @Date 2018/12/18
 **/
public class OtaResponse {
    public OtaResponse(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess;
    public OtaComponent component;
    public boolean isForced;
    public String version;
    public String filePath;
    public String md5;
    public String url;
    public int size;
}
