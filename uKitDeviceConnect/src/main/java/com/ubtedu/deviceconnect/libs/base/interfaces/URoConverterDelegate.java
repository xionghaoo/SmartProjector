package com.ubtedu.deviceconnect.libs.base.interfaces;

/**
 * @Author naOKi
 * @Date 2019/09/25
 **/
public interface URoConverterDelegate<S, T> {
    T convert(S source) throws Throwable;
}
