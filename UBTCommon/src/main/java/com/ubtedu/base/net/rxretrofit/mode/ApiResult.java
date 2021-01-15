package com.ubtedu.base.net.rxretrofit.mode;

import com.google.gson.annotations.SerializedName;

/**
 * @Description: 封装的通用服务器返回对象，可自行定义
 * @author: qinicy
 * @date: 17/5/416:43
 */
public class ApiResult<T> {
    @SerializedName("code")
    public int code;
    @SerializedName(value = "msg", alternate = {"message"})
    public String msg;
    @SerializedName("data")
    public T data;

    @SerializedName("sourceRawData")
    public T sourceRawData;

    @SerializedName("sourceRawString")
    public String sourceRawString;

    @Override
    public String toString() {
        return "ApiResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
