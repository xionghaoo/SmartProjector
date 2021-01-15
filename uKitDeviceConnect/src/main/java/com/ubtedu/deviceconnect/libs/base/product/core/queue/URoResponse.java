package com.ubtedu.deviceconnect.libs.base.product.core.queue;

/**
 * @Author naOKi
 **/
public class URoResponse<T> {

    private boolean abort = false;
    private boolean push = false;

    public void abort() {
        abort = true;
    }

    public boolean isAborted() {
        return abort;
    }

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
    }

    private URoCommand cmd;

    private boolean isSuccess;
    private byte[] rawResponse;
    private byte[] bizData;
    private URoRequest request;
    private T data;
    private Object extraData;
    private URoResponse() {}

    private int errorCode = 0;

    public URoCommand getCmd() {
        return cmd;
    }

    public void setCmd(URoCommand cmd) {
        this.cmd = cmd;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public URoRequest getRequest() {
        return request;
    }

    public void setRequest(URoRequest request) {
        this.request = request;
    }

    public byte[] getBizData() {
        return bizData;
    }

    public void setBizData(byte[] bizData) {
        this.bizData = bizData;
    }

    public byte[] getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(byte[] rawResponse) {
        this.rawResponse = rawResponse;
    }

    public T getData() {
        return data;
    }

    public <D> D getExtraData() {
        try {
            return (D)extraData;
        } catch (Throwable e) {
            return null;
        }
    }

    public void setExtraData(Object extraData) {
        this.extraData = extraData;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> URoResponse<T> newInstance(T data) {
        return newInstance(data != null, null, null, data);
    }

    public static <T> URoResponse<T> newInstance(boolean success, T data) {
        return newInstance(success, null, null, data);
    }

    public static <T> URoResponse<T> newInstance(boolean success, URoRequest request, byte[] rawResponse, T data) {
        URoResponse<T> result = new URoResponse<>();
        result.setSuccess(success);
        result.setData(data);
        result.setRequest(request);
        result.setRawResponse(rawResponse);
        return result;
    }
}
