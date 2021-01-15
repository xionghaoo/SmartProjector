/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.alpha1x.core.base.Lifecycle.vo;

/**
 * A generic class that holds a value with its loading status.
 *
 * @Author qinicy
 * @Date 2018/9/4
 **/
public class Resource<T> {
    public Status status;
    public T data;
    public String message;
    public Exception exception;

    public Resource(Status status, T data, String message, Exception e) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.exception = e;
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null, null);
    }

    public static <T> Resource<T> error(String msg, T data, Exception e) {
        return new Resource<>(Status.ERROR, data, msg, e);
    }

    public static <T> Resource<T> loading(T data) {
        return new Resource<>(Status.LOADING, data, null, null);
    }

}
