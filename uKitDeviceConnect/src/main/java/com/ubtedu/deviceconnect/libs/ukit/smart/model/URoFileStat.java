package com.ubtedu.deviceconnect.libs.ukit.smart.model;

/**
 * @Author naOKi
 * @Date 2019/12/18
 **/
public class URoFileStat {

    private final String name;
    private final long size;
    private final Type type;
    private final String md5;

    public enum Type {
        NOT_EXIST, DIR, FILE
    }

    public URoFileStat(String name, long size, Type type, String md5) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.md5 = md5;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public Type getType() {
        return type;
    }

    public String getMd5() {
        return md5;
    }

}
