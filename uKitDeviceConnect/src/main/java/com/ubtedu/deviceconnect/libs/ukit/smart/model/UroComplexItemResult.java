package com.ubtedu.deviceconnect.libs.ukit.smart.model;

public class UroComplexItemResult<T> {
    private int id;
    private int ack;
    private int cmd;
    private T data;

    private UroComplexItemResult(){}

    public static <D> UroComplexItemResult<D> newInstance(D data, int id, int ack, int cmd) {
        UroComplexItemResult<D> result = new UroComplexItemResult<>();
        result.id = id;
        result.ack = ack;
        result.cmd = cmd;
        result.data = data;
        return result;
    }

    public int getId() {
        return id;
    }

    public T getData() {
        return data;
    }

    public int getErrorCode() {
        return ack;
    }

    public int getCmd() {
        return cmd;
    }

    public boolean isSuccess() {
        return ack == 0;
    }
}
