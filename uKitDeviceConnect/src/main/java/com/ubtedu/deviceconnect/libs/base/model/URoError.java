package com.ubtedu.deviceconnect.libs.base.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoError {

    public static class UkitError {
        public static final int PT_ERR_OK = 0; //成功
        public static final int PT_ERR_DEV = 1; //设备异常
        public static final int PT_ERR_NO_MEM = 2; //内存不足
        public static final int PT_ERR_FS = 3; //文件系统错误
        public static final int PT_ERR_CMD = 4; //指令不存在
        public static final int PT_ERR_PARAM = 5; //参数错误
        public static final int PT_ERR_BUSY = 6; //设备忙
        public static final int PT_ERR_DISK = 7; //磁盘空间不足
        public static final int PT_ERR_FUNCTION = 8; //功能不支持
        public static final int PT_ERR_OVERTIME = 9; //超时
        public static final int PT_ERR_ID = 10; //ID异常
        public static final int PT_ERR_PATH = 11; //路径非法
        public static final int PT_ERR_CONTENT = 12; //文件内容非法
        public static final int PT_ERR_SIZE = 13; //文件大小非法
        public static final int PT_ERR_INDEX = 14; //索引非法
        public static final int PT_ERR_CHECK = 15; //校验错误
    }

    public static final URoError SUCCESS = new URoError(0, "success");
    public static final URoError TIMEOUT = new URoError(1000, "timeout");
    public static final URoError MISSION_TIMEOUT = new URoError(1000, "mission_timeout");
    public static final URoError READ_ERROR = new URoError(9990, "conflict");
    public static final URoError CTRL_ERROR = new URoError(9991, "conflict");
    public static final URoError CONFLICT = new URoError(9992, "conflict");
    public static final URoError ABNORMAL = new URoError(9993, "abnormal");
    public static final URoError NO_TARGET = new URoError(9994, "no target");
    public static final URoError NOT_ALLOWED = new URoError(9995, "not allowed");
    public static final URoError NOT_CONNECTED = new URoError(9995, "not connected");
    public static final URoError NO_SUCH_METHOD = new URoError(9996, "no such method");
    public static final URoError INVALID = new URoError(9997, "invalid");
    public static final URoError ABORT = new URoError(9998, "abort");
    public static final URoError UNKNOWN = new URoError(9999, "unknown");

    public final int code;
    public final String domain;
    public final Throwable e;
    public final Map<String, Object> userInfo;

    public URoError(int code, String domain) {
        this(code, domain, null);
    }

    public URoError(int code, String domain, Throwable e) {
        this.code = code;
        this.domain = domain;
        this.e = e;
        userInfo = new HashMap<>();
    }

    public boolean isSuccess() {
        return code == 0;
    }

    public void setAddonInfo(String key, Object value) {
        userInfo.put(key, value);
    }

    @Override
    public String toString() {
        return "URoError{" +
                "code=" + code +
                ", domain='" + domain + '\'' +
                ", e=" + e +
                ", userInfo=" + userInfo +
                '}';
    }

    public String localizedDescription() {
        return toString();
    }

}
